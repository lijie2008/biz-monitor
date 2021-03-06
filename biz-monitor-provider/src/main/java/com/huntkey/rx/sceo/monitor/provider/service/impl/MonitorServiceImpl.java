package com.huntkey.rx.sceo.monitor.provider.service.impl;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.ENDTIME;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.ID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.PID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.STARTTIME;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.YYYY_MM_DD;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.edm.entity.EmployeeEntity;
import com.huntkey.rx.edm.entity.MoniMoniHisSetaEntity;
import com.huntkey.rx.edm.entity.MonitorEntity;
import com.huntkey.rx.edm.entity.MonitortreeorderEntity;
import com.huntkey.rx.edm.entity.MtorMtorNodeSetaEntity;
import com.huntkey.rx.edm.entity.MtorMtorResSetbEntity;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.CurrentSessionEntity;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.service.BizFormService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.orm.common.model.OrmParam;
import com.huntkey.rx.sceo.orm.common.type.SQLCurdEnum;
import com.huntkey.rx.sceo.orm.common.type.SQLSortEnum;
import com.huntkey.rx.sceo.orm.common.type.SQLSymbolEnum;
import com.huntkey.rx.sceo.orm.common.util.EdmUtil;
import com.huntkey.rx.sceo.orm.service.OrmService;
@Service
public class MonitorServiceImpl implements MonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    
    @Autowired
    private ModelerClient edmClient;
    
    @Autowired
    private MonitorTreeService treeService;
    
    @Autowired
    private MonitorTreeOrderService orderTree;
    
    @Autowired
    private BizFormService formService;
    
    @Autowired
    private OrmService ormService;
    
    @Resource(name="redisTemplate")
    private HashOperations<String, String, NodeTo> hasOps;
    
    @Value("${redis.key.timeout}")
    private Long timeout;
    
	/**
	 * type - 1 新增操作  2- 维护操作
	 * 
	 * 1. 校验是否存在相应的临时单
	 * 2. 校验临时单中的临时树是否已失效 失效做删除操作，接口返回null - 走原有流程 最后返回 redis中的 key 选择在redis中查询 、复制选择 不复制
	 * 3. 临时单中存在生效的临时树，查看临时树在redis中是否存在 
	 * 4. 存在 返回true 前端提示 是否继续上次操作  
	 *     4.1 选择是 调用 临时单查询 type 选择 redis、 复制选择 不复制
	 *     4.2 选择 否 调用临时单查询 type 选择 临时单表、 复制选择 复制 
	 * 5. 不存在 返回false 前端调用 临时单查询 、type 选择临时单 、  复制选择到redis 
	 * @throws Exception 
	 * 
	 */
	public JSONObject checkOrder(String classId, String rootId, int type) throws Exception{
	    
	    JSONObject obj = new JSONObject();
	    
	    if(StringUtil.isNullOrEmpty(rootId))
            rootId = "";
	    
	    OrmParam param = new OrmParam();
	    
	    // 判断当前节点类型的临时单状态是否是 等待审批 和 等待批准
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        param.setWhereExp(OrmParam.or(param.getEqualXML("orde_status", Constant.ORDER_STATUS_WAIT), 
                param.getEqualXML("orde_status", Constant.ORDER_STATUS_WAIT_COMMIT)));
        param.setWhereExp(OrmParam.and(param.getWhereExp(),param.getEqualXML("mtor_cls_id", classId), 
                param.getEqualXML("mtor_order_type", String.valueOf(type)),
                param.getEqualXML("mtor_order_root", rootId)));
                                       
        List<MonitortreeorderEntity> wait_list = ormService.selectBeanList(MonitortreeorderEntity.class, param);
        
        if(wait_list != null && !wait_list.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60024.getCode(), ErrorMessage._60024.getMsg());
        
        param.reset();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        param.setWhereExp(OrmParam.and(param.getEqualXML("mtor_cls_id", classId), 
                                       param.getEqualXML("mtor_order_type", String.valueOf(type)),
                                       param.getEqualXML("mtor_order_root", rootId),
                                       param.getUnequalXML("orde_status", Constant.ORDER_STATUS_COMMIT)));
	    
        List<MonitortreeorderEntity> o_list = ormService.selectBeanList(MonitortreeorderEntity.class, param);
	    
        if(o_list == null || o_list.isEmpty())
            return null;
        
        if(o_list.size() > 1)
            ApplicationException.throwCodeMesg(ErrorMessage._60019.getCode(), ErrorMessage._60019.getMsg());
        
	    // 存在临时单 - 判断临时单是否失效
        param.reset();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        param.setWhereExp(OrmParam.and(param.getLessThanAndEqualXML("mtor_end", new SimpleDateFormat(YYYY_MM_DD).format(new Date())), 
                                       param.getEqualXML("mtor_lvl", Constant.ROOT_LVL),
                                       param.getEqualXML("mtor_lvl_code", Constant.ROOT_LVL_CODE),
                                       param.getEqualXML(Constant.PID, o_list.get(0).getId())));
        List<MtorMtorNodeSetaEntity> r_list = ormService.selectBeanList(MtorMtorNodeSetaEntity.class, param);
        
        // 存在的临时单节点  已失效 - 需要将临时单  和 节点信息全部清除
        if(r_list != null && !r_list.isEmpty()){
            // 查询出所有的节点id
            param.reset();
            param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            param.setWhereExp(param.getEqualXML(Constant.PID, o_list.get(0).getId()));
            List<MtorMtorNodeSetaEntity> n_list = ormService.selectBeanList(MtorMtorNodeSetaEntity.class, param);
            Object[] ids = n_list.stream().map(MtorMtorNodeSetaEntity::getId).toArray();
            
            // 删除节点
            param.reset();
            param.setWhereExp(param.getInXML(Constant.ID, ids));
            ormService.delete(MtorMtorNodeSetaEntity.class, param);
            
            // 删除资源
            param.reset();
            param.setWhereExp(param.getInXML(Constant.PID, ids));
            ormService.delete(MtorMtorResSetbEntity.class, param);
            
            // 删除临时单
            ormService.delete(MonitortreeorderEntity.class, o_list.get(0).getId());
            
            // 删除redis中现存的数据
            hasOps.getOperations().delete(o_list.get(0).getId()+ Constant.KEY_SEP +classId);
            hasOps.getOperations().delete(o_list.get(0).getId()+ Constant.KEY_SEP +classId + Constant.REVOKE_KEY);
        }
	    
	    String key = o_list.get(0).getId() + Constant.KEY_SEP + classId;
	    String revokeKey = key + Constant.REVOKE_KEY;
	    
	    NodeTo root = hasOps.get(key, Constant.ROOT_LVL_CODE);

	    // redis中数据 过期 || redis中没有 数据
	    if(root == null || !getDate(root.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS).after(new Date())){
	        hasOps.getOperations().delete(key);
	        hasOps.getOperations().delete(revokeKey);
	        
	        // 临时单中数据同步到redis中
	        List<NodeTo> list = tempTree(key, "", 1, true);
	        
	        Map<String,NodeTo> map = new HashMap<String,NodeTo>();
	        
	        list.stream().forEach(s->{
	            s.setKey(key);
	            map.put(s.getLvlCode(), s);
	        });
	        
	        hasOps.putAll(key, map);
	        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
	        obj.put("flag", false);
	    }else
	        obj.put("flag", true);
	    
	    obj.put("key", key);
	    
	    return obj;
	}

    
    private Date getDate(String str,String mat){
        DateFormat format = new SimpleDateFormat(mat);
        try {
            return format.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误"+ str);
        }
    }
    
	/**
	 * flag
	 *    true 继续上一步操作 - 直接返回key
	 *    false - 不继续上一步操作 
	 * @throws Exception 
	 */
	@Override
    public String editBefore(String key, boolean flag) throws Exception{
	    
	    if(!hasOps.getOperations().hasKey(key))
	        ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
	    
	    if(flag)
	        return key;
	    
	    hasOps.getOperations().delete(key);
	    hasOps.getOperations().delete(key + Constant.REVOKE_KEY);
	    
        List<NodeTo> list = tempTree(key, "", 1, true);
        Map<String,NodeTo> map = new HashMap<String,NodeTo>();
        list.stream().forEach(s->{
            s.setKey(key);
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        
        return key;
    }
    
    @Override
    public List<NodeTo> tempTree(String tempId, String validDate,int type,boolean flag) throws Exception {
        
        List<NodeTo> list = new ArrayList<NodeTo>();
        
        // 传入进来的可能是 redis的key
        tempId = tempId.split(Constant.KEY_SEP)[0];
        
        MonitortreeorderEntity order = ormService.load(MonitortreeorderEntity.class, tempId);
        
        if(order == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "单据表中临时单["+ tempId +"]"+ ErrorMessage._60005.getMsg());
      
        // 取出classId
        String classId = order.getMtor_cls_id();
        
        if(StringUtil.isNullOrEmpty(classId))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        switch(type){
            
            case 1:
                OrmParam param = new OrmParam();
                param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
                param.setWhereExp(OrmParam.and(param.getEqualXML(Constant.PID, tempId), 
                        param.getLessThanXML("mtor_type", ChangeType.INVALID.toString())));
                param.addOrderExpElement(SQLSortEnum.ASC, "mtor_lvl")
                     .addOrderExpElement(SQLSortEnum.ASC, "mtor_seq");
                
                if(StringUtil.isNullOrEmpty(validDate)){
                    validDate=getNowDateStr(YYYY_MM_DD);
                    param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getGreaterThanXML("mtor_end", validDate)));
                }else{
                    validDate+=" 00:00:00";
                    param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                                   param.getGreaterThanXML("mtor_end", validDate),
                                                   param.getLessThanAndEqualXML("mtor_beg", validDate)));
                }
                
                List<MtorMtorNodeSetaEntity> nodes = ormService.selectBeanList(MtorMtorNodeSetaEntity.class, param);
                
                if(nodes == null || nodes.isEmpty())
                    return list;
                
                JSONArray nodeArry = new JSONArray();
                
                for(MtorMtorNodeSetaEntity node : nodes)
                    nodeArry.add(JSONObject.parseObject(JSON.toJSONString(node)));
                
                list = NodeTo.setValue(nodeArry);
                
                // 增加监管人和协管人赋值
                for(NodeTo tt : list){
                    tt.setAssitText(getStaffText(tt.getAssit()));
                    tt.setMajorText(getStaffText(tt.getMajor()));
                }
                
                //不包含资源
                if(!flag)
                    return list;
                
                
                // 查询出所有的资源 和 资源id
                List<String> ids = nodes.stream().map(MtorMtorNodeSetaEntity::getId).collect(Collectors.toList());
                
                // 1 - 代表从正式监管类去资源信息  2 - 代表从临时单据中查询资源信息
                JSONArray resources = treeService.getNodeResources(null, ids, classId, Constant.MTOR_NODES_EDM, 2);
                
                for(int i = 0; i < nodeArry.size(); i++){
                    JSONObject to = nodeArry.getJSONObject(i);
                    
                    if(resources != null && !resources.isEmpty()){
                        JSONArray node_res = new JSONArray();
                        
                        for(int k = 0; k < resources.size(); k++){
                            if(!to.getString(Constant.ID).equals(resources.getJSONObject(k).getString("nodeId")))
                                continue;
                            JSONObject re = new JSONObject();
                            re.put("mtor_res_id", resources.getJSONObject(k).getString(ID));
                            re.put("text", resources.getJSONObject(k).getString("text"));
                            re.put(PID, to.getString(Constant.ID));
                            node_res.add(re);
                        }
                        
                        if(!node_res.isEmpty())
                            to.put("mtor_res_set", node_res);
                    }
                }
                
                list = NodeTo.setValue(nodeArry);
                
                // 增加监管人和协管人赋值
                for(NodeTo tt : list){
                    tt.setAssitText(getStaffText(tt.getAssit()));
                    tt.setMajorText(getStaffText(tt.getMajor()));
                }
                
                break;
                
            case 2:
                String key = tempId + Constant.KEY_SEP + classId;
                if(!hasOps.getOperations().hasKey(key))
                    ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
                Set<String> fields = hasOps.keys(key);
                list = new ArrayList<NodeTo>();
                
                for(String field : fields){
                    NodeTo to= hasOps.get(key, field);
                    Date begin = getDate(to.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
                    Date end = getDate(to.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
                    if(StringUtil.isNullOrEmpty(validDate)){
                        Date now = getDate(new SimpleDateFormat(YYYY_MM_DD).format(new Date()) + STARTTIME, Constant.YYYY_MM_DD_HH_MM_SS);
                        if(end.after(now) && ChangeType.INVALID.getValue() != to.getType())
                            list.add(to);
                    }else{
                        validDate+=Constant.STARTTIME;
                        Date now = getDate(validDate, Constant.YYYY_MM_DD_HH_MM_SS);
                        if(!begin.after(now) && end.after(now) && ChangeType.INVALID.getValue() != to.getType())
                            list.add(to);
                    }
                }
                
                break;
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60000.getCode(), ErrorMessage._60000.getMsg());
        }
        return list;
    }
	
	 /**
     * 监管树的操作
     * type： 1 - 新增  2 - 复制新增 
     * @param addMonitorTreeTo
     * @return
	 * @throws Exception 
     */
    @Override
    @Transactional(readOnly=false)
    public String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo) throws Exception {
        
        int type=addMonitorTreeTo.getType(); 
        
        String beginDate=addMonitorTreeTo.getBeginDate(); 
        String endDate=addMonitorTreeTo.getEndDate(); 
        String classId=addMonitorTreeTo.getClassId();
        String rootId=StringUtil.isNullOrEmpty(addMonitorTreeTo.getRootId()) ? "" : addMonitorTreeTo.getRootId();
        String rootEdmcNameEn = addMonitorTreeTo.getRootEdmcNameEn();
        
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        param.setWhereExp(OrmParam.and(param.getEqualXML("mtor_order_root", rootId), 
                                       param.getEqualXML("mtor_order_type", String.valueOf(ChangeType.ADD.getValue())),
                                       param.getEqualXML("mtor_cls_id", classId),
                                       param.getUnequalXML("orde_status", Constant.ORDER_STATUS_COMMIT)));
        
        List<MonitortreeorderEntity> o_list = ormService.selectBeanList(MonitortreeorderEntity.class, param);
        // 检查临时单是否存在
        if(o_list != null && o_list.size() == 1)
            return o_list.get(0).getId() + Constant.KEY_SEP + classId;
        
        // 如果不存在临时单 生成临时单信息
        String tempId=createTemp(classId,ChangeType.ADD.getValue(),"");
        
        JSONArray nodes = new JSONArray();
        
        switch(type){
            
            case 1:
                nodes.add(createRootNode(tempId,beginDate,endDate));
                break;
                
            case 2:
                nodes.addAll(copyTree(rootEdmcNameEn, classId, rootId,tempId,ChangeType.ADD.getValue(),beginDate,endDate));
                break;  
        }
        
        if(nodes == null || nodes.isEmpty())
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "节点" + ErrorMessage._60005.getMsg());
        
        // 将nodes数据放入到redis中
        String key = tempId + Constant.KEY_SEP + classId;
        String revokedKey = key + Constant.REVOKE_KEY;
        
        List<NodeTo> list = NodeTo.setValue(nodes);
        
        // 增加监管人和协管人赋值
        for(NodeTo tt : list){
            tt.setAssitText(getStaffText(tt.getAssit()));
            tt.setMajorText(getStaffText(tt.getMajor()));
        }
        
        hasOps.getOperations().delete(key);
        hasOps.getOperations().delete(revokedKey);
        
        Map<String, NodeTo> map = new HashMap<String,NodeTo>();
        
        list.stream().forEach(s->{
            s.setKey(key);
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // 新增临时单节点信息
        List<MtorMtorNodeSetaEntity> m_list = JSONArray.parseArray(nodes.toJSONString(), MtorMtorNodeSetaEntity.class);
        
        CurrentSessionEntity session = formService.getCurrentSessionInfo();
        for(MtorMtorNodeSetaEntity n : m_list){
            //主表 - 临时单节点集合
            n.setCreuser(session.getEmployeeId());
            n.setClassName(EdmUtil.getEdmClassName(MonitortreeorderEntity.class));
            String id = ormService.insertSelective(n).toString();
            n.setId(id);
            //属性集 - 临时单节点的资源集合
            List<MtorMtorResSetbEntity> res = n.getMtor_res_set();
            if(res != null && !res.isEmpty()){
                EdmUtil.setPropertyBaseEntitiesSysColumns(MonitortreeorderEntity.class,n, res, SQLCurdEnum.INSERT);
                ormService.insert(res);
            }
        }
        
        return key;
    }
    
    @Override
    @Transactional(readOnly=false)
    public String treeMaintaince(String classId, String rootId, String rootEdmcNameEn) throws Exception {
        
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        param.setWhereExp(OrmParam.and(param.getEqualXML("mtor_order_root", rootId), 
                                       param.getEqualXML("mtor_order_type", String.valueOf(ChangeType.UPDATE.getValue())),
                                       param.getEqualXML("mtor_cls_id", classId),
                                       param.getUnequalXML("orde_status", Constant.ORDER_STATUS_COMMIT)));
        
        List<MonitortreeorderEntity> order = ormService.selectBeanList(MonitortreeorderEntity.class,param);
        
        if(order != null && !order.isEmpty())
            return order.get(0).getId() + Constant.KEY_SEP + classId;
        
        String tempId=createTemp(classId,ChangeType.UPDATE.getValue(),rootId);
        
        JSONArray nodes = copyTree(rootEdmcNameEn,classId, rootId,tempId,ChangeType.UPDATE.getValue(),null,null);
        
        // 将nodes数据放入到redis中
        String key = tempId + Constant.KEY_SEP + classId;
        String revokedKey = key + Constant.REVOKE_KEY;
        
        List<NodeTo> list = NodeTo.setValue(nodes);
        
        // 增加监管人和协管人赋值
        for(NodeTo tt : list){
            tt.setAssitText(getStaffText(tt.getAssit()));
            tt.setMajorText(getStaffText(tt.getMajor()));
        }
        
        hasOps.getOperations().delete(key);
        hasOps.getOperations().delete(revokedKey);
        
        Map<String, NodeTo> map = new HashMap<String,NodeTo>();
        
        list.stream().forEach(s->{
            s.setKey(key);
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // 新增临时单节点信息
        List<MtorMtorNodeSetaEntity> m_list = JSONArray.parseArray(nodes.toJSONString(), MtorMtorNodeSetaEntity.class);
        CurrentSessionEntity session = formService.getCurrentSessionInfo();
        for(MtorMtorNodeSetaEntity n : m_list){
            
            //主表 - 临时单节点集合
            n.setCreuser(session.getEmployeeId());
            n.setClassName(EdmUtil.getEdmClassName(MonitortreeorderEntity.class));
            String id = ormService.insertSelective(n).toString();
            n.setId(id);
            //属性集 - 临时单节点的资源集合
            List<MtorMtorResSetbEntity> res = n.getMtor_res_set();
            if(res != null && !res.isEmpty()){
                EdmUtil.setPropertyBaseEntitiesSysColumns(MonitortreeorderEntity.class,n, res, SQLCurdEnum.INSERT);
                ormService.insert(res);
            }
        }
        
        return key;
    }
    
    /**
     * 创建临时单
     * @param classId 监管类ID
     * @param changeType 树的变更类型
     * @param rootId 根节点ID
     * @return 临时单ID
     * @throws Exception 
     */
    private String createTemp(String classId,int changeType,String rootId) throws Exception{
        
        MonitortreeorderEntity order = new MonitortreeorderEntity();
        order.setOrde_nbr("LS"+System.currentTimeMillis());
        order.setMtor_order_type(changeType);
        order.setMtor_cls_id(classId);
        order.setMtor_order_root(rootId);
        CurrentSessionEntity session = formService.getCurrentSessionInfo();
        // 制单人和 制单岗位
        order.setOrde_adduser(session.getEmployeeId());
        order.setOrde_duty(session.getPositionId());
        
        order.setOrde_status(Constant.ORDER_STATUS_TEMP);
        
        order.setCreuser(session.getEmployeeId());
        return ormService.insertSelective(order).toString();
    }
	
    /**
     * 新增根节点
     * @param tempId 临时单ID
     * @param beginDate 生效日期  
     * @param endDate 失效日期
     * @return
     */
    private JSONObject createRootNode(String tempId,String beginDate,String endDate) {
        
        MtorMtorNodeSetaEntity root = new MtorMtorNodeSetaEntity();
        root.setPid(tempId);
        root.setMtor_node_no("NODE"+System.currentTimeMillis());
        root.setMtor_node_name(Constant.INITNODENAME);
        root.setMtor_node_def(Constant.INITNODENAME);
        root.setMtor_major("");
        root.setMtor_assit("");
        root.setMtor_beg(getDate(beginDate, Constant.YYYY_MM_DD_HH_MM_SS));
        root.setMtor_end(getDate(endDate, Constant.YYYY_MM_DD_HH_MM_SS));
        root.setMtor_index_conf("");
        root.setMtor_seq(Constant.ROOT_SEQ);
        root.setMtor_lvl(Constant.ROOT_LVL);
        root.setMtor_lvl_code(Constant.ROOT_LVL_CODE);
        root.setMtor_enum("");
        root.setMtor_relate_cnd("");
        root.setMtor_type(ChangeType.ADD.getValue());
        root.setMtor_relate_id("");
        root.setCreuser(formService.getCurrentSessionInfo().getEmployeeId());
        return JSONObject.parseObject(JSON.toJSONString(root));
    }
    
    /**
     * 复制监管树
     * @param rootEdmcNameEn edm类英文名  即监管树实体对象表
     * @param rootId 根节点ID
     * @param tempId 临时单ID
     * @param changeType 变更类型 1 - 复制(历史树、未来树  和 正在生效树)  、2 - 维护(正在生效树和未来树)
     * @throws Exception 
     */
    private JSONArray copyTree(String rootEdmcNameEn,String classId, String rootId,String tempId,int changeType,String beginDate,String endDate) throws Exception {
        // 查询根节点信息  的开始时间 和 结束时间
        Date rootBegin = null;
        Date rootEnd = null;
        
        if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
            MoniMoniHisSetaEntity hisNode = ormService.load(MoniMoniHisSetaEntity.class, rootId);
            
            if(hisNode == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
            
            rootBegin = hisNode.getMoni_hbeg();
            rootEnd = hisNode.getMoni_hend();
        }else{
            @SuppressWarnings("rawtypes")
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(rootEdmcNameEn));
            @SuppressWarnings("unchecked")
            MonitorEntity node = (MonitorEntity)ormService.load(cls, rootId);
            
            if(node == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
            
            rootBegin = node.getMoni_beg();
            rootEnd = node.getMoni_end();
        }
        
        if(!rootBegin.before(rootEnd))
            ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), ErrorMessage._60009.getMsg());
        
        // 查询出需要复制的节点
        ChangeType type = ChangeType.valueOf(changeType);
        Date now = getDate(new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date())+STARTTIME,Constant.YYYY_MM_DD_HH_MM_SS);
        
        OrmParam param = new OrmParam();
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
            param.setWhereExp(OrmParam.and(param.getGreaterThanAndEqualXML("moni_hbeg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                    .format(rootBegin)),
                                          param.getLessThanAndEqualXML("moni_hend", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                    .format(rootEnd)),
                                          param.getMatchLeftXML("moni_hlvl_code", Constant.ROOT_LVL_CODE)));
            param.addOrderExpElement(SQLSortEnum.ASC, "moni_hlvl")
                 .addOrderExpElement(SQLSortEnum.ASC, "moni_hseq");
        }else{
            param.setWhereExp(OrmParam.and(param.getGreaterThanAndEqualXML("moni_beg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                    .format(rootBegin)), 
                                           param.getLessThanAndEqualXML("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                    .format(rootEnd)),     
                                           param.getMatchLeftXML("moni_lvl_code", Constant.ROOT_LVL_CODE)));
                    
            param.addOrderExpElement(SQLSortEnum.ASC, "moni_lvl")
            .addOrderExpElement(SQLSortEnum.ASC, "moni_seq");
        }
        
        switch (type) {
            
            case ADD:
                // 正在生效的树复制                      moni_beg  <= now < moni_end
                if(!rootBegin.after(now) && rootEnd.after(now)){
                    if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
                        param.setWhereExp(OrmParam.and(param.getWhereExp(),
                                                        param.getLessThanAndEqualXML("moni_hbeg", new SimpleDateFormat(Constant.YYYY_MM_DD)
                                .format(new Date())+STARTTIME), 
                                                        param.getGreaterThanXML("moni_hend", new SimpleDateFormat(Constant.YYYY_MM_DD)
                                .format(new Date())+STARTTIME)));
                    }else{
                        param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                                       param.getLessThanAndEqualXML("moni_beg", new SimpleDateFormat(Constant.YYYY_MM_DD)
                                .format(new Date())+STARTTIME),
                                                       param.getGreaterThanXML("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD)
                                .format(new Date())+STARTTIME)));
                    }
                }else{
                    // 历史树和 未来树复制  需要  moni_end = 根节点失效时间
                    if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET))
                        param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("moni_hend", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                                .format(rootEnd))));
                    else
                        param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                                .format(rootEnd))));
                }
                break;
                
            case UPDATE:
                if(!rootEnd.after(now))
                    ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), "历史树不能维护");
                // 维护 时 复制 需要 moni_end > 当前时间
                if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET))
                    param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getGreaterThanXML("moni_hend", new SimpleDateFormat(Constant.YYYY_MM_DD)
                            .format(new Date())+STARTTIME)));
                else
                    param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getGreaterThanXML("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD)
                            .format(new Date())+STARTTIME)));
                break;
            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), "type类型[" + changeType +"]" +ErrorMessage._60004.getMsg());
        }
        
        List<String> ids = null;
        
        JSONArray nodes = new JSONArray();
        
        if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
            param.setWhereExp(OrmParam.and(param.getWhereExp(), param.getEqualXML("classname", rootEdmcNameEn.split("\\.")[0])));
            List<MoniMoniHisSetaEntity> hNodes = ormService.selectBeanList(MoniMoniHisSetaEntity.class, param);
            
            if(hNodes == null)
                return null;
            
            ids = hNodes.stream().map(MoniMoniHisSetaEntity::getId).collect(Collectors.toList());
            
            for(MoniMoniHisSetaEntity ee : hNodes)
                nodes.add(JSON.parseObject(JSON.toJSONString(ee)));
        }else{
            @SuppressWarnings("rawtypes")
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(rootEdmcNameEn));
            @SuppressWarnings("unchecked")
            List<? extends MonitorEntity>  nNodes= ormService.selectBeanList(cls, param);
            
            if(nNodes == null)
                return null;
            
            ids = nNodes.stream().map(MonitorEntity::getId).collect(Collectors.toList());
            
            for(MonitorEntity ee : nNodes)
                nodes.add(JSON.parseObject(JSON.toJSONString(ee)));
        }
        
        JSONArray resources = treeService.getNodeResources(null, ids, classId, rootEdmcNameEn, 1);
        
        if (resources == null || resources.isEmpty())
            return setOrderValues(tempId,nodes,type, beginDate, endDate,rootEdmcNameEn);
        
        for(int i = 0 ; i < nodes.size(); i++){
            String id = nodes.getJSONObject(i).getString(ID);
            JSONArray res = new JSONArray();
            for(int k = 0; k < resources.size(); k++){
                String nodeId = resources.getJSONObject(k).getString("nodeId");
                String resId = resources.getJSONObject(k).getString(ID);
                String text = resources.getJSONObject(k).getString("text");
                if(!id.equals(nodeId))
                    continue;
                JSONObject re = new JSONObject();
                re.put("moni_res_id", resId);
                re.put(PID, nodeId);
                re.put("text", text);
                res.add(re);
            }
            if(res != null && !res.isEmpty())
                nodes.getJSONObject(i).put("moni_res_set", res);
        }
        
        
        return setOrderValues(tempId,nodes,type, beginDate, endDate,rootEdmcNameEn);
    }
    
    
    private JSONArray setOrderValues(String orderId, JSONArray nodes, ChangeType type,String beginDate,String endDate,String rootEdmcNameEn){
        JSONArray no = new JSONArray();
        for(int i = 0; i < nodes.size(); i++){
            JSONObject to = nodes.getJSONObject(i);
            JSONObject node = new JSONObject();
            node.put(PID, orderId);
            if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
                node.put("mtor_node_no", to.getString("moni_hnode_no"));
                node.put("mtor_node_name", to.getString("moni_hnode_name"));
                node.put("mtor_node_def", to.getString("moni_hnode_def"));
                node.put("mtor_major", to.getString("moni_hmajor"));
                node.put("mtor_assit", to.getString("moni_hassit"));
                node.put("mtor_index_conf", to.getString("moni_hindex_conf"));
                node.put("mtor_seq", to.get("moni_hseq"));
                node.put("mtor_lvl_code", to.getString("moni_hlvl_code"));
                node.put("mtor_lvl", to.getString("moni_hlvl"));
                node.put("mtor_enum", to.getString("moni_henum"));
                node.put("mtor_relate_cnd", to.getString("moni_hrelate_cnd"));
                if(type == ChangeType.ADD){
                    node.put("mtor_type", ChangeType.ADD.getValue());
                    node.put("mtor_beg", getDate(beginDate,Constant.YYYY_MM_DD_HH_MM_SS).getTime());
                    node.put("mtor_end", getDate(endDate,Constant.YYYY_MM_DD_HH_MM_SS).getTime());
                }else{
                    node.put("mtor_type", ChangeType.UPDATE.getValue());
                    node.put("mtor_relate_id", to.getString(ID));
                    node.put("mtor_beg", to.getLong("moni_hbeg"));
                    node.put("mtor_end", to.getLong("moni_hend"));
                }
            }else{
                node.put("mtor_node_no", to.getString("moni_node_no"));
                node.put("mtor_node_name", to.getString("moni_node_name"));
                node.put("mtor_node_def", to.getString("moni_node_def"));
                node.put("mtor_major", to.getString("moni_major"));
                node.put("mtor_assit", to.getString("moni_assit"));
                node.put("mtor_index_conf", to.getString("moni_index_conf"));
                node.put("mtor_seq", to.getString("moni_seq"));
                node.put("mtor_lvl_code", to.getString("moni_lvl_code"));
                node.put("mtor_lvl", to.getString("moni_lvl"));
                node.put("mtor_enum", to.getString("moni_enum"));
                node.put("mtor_relate_cnd", to.getString("moni_relate_cnd"));
                if(type == ChangeType.ADD){
                    node.put("mtor_type", ChangeType.ADD.getValue());
                    node.put("mtor_beg", getDate(beginDate,Constant.YYYY_MM_DD_HH_MM_SS).getTime());
                    node.put("mtor_end", getDate(endDate,Constant.YYYY_MM_DD_HH_MM_SS).getTime());
                }else{
                    node.put("mtor_type", ChangeType.UPDATE.getValue());
                    node.put("mtor_relate_id", to.getString(ID));
                    node.put("mtor_beg", to.getLong("moni_beg"));
                    node.put("mtor_end", to.getLong("moni_end"));
                }
            }
            
            // 资源集 
            JSONArray res = to.getJSONArray("moni_res_set");
            if(res != null && !res.isEmpty()){
                JSONArray mtorRes = new JSONArray();
                for(int k = 0; k < res.size(); k++){
                    JSONObject rr = res.getJSONObject(k);
                    JSONObject obj = new JSONObject();
                    obj.put("mtor_res_id", rr.getString("moni_res_id"));
                    obj.put("text", rr.getString("text"));
                    mtorRes.add(obj);
                }
                
                node.put("mtor_res_set", mtorRes);
            }
            no.add(node);
        }
        return no;
    }

    private  String getNowDateStr(String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String formatDateStr= sdf.format(new Date());
        return formatDateStr;
    } 

    /***
     * 查询节点详情
     * @param key 临时单ID
     * @param lvlCode 节点层及编码
     * @return 节点信息
     * @author fangkun 2017-10-21
     */
    @Override
    public NodeTo nodeDetail(String key,String lvlCode) {
        NodeTo node=hasOps.get(key, lvlCode);
        if(node!=null){
            node.setBegin(node.getBegin().substring(0, 10));
            node.setEnd(node.getEnd().substring(0, 10));
        }else{
            ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(),
                    ErrorMessage._60003.getMsg());
            logger.info("MonitorServiceImpl类的nodeDetail方法：==》"+ErrorMessage._60003.getMsg());
        }
        return node;
    }
    /***
     * 保存节点详情
     * @param 节点详情
     * @return 节点的层级编码
     * @author fangkun
     */
    @Override
    public String saveNodeDetail(NodeTo nodeDetail) {
        String key=nodeDetail.getKey();
        String nodeNo=nodeDetail.getNodeNo();
        String lvlCode=nodeDetail.getLvlCode();
        String endDate=nodeDetail.getEnd()+ENDTIME;
        nodeDetail.setEnd(endDate);
        String beginDate=nodeDetail.getBegin()+STARTTIME;
        nodeDetail.setBegin(beginDate);
        if(StringUtil.isNullOrEmpty(nodeNo)){
            logger.info("不存在当前节点信息！");
            throw new ServiceException("不存在当前节点信息！");
        }
        //获取到原节点资源 set进入新的节点详情
        NodeTo oldNode=hasOps.get(key, lvlCode);
        List<ResourceTo> resourceList=oldNode.getResources();
        nodeDetail.setResources(resourceList);
        nodeDetail.setSeq(oldNode.getSeq());
        nodeDetail.setLvl(oldNode.getLvl());
        nodeDetail.setType(oldNode.getType());
        nodeDetail.setRelateId(oldNode.getRelateId());
        nodeDetail.setRelateCnd(nodeDetail.getRelateCnd());
        nodeDetail.setRelateCndText(nodeDetail.getRelateCndText());
        
        //操作redis修改
        hasOps.put(key, lvlCode, nodeDetail);
        
        //修改下级节点失效日期
        List<NodeTo> list=new ArrayList<NodeTo>();
        list.add(nodeDetail);
        list.addAll(getChildOneLvNode(key, lvlCode));
        //一级子节点的时间修改影响  ==>返回剔除删除节点后的一级节点列表
        List<NodeTo> listLv1=updateNodesByDate(beginDate,endDate,list,key);

        //查询新的一级节点的子节点
        List<NodeTo> listChildren=null;
        if(listLv1!=null && listLv1.size()>0){
            listChildren=new ArrayList<NodeTo>();
            NodeTo node=null;
            for(int i=0;i<listLv1.size();i++){
                node=listLv1.get(i);
                //查询子节点
                List<NodeTo> listRet=getChildNode(node.getKey(), node.getLvlCode());
                if(listRet!=null && listRet.size()>0){
                    listChildren.addAll(listRet);
                }
            }
        }
        //如果存在子节点  则全部更新
        if(listChildren!=null && listChildren.size()>0){
            updateNodesByDate(beginDate,endDate,listChildren,key);
        }
        return lvlCode;
    }

    private List<NodeTo> updateNodesByDate(String beginDate,String endDate,List<NodeTo> nodes,String key){
        //遍历子节点，判断父节点修改时间对子节点的影响
        String childBeginDate=null;
        String childEndDate=null;
        NodeTo node=null;
        for(int i=nodes.size()-1;i>=0;i--){
            node=nodes.get(i);
            childBeginDate=node.getBegin();
            childEndDate=node.getEnd();
            Date cts=getDate(childBeginDate);
            Date cte=getDate(childEndDate);
            Date ts=getDate(beginDate);
            Date te=getDate(endDate);
            //-->修改父节点的失效日期
            //2.修改的子节点失效日期小于等于子节点的生效日期==>子节点失效
            if(!cts.before(te)){
                deleteNode(key, node.getLvlCode(), 0);
                nodes.remove(i);
            }
            else if(te.before(cte)){//1.子节点失效日期大于父节点修改的失效日期  ==>子节点失效日期=父节点失效日期
                node.setEnd(endDate);
                hasOps.put(key, node.getLvlCode(), node);
            }
            //-->修改父节点的生效日期
            //2.如果父节点的生效日期大于等于子节点失效日期==>子节点失效
            if(!ts.before(cte)){
                deleteNode(key, node.getLvlCode(), 0);
                nodes.remove(i);
            }
            else if(cts.before(ts)){//1.父节点生效日期>子节点生效日期时==>子节点生效日期=父节点生效日期
                node.setBegin(beginDate);
                hasOps.put(key, node.getLvlCode(), node);
            }
        }
        return nodes;
    }

    /***
     * 删除节点资源 - 单个操作
     * @param key redis key
     * @param levelCode 节点层及编码
     * @param resourceId 资源ID
     * @return 被删除的节点ID
     */
    @Override
    public String deleteNodeResource(String key,String lvlCode,String resourceId) {
        NodeTo node=hasOps.get(key, lvlCode);
        if(node!=null){
            List<ResourceTo> resourceList=node.getResources();
            if(resourceList!=null && resourceList.size()>0){
                for(int i=0;i<resourceList.size();i++){
                    ResourceTo resource=resourceList.get(i);
                    if(StringUtil.isEqual(resourceId,resource.getResId())){
                        resourceList.remove(i);
                        break;
                    }
                }
                node.setResources(resourceList);
            }else{
                logger.info("deleteNodeResource方法==>层级编码为："+lvlCode+"的节点下不存在资源!!!");
                throw new ServiceException("deleteNodeResource方法==>层级编码为："+lvlCode+"的节点下不存在资源!!!");
            }
        }else{
            logger.info("deleteNodeResource方法==>未找到节点!!!");
            throw new ServiceException("deleteNodeResource方法==>未找到节点!!!");
        }
        
        // 清空公式设计器对资源的筛选条件
        node.setRelateCnd(null);
        node.setRelateCndText(null);
        
        hasOps.put(key, lvlCode, node);
        return resourceId;
    }

    /***
     * 添加节点资源 - 单点操作
     * @param key redis key
     * @param levelCode 节点层级编码
     * @param resourceId 资源ID
     * @param resourceText 资源名称
     * @return 资源ID
     * @author fangkun 2017-10-24
     */
    @Override
    public String addResource(String key,String lvlCode,String resourceId,String resourceText) {
        NodeTo node=hasOps.get(key, lvlCode);
        if(node!=null){
            List<ResourceTo> resourceList=node.getResources();
            resourceList=resourceList==null?new ArrayList<ResourceTo>():resourceList;
            //获取监管树类Id  如果是岗位树先清空资源表
            String[] classId=key.split("-");
            if(StringUtil.isEqual("6fa512bf66e211e7b2e4005056bc4879",classId[classId.length-1])){
            	resourceList=new ArrayList<ResourceTo>();
            	node.setNodeName(resourceText);
            }
            ResourceTo resource=new ResourceTo();
            resource.setResId(resourceId);
            resource.setText(resourceText);
            resourceList.add(resource);
            node.setResources(resourceList);
            
            // 清空公式设计器对资源的筛选条件
            node.setRelateCnd(null);
            node.setRelateCndText(null);
            
            hasOps.put(key, lvlCode, node);
        }else{
            logger.info("addResource方法==>未找到节点!!!");
            throw new ServiceException("addResource方法==>未找到节点!!!");
        }
        
        return resourceId;
    }

    /****
     * 添加节点
     * @param key redis key
     * @param levelCode 节点层级编码
     * @return 新增节点的层级编码
     * @author fangkun 2017-10-24
     */
	@Override
    public String addNode(String key,String lvlCode,int type) {
        //如果新增子节点==>获取下级所有子节点==》找到最大子节点==》生成子节点
        //如果新增左节点==>获取本级所有节点==》找到当前节点的左节点==>生成左节点
        //如果新增右节点==》获取本级节点==》找到当前节点的右节点==》生成右节点
    	String newLvlCode=null;
    	String beginDate="";
    	String endDate="";
    	//找出父级节点信息
    	String pLvlCode=(type==0?lvlCode:lvlCode.substring(
    			0,lvlCode.substring(0, lvlCode.length()-1).lastIndexOf(",")+1));
    	NodeTo pNode=hasOps.get(key, pLvlCode);
    	beginDate=pNode.getBegin();
    	endDate=pNode.getEnd();
    	if(type==0){//新增子节点
    		newLvlCode=addChildNode(key, pLvlCode);
    	}else {//新增左节点
    		newLvlCode=addLRNode(type,key, lvlCode, pLvlCode);
    	}
    	
        createNewNode(key,newLvlCode.split(Constant.LVSPLIT).length,newLvlCode,beginDate,endDate);
        return newLvlCode;
    }
    private String addChildNode(String key,String pLvlCode){
    	DecimalFormat df=new DecimalFormat("##0.##");
    	//新的节点层级编码
        String newLvlCode="";
        //获取到父节点下面的一级节点
    	List<NodeTo> listNodes=getChildOneLvNode(key, pLvlCode);
        //取出排序后最大的子节点
        if(listNodes!=null && listNodes.size()>0){
            NodeTo maxNode=listNodes.get(listNodes.size()-1);
            double seq=maxNode.getSeq();
            newLvlCode=pLvlCode+df.format(seq+1)+Constant.LVSPLIT;
        }else{//不存在子节点 则直接在当前层级编码后面加,1
            newLvlCode=pLvlCode+"1"+Constant.LVSPLIT;
        }
        return newLvlCode;
    }
    private String addLRNode(int type,String key,String lvlCode,String pLvlCode){
    	//新增节点的层级编码
    	String newLvlCode="";
    	//获取到父节点下面的一级节点
    	List<NodeTo> listNodes=getChildOneLvNode(key, pLvlCode);
    	//本节点的层级编码后缀
    	NodeTo curNode=hasOps.get(key, lvlCode);
        double curNodeEnd=curNode.getSeq();
        /**找到当前节点左节点*/
        int index=0;
        //找出当前节点的索引
        NodeTo node=null;
        for(int i=0;i<listNodes.size();i++){
            node=listNodes.get(i);
            if(StringUtil.isEqual(lvlCode, node.getLvlCode())){
                index=i;
            }
        }
        //生成0-1之间的随机双精小数
        Double ranNum=0.00;
        DecimalFormat df=new DecimalFormat("##0.##");
        if(type==1){//创建左节点
	        //如果index为0 ，代表当前节点为左边节点
	        if(index==0){
	        	//取0 到当前节点后缀之间的数
	        	ranNum=(0+curNodeEnd)/2.00;
	        	//取0到当前节点编码之间的随机数 并且与层级编码前缀组合成新的层级编码
	            newLvlCode=pLvlCode+df.format(ranNum)+",";
	        }else{
	            //取出当前节点的左节点
	            node=listNodes.get(index-1);
	            //取出当前节点的层级编码后缀
	            double lvlCodeLeftEnd=node.getSeq();
	            //取左节点后缀 到当前节点后缀之间的数
	        	ranNum=(lvlCodeLeftEnd+curNodeEnd)/2.00;
	            newLvlCode=pLvlCode
	            +df.format(ranNum)
	            +Constant.LVSPLIT;
	        }
        }else{//常见右节点
        	//如果index为最大的 ，代表当前节点没有右边
            if(index==listNodes.size()-1){
            	//如果没有右节点 则在当前结点后缀加1
                newLvlCode=pLvlCode+df.format(curNodeEnd+1)+Constant.LVSPLIT;
            }else{
                //取出当前节点的右节点
                node=listNodes.get(index+1);
                ranNum=(node.getSeq()+curNodeEnd)/2.00;
                newLvlCode=pLvlCode
                +df.format(ranNum)
                +Constant.LVSPLIT;
            }
        }
        return newLvlCode;
    }
    private void createNewNode(String key,int level,String newLvlCode,
    		String beginDate,String endDate){
    	String[] arr=newLvlCode.split(Constant.LVSPLIT);
    	//生成新节点
        NodeTo newNode=new NodeTo();
        newNode.setNodeNo("NODE"+System.currentTimeMillis());
        newNode.setKey(key);
        newNode.setLvl(level);
        newNode.setLvlCode(newLvlCode);
        newNode.setNodeName(Constant.INITNODENAME);
        newNode.setType(ChangeType.ADD.getValue());
        newNode.setSeq(Double.parseDouble(arr[arr.length-1]));
        //判断时间 如果父级节点的生效日期小于当前日期  则设置为当天 否则跟父节点的生效日期一直
        if(getDate(beginDate).before(new Date())){
            SimpleDateFormat format=new SimpleDateFormat(Constant.YYYY_MM_DD);
            String nowDate=format.format(new Date())+" 00:00:00";
            newNode.setBegin(nowDate);
        }else{
        	newNode.setBegin(beginDate);
        }
        newNode.setEnd(endDate);
        hasOps.put(key, newLvlCode, newNode);
    }
    /****
     * 删除节点
     * @param key redis key
     * @param levelCode 节点层级编码
     * @param type 删除类型 0 删除 1失效
     * @return levelCode 节点层级编码
     * @author fangkun 2017-10-24
     */
    @Override
    public String deleteNode(String key,String levelCode,int type) {
        if(type==0){
            NodeTo node=hasOps.get(key, levelCode);
            if(node!=null){
	            node.setType(ChangeType.INVALID.getValue());
	            //删除原失效节点
	            hasOps.delete(key, levelCode);
	            //新增修改后的失效节点
	            hasOps.put(key, "D"+node.getLvlCode(), node);
            }else{
            	logger.info("该节点不存在！");
            	throw new ServiceException("该节点不存在！");
            }
        }else{
            //删除原失效节点
            hasOps.delete(key, levelCode);
        }
        //查询出子节点
        List<NodeTo> nodes=getChildNode(key,levelCode);//查询子节点
        if(nodes!=null && nodes.size()>0){//存在子节点
            delNodes(key, nodes);
        }
        return levelCode;
    }
    /***
     * 节点失效与删除
     * 1.修改的节点 将节点的状态置为3 失效，并且层及编码前加上D==>原修改的节点数据删除==》新增修改后的节点数据
     * 2.新增节点 直接删除
     * @param tempId 临时单ID
     * @param nodes 节点集合
     * @return
     */
    private void delNodes(String tempId,List<NodeTo> nodes) {
        NodeTo node=null;
        List<String> addList=new ArrayList<String>();//新增的节点需要删除的集合
        Map<String, NodeTo> updateNodes=new HashMap<String, NodeTo>();//失效节点集合
        List<String> deleteList=new ArrayList<String>();//失效节点先删除集合
        for(int i=0;i<nodes.size();i++){
            node=nodes.get(i);
            //将节点下面的资源清空
            node.setResources(null);
            if(node.getType()==ChangeType.UPDATE.getValue()){
                deleteList.add(node.getLvlCode());
                node.setType(ChangeType.INVALID.getValue());
                updateNodes.put("D"+node.getLvlCode(), node);
            }else{
                addList.add(node.getLvlCode());
            }

        }
        if(deleteList!=null && deleteList.size()>0){
        	for(int i=0;i<deleteList.size();i++){
        		hasOps.delete(tempId, deleteList.get(i));//删除失效节点
        	}
            hasOps.putAll(tempId, updateNodes);//新增标志失效状态过后的失效节点
        }
        if(addList!=null && addList.size()>0){
        	for(int j=0;j<addList.size();j++){
        		hasOps.delete(tempId, addList.get(j));//删除新增节点
        	}
        }
    }
    /***
     * 移动节点
     * @param key redis key
     * @param moveLvlcode 移动节点的层及编码
     * @param desLvlcode 目的节点的层及编码
     * @param type 0：创建子节点 1：创建左节点 2：创建右节点
     * @return
     */
    @Override
    public String moveNode(String key,String moveLvlcode,String desLvlcode,int type) {
    	//取出目标节点  移动节点 以及移动节点的子节点
    	String newLvlCode="";
    	//找出父级节点信息
    	String pLvlCode=(type==0?desLvlcode:desLvlcode.substring(
    			0,desLvlcode.substring(0, desLvlcode.length()-1).lastIndexOf(",")+1));
    	//获取父节点信息
    	NodeTo nodeP=hasOps.get(key, pLvlCode);
    	//获取移动节点信息
    	NodeTo moveNode=hasOps.get(key, moveLvlcode);
    	//如果移动节点的时间段在目标节点的时间段内
    	if(!getDate(moveNode.getBegin()).before(getDate(nodeP.getBegin()))
    		&& !getDate(nodeP.getEnd()).before(getDate(nodeP.getEnd()))
    		){
    		//生成新节点
        	newLvlCode=addNode(key, desLvlcode, type);
        	NodeTo newNode=hasOps.get(key, newLvlCode);
        	
        	//新老节点层级差值
        	int lvlGap=newNode.getLvl()-moveNode.getLvl();
        	//变更移动节点的层级 层级编码 排序字段参数
        	Map<String, NodeTo> map=new HashMap<String, NodeTo>();
        	moveNode.setLvl(newNode.getLvl());
        	moveNode.setLvlCode(newLvlCode);
        	moveNode.setSeq(newNode.getSeq());
        	map.put(newLvlCode, moveNode);
        	
        	//删除老的移动节点
        	hasOps.delete(key,moveLvlcode);
        	
        	//获取移动节点的所有子节点
        	List<NodeTo> listChildren=getChildNode(key, moveLvlcode);
        	if(listChildren!=null && listChildren.size()>0){
        		//更新移动节点子节点的节点层级和节点编码
                for(int i=0;i<listChildren.size();i++){
                	NodeTo node=listChildren.get(i);
                	//删除老的节点
                	hasOps.delete(key, node.getLvlCode());
                	//生成新的节点
                	node.setLvl(node.getLvl()+lvlGap);
                	node.setLvlCode(node.getLvlCode().replaceFirst(moveLvlcode, newLvlCode));
                	map.put(node.getLvlCode(), node);
                }
        	}
        	//统一插入新的节点
        	hasOps.putAll(key, map);
    	}else{//如果移动节点的时间段不在目标节点的时间段内, 则不允许拖动
    		logger.info(ErrorMessage._60018.getMsg());
    		ApplicationException.throwCodeMesg(ErrorMessage._60018.getCode(), 
    				ErrorMessage._60018.getMsg());
    	}
    	
        return newLvlCode; 
    }
    
    //根据层级编码查询所有子节点
    public List<NodeTo> getChildNode(String key,String levelCode){
        List<NodeTo> list=new ArrayList<NodeTo>();
        Map<String, NodeTo> nodeMap=hasOps.entries(key);
        for(Map.Entry<String, NodeTo> entry :nodeMap.entrySet()){
            String field=entry.getKey();
            if(!StringUtil.isNullOrEmpty(field) && field.startsWith(levelCode)){
                list.add(nodeMap.get(field));
            }
        }
        return list;
    }
    //根据层级编码查询下面第一级子节点
    public List<NodeTo> getChildOneLvNode(String key,String levelCode){
        List<NodeTo> list=new ArrayList<NodeTo>();
        Map<String, NodeTo> nodeMap=hasOps.entries(key);
        for(Map.Entry<String, NodeTo> entry :nodeMap.entrySet()){
            String field=entry.getKey();
            if(!StringUtil.isNullOrEmpty(field) 
            	&& field.startsWith(levelCode)){
            	//取下一级节点:下级节点层级编码的逗号个数减去当前节点层级编码的逗号个数
                if(field.split(Constant.LVSPLIT).length-levelCode.split(Constant.LVSPLIT).length==1){
                    list.add(nodeMap.get(field));
                }
            }
        }
        Collections.sort(list, new Comparator<NodeTo>() {//对节点按照层及编码升序排序
            @Override
            public int compare(NodeTo o1, NodeTo o2) {
            	double d=o1.getSeq()-o2.getSeq();
				int ret=0;
				if(d>0){
					ret=1;
				}else if(d<0){
					ret=-1;
				}
				return ret;
            }
        });
        return list;
    }
    private  Date getDate(String str){
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误"+ str);
        }
    }
    
    public String getStaffText(String id) throws Exception{
        
        if(StringUtil.isNullOrEmpty(id))
            return null;
        
        EmployeeEntity staffInfo = ormService.load(EmployeeEntity.class, id);
        
        if(staffInfo == null)
           return null;
        
        JSONObject staff = JSON.parseObject(JSON.toJSONString(staffInfo));
        
        Result formatResult = edmClient.getCharacterAndFormat(Constant.STAFFCLASSID);
        
        if(formatResult.getRetCode() == Result.RECODE_SUCCESS){
            
            if(formatResult.getData() != null){
                
                JSONArray character = JSONObject.parseObject(JSONObject.toJSONString(formatResult.getData())).getJSONArray("character");
                String format = JSONObject.parseObject(JSONObject.toJSONString(formatResult.getData())).getString("format");
                
                if(character == null || format == null || character.isEmpty())
                    ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"资源无法呈现！");
                
                String[] resourceFields = new String[character.size()];
                character.toArray(resourceFields);
                
                String txt = format.toLowerCase();
                for (String fieldName : resourceFields){
                    String f_str = StringUtil.isNullOrEmpty(staff.getString(fieldName))?""
                            : staff.getString(fieldName);
                    txt = txt.replace(fieldName,f_str);
                }
                return txt;
                
            }else
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(),"员工类资源无法呈现");
        }else
            throw new ServiceException(formatResult.getErrMsg());
        return null;
    }

    @Override
    public List<ResourceTo> formula(NodeTo node) throws Exception{
        
       String key = node.getKey();
       String lvlCode = node.getLvlCode();
       List<ResourceTo> f_resources = node.getResources();
       
       NodeTo o_node = hasOps.get(key, lvlCode);
       
       if(o_node == null || StringUtil.isNullOrEmpty(key) || StringUtil.isNullOrEmpty(lvlCode)
               || f_resources == null || f_resources.isEmpty())
           ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), ErrorMessage._60004.getMsg());
       
       List<ResourceTo> o_resources = o_node.getResources();
       
       // 先将节点的已选资源清空
       if(o_resources != null && !o_resources.isEmpty()){
           o_node.setResources(null);
           hasOps.put(key, lvlCode, o_node);
       }
       
       // 获取当前节点未使用的资源
       JSONObject unusedRes = orderTree.queryNotUsingResource(key, lvlCode, 1, Integer.MAX_VALUE);
       
       JSONArray datas = unusedRes.getJSONArray("data");
       
       if(datas == null || datas.isEmpty()){
           o_node.setResources(o_resources);
           hasOps.put(key, lvlCode, o_node);
           ApplicationException.throwCodeMesg(ErrorMessage._60020.getCode(), ErrorMessage._60020.getMsg());
       }
       
       List<ResourceTo> u_resources = JSONArray.parseArray(JSONArray.toJSONString(datas), ResourceTo.class);
       
       // 取出新 和 未使用资源的集合的差集
       List<ResourceTo> n_resources = new ArrayList<ResourceTo>();
       
       for(ResourceTo re : f_resources){
           String resId = re.getResId();
           for(ResourceTo rs : u_resources){
               if(rs.getResId().equals(resId)){
                   n_resources.add(rs);
                   break;
               }
           }
       }
       
       // 没有满足公式的资源 - 或者 是岗位树 但是资源不是一个
       if(n_resources.isEmpty() || 
               (StringUtil.isEqual(Constant.JOBPOSITIONCLASSID, key.split(Constant.KEY_SEP)[1]) && n_resources.size() != 1)){
           o_node.setResources(o_resources);
           hasOps.put(key, lvlCode, o_node);
           ApplicationException.throwCodeMesg(ErrorMessage._60022.getCode(), ErrorMessage._60022.getMsg());
       }else{
           o_node.setResources(n_resources);
           o_node.setRelateCnd(node.getRelateCnd());
           o_node.setRelateCndText(node.getRelateCndText());
           
           if(StringUtil.isEqual(Constant.JOBPOSITIONCLASSID, key.split(Constant.KEY_SEP)[1])){
               o_node.setNodeName(n_resources.get(0).getText());
           }
           hasOps.put(key, lvlCode, o_node);
       }
        return n_resources;
    }
}
