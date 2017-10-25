package com.huntkey.rx.sceo.monitor.provider.service.impl;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.ID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.MONITORTREEORDER;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.NULL;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.PID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.STARTTIME;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.YYYY_MM_DD;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ChangeType;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
import com.huntkey.rx.sceo.serviceCenter.common.model.MergeParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortNode;
@Service
public class MonitorServiceImpl implements MonitorService {
    
    private static final String ADDUSER = "admin";
    
    private static final String ROOT_LVL_CODE = "1,";
    private static final String REVOKE_KEY = "REVOKE";
    private static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    
    private static final String MTOR_NODES_EDM = "monitortreeorder.mtor_node_set";
    private static final String KEY_SEP = "-";
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private MonitorTreeService treeService;
    
    @Autowired
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
	 * 
	 */
	public JSONObject checkOrder(String classId, String rootId, int type){
	    
	    JSONObject obj = new JSONObject();
	    
	    if(StringUtil.isNullOrEmpty(rootId))
            rootId = NULL;
	    
	    SearchParam params = new SearchParam(MONITORTREEORDER);
	    params.addCond_equals("mtor_cls_id", classId);
	    params.addCond_equals("mtor_order_type", String.valueOf(type));
	    params.addCond_equals("mtor_order_root", rootId);
	    params.addColumns(new String[]{ID});
	    
	    Result result = client.queryServiceCenter(params.toJSONString());
	    
	    JSONObject order = null;
	    if(result.getRetCode() == Result.RECODE_SUCCESS){
	        if(result.getData() != null){
                JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(result.getData()))
                        .getJSONArray(Constant.DATASET);
                if(arry != null && arry.size() == 1)
                    order = arry.getJSONObject(0);
	        }
	    }else
	        new ServiceException(result.getErrMsg());
	    
	    // 单据中不存在当前树的临时单
	    if(order == null)
	        return obj;
	    
	    // 存在临时单 - 判断临时单是否失效
	    params = null;
	    params = new SearchParam(MTOR_NODES_EDM);
	    params.addCond_lessOrEquals("mtor_end", new SimpleDateFormat(YYYY_MM_DD).format(new Date()))
	    .addCond_equals("mtor_lvl", "1")
	    .addCond_equals("mtor_lvl_code", ROOT_LVL_CODE)
	    .addCond_equals(PID, order.getString(ID));

	    Result rootNode = client.queryServiceCenter(params.toJSONString());
	    
	    if(rootNode.getRetCode() == Result.RECODE_SUCCESS){
	           if(rootNode.getData() != null){
	                JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(rootNode.getData()))
	                        .getJSONArray(Constant.DATASET);
	                if(arry != null && !arry.isEmpty()){
	                    // 临时单已失效 - 需要将临时单  和 节点信息全部清除
	                    params.clearConditions();
	                    params.addCond_equals(PID, order.getString(ID));
	                    params.addColumns(new String[]{ID});
	                    Result nodes = client.queryServiceCenter(params.toJSONString());
	                    
	                    if(nodes.getRetCode() == Result.RECODE_SUCCESS){
	                        if(nodes.getData() != null){
	                            JSONArray nodeIds = JSONObject.parseObject(JSONObject.toJSONString(nodes.getData()))
	                                    .getJSONArray(Constant.DATASET);
	                            if(nodeIds != null && !nodeIds.isEmpty()){
	                                MergeParam delNode = new MergeParam(MTOR_NODES_EDM);
	                                delNode.addAllData(nodeIds);
	                                client.delete(delNode.toJSONString());
	                                
	                                MergeParam delOrder = new MergeParam(MONITORTREEORDER);
	                                delOrder.addData(order);
	                                client.delete(delOrder.toJSONString());
	                                hasOps.getOperations().delete(order.getString(ID)+ KEY_SEP +classId);
	                                hasOps.getOperations().delete(order.getString(ID)+ KEY_SEP +classId + REVOKE_KEY);
	                                return obj;
	                            }
	                        }else
                                ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
	                    }else
	                        new ServiceException(nodes.getErrMsg());
	                }
	            }
	    }else
	        new ServiceException(rootNode.getErrMsg());
	    
	    String key = order.getString(ID)+KEY_SEP+classId;
	    String revokeKey = key + REVOKE_KEY;
	    
	    NodeTo root = hasOps.get(key, ROOT_LVL_CODE);

	    // redis中数据 过期 || redis中没有 数据
	    if(root == null || !getDate(root.getEnd(),DATE_TIME).after(new Date())){
	        hasOps.getOperations().delete(key);
	        hasOps.getOperations().delete(revokeKey);
	        // 临时单中数据同步到redis中
	        List<NodeTo> list = tempTree(key, "", 1, true);
	        Map<String,NodeTo> map = new HashMap<String,NodeTo>();
	        list.stream().forEach(s->{
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
	
	/**
	 * flag
	 *    true 继续上一步操作 - 直接返回key
	 *    false - 不继续上一步操作 
	 */
	@Override
    public String editBefore(String key, boolean flag){
	    
	    if(!hasOps.getOperations().hasKey(key))
	        ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
	    
	    if(flag)
	        return key;
	    
	    hasOps.getOperations().delete(key);
	    hasOps.getOperations().delete(key+REVOKE_KEY);
	    
        List<NodeTo> list = tempTree(key, "", 1, true);
        Map<String,NodeTo> map = new HashMap<String,NodeTo>();
        list.stream().forEach(s->{
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        
        return key;
    }
    
    @Override
    public List<NodeTo> tempTree(String tempId, String validDate,int type,boolean flag) {
        
        List<NodeTo> list = new ArrayList<NodeTo>();
        
        // 传入进来的可能是 redis的key
        tempId = tempId.split(KEY_SEP)[0];
        
        // 查询出classId
        SearchParam params = new SearchParam(MONITORTREEORDER);
        params.addCond_equals(ID, tempId);
        
        Result orderRes = client.queryServiceCenter(params.toJSONString());
        
        String classId = "";
        
        if(orderRes.getRetCode() == Result.RECODE_SUCCESS){
            if (orderRes.getData() != null) {
                JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(orderRes.getData()))
                        .getJSONArray(Constant.DATASET);
                if(arry != null && arry.size() == 1)
                    classId = arry.getJSONObject(0).getString(ID);
            }
        }else
            new ServiceException(orderRes.getErrMsg());
        
        if(StringUtil.isNullOrEmpty(classId))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        
        switch(type){
            
            case 1:
                SearchParam reParams = new SearchParam("monitortreeorder.mtor_node_set");
                reParams.addCond_equals(PID, tempId);
                reParams.addCond_less("mtor_type", ChangeType.INVALID.toString());
                if(StringUtil.isNullOrEmpty(validDate)){
                    validDate=getNowDateStr(YYYY_MM_DD);
                    reParams.addCond_greater("mtor_end", validDate);
                }else{
                    validDate+=" 00:00:00";
                    reParams.addCond_lessOrEquals("mtor_beg", validDate);
                    reParams.addCond_greater("mtor_end", validDate);
                }
                
                Result nodesRes = client.queryServiceCenter(params.toJSONString());
                
                JSONArray nodes = null;
                if(nodesRes.getRetCode() == Result.RECODE_SUCCESS){
                    if (nodesRes.getData() != null) {
                        nodes = JSONObject.parseObject(JSONObject.toJSONString(nodesRes.getData()))
                                .getJSONArray(Constant.DATASET);
                    }
                }else
                    new ServiceException(nodesRes.getErrMsg());
                
                if(nodes == null || nodes.isEmpty())
                    return list;
                
                //不包含资源
                if(!flag)
                    return NodeTo.setValue(nodes);
                
                // 查询出所有的资源 和 资源id
                List<String> ids = new ArrayList<String>();
                
                for(int i = 0; i < nodes.size(); i++){
                    JSONObject to = nodes.getJSONObject(i);
                    ids.add(to.getString(ID));
                }
                // 1 - 代表从正式监管类去资源信息  2 - 代表从临时单据中查询资源信息
                JSONArray resources = treeService.getNodeResources(null, ids, classId, MTOR_NODES_EDM, 2);
                
                for(int i = 0; i < nodes.size(); i++){
                    JSONObject to = nodes.getJSONObject(i);
                    if(resources != null && !resources.isEmpty()){
                        JSONArray node_res = new JSONArray();
                        
                        for(int k = 0; k < resources.size(); k++){
                            if(!to.getString(ID).equals(resources.getJSONObject(k).getString("nodeId")))
                                continue;
                            JSONObject re = new JSONObject();
                            re.put("mtor_res_id", resources.getJSONObject(k).getString(ID));
                            re.put("text", resources.getJSONObject(k).getString("text"));
                            re.put(PID, to.getString(ID));
                            node_res.add(re);
                        }
                        
                        if(!node_res.isEmpty())
                            to.put("mtor_res_set", node_res);
                    }
                    
                    // 查询出备用属性集
                    SearchParam seParams = new SearchParam(MTOR_NODES_EDM+".mtor_bk_set");
                    seParams.addCond_equals(PID, to.getString(ID));
                    Result mtor_bk_set = client.queryServiceCenter(seParams.toJSONString());
                    
                    if(mtor_bk_set.getRetCode() == Result.RECODE_SUCCESS){
                        if(mtor_bk_set.getData() != null){
                            JSONArray bk_set = JSONObject.parseObject(JSONObject.toJSONString(mtor_bk_set.getData()))
                                    .getJSONArray(Constant.DATASET);
                            
                            if(bk_set != null && !bk_set.isEmpty())
                                to.put("mtor_bk_set", bk_set);
                        }
                    }else
                        new ServiceException(mtor_bk_set.getErrMsg());
                }
                
                list = NodeTo.setValue(nodes);
                break;
                
            case 2:
                String key = tempId + KEY_SEP + classId;
                if(!hasOps.getOperations().hasKey(key))
                    ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
                Set<String> fields = hasOps.keys(key);
                list = new ArrayList<NodeTo>();
                
                for(String field : fields){
                    NodeTo to= hasOps.get(key, field);
                    Date begin = getDate(to.getBegin(),DATE_TIME);
                    Date end = getDate(to.getEnd(),DATE_TIME);
                    if(StringUtil.isNullOrEmpty(validDate)){
                        Date now = getDate(new SimpleDateFormat(YYYY_MM_DD).format(new Date()) + STARTTIME, DATE_TIME);
                        if(end.after(now))
                            list.add(to);
                    }else{
                        validDate+=" 00:00:00";
                        Date now = getDate(validDate, DATE_TIME);
                        if(!begin.after(now) && end.after(now))
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
     */
    @Override
    public String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo) {
        
        int type=addMonitorTreeTo.getType(); 
        
        String beginDate=addMonitorTreeTo.getBeginDate(); 
        String endDate=addMonitorTreeTo.getEndDate(); 
        String classId=addMonitorTreeTo.getClassId();
        String rootId=StringUtil.isNullOrEmpty(addMonitorTreeTo.getRootId()) ? NULL : addMonitorTreeTo.getRootId();
        String rootEdmcNameEn = addMonitorTreeTo.getRootEdmcNameEn();
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
        String key = tempId + KEY_SEP + classId;
        String revokedKey = key + REVOKE_KEY;
        
        List<NodeTo> list = NodeTo.setValue(nodes);
        
        hasOps.getOperations().delete(key);
        hasOps.getOperations().delete(revokedKey);
        
        Map<String, NodeTo> map = new HashMap<String,NodeTo>();
        
        list.stream().forEach(s->{
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // TODO 去除多多余的字段
        for(int i = 0; i < nodes.size(); i++){
            JSONObject node = nodes.getJSONObject(i);
            JSONArray res = node.getJSONArray("mtor_res_set");
            if(res == null || res.isEmpty())
                continue;
            for(int k = 0; k < res.size(); k++){
                res.getJSONObject(k).remove("text");
            }
        }
        
        // 将复制的节点插入到表中
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                MergeParam params = new MergeParam(MTOR_NODES_EDM);
                
                params.addAllData(nodes);
                Result result = client.add(params.toJSONString());
                
                if(result.getRetCode() != Result.RECODE_SUCCESS)
                    new ServiceException(result.getErrMsg());
            }
        });
        
        return key;
    }
    
    @Override
    public String treeMaintaince(String classId, String rootId, String rootEdmcNameEn) {
        
        SearchParam params = new SearchParam(MONITORTREEORDER);
        params.addCond_equals("mtor_order_root", rootId);
        params.addCond_equals("mtor_order_type", "2");
        params.addCond_equals("mtor_cls_id", classId);
        
        Result result = client.queryServiceCenter(params.toJSONString());
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() != null){
                JSONArray arry = JSONObject.parseObject(JSONObject.toJSONString(result.getData()))
                        .getJSONArray(Constant.DATASET);
                if(arry != null && arry.size() == 1)
                    return arry.getJSONObject(0).getString(ID)+KEY_SEP+classId;
            }
        }else
            new ServiceException(result.getErrMsg());
        
        String tempId=createTemp(classId,ChangeType.UPDATE.getValue(),rootId);
        
        JSONArray nodes = copyTree(rootEdmcNameEn,classId, rootId,tempId,ChangeType.UPDATE.getValue(),null,null);
        
        // 将nodes数据放入到redis中
        String key = tempId + "-" + classId;
        String revokedKey = key + REVOKE_KEY;
        
        List<NodeTo> list = NodeTo.setValue(nodes);
        
        hasOps.getOperations().delete(key);
        hasOps.getOperations().delete(revokedKey);
        
        Map<String, NodeTo> map = new HashMap<String,NodeTo>();
        
        list.stream().forEach(s->{
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // TODO 去除多多余的字段
        for(int i = 0; i < nodes.size(); i++){
            JSONObject node = nodes.getJSONObject(i);
            JSONArray res = node.getJSONArray("mtor_res_set");
            if(res == null || res.isEmpty())
                continue;
            for(int k = 0; k < res.size(); k++){
                res.getJSONObject(k).remove("text");
            }
        }
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                MergeParam addParams = new MergeParam(MTOR_NODES_EDM);
                addParams.addAllData(nodes);
                
                Result ret = client.add(params.toJSONString());
                
                if(ret.getRetCode() != Result.RECODE_SUCCESS)
                    new ServiceException(ret.getErrMsg());
            }
        });
        
        return key;
    }
    
    /**
     * 创建临时单
     * @param classId 监管类ID
     * @param changeType 树的变更类型
     * @param rootId 根节点ID
     * @return 临时单ID
     */
    private String createTemp(String classId,int changeType,String rootId){
        
        JSONObject temp = new JSONObject();
        
        temp.put("orde_order_num","LS"+System.currentTimeMillis());
        temp.put("mtor_order_type", changeType);
        temp.put("mtor_cls_id", classId);
        temp.put("mtor_order_root", rootId);
        temp.put("adduser", "admin");
        
        MergeParam params = new MergeParam(MONITORTREEORDER);
        params.addData(temp);
        
        Result result = client.add(params.toJSONString());
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() != null)
                return JSONObject.toJSONString(result.getData());
        }else
            new ServiceException(result.getErrMsg());
        
        return null;
    }
	
    /**
     * 新增根节点
     * @param tempId 临时单ID
     * @param beginDate 生效日期  
     * @param endDate 失效日期
     * @return
     */
    private JSONObject createRootNode(String tempId,String beginDate,String endDate) {
        
        JSONObject node = new JSONObject();
        
        node.put(PID, tempId);
        node.put("mtor_node_no", "NODE"+System.currentTimeMillis());
        node.put("mtor_node_name", "未命名节点");
        node.put("mtor_node_def", "未命名节点");
        node.put("mtor_major", NULL);
        node.put("mtor_assit", NULL);
        node.put("mtor_beg", beginDate);
        node.put("mtor_end", endDate);
        node.put("mtor_index_conf", NULL);
        node.put("mtor_seq", 1);
        node.put("mtor_lvl_code", ROOT_LVL_CODE);
        node.put("mtor_lvl", 1);
        node.put("mtor_enum", NULL);
        node.put("mtor_relate_cnd", NULL);
        node.put("mtor_type", ChangeType.ADD.getValue());
        node.put("mtor_relate_id", NULL);
        node.put("adduser", ADDUSER);
        
        return node;
    }
    
    /**
     * 复制监管树
     * @param rootEdmcNameEn edm类英文名  即监管树实体对象表
     * @param rootId 根节点ID
     * @param tempId 临时单ID
     * @param changeType 变更类型 1 - 复制(历史树、未来树  和 正在生效树)  、2 - 维护(正在生效树和未来树)
     */
    private JSONArray copyTree(String rootEdmcNameEn,String classId, String rootId,String tempId,int changeType,String beginDate,String endDate) {
        // 查询根节点信息
        SearchParam params = new SearchParam(rootEdmcNameEn);
        params.addCond_equals(Constant.ID, rootId);
        
        Result rootResult = client.queryServiceCenter(params.toJSONString());
        
        JSONObject rootNode = null;
        
        if(rootResult.getRetCode() == Result.RECODE_SUCCESS){
            if(rootResult.getData() != null){
                JSONArray nodes = JSONObject.parseObject(JSONObject.toJSONString(rootResult.getData()))
                        .getJSONArray(Constant.DATASET);
                if(nodes != null && nodes.size() == 1)
                    rootNode = nodes.getJSONObject(0);
            }
        }else
            new ServiceException(rootResult.getErrMsg());
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        Date rootBegin = getDate(rootNode.getString("moni_beg"),DATE_TIME);
        Date rootEnd = getDate(rootNode.getString("moni_end"),DATE_TIME);
        
        if(!rootBegin.before(rootEnd))
            ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), ErrorMessage._60009.getMsg());
        
        // 查询出需要复制的节点
        params.clearConditions();
        
        ChangeType type = ChangeType.valueOf(changeType);
        
        Date now = getDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date())+STARTTIME,DATE_TIME);
        
        switch (type) {
            
            case ADD:
                // 正在生效的树复制                      moni_beg  <= now < moni_end
                if(!rootBegin.after(new Date()) && rootEnd.after(new Date())){
                    params.addCond_lessOrEquals("moni_beg", new SimpleDateFormat("yyyy-MM-dd").format(new Date())+STARTTIME);
                    params.addCond_greater("moni_end", new SimpleDateFormat("yyyy-MM-dd").format(new Date())+STARTTIME);
                }else{
                    // 历史树和 未来树复制  需要  moni_end = 根节点失效时间
                    params.addCond_equals("moni_end", rootNode.getString("moni_end"));
                }
                break;
                
            case UPDATE:
                if(!rootEnd.after(now))
                    ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), "历史树不能维护");
                // 维护 时 复制 需要 moni_end > 当前时间
                params.addCond_greater("moni_end", new SimpleDateFormat("yyyy-MM-dd").format(new Date())+STARTTIME);
                break;

            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), "type类型[" + changeType +"]" +ErrorMessage._60004.getMsg());
        }
        
        params.addCond_greaterOrEquals("moni_beg", rootNode.getString("moni_beg"));
        params.addCond_lessOrEquals("moni_end", rootNode.getString("moni_end"));
        params.addCond_like("moni_lvl_code", ROOT_LVL_CODE);
        params.addSortParam(new SortNode("moni_lvl", SortType.ASC));
        params.addSortParam(new SortNode("moni_lvl_code", SortType.ASC));
        
        Result result = client.queryServiceCenter(params.toJSONString());
        
        JSONArray nodes = null;
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() != null){
                nodes = JSONObject.parseObject(JSONObject.toJSONString(result.getData()))
                        .getJSONArray(Constant.DATASET);
            }
        }else
            new ServiceException(result.getErrMsg());
        
        if(nodes == null || nodes.isEmpty())
            return nodes;
        
        // 查询出所有的资源信息
        List<String> ids = new ArrayList<String>();
        
        for(int i = 0; i < nodes.size(); i++){
            JSONObject to = nodes.getJSONObject(i);
            ids.add(to.getString(ID));
        }
        
        JSONArray resources = treeService.getNodeResources(null, ids, classId, rootEdmcNameEn, 1);
        
        if (resources == null || resources.isEmpty()) 
            return nodes;
        
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
            node.put("adduser", ADDUSER);
            if(type == ChangeType.ADD){
                node.put("mtor_type", ChangeType.ADD.getValue());
                node.put("mtor_beg", beginDate);
                node.put("mtor_end", endDate);
            }else{
                node.put("mtor_type", ChangeType.UPDATE.getValue());
                node.put("mtor_relate_id", to.getString(ID));
                node.put("mtor_beg", to.getString("moni_beg"));
                node.put("mtor_end", to.getString("moni_end"));
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
                    obj.put("adduser", ADDUSER);
                    mtorRes.add(obj);
                }
                node.put("mtor_res_set", mtorRes);
            }
            // 备用字段集 - 特殊树 部门树 主责岗位
            if(rootEdmcNameEn.startsWith("depttree") &&
                    !StringUtil.isNullOrEmpty(to.getString("mdep_leader_post"))){
                JSONArray bkSet = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("mtor_bk1", to.getString("mdep_leader_post"));
                bkSet.add(obj);
                node.put("mtor_bk_set", bkSet);
            }
            no.add(node);
        }
        return no;
    }
    
    private Date getDate(String str,String mat){
        DateFormat format = new SimpleDateFormat(mat);
        try {
            return format.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误"+ str);
        }
    }
    
    private  String getNowDateStr(String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String formatDateStr= sdf.format(new Date());
        return formatDateStr;
    } 
}
