package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.datetime.DateUtil;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.DateConstant;
import com.huntkey.rx.sceo.monitor.commom.constant.ServiceCenterConstant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.commom.utils.JsonUtil;
import com.huntkey.rx.sceo.monitor.commom.utils.ToolUtil;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.OperatorType;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
import com.huntkey.rx.sceo.serviceCenter.common.model.ConditionNode;
import com.huntkey.rx.sceo.serviceCenter.common.model.PagenationNode;
import com.huntkey.rx.sceo.serviceCenter.common.model.SearchParam;
import com.huntkey.rx.sceo.serviceCenter.common.model.SortNode;

/**
 * Created by zhaomj on 2017/8/9.
 */
@Service
public class MonitorTreeServiceImpl implements MonitorTreeService {

    private static final Logger LOG = LoggerFactory.getLogger(MonitorTreeServiceImpl.class);
    
    private static final String ROOT_LVL = "1";
    private static final String ROOT_LVL_CODE = "1,";
    private static final String MONITOR_HISTORY_SET=".moni_his_set";
    private static final String MONITOR_VERSION = "monitortree";
    
    @Autowired
    ServiceCenterClient serviceCenterClient;
    @Autowired
    ModelerClient modelerClient;
    @Value("${edm.version}")
    private String edmdVer;

    @Value("${edm.edmcNameEn.monitor}")
    private String monitorEdmcNameEn;

    @Override
    public Result getEntityByVersionAndEnglishName(String treeName, String beginTime,
                                                   String endTime) {

        Result monitorClassesResult = serviceCenterClient.getMonitorClasses(treeName, beginTime,
                endTime, edmdVer, monitorEdmcNameEn);
        if (monitorClassesResult.getRetCode() != Result.RECODE_SUCCESS) {
            throw new ServiceException(monitorClassesResult.getErrMsg());
        }
        return monitorClassesResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId) {
    	searchDate=searchDate+" 00:00:00";
    	// 需要确定必须从哪里开始查-- 有根节点id 必须根据edmcNameEn 查一次就可以
    	String[] edmNames = null;
    	if(StringUtil.isNullOrEmpty(rootNodeId))
    	    edmNames = new String[]{edmcNameEn,edmcNameEn+MONITOR_HISTORY_SET};
    	else
    	    edmNames = new String[]{edmcNameEn};
    	
    	for(String edmName : edmNames){
    	    //组装参数
    	    SearchParam requestParams = new SearchParam(edmName);
    	    
            String characters[] = new String[] {"id","moni_node_no", "moni_node_name", "moni_lvl", "moni_lvl_code"};
            requestParams.addColumns(characters);
            
            requestParams.addSortParam(new SortNode("moni_lvl",SortType.ASC));
            requestParams.addSortParam(new SortNode("moni_lvl_code",SortType.ASC));
            
            if (StringUtil.isNullOrEmpty(rootNodeId)) {
                //根据时间查询根节点
                requestParams.addCondition(new ConditionNode("moni_lvl", OperatorType.Equals, ROOT_LVL));
                requestParams.addCondition(new ConditionNode("moni_lvl_code", OperatorType.Equals, ROOT_LVL_CODE));
                       
            } else {
                //根据ID查询跟节点
                requestParams.addCondition(new ConditionNode("id", OperatorType.Equals, rootNodeId));
            }
            requestParams
            .addCondition(new ConditionNode("moni_beg", OperatorType.LessEquals, searchDate))
            .addCondition(new ConditionNode("moni_end", OperatorType.Greater, searchDate));

            Result rootNodeResult = serviceCenterClient
                    .queryServiceCenter(requestParams.toJSONString());
            
            if (rootNodeResult.getRetCode() != Result.RECODE_SUCCESS) 
                throw new ServiceException(rootNodeResult.getErrMsg());
                
            if (rootNodeResult.getData() != null) {
                JSONObject rootData = JSONObject.parseObject(JSONObject.toJSONString(rootNodeResult.getData()));

                JSONArray rootArray = rootData.getJSONArray("dataset");

                if (rootArray.size() == 1) {
                    
                    // 表中存在需要的根节点 - 查找出所有的节点信息
                    SearchParam params = new SearchParam(edmName);
                    params.addColumns(characters);
                    
                    params.addSortParam(new SortNode("moni_lvl",SortType.ASC));
                    params.addSortParam(new SortNode("moni_lvl_code",SortType.ASC));
                    
                    params
                    .addCondition(new ConditionNode("moni_beg", OperatorType.LessEquals, searchDate))
                    .addCondition(new ConditionNode("moni_end", OperatorType.Greater, searchDate));
                    
                    params.addCond_like("moni_lvl_code", ROOT_LVL_CODE);
                    
                    Result allResult = serviceCenterClient
                            .queryServiceCenter(params.toJSONString());
                    
                    if (allResult.getRetCode() != Result.RECODE_SUCCESS) {
                        throw new ServiceException(allResult.getErrMsg());
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("edmName", edmName);
                        obj.put("nodes", new JSONArray((List<Object>) allResult.getData()));
                        return obj;
                    }

                } else {
                    if (rootArray.size() > 1) {
                        throw new ServiceException("数据异常，同一时间找到多个监管树！");
                    }
                }
            }
    	}
    	return null;
    }

    @Override
    public JSONArray getMonitorTrees(String treeName, String edmcNameEn,String edmcEdmdId, String beginTime,
                                     String endTime) {
        JSONArray monitorTrees = new JSONArray();
        
        // 根据edmcEdmdId endTime 查询出所有的版本信息
        JSONArray versions = new JSONArray();

        SearchParam versionParams = new SearchParam(MONITOR_VERSION);
        
        String versionCharacters[] = new String[] { "motr_ver_code", "motr_beg", "motr_end", "motr_root_id" };
        
        versionParams.addColumns(versionCharacters);
        versionParams.addCond_equals("motr_edm_id", edmcEdmdId);
        
        if (!StringUtil.isNullOrEmpty(endTime)) {
            versionParams.addCond_lessOrEquals("motr_beg", endTime);
            versionParams.addCond_greaterOrEquals("motr_end", endTime);
        }
        
        Result versionResult = serviceCenterClient.queryServiceCenter(versionParams.toJSONString());
        
        if (versionResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (versionResult.getData() != null) {
                JSONObject versionData = JSONObject.parseObject(JSONObject.toJSONString(versionResult.getData()));
                JSONArray versionArray = versionData.getJSONArray("dataset");

                for (int i = 0; i < versionArray.size(); i++) {
                    JSONObject temp = versionArray.getJSONObject(i);
                    JSONObject version = new JSONObject();
                    version.put("versionCode", temp.getString("motr_ver_code"));
                    version.put("beginTime", ToolUtil.formatDateStr(temp.getString("motr_beg"),Constant.YYYY_MM_DD));
                    version.put("endTime", ToolUtil.formatDateStr(temp.getString("motr_end"),Constant.YYYY_MM_DD));
                    version.put("rootNodeId", temp.getString("motr_root_id"));
                    versions.add(version);
                }
            } else {
                return null;
            }
        } else {
            throw new ServiceException(versionResult.getErrMsg());
        }
        
        // 根据版本编号去查询正式表 和 历史表查询
        for(int i = 0 ; i < versions.size(); i++){
            
            JSONObject version = versions.getJSONObject(i);
            String[] edmNames = new String[]{edmcNameEn,edmcNameEn+MONITOR_HISTORY_SET};
            
            for(String edmName : edmNames){
                SearchParam requestParams = new SearchParam(edmName);
                
                String characters[] = new String[] { "moni_node_no", "moni_node_name", "moni_beg", "moni_end" };
                requestParams.addColumns(characters);
                requestParams.addSortParam(new SortNode("moni_end",SortType.ASC));
                
                requestParams.addCondition(new ConditionNode("moni_lvl_code", OperatorType.Equals, ROOT_LVL_CODE));
                requestParams.addCondition(new ConditionNode("moni_lvl", OperatorType.Equals, ROOT_LVL));

                if (!StringUtil.isNullOrEmpty(treeName)) {
                    requestParams.addCondition(new ConditionNode("moni_node_name", OperatorType.Like, treeName));
                }
                // ORM暂不支持or查询，先只根据失效时间过滤
                if (!StringUtil.isNullOrEmpty(endTime)) {
                    requestParams.addCondition(new ConditionNode("moni_beg",OperatorType.LessEquals,endTime));
                    requestParams.addCondition(new ConditionNode("moni_end", OperatorType.GreaterEquals, endTime));
                }else{
                    requestParams.addCondition(new ConditionNode("moni_beg",OperatorType.LessEquals,versions.getJSONObject(i).getString("motr_beg")));
                    requestParams.addCondition(new ConditionNode("moni_end", OperatorType.GreaterEquals, versions.getJSONObject(i).getString("motr_end")));
                }
                
                Result treesResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
                
                if (treesResult.getRetCode() == Result.RECODE_SUCCESS) {
                    if (treesResult.getData() != null) {
                        JSONObject treeData = JSONObject.parseObject(JSONObject.toJSONString(treesResult.getData()));
                        JSONArray treeArray = treeData.getJSONArray("dataset");

                        for (int j = 0; j < treeArray.size(); j++) {
                            JSONObject temp = treeArray.getJSONObject(i);
                            JSONObject tree = new JSONObject();
                            tree.put("rootNodeId", temp.getString("id"));
                            tree.put("rootNodeName", temp.getString("moni_node_name"));
                            tree.put("beginTime", ToolUtil.formatDateStr(temp.getString("moni_beg"),Constant.YYYY_MM_DD));
                            tree.put("endTime", ToolUtil.formatDateStr(temp.getString("moni_end"),Constant.YYYY_MM_DD));
                            tree.put("rootEdmcNameEn", edmName);
                            
                            JSONArray rootNodes = version.getJSONArray("rootNodes") == null ? new JSONArray():version.getJSONArray("rootNodes");
                            rootNodes.add(tree);
                            version.put("rootNodes", rootNodes);

                            if(temp.getString("id").equals(version.getString("rootNodeId")))
                                version.put("rootNodeName", temp.getString("moni_node_name"));
                        }
                    }
                } else {
                    throw new ServiceException(treesResult.getErrMsg());
                }
            }
            
            version.put("count",version.getJSONArray("rootNodes") == null ? 0 : version.getJSONArray("rootNodes").size() );
            
            monitorTrees.add(version);
        }
        
        return monitorTrees;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONArray getNodeResources(String name, List<String> nodes, String edmcId,String edmName) {
        Result resourcesResult = serviceCenterClient.getNodeResources(name, nodes, edmcId,edmName);
        if (resourcesResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (resourcesResult.getData() != null) {
                JSONArray childrenArray = new JSONArray((List<Object>) resourcesResult.getData());
                return childrenArray;
            } else {
                return null;
            }
        } else {
            throw new ServiceException(resourcesResult.getErrMsg());
        }
    }

    @Override
    public JSONArray getConProperties(String edmcNameEn, boolean enable) {
        Result resourcesResult = modelerClient.getConProperties(edmdVer, edmcNameEn);
        if (resourcesResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (resourcesResult.getData() != null) {
                @SuppressWarnings("unchecked")
                JSONArray allConPropertes = new JSONArray((List<Object>) resourcesResult.getData());
                allConPropertes.removeIf(o -> {
                    JSONObject object = (JSONObject) JSONObject.toJSON(o);
                    return object.getBoolean("isVisible")   != enable;
                });
                return allConPropertes;
            } else {
                return null;
            }

        } else {
            throw new ServiceException("调用" + resourcesResult.getErrMsg());
        }
    }
    
    /**
     * 
     * 1 - 返回最大的开始时间 - 跳复制页面
     * 2 - 不存在临时单也不存在任何监管树 直接新增临时单和节点
     * 3 - 存在一颗最大的树 9999-12-31 不能新增
     * 4 - 存在临时单 打开临时单
     */
    @Override
    public JSONObject getNewMonitorTreeStartDate(String edmcNameEn, String classId) {

        JSONObject resultData = new JSONObject();

        // 是否存在临时单
        SearchParam params = new SearchParam("monitortreeorder");
        
        params.addCondition(new ConditionNode("mtor_order_type", OperatorType.Equals, "1"));
        params.addCondition(new ConditionNode("mtor_cls_id", OperatorType.Equals, classId));
        Result orderResult = serviceCenterClient.queryServiceCenter(params.toJSONString());
        
        if (orderResult.getRetCode() == Result.RECODE_SUCCESS) {
            
            JSONObject tempTree = (JSONObject) JSONObject.toJSON(orderResult.getData());
            JSONArray tempArray = tempTree.getJSONArray("dataset");
            
            if (tempArray != null && !tempArray.isEmpty()) {
                resultData.put("type", 4);
                resultData.put("tempId", tempArray.getJSONObject(0).getString("id"));
                return resultData;
            } 
        }else 
            throw new ServiceException("调用" + orderResult.getErrMsg());
        
        // 查找最大失效时间的树
        SearchParam requestParams = new SearchParam(edmcNameEn);
        
        requestParams.addCond_equals("moni_lvl_code", ROOT_LVL_CODE);
        requestParams.addCond_equals("moni_lvl", ROOT_LVL);

        requestParams.addSortParam(new SortNode("moni_end", SortType.DESC));

        requestParams.addPagenation(new PagenationNode(1, 1));

        String characters[] = new String[] {"moni_end" };
        requestParams.addColumns(characters);

        Result treeResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
        
        if (treeResult.getRetCode() == Result.RECODE_SUCCESS) {
            
            JSONObject data = (JSONObject) JSONObject.toJSON(treeResult.getData());
            
            // 未查询到最大时间 说明 当前监管类下没有树
            if(data == null){
                resultData.put("type", 2);
                return resultData;
            }
            
            JSONArray rootArray = data.getJSONArray("dataset");
            
            if (rootArray != null && !rootArray.isEmpty()) {

                String lastDate = rootArray.getJSONObject(0).getString("moni_end");
                
                DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
                
                try {
                    
                    Date tempDate = format.parse(lastDate);
                    
                    // 存在最大时间区间的记录
                    if(tempDate.compareTo(format.parse(Constant.MAXINVALIDDATE)) == 0){
                        resultData.put("type", 3);
                        return resultData;
                    }
                    
                    // 计算出最大时间加一天的 时间 作为可新增的最大时间树
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(tempDate);
                    cal.add(Calendar.DATE, 1);
                    String newDate = format.format(cal.getTime());
                    
                    resultData.put("type", 1);
                    resultData.put("date",newDate);
                    
                } catch (ParseException e) {
                    throw new ServiceException("日期格式不正确！");
                }
            }else{
                resultData.put("type", 2);
                return resultData;
            }
        }else 
            throw new ServiceException("调用" + treeResult.getErrMsg());
        
        return resultData;
    }

    /**
     * getChileNodes:根据节点id查询其子节点信息
     * @author caozhenx
     * @param nodeId
     * @return
     */
    public JSONArray getChileNodes(String nodeId, String edmcNameEn) {

        LOG.info("查询子节点nodeId:{},edmcNameEn:{}", nodeId, edmcNameEn);

        if (StringUtils.isNotBlank(nodeId) && StringUtils.isNotBlank(edmcNameEn)) {

            SearchParam requestParams = new SearchParam(edmcNameEn);
            //父节点id
            if (StringUtils.isNotBlank(nodeId)) {
                requestParams
                        .addCondition(new ConditionNode("moni006", OperatorType.Equals, nodeId));
            }

            Calendar cl = Calendar.getInstance();
            String currentDate = DateUtil.parseFormatDate(cl.getTime(),
                    DateConstant.FORMATE_YYYY_MM_DD);
            //生效时间
            requestParams.addCondition(
                    new ConditionNode("moni004", OperatorType.LessEquals, currentDate));
            //失效时间
            requestParams.addCondition(
                    new ConditionNode("moni005", OperatorType.Greater, currentDate));

            LOG.info("查询json:{}", requestParams.toJSONString());

            Result result = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());

            if (result != null && result.getRetCode() == Result.RECODE_SUCCESS) {
                if (result.getData() == null) {
                    return null;
                }
                JSONObject jsonObj = JsonUtil.getJson(result.getData());
                JSONArray jsonArray = jsonObj.getJSONArray(ServiceCenterConstant.DATA_SET);

                return jsonArray;
            } else {
                throw new ServiceException(result.getErrMsg());
            }

        }

        return null;
    }

    @Override
    public JSONArray searchResourceObj(String resourceClassId, String resourceValue) {
        // TODO Auto-generated method stub
        Result resourcesResult = serviceCenterClient.searchResourceObj(resourceClassId,
                resourceValue);
        if (resourcesResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (resourcesResult.getData() != null) {
                @SuppressWarnings("unchecked")
                JSONArray childrenArray = new JSONArray((List<Object>) resourcesResult.getData());
                return childrenArray;
            } else {
            	return null;
            }
        } else {
        	LOG.info(resourcesResult.getErrMsg());
            throw new ServiceException(resourcesResult.getErrMsg());
        }
    }
}
