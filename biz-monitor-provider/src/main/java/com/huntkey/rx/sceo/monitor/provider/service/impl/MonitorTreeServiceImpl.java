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
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.enums.ErrorMessage;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ServiceCenterClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.serviceCenter.common.emun.SortType;
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
    private static final String SPE_HIS_SET = ".mdep_chag_set";
    
    @Autowired
    ServiceCenterClient serviceCenterClient;
    @Autowired
    ModelerClient modelerClient;
    @Value("${edm.version}")
    private String edmdVer;

    @Value("${edm.edmcNameEn.monitor}")
    private String monitorEdmcNameEn;
    
    @Override
    public JSONArray getMonitorTrees(String treeName, String edmcNameEn,String edmId, String beginTime,
                                     String endTime) {
        JSONArray monitorTrees = new JSONArray();
        
        // 根据edmId endTime 查询出所有的版本信息
        JSONArray versions = new JSONArray();

        SearchParam versionParams = new SearchParam(MONITOR_VERSION);
        
        String versionCharacters[] = new String[] { "motr_ver_code", "motr_beg", "motr_end", "motr_root_id" };
        
        versionParams.addColumns(versionCharacters)
                    .addCond_equals("motr_edm_id", edmId)
                    .addSortParam(new SortNode("motr_beg", SortType.DESC));
        
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
                    version.put("beginTime", formatDateStr(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(temp.getLong("motr_beg"))),Constant.YYYY_MM_DD));
                    version.put("endTime", formatDateStr(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(temp.getLong("motr_end"))),Constant.YYYY_MM_DD));
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
            
            String[] edmNames = null;
            if(edmcNameEn.endsWith("depttree"))
                edmNames = new String[]{edmcNameEn,edmcNameEn+SPE_HIS_SET};
            else
                edmNames = new String[]{edmcNameEn,edmcNameEn+MONITOR_HISTORY_SET};
                
            for(String edmName : edmNames){
                SearchParam requestParams = new SearchParam(edmName);
                
                String characters[] = new String[] { "id","moni_node_no", "moni_node_name", "moni_beg", "moni_end" };
                requestParams.addColumns(characters);
                requestParams
                .addSortParam(new SortNode("moni_end",SortType.ASC))
                .addCond_equals("moni_lvl_code", ROOT_LVL_CODE)
                .addCond_equals("moni_lvl", ROOT_LVL)
                .addSortParam(new SortNode("moni_beg", SortType.DESC));

                if (!StringUtil.isNullOrEmpty(treeName)) 
                    requestParams.addCond_like("moni_node_name", treeName);
                
                // ORM暂不支持or查询，先只根据失效时间过滤
                if (!StringUtil.isNullOrEmpty(endTime)) {
                    requestParams
                    .addCond_lessOrEquals("moni_beg", endTime)
                    .addCond_greaterOrEquals("moni_end", endTime);
                }else{
                    requestParams
                    .addCond_greaterOrEquals("moni_beg", versions.getJSONObject(i).getString("beginTime") + Constant.STARTTIME)
                    .addCond_lessOrEquals("moni_end", versions.getJSONObject(i).getString("endTime")+Constant.ENDTIME);
                }
                
                Result treesResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
                
                if (treesResult.getRetCode() == Result.RECODE_SUCCESS) {
                    if (treesResult.getData() != null) {
                        JSONObject treeData = JSONObject.parseObject(JSONObject.toJSONString(treesResult.getData()));
                        JSONArray treeArray = treeData.getJSONArray("dataset");

                        for (int j = 0; j < treeArray.size(); j++) {
                            JSONObject temp = treeArray.getJSONObject(j);
                            JSONObject tree = new JSONObject();
                            tree.put("rootNodeId", temp.getString("id"));
                            tree.put("rootNodeName", temp.getString("moni_node_name"));
                            tree.put("beginTime", formatDateStr(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(temp.getLong("moni_beg"))),Constant.YYYY_MM_DD));
                            tree.put("endTime", formatDateStr(new SimpleDateFormat(Constant.YYYY_MM_DD_HH_MM_SS).format(new Date(temp.getLong("moni_end"))),Constant.YYYY_MM_DD));
                            tree.put("rootEdmcNameEn", edmName);
                            
                            JSONArray rootNodes = version.getJSONArray("rootNodes") == null ? new JSONArray():version.getJSONArray("rootNodes");
                            rootNodes.add(tree);
                            version.put("rootNodes", rootNodes);

                            if(version.getString("rootNodeId").equals(temp.getString("id")))
                                version.put("rootNodeName", temp.getString("moni_node_name"));
                        }
                    }
                } else 
                    throw new ServiceException(treesResult.getErrMsg());
            }
            
            version.put("count",version.getJSONArray("rootNodes") == null ? 0 : version.getJSONArray("rootNodes").size() );
            
            // 版本下没有树时 数据不送给前端
            if(version.getInteger("count") == 0)
                continue;
                
            // 如果查历史树 导致版本树没有名称
            if(version.getInteger("count") != 0 && 
                    StringUtil.isNullOrEmpty(version.getString("rootNodeName")))
                version.put("rootNodeName", version.getJSONArray("rootNodes").getJSONObject(0).getString("rootNodeName"));
            
            monitorTrees.add(version);
        }
        
        return monitorTrees;
    }
    
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
    
    /**
     * 可查询一个时间点的树结构
     * 也可查一个时间区间内的树结构 
     * 
     * 时间必须按照 YYYY-MM-DD传
     */
    @Override
    public JSONObject getMonitorTreeNodes(String rootEdmcNameEn, String startDate, String endDate, String rootNodeId) {
        
        JSONObject nodeRet = new JSONObject();
        
        startDate = startDate + Constant.STARTTIME;
        endDate = endDate + Constant.ENDTIME;
    	
    	// 需要确定必须从哪里开始查-- 有根节点id 必须根据edmcNameEn 查一次就可以
    	String[] edmNames = null;
    	if(StringUtil.isNullOrEmpty(rootNodeId)){
    	    if(rootEdmcNameEn.endsWith("depttree"))
    	        edmNames = new String[]{rootEdmcNameEn,rootEdmcNameEn+SPE_HIS_SET};
    	    else
    	        edmNames = new String[]{rootEdmcNameEn,rootEdmcNameEn+MONITOR_HISTORY_SET};
    	}else
    	    edmNames = new String[]{rootEdmcNameEn};
    	
    	for(String edmName : edmNames){
    	    //组装参数
    	    SearchParam requestParams = new SearchParam(edmName);
    	    
            requestParams
            .addSortParam(new SortNode("moni_lvl",SortType.ASC))
            .addSortParam(new SortNode("moni_lvl_code",SortType.ASC));
            if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME)){
                requestParams.addCond_lessOrEquals("moni_beg", startDate)
                             .addCond_greater("moni_end", startDate);
                
            }else{
                requestParams.addCond_greaterOrEquals("moni_beg", startDate)
                .addCond_lessOrEquals("moni_end", endDate);
            }
            
            if (StringUtil.isNullOrEmpty(rootNodeId)) {
                requestParams
                .addCond_equals("moni_lvl", ROOT_LVL)
                .addCond_equals("moni_lvl_code", ROOT_LVL_CODE);
            }else 
                requestParams
                .addCond_equals(Constant.ID, rootNodeId);

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
                    params
                    .addSortParam(new SortNode("moni_lvl",SortType.ASC))
                    .addSortParam(new SortNode("moni_lvl_code",SortType.ASC))
                    .addCond_like("moni_lvl_code", ROOT_LVL_CODE);
                    
                    if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME)){
                        params.addCond_lessOrEquals("moni_beg", startDate)
                                     .addCond_greater("moni_end", startDate);
                        
                    }else{
                        params.addCond_greaterOrEquals("moni_beg", startDate)
                        .addCond_lessOrEquals("moni_end", endDate);
                    }
                    
                    Result allResult = serviceCenterClient.queryServiceCenter(params.toJSONString());
                    
                    if (allResult.getRetCode() != Result.RECODE_SUCCESS) {
                        throw new ServiceException(allResult.getErrMsg());
                    } else {
                        nodeRet.put("edmName", edmName);
                        if(allResult.getData() != null)
                            nodeRet.put("nodes", JSONObject.parseObject(JSONObject.toJSONString(allResult.getData())).getJSONArray("dataset"));
                        else
                            nodeRet.put("nodes", null);
                        return nodeRet;
                    }

                } else 
                    if (rootArray.size() > 1) {
                        throw new ServiceException("数据异常，同一时间找到多个监管树！");
                    }
            }
    	}
    	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONArray getNodeResources(String name, List<String> nodes, String edmId,String edmName,int type) {
        
        Result resourcesResult = serviceCenterClient.getNodeResources(name, nodes, edmId,edmName,type);
        
        if (resourcesResult.getRetCode() == Result.RECODE_SUCCESS) 
            return new JSONArray((List<Object>) resourcesResult.getData());
        else 
            throw new ServiceException(resourcesResult.getErrMsg());
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
    
    @Override
    public JSONObject getNewMonitorTreeStartDate(String edmcNameEn) {

        JSONObject resultData = new JSONObject();

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

                String lastDate = new SimpleDateFormat(Constant.YYYY_MM_DD).format(new Date(rootArray.getJSONObject(0).getLong("moni_end")));
                DateFormat format =  new SimpleDateFormat(Constant.YYYY_MM_DD);
                try {
                    Date tempDate = format.parse(lastDate);
                    // 存在最大时间区间的记录
                    if(tempDate.compareTo(format.parse(Constant.MAXINVALIDDATE)) == 0)
                        resultData.put("type", 3);
                    else{
                        // 计算出最大时间加一天的 时间 作为可新增的最大时间树
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(tempDate);
                        cal.add(Calendar.DATE, 1);
                        String newDate = format.format(cal.getTime());
                        resultData.put("type", 1);
                        resultData.put("date",newDate);
                    }
                    
                    return resultData;
                } catch (ParseException e) {
                    throw new ServiceException("日期格式不正确！");
                }
            }else
                resultData.put("type", 2);
        }else 
            throw new ServiceException("调用" + treeResult.getErrMsg());
        
        return resultData;
    }

    private  String formatDateStr(String dateStr,String formatStr) {
        String formatDateStr=null;
        if(!StringUtil.isNullOrEmpty(dateStr)){
            SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
            Date datetime=null;
            try {
                datetime=(Date) sdf.parse(dateStr);
            } catch (ParseException e) {
                throw new ServiceException("传入日期格式错误！");
            }
            formatDateStr= sdf.format(datetime);
        }
        return formatDateStr;
    }
    
    @Override
    public JSONArray searchResourceObj(String resourceClassId, String resourceValue) {
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
            
            requestParams.addCond_equals(Constant.ID, nodeId);
            
            Result result = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());
            
            JSONObject node = null;
            
            if(result.getRetCode() == Result.RECODE_SUCCESS){
                
                if(result.getData() != null){
                    JSONArray nodesArray = JSONObject.parseObject(JSONObject.toJSONString(result.getData()))
                            .getJSONArray(Constant.DATASET);
                    if(nodesArray != null && nodesArray.size() == 1)
                        node = nodesArray.getJSONObject(0);
                }
            }else
                throw new ServiceException(result.getErrMsg());
            
            if(node == null)
                ApplicationException.throwCodeMesg(ErrorMessage._60005.getCode(), "节点记录" + ErrorMessage._60005.getMsg());            
            
            String begin = new SimpleDateFormat(Constant.YYYY_MM_DD)
                    .format(new Date(node.getLong("moni_beg")));
            String end = new SimpleDateFormat(Constant.YYYY_MM_DD)
                    .format(new Date(node.getLong("moni_end")));
            
            requestParams.clearConditions();
            requestParams
                .addCond_greater("moni_lvl", String.valueOf(node.getInteger("moni_lvl")))
                .addCond_lessOrEquals("moni_beg", begin)
                .addCond_greater("moni_end", end)
                .addCond_like("moni_lvl_code", node.getString("moni_lvl_code"));

            LOG.info("查询json:{}", requestParams.toJSONString());

            Result allRet = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());

            if (allRet != null && allRet.getRetCode() == Result.RECODE_SUCCESS) {
                if (allRet.getData() == null) {
                    return null;
                }
                return JSONObject.parseObject(JSONObject.toJSONString(allRet.getData()))
                        .getJSONArray(Constant.DATASET);
            } else {
                throw new ServiceException(allRet.getErrMsg());
            }

        }

        return null;
    }
    
    
}
