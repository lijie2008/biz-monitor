package com.huntkey.rx.sceo.monitor.provider.service.impl;

import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.ID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.MONITORTREEORDER;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.NULL;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.PID;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.STARTTIME;
import static com.huntkey.rx.sceo.monitor.commom.constant.Constant.ENDTIME;
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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.huntkey.rx.sceo.monitor.commom.model.ResourceTo;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
import com.huntkey.rx.sceo.serviceCenter.common.model.MergeParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortNode;
@Service
public class MonitorServiceImpl implements MonitorService {
    
	private static final String CREUSER = "admin";
    private static final String MODUSER = "admin";
    private static final String LVSPLIT = ",";
    private static final String ROOT_LVL_CODE = "1,";
    private static final String REVOKE_KEY = "REVOKE";
    private static final String MTOR_NODES_EDM = "monitortreeorder.mtor_node_set";
    private static final String KEY_SEP = "-";
    private static final String DEFAULTNODENAME="未命名节点";
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);
    
    @Autowired
    private ServiceCenterClient client;
    
    @Autowired
    private MonitorTreeService treeService;
    
    @Autowired
    private MonitorTreeOrderService orderTree ;
    
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
                else if(arry != null && arry.size() > 1)
                    ApplicationException.throwCodeMesg(ErrorMessage._60019.getCode(), ErrorMessage._60019.getMsg());
	        }
	    }else
	     throw new ServiceException(result.getErrMsg());
	    
	    // 单据中不存在当前树的临时单
	    if(order == null)
	        return null;
	    
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
	                                // 删除节点和资源
	                                MergeParam delNode = new MergeParam(MTOR_NODES_EDM);
	                                delNode.addAllData(nodeIds);
	                                Result delRet = client.delete(delNode.toJSONString());
	                                if(delRet.getRetCode() != Result.RECODE_SUCCESS)
	                                    throw new ServiceException(delRet.getErrMsg());
	                                
	                                // 删除临时单
	                                MergeParam delOrder = new MergeParam(MONITORTREEORDER);
	                                delOrder.addData(order);
	                                Result delOr = client.delete(delOrder.toJSONString());
	                                if(delOr.getRetCode() != Result.RECODE_SUCCESS)
	                                     throw new ServiceException(delOr.getErrMsg());
	                                
	                                // 删除redis中现存的数据
	                                hasOps.getOperations().delete(order.getString(ID)+ KEY_SEP +classId);
	                                hasOps.getOperations().delete(order.getString(ID)+ KEY_SEP +classId + REVOKE_KEY);
	                                
	                                return obj;
	                            }
	                        }else
                                ApplicationException.throwCodeMesg(ErrorMessage._60003.getCode(), ErrorMessage._60003.getMsg());
	                    }else{
	                    	throw new ServiceException(nodes.getErrMsg());
	                    }
	                }
	            }
	    }else
	    	throw new ServiceException(rootNode.getErrMsg());
	    
	    String key = order.getString(ID)+KEY_SEP+classId;
	    String revokeKey = key + REVOKE_KEY;
	    
	    NodeTo root = hasOps.get(key, ROOT_LVL_CODE);

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
            s.setKey(key);
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
                    classId = arry.getJSONObject(0).getString("mtor_cls_id");
            }
        }else{
        	throw new ServiceException(orderRes.getErrMsg());
        }
        
        if(StringUtil.isNullOrEmpty(classId))
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        
        switch(type){
            
            case 1:
                SearchParam reParams = new SearchParam("monitortreeorder.mtor_node_set");
                reParams.addCond_equals(PID, tempId)
                        .addSortParam(new SortNode("mtor_lvl", SortType.ASC))
                        .addSortParam(new SortNode("mtor_seq", SortType.ASC))
                        .addCond_less("mtor_type", ChangeType.INVALID.toString());
                if(StringUtil.isNullOrEmpty(validDate)){
                    validDate=getNowDateStr(YYYY_MM_DD);
                    reParams.addCond_greater("mtor_end", validDate);
                }else{
                    validDate+=" 00:00:00";
                    reParams.addCond_lessOrEquals("mtor_beg", validDate);
                    reParams.addCond_greater("mtor_end", validDate);
                }
                
                Result nodesRes = client.queryServiceCenter(reParams.toJSONString());
                
                JSONArray nodes = null;
                if(nodesRes.getRetCode() == Result.RECODE_SUCCESS){
                    if (nodesRes.getData() != null) {
                        nodes = JSONObject.parseObject(JSONObject.toJSONString(nodesRes.getData()))
                                .getJSONArray(Constant.DATASET);
                    }
                }else{
                	throw new ServiceException(nodesRes.getErrMsg());
                }
                
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
                    }else{
                    	throw new ServiceException(mtor_bk_set.getErrMsg());
                    }
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
                    Date begin = getDate(to.getBegin(),Constant.YYYY_MM_DD_HH_MM_SS);
                    Date end = getDate(to.getEnd(),Constant.YYYY_MM_DD_HH_MM_SS);
                    if(StringUtil.isNullOrEmpty(validDate)){
                        Date now = getDate(new SimpleDateFormat(YYYY_MM_DD).format(new Date()) + STARTTIME, Constant.YYYY_MM_DD_HH_MM_SS);
                        if(end.after(now) && ChangeType.INVALID.getValue() != to.getType())
                            list.add(to);
                    }else{
                        validDate+=" 00:00:00";
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
     */
    @Override
    public String addMonitorTree(AddMonitorTreeTo addMonitorTreeTo) {
        
        int type=addMonitorTreeTo.getType(); 
        
        String beginDate=addMonitorTreeTo.getBeginDate(); 
        String endDate=addMonitorTreeTo.getEndDate(); 
        String classId=addMonitorTreeTo.getClassId();
        String rootId=StringUtil.isNullOrEmpty(addMonitorTreeTo.getRootId()) ? NULL : addMonitorTreeTo.getRootId();
        String rootEdmcNameEn = addMonitorTreeTo.getRootEdmcNameEn();
        
        // 检查临时单是否存在
        SearchParam params = new SearchParam(MONITORTREEORDER);
        params.addCond_equals("mtor_order_root", rootId);
        params.addCond_equals("mtor_order_type", String.valueOf(ChangeType.ADD.getValue()));
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
            throw new ServiceException(result.getErrMsg());
        
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
            s.setKey(key);
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // TODO 去除多多余的字段
        for(int i = 0; i < nodes.size(); i++){
            JSONObject node = nodes.getJSONObject(i);
            node.put("mtor_beg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(node.getLong("mtor_beg"))));
            node.put("mtor_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(node.getLong("mtor_end"))));
            node.put("moduser", MODUSER);
            node.put("creuser", CREUSER);
            JSONArray res = node.getJSONArray("mtor_res_set");
            if(res == null || res.isEmpty())
                continue;
            for(int k = 0; k < res.size(); k++){
                res.getJSONObject(k).remove("text");
            }
        }
        
        // 将复制的节点插入到表中
        MergeParam pp = new MergeParam(MTOR_NODES_EDM);
        
        pp.addAllData(nodes);
        Result ret = client.add(pp.toJSONString());
        
        if(ret.getRetCode() != Result.RECODE_SUCCESS){
            throw new ServiceException(ret.getErrMsg());
        }
        
        return key;
    }
    
    @Override
    public String treeMaintaince(String classId, String rootId, String rootEdmcNameEn) {
        
        SearchParam params = new SearchParam(MONITORTREEORDER);
        params.addCond_equals("mtor_order_root", rootId);
        params.addCond_equals("mtor_order_type", String.valueOf(ChangeType.UPDATE.getValue()));
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
        	throw new ServiceException(result.getErrMsg());
        
        String tempId=createTemp(classId,ChangeType.UPDATE.getValue(),rootId);
        
        JSONArray nodes = copyTree(rootEdmcNameEn,classId, rootId,tempId,ChangeType.UPDATE.getValue(),null,null);
        
        // 将nodes数据放入到redis中
        String key = tempId + KEY_SEP + classId;
        String revokedKey = key + REVOKE_KEY;
        
        List<NodeTo> list = NodeTo.setValue(nodes);
        
        hasOps.getOperations().delete(key);
        hasOps.getOperations().delete(revokedKey);
        
        Map<String, NodeTo> map = new HashMap<String,NodeTo>();
        
        list.stream().forEach(s->{
            s.setKey(key);
            map.put(s.getLvlCode(), s);
        });
        
        hasOps.putAll(key, map);
        hasOps.getOperations().expire(key, timeout , TimeUnit.HOURS);
        
        // TODO 去除多多余的字段
        for(int i = 0; i < nodes.size(); i++){
            JSONObject node = nodes.getJSONObject(i);
            node.put("mtor_beg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(node.getLong("mtor_beg"))));
            node.put("mtor_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(node.getLong("mtor_end"))));
            node.put("moduser", MODUSER);
            node.put("creuser", CREUSER);
            JSONArray res = node.getJSONArray("mtor_res_set");
            if(res == null || res.isEmpty())
                continue;
            for(int k = 0; k < res.size(); k++){
                res.getJSONObject(k).remove("text");
            }
        }
        
        MergeParam addParams = new MergeParam(MTOR_NODES_EDM);
        addParams.addAllData(nodes);
        
        Result ret = client.add(addParams.toJSONString());
        
        if(ret.getRetCode() != Result.RECODE_SUCCESS){
            throw new ServiceException(ret.getErrMsg());
        }
        
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
        temp.put("creuser", "admin");
        temp.put("moduser", MODUSER);
        
        MergeParam params = new MergeParam(MONITORTREEORDER);
        params.addData(temp);
        
        Result result = client.add(params.toJSONString());
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() != null){
            	@SuppressWarnings("unchecked")
				List<String> listRet= (List<String>) result.getData();
                return listRet!=null ? listRet.get(0) : "";
            }
        }else
            throw new ServiceException(result.getErrMsg());
        
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
        node.put("mtor_beg", getDate(beginDate, Constant.YYYY_MM_DD_HH_MM_SS).getTime());
        node.put("mtor_end", getDate(endDate, Constant.YYYY_MM_DD_HH_MM_SS).getTime());
        node.put("mtor_index_conf", NULL);
        node.put("mtor_seq", 1);
        node.put("mtor_lvl_code", ROOT_LVL_CODE);
        node.put("mtor_lvl", 1);
        node.put("mtor_enum", NULL);
        node.put("mtor_relate_cnd", NULL);
        node.put("mtor_type", ChangeType.ADD.getValue());
        node.put("mtor_relate_id", NULL);
        node.put("creuser", CREUSER);
        
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
        }else{
        	throw new ServiceException(rootResult.getErrMsg());
        }
        
        if(rootNode == null)
            ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), ErrorMessage._60005.getMsg());
        
        Date rootBegin = new Date(rootNode.getLong("moni_beg"));
        Date rootEnd = new Date(rootNode.getLong("moni_end"));
        
        if(!rootBegin.before(rootEnd))
            ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), ErrorMessage._60009.getMsg());
        
        // 查询出需要复制的节点
        params.clearConditions();
        
        ChangeType type = ChangeType.valueOf(changeType);
        
        Date now = getDate(new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date())+STARTTIME,Constant.YYYY_MM_DD_HH_MM_SS);
        
        switch (type) {
            
            case ADD:
                // 正在生效的树复制                      moni_beg  <= now < moni_end
                if(!rootBegin.after(now) && rootEnd.after(now)){
                    params.addCond_lessOrEquals("moni_beg", new SimpleDateFormat(Constant.YYYY_MM_DD)
                            .format(new Date())+STARTTIME);
                    params.addCond_greater("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD)
                            .format(new Date())+STARTTIME);
                }else{
                    // 历史树和 未来树复制  需要  moni_end = 根节点失效时间
                    params.addCond_equals("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                            .format(new Date(rootNode.getLong("moni_end"))));
                }
                break;
                
            case UPDATE:
                if(!rootEnd.after(now))
                    ApplicationException.throwCodeMesg(ErrorMessage._60009.getCode(), "历史树不能维护");
                // 维护 时 复制 需要 moni_end > 当前时间
                params.addCond_greater("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD)
                        .format(new Date())+STARTTIME);
                break;

            default:
                ApplicationException.throwCodeMesg(ErrorMessage._60004.getCode(), "type类型[" + changeType +"]" +ErrorMessage._60004.getMsg());
        }
        
        params.addCond_greaterOrEquals("moni_beg", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                .format(new Date(rootNode.getLong("moni_beg"))))
                .addCond_lessOrEquals("moni_end", new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS)
                .format(new Date(rootNode.getLong("moni_end"))))
                .addCond_like("moni_lvl_code", ROOT_LVL_CODE)
                .addSortParam(new SortNode("moni_lvl", SortType.ASC))
                .addSortParam(new SortNode("moni_seq", SortType.ASC));
        
        Result result = client.queryServiceCenter(params.toJSONString());
        
        JSONArray nodes = null;
        
        if(result.getRetCode() == Result.RECODE_SUCCESS){
            if(result.getData() != null){
                nodes = JSONObject.parseObject(JSONObject.toJSONString(result.getData()))
                        .getJSONArray(Constant.DATASET);
            }
        }else{
        	throw new ServiceException(result.getErrMsg());
        }
        
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
            node.put("creuser", CREUSER);
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
            
            // 资源集 
            JSONArray res = to.getJSONArray("moni_res_set");
            if(res != null && !res.isEmpty()){
                JSONArray mtorRes = new JSONArray();
                for(int k = 0; k < res.size(); k++){
                    JSONObject rr = res.getJSONObject(k);
                    JSONObject obj = new JSONObject();
                    obj.put("mtor_res_id", rr.getString("moni_res_id"));
                    obj.put("text", rr.getString("text"));
                    obj.put("creuser", CREUSER);
                    mtorRes.add(obj);
                }
                node.put("mtor_res_set", mtorRes);
            }
            // 备用字段集 - 特殊树 部门树 主责岗位
            if(rootEdmcNameEn.endsWith("depttree") &&
                    !StringUtil.isNullOrEmpty(to.getString("mdep_leader_post"))){
                
                // TODO - 部门树的历史集情况需要特殊处理
                
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
        // TODO Auto-generated method stub
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
     * 删除节点资源
     * @param key redis key
     * @param levelCode 节点层及编码
     * @param resourceId 资源ID
     * @return 被删除的节点ID
     */
    @Override
    public String deleteNodeResource(String key,String lvlCode,String resourceId) {
        // TODO Auto-generated method stub
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
        hasOps.put(key, lvlCode, node);
        return resourceId;
    }

    /***
     * 添加节点资源
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
            }
            ResourceTo resource=new ResourceTo();
            resource.setResId(resourceId);
            resource.setText(resourceText);
            resourceList.add(resource);
            node.setResources(resourceList);
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
    	
        createNewNode(key,newLvlCode.split(LVSPLIT).length,newLvlCode,beginDate,endDate);
        // TODO Auto-generated method stub
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
            newLvlCode=pLvlCode+df.format(seq+1)+LVSPLIT;
        }else{//不存在子节点 则直接在当前层级编码后面加,1
            newLvlCode=pLvlCode+"1"+LVSPLIT;
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
	            +LVSPLIT;
	        }
        }else{//常见右节点
        	//如果index为最大的 ，代表当前节点没有右边
            if(index==listNodes.size()-1){
            	//如果没有右节点 则在当前结点后缀加1
                newLvlCode=pLvlCode+df.format(curNodeEnd+1)+LVSPLIT;
            }else{
                //取出当前节点的右节点
                node=listNodes.get(index+1);
                ranNum=(node.getSeq()+curNodeEnd)/2.00;
                newLvlCode=pLvlCode
                +df.format(ranNum)
                +LVSPLIT;
            }
        }
        return newLvlCode;
    }
    private void createNewNode(String key,int level,String newLvlCode,
    		String beginDate,String endDate){
    	String[] arr=newLvlCode.split(LVSPLIT);
    	//生成新节点
        NodeTo newNode=new NodeTo();
        newNode.setNodeNo("NODE"+System.currentTimeMillis());
        newNode.setKey(key);
        newNode.setLvl(level);
        newNode.setLvlCode(newLvlCode);
        newNode.setNodeName(DEFAULTNODENAME);
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
                if(field.split(LVSPLIT).length-levelCode.split(LVSPLIT).length==1){
                    list.add(nodeMap.get(field));
                }
            }
        }
        Collections.sort(list, new Comparator<NodeTo>() {//对节点按照层及编码升序排序
            @Override
            public int compare(NodeTo o1, NodeTo o2) {
                // TODO Auto-generated method stub
                return (int) Math.ceil(o1.getSeq()-o2.getSeq());
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
    /***
     * 公式计算的资源保存
     */
    @Override
	public List<String> formula(NodeTo node) {
		// TODO Auto-generated method stub
    	String key=node.getKey();
    	String lvlCode=node.getLvlCode();
    	List<ResourceTo> listAll=node.getResources();
    	//获取节点信息
    	NodeTo curNode=hasOps.get(key, lvlCode);
    	List<ResourceTo> listUsed=node.getResources();
    	//调用未使用资源接口
    	JSONObject listNotUsing=orderTree.queryNotUsingResource(key, lvlCode, 1, 20);
    	
		return null;
	}
}
