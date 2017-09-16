package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public JSONArray getMonitorTreeNodes(String edmcNameEn, String searchDate, String rootNodeId) {
    	searchDate=searchDate+" 00:00:00";
        //组装参数
        SearchParam requestParams = new SearchParam(edmcNameEn);

        String characters[] = new String[] { "moni001", "moni002", "moni006", "moni007", "moni008",
                "moni009" };
        requestParams.addColumns(characters);

        if (StringUtil.isNullOrEmpty(rootNodeId)) {
            //根据时间查询根节点
            requestParams
                    .addCondition(new ConditionNode("moni004", OperatorType.LessEquals, searchDate))
                    .addCondition(new ConditionNode("moni005", OperatorType.Greater, searchDate))
                    .addCondition(new ConditionNode("moni006", OperatorType.Equals, ""));
        } else {
            //根据ID查询跟节点
            requestParams.addCondition(new ConditionNode("id", OperatorType.Equals, rootNodeId));
        }

        JSONObject rootNode;

        Result rootNodeResult = serviceCenterClient
                .queryServiceCenter(requestParams.toJSONString());
        if (rootNodeResult.getRetCode() != Result.RECODE_SUCCESS) {
            throw new ServiceException(rootNodeResult.getErrMsg());
        }
        if (rootNodeResult.getData() != null) {
            JSONObject rootData = JSONObject
                    .parseObject(JSONObject.toJSONString(rootNodeResult.getData()));

            JSONArray rootArray = rootData.getJSONArray("dataset");

            if (rootArray.size() == 1) {
                rootNode = rootArray.getJSONObject(0);

                rootNodeId = rootNode.getString("id");

                //根据根节点ID，查询所有子节点
                Result childrenNpdeResult = serviceCenterClient.getMonitorTreeNodes(edmcNameEn,
                        searchDate, rootNodeId,4);
                if (childrenNpdeResult.getRetCode() != Result.RECODE_SUCCESS) {
                    throw new ServiceException(childrenNpdeResult.getErrMsg());
                } else {
                    JSONArray childrenArray = new JSONArray(
                            (List<Object>) childrenNpdeResult.getData());
                    childrenArray.add(rootNode);
                    return childrenArray;
                }

            } else {
                if (rootArray.size() > 1) {
                    throw new ServiceException("数据异常，同一时间找到多个监管树！");
                }
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public JSONArray getMonitorTrees(String treeName, String edmcNameEn, String beginTime,
                                     String endTime) {
        JSONArray monitorTrees = new JSONArray();

        SearchParam requestParams = new SearchParam(edmcNameEn);

        String characters[] = new String[] { "moni001", "moni002", "moni004", "moni005" };

        requestParams.addColumns(characters);

        requestParams.addSortParam(new SortNode("moni005",SortType.ASC));

        requestParams.addCondition(new ConditionNode("moni006", OperatorType.Equals, ""));

        if (!StringUtil.isNullOrEmpty(treeName)) {
            requestParams.addCondition(new ConditionNode("moni002", OperatorType.Like, treeName));
        }

        //ORM暂不支持or查询，先只根据失效时间过滤
        if (!StringUtil.isNullOrEmpty(endTime)) {
            requestParams
                    .addCondition(new ConditionNode("moni004", OperatorType.LessEquals, endTime));
            requestParams.addCondition(
                    new ConditionNode("moni005", OperatorType.GreaterEquals, endTime));
        }

        Result treesResult = serviceCenterClient.queryServiceCenter(requestParams.toJSONString());

        if (treesResult.getRetCode() == Result.RECODE_SUCCESS) {
            if (treesResult.getData() != null) {
                JSONObject treeData = JSONObject
                        .parseObject(JSONObject.toJSONString(treesResult.getData()));
                JSONArray treeArray = treeData.getJSONArray("dataset");

                for (int i = 0; i < treeArray.size(); i++) {
                    JSONObject temp = treeArray.getJSONObject(i);
                    JSONObject tree = new JSONObject();
                    tree.put("rootNodeId", temp.getString("id"));
                    tree.put("rootNodeName", temp.getString("moni002"));
                    tree.put("beginTime", ToolUtil.formatDateStr(temp.getString("moni004"),Constant.YYYY_MM_DD));
                    tree.put("endTime", ToolUtil.formatDateStr(temp.getString("moni005"),Constant.YYYY_MM_DD));
                    monitorTrees.add(tree);
                }
            } else {
                return null;
            }
        } else {
            throw new ServiceException(treesResult.getErrMsg());
        }

        return monitorTrees;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONArray getNodeResources(String name, List<String> nodes, String edmcId) {
        Result resourcesResult = serviceCenterClient.getNodeResources(name, nodes, edmcId);
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
                JSONArray allConPropertes = new JSONArray((List<Object>) resourcesResult.getData());
                allConPropertes.removeIf(o -> {
                    JSONObject object = (JSONObject) JSONObject.toJSON(o);
                    return object.getBoolean("isVisible") != enable;
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
    public JSONObject getNewMonitorTreeStartDate(String edmcNameEn, String classId) {

        JSONObject resultData = new JSONObject();

        SearchParam requestParams = new SearchParam(edmcNameEn);
        ConditionNode nodeIdCondition = new ConditionNode("moni006", OperatorType.Equals, "");

        requestParams.addCondition(nodeIdCondition);

        //统计所有根节点
        Result counttreeResult = serviceCenterClient
                .countByConditions(requestParams.toJSONString());
        if (counttreeResult.getRetCode() == Result.RECODE_SUCCESS) {
            JSONObject object = (JSONObject) JSONObject.toJSON(counttreeResult.getData());
            int count = object.getIntValue("count");
            //查询临时单是否存在
            String tempId = "";
            SearchParam Params = new SearchParam("monitortreeorder");
            Params.addCondition(new ConditionNode("mtor002", OperatorType.Equals, "1"));
            Params.addCondition(new ConditionNode("mtor003", OperatorType.Equals, classId));
            Result temptreeResult = serviceCenterClient.queryServiceCenter(Params.toJSONString());
            if (temptreeResult.getRetCode() == Result.RECODE_SUCCESS) {
                JSONObject tempTree = (JSONObject) JSONObject.toJSON(temptreeResult.getData());
                JSONArray tempArray = tempTree.getJSONArray("dataset");
                if (tempArray.size() > 0) {
                    tempId = tempArray.getJSONObject(0).getString("id");
                    resultData.put("type", 4);
                    resultData.put("tempId", tempId);
                }
            }
            if (!StringUtils.isNotEmpty(tempId)) {
                if (count <= 0) {
                    resultData.put("type", 2);
                } else {
                    //统计没有失效时间的根节点

                    requestParams.addCondition(new ConditionNode("moni005", OperatorType.Equals,
                            Constant.MAXINVALIDDATE));

                    Result countResult = serviceCenterClient
                            .countByConditions(requestParams.toJSONString());
                    if (countResult.getRetCode() == Result.RECODE_SUCCESS) {
                        JSONObject maxObject = (JSONObject) JSONObject
                                .toJSON(countResult.getData());
                        int maxCount = maxObject.getIntValue("count");
                        if (maxCount > 0) {
                            resultData.put("type", 3);
                        } else {
                            //查询最大失效时间
                            requestParams.clearConditions();
                            requestParams.addCondition(nodeIdCondition);

                            requestParams.addSortParam(new SortNode("moni005", SortType.DESC));

                            requestParams.addPagenation(new PagenationNode(1, 1));

                            String characters[] = new String[] { "moni005" };
                            requestParams.addColumns(characters);

                            Result treeResult = serviceCenterClient
                                    .queryServiceCenter(requestParams.toJSONString());
                            if (treeResult.getRetCode() == Result.RECODE_SUCCESS) {
                                JSONObject data = (JSONObject) JSONObject
                                        .toJSON(treeResult.getData());
                                JSONArray rootArray = data.getJSONArray("dataset");
                                if (rootArray.size() > 0) {

                                    String lastDate = rootArray.getJSONObject(0).getString("moni005");
                                    //TODO
                                    DateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date tempDate = format.parse(lastDate);
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(tempDate);
                                        cal.add(Calendar.DATE, 1);
                                        String newDate = format.format(cal.getTime());
                                        resultData.put("type", 1);
                                        resultData.put("date",newDate);
                                    } catch (ParseException e) {
                                        throw new ServiceException("日期格式不正确！");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

    @Override
    public JSONObject getMonitorTreeNodesAndResource(String edmcNameEn, String searchDate,
                                                     String rootNodeId, String edmcId) {

        JSONObject obj = new JSONObject();

        JSONArray allNode = getMonitorTreeNodes(edmcNameEn, searchDate, rootNodeId);

        obj.put("nodes", allNode);

        if (allNode == null || allNode.isEmpty())
            return obj;

        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < allNode.size(); i++)
            ids.add(allNode.getJSONObject(i).getString(Constant.ID));

        obj.put("resources", getNodeResources(null, ids, edmcId));

        return obj;
    }
}
