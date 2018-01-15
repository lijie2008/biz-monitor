package com.huntkey.rx.sceo.monitor.provider.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.edm.entity.MoniMoniHisSetaEntity;
import com.huntkey.rx.edm.entity.MoniMoniHresSetbEntity;
import com.huntkey.rx.edm.entity.MoniMoniResSetaEntity;
import com.huntkey.rx.edm.entity.MonitorEntity;
import com.huntkey.rx.edm.entity.MonitortreeEntity;
import com.huntkey.rx.edm.entity.MtorMtorResSetbEntity;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.exception.ServiceException;
import com.huntkey.rx.sceo.monitor.provider.controller.client.ModelerClient;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import com.huntkey.rx.sceo.orm.common.model.OrmParam;
import com.huntkey.rx.sceo.orm.common.type.SQLSortEnum;
import com.huntkey.rx.sceo.orm.common.type.SQLSymbolEnum;
import com.huntkey.rx.sceo.orm.common.util.EdmUtil;
import com.huntkey.rx.sceo.orm.service.OrmService;

/**
 * Created by zhaomj on 2017/8/9.
 */
@Service
public class MonitorTreeServiceImpl implements MonitorTreeService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorTreeServiceImpl.class);
    
    @Autowired
    ModelerClient modelerClient;
    
    @Autowired
    OrmService ormService;
    
    @Value("${edm.version}")
    private String edmdVer;

    @Value("${edm.edmcNameEn.monitor}")
    private String monitorEdmcNameEn;
    
    @Override
    public Result getEntityByVersionAndEnglishName(String treeName, String beginTime,String endTime) throws Exception {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        JSONArray monitorClasses = new JSONArray();
        
        Result monitorClassResult = modelerClient.getEntityByVersionAndEnglishName(edmdVer, monitorEdmcNameEn);

        if (monitorClassResult.getRetCode() != Result.RECODE_SUCCESS) {
            logger.error("根据类英文名和版本号查询类所有实体子类接口调用出错！");
            throw new ServiceException("根据类英文名和版本号查询类所有实体子类接口调用出错！");
        }
        
        if (monitorClassResult.getData() == null) 
            return result;

        JSONArray edmClasses = JSONArray.parseArray(JSONObject.toJSONString(monitorClassResult.getData()));

        for (int i = 0; i < edmClasses.size(); i++) {
            
            JSONObject tempEdm = edmClasses.getJSONObject(i);
            String searchEdmName = tempEdm.getString("edmcNameEn");

            JSONObject temp = new JSONObject();
            temp.put("id", tempEdm.getString("id"));
            temp.put("edmcName", tempEdm.getString("edmcName"));
            temp.put("edmcEdmdId", tempEdm.getString("edmcEdmdId"));
            temp.put("edmcNameEn", searchEdmName);
            
            Result resourcesResult = modelerClient.getPropertyValue(tempEdm.getString("id"),
                    Constant.MONITOR_CLASS_PROP_RESOURCES);

            if (resourcesResult.getRetCode() != Result.RECODE_SUCCESS) {
                logger.error("根据监管类查询从属资源类失败！");
                throw new ServiceException("根据监管类查询从属资源类失败！");
            }
            
            if (resourcesResult.getData() != null) {
                JSONObject jsonObject = JSONObject
                        .parseObject(JSON.toJSONString(resourcesResult.getData()));
                String dataType = jsonObject.getString("dataType");
                if ("class".equals(dataType)) {
                    JSONObject value = jsonObject.getJSONObject("value");
                    temp.put("resourceName", value.getString("edmcName"));
                    temp.put("resourceEdmNameEn", value.getString("edmcNameEn"));
                    temp.put("resourceEdmId", value.getString("id"));
                }
            }
            
            OrmParam ormParam = new OrmParam();
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            ormParam.setWhereExp(ormParam.getEqualXML("motr_edm_id",temp.getString(Constant.ID)));
            
            if (!StringUtil.isNullOrEmpty(endTime)) 
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getGreaterThanAndEqualXML("motr_end", endTime), 
                        ormParam.getLessThanAndEqualXML("motr_beg", endTime)));
            
            List<MonitortreeEntity> vvList = ormService.selectBeanList(MonitortreeEntity.class, ormParam);
            
            if(vvList == null || vvList.size() == 0){
                temp.put("count", 0);
                monitorClasses.add(temp);
                continue;
            }
            
            // 查询监管树正式表
            ormParam.reset();
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            ormParam.setWhereExp(OrmParam.and(ormParam.getEqualXML("moni_lvl_code", Constant.ROOT_LVL_CODE), 
                    ormParam.getEqualXML("moni_lvl", Constant.ROOT_LVL)));
            if (!StringUtil.isNullOrEmpty(treeName))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), ormParam.getMatchMiddleXML("moni_node_name", treeName)));
            
            if (!StringUtil.isNullOrEmpty(endTime)) 
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_beg", endTime), 
                        ormParam.getGreaterThanAndEqualXML("moni_end", endTime)));
            
            @SuppressWarnings("rawtypes")
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(searchEdmName));
            
            @SuppressWarnings("unchecked")
            List<? extends MonitorEntity> list = ormService.selectBeanList(cls, ormParam);
            
            temp.put("count", list == null ? 0 : list.size());
            
            // 查询监管树历史属性集
            ormParam.reset();
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            ormParam.setWhereExp(OrmParam.and(ormParam.getEqualXML("moni_hlvl_code", Constant.ROOT_LVL_CODE), 
                    ormParam.getEqualXML("moni_hlvl", Constant.ROOT_LVL),ormParam.getEqualXML("classname", searchEdmName)));
            if (!StringUtil.isNullOrEmpty(treeName))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), ormParam.getMatchMiddleXML("moni_hnode_name", treeName)));
            
            if (!StringUtil.isNullOrEmpty(endTime)) 
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_hbeg", endTime), 
                        ormParam.getGreaterThanAndEqualXML("moni_hend", endTime)));
            
            List<MoniMoniHisSetaEntity> list1 = ormService.selectBeanList(MoniMoniHisSetaEntity.class, ormParam);
            
            temp.put("count", temp.getInteger("count") + (list1 == null ? 0 : list1.size()));
            
            monitorClasses.add(temp);
        }
        
        result.setData(monitorClasses);
        
        return result;
    }
    
    
    @Override
    public JSONArray getMonitorTrees(String treeName, String edmcNameEn,
                                     String edmId, String beginTime,
                                     String endTime) throws Exception {
        
        JSONArray monitorTrees = new JSONArray();
        
        // 根据edmId endTime 查询出所有的版本信息
        JSONArray versions = new JSONArray();
        
        OrmParam ormParam = new OrmParam();
        
        ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        ormParam.setOrderExp(SQLSortEnum.DESC, "motr_beg");
        
        ormParam.setWhereExp(ormParam.getEqualXML("motr_edm_id", edmId));
                
        if (!StringUtil.isNullOrEmpty(endTime))
            ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                    ormParam.getLessThanAndEqualXML("motr_beg", endTime),
                    ormParam.getGreaterThanAndEqualXML("motr_end", endTime)));
        
        List<MonitortreeEntity> vvList = ormService.selectBeanList(MonitortreeEntity.class, ormParam);
        
        if(vvList == null || vvList.isEmpty())
            return null;
        
        for(MonitortreeEntity vv : vvList){
            JSONObject version = new JSONObject();
            version.put("versionCode", vv.getMotr_ver_code());
            version.put("beginTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(vv.getMotr_beg()));
            version.put("endTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(vv.getMotr_end()));
            version.put("rootNodeId", vv.getMotr_root_id());
            versions.add(version);
        }
        
        
        for(int i = 0 ; i < versions.size(); i++){
            
            JSONObject version = versions.getJSONObject(i);
            
            // 根据版本信息去 监管树正式表查询
            ormParam.reset();
            
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            
            ormParam.addOrderExpElement(SQLSortEnum.DESC, "moni_beg")
                    .addOrderExpElement(SQLSortEnum.ASC, "moni_end");
            
            ormParam.setWhereExp(OrmParam.and(ormParam.getEqualXML("moni_lvl_code", Constant.ROOT_LVL_CODE), 
                    ormParam.getEqualXML("moni_lvl", Constant.ROOT_LVL)));
            
            if(!StringUtil.isNullOrEmpty(treeName))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), ormParam.getMatchMiddleXML("moni_node_name", treeName)));
            
            if(!StringUtil.isNullOrEmpty(endTime))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_beg", endTime), ormParam.getGreaterThanAndEqualXML("moni_end", endTime)));
            else
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_end", versions.getJSONObject(i).getString("endTime")+Constant.ENDTIME),
                        ormParam.getGreaterThanAndEqualXML("moni_beg", versions.getJSONObject(i).getString("beginTime") + Constant.STARTTIME)));
            
            @SuppressWarnings("rawtypes")
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(edmcNameEn));
            
            @SuppressWarnings("unchecked")
            List<? extends MonitorEntity> list = ormService.selectBeanList(cls, ormParam);
            
            if(list != null && !list.isEmpty()){
                for(MonitorEntity me : list){
                    JSONObject tree = new JSONObject();
                    tree.put("rootNodeId", me.getId());
                    tree.put("rootNodeName", me.getMoni_node_name());
                    tree.put("beginTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(me.getMoni_beg()));
                    tree.put("endTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(me.getMoni_end()));
                    
                    tree.put("rootEdmcNameEn", edmcNameEn);
                    
                    JSONArray rootNodes = version.getJSONArray("rootNodes") == null ? new JSONArray():version.getJSONArray("rootNodes");
                    rootNodes.add(tree);
                    
                    version.put("rootNodes", rootNodes);

                    if(version.getString("rootNodeId").equals(me.getId()))
                        version.put("rootNodeName", me.getMoni_node_name());
                }
            }
            
            // 根据版本信息去 监管树历史表查询
            ormParam.reset();
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            ormParam.addOrderExpElement(SQLSortEnum.DESC, "moni_hbeg")
            .addOrderExpElement(SQLSortEnum.ASC, "moni_hend");
            
            ormParam.setWhereExp(OrmParam.and(ormParam.getEqualXML("moni_hlvl_code", Constant.ROOT_LVL_CODE), 
                    ormParam.getEqualXML("moni_hlvl", Constant.ROOT_LVL), ormParam.getEqualXML("classname", edmcNameEn)));
            
            if(!StringUtil.isNullOrEmpty(treeName))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), ormParam.getMatchMiddleXML("moni_hnode_name", treeName)));
            
            if(!StringUtil.isNullOrEmpty(endTime))
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_hbeg", endTime), ormParam.getGreaterThanAndEqualXML("moni_hend", endTime)));
            else
                ormParam.setWhereExp(OrmParam.and(ormParam.getWhereExp(), 
                        ormParam.getLessThanAndEqualXML("moni_hend", versions.getJSONObject(i).getString("endTime")+Constant.ENDTIME),
                        ormParam.getGreaterThanAndEqualXML("moni_hbeg", versions.getJSONObject(i).getString("beginTime") + Constant.STARTTIME)));
            
            List<MoniMoniHisSetaEntity> hList = ormService.selectBeanList(MoniMoniHisSetaEntity.class, ormParam);
            
            if(hList != null && !hList.isEmpty()){
                for(MoniMoniHisSetaEntity his : hList){
                    
                    JSONObject tree = new JSONObject();
                    
                    tree.put("rootNodeId", his.getId());
                    tree.put("rootNodeName", his.getMoni_hnode_name());
                    tree.put("beginTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(his.getMoni_hbeg()));
                    tree.put("endTime", new SimpleDateFormat(Constant.YYYY_MM_DD).format(his.getMoni_hend()));
                    
                    tree.put("rootEdmcNameEn", edmcNameEn+ "." + Constant.MONITOR_HISTORY_SET );
                    
                    JSONArray rootNodes = version.getJSONArray("rootNodes") == null ? new JSONArray():version.getJSONArray("rootNodes");
                    rootNodes.add(tree);
                    
                    version.put("rootNodes", rootNodes);

                    if(version.getString("rootNodeId").equals(his.getId()))
                        version.put("rootNodeName", his.getMoni_hnode_name());
                }
            }
            
            version.put("count",version.getJSONArray("rootNodes") == null ? 0 : version.getJSONArray("rootNodes").size() );
            
            // 当前版本下没有满足条件的树
            if(version.getInteger("count") == 0)
                continue;
                
            // 如果查历史树 导致版本树没有名称 - 赋值第一颗树的名称
            if(version.getInteger("count") != 0 && 
                    StringUtil.isNullOrEmpty(version.getString("rootNodeName")))
                version.put("rootNodeName", version.getJSONArray("rootNodes").getJSONObject(0).getString("rootNodeName"));
            
            monitorTrees.add(version);
        }
        
        return monitorTrees;
    }
    
    @Override
    public JSONObject getMonitorTreeNodes(String rootEdmcNameEn, String startDate, String endDate, String rootNodeId) throws Exception {
        
        JSONObject nodeRet = new JSONObject();
        
        startDate = startDate + Constant.STARTTIME;
        endDate = endDate + Constant.ENDTIME;
    	
        if(StringUtil.isNullOrEmpty(rootNodeId)){
            
            JSONArray nodes = getNodes(rootEdmcNameEn,startDate, endDate,rootNodeId);
            
            if(nodes == null || nodes.isEmpty()){
                rootEdmcNameEn = rootEdmcNameEn+"."+Constant.MONITOR_HISTORY_SET;
                nodes = getHisNodes(rootEdmcNameEn,startDate, endDate,rootNodeId);
            }
            
            nodeRet.put("nodes", nodes);
            
        }else{
            
            if(rootEdmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET))
                nodeRet.put("nodes", getHisNodes(rootEdmcNameEn,startDate, endDate,rootNodeId));
            else
                nodeRet.put("nodes", getNodes(rootEdmcNameEn,startDate, endDate,rootNodeId));
        }
        
        nodeRet.put("edmName", rootEdmcNameEn);
        
        return nodeRet;
    }

    private JSONArray getHisNodes(String rootEdmcNameEn, String startDate, String endDate,
                             String rootNodeId) throws Exception {
        OrmParam param = new OrmParam();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        param.setWhereExp(param.getEqualXML("classname", rootEdmcNameEn.split("\\.")[0]));
        
        // 查询出当前树的根节点信息
        if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME))
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getLessThanAndEqualXML("moni_hbeg", startDate),
                                           param.getGreaterThanXML("moni_hend", startDate)));
        else
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getGreaterThanAndEqualXML("moni_hbeg", startDate), 
                                           param.getLessThanAndEqualXML("moni_hend", endDate)));
        
        if (StringUtil.isNullOrEmpty(rootNodeId))
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getEqualXML("moni_hlvl", Constant.ROOT_LVL),
                                           param.getEqualXML("moni_hlvl_code", Constant.ROOT_LVL_CODE)));
        else
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getEqualXML(Constant.ID, rootNodeId)));
        
        List<MoniMoniHisSetaEntity> rootList = ormService.selectBeanList(MoniMoniHisSetaEntity.class, param);
        
        if(rootList == null || rootList.isEmpty())
            return null;
        
        if(rootList.size() > 1)
            throw new ServiceException("监管树历史表数据异常，同一时间找到多个历史监管树！");
        
        param.reset();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        param.setWhereExp(param.getEqualXML("classname", rootEdmcNameEn.split("\\.")[0]));
        
        if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME))
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getLessThanAndEqualXML("moni_hbeg", startDate), 
                                           param.getGreaterThanXML("moni_hend", startDate)));
        else
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getGreaterThanAndEqualXML("moni_hbeg", startDate), 
                                           param.getLessThanAndEqualXML("moni_hend", endDate)));
        
        param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                       param.getMatchLeftXML("moni_hlvl_code", Constant.ROOT_LVL_CODE)));
        
        param.addOrderExpElement(SQLSortEnum.ASC, "moni_hlvl")
        .addOrderExpElement(SQLSortEnum.ASC, "moni_hlvl_code");
        
        List<MoniMoniHisSetaEntity> nodes = ormService.selectBeanList(MoniMoniHisSetaEntity.class, param);
        
        return JSON.parseArray(JSON.toJSONString(nodes));
    }


    private JSONArray getNodes(String rootEdmcNameEn, String startDate, String endDate,
                          String rootNodeId) throws Exception {
        
        OrmParam param = new OrmParam();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        // 查询出当前树的根节点信息
        if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME))
            param.setWhereExp(OrmParam.and(param.getLessThanAndEqualXML("moni_beg", startDate),
                                           param.getGreaterThanXML("moni_end", startDate)));
        else
            param.setWhereExp(OrmParam.and(param.getGreaterThanAndEqualXML("moni_beg", startDate), 
                                           param.getLessThanAndEqualXML("moni_end", endDate)));
        
        if (StringUtil.isNullOrEmpty(rootNodeId))
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                    param.getEqualXML("moni_lvl", Constant.ROOT_LVL),
                    param.getEqualXML("moni_lvl_code", Constant.ROOT_LVL_CODE)));
        else
            param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                           param.getEqualXML(Constant.ID, rootNodeId)));
        
        param.addOrderExpElement(SQLSortEnum.ASC, "moni_lvl")
        .addOrderExpElement(SQLSortEnum.ASC, "moni_lvl_code");
        
        @SuppressWarnings("rawtypes")
        Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(rootEdmcNameEn));
        
        @SuppressWarnings("unchecked")
        List<? extends MonitorEntity> rootList = ormService.selectBeanList(cls, param);
        
        if(rootList == null || rootList.isEmpty())
            return null;
        
        if(rootList.size() > 1)
            throw new ServiceException("监管树正式表数据异常，同一时间找到多个监管树！");
        
        param.reset();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        if(StringUtil.isNullOrEmpty(endDate) || endDate.startsWith(Constant.ENDTIME))
            param.setWhereExp(OrmParam.and(param.getLessThanAndEqualXML("moni_beg", startDate), 
                                           param.getGreaterThanXML("moni_end", startDate)));
        else
            param.setWhereExp(OrmParam.and(param.getGreaterThanAndEqualXML("moni_beg", startDate), 
                                           param.getLessThanAndEqualXML("moni_end", endDate)));
        
        param.setWhereExp(OrmParam.and(param.getWhereExp(), 
                                       param.getMatchLeftXML("moni_lvl_code", Constant.ROOT_LVL_CODE)));
        
        param.addOrderExpElement(SQLSortEnum.ASC, "moni_lvl")
        .addOrderExpElement(SQLSortEnum.ASC, "moni_lvl_code");

        @SuppressWarnings("unchecked")
        List<? extends MonitorEntity> nodes = ormService.selectBeanList(cls, param);
        
        return JSON.parseArray(JSON.toJSONString(nodes));
    }


    @Override
    public JSONArray getNodeResources(String name, List<String> nodes, String edmId,String edmName,int type) throws Exception{
        
        JSONArray resources = new JSONArray();

        String edmcNameEn = "";
        
        if(!StringUtil.isNullOrEmpty(edmName)){
            edmcNameEn = edmName;
        }else{
            Result edmResult = modelerClient.getEdmByid(edmId);
            
            if (edmResult.getRetCode() != Result.RECODE_SUCCESS) {
                logger.error("根据id查询EDM类信息失败！");
                throw new ServiceException("根据id查询EDM类信息失败！");
            }
            if (edmResult.getData() == null) {
                logger.error("未找到对应的EMD类信息！ID：{0}",edmId);
                throw new ServiceException("未找到对应的EMD类信息！ID："+ edmId);
            }
            
            JSONObject edmInfo = JSONObject.parseObject(JSON.toJSONString(edmResult.getData()));
            edmcNameEn = edmInfo.getString("edmcNameEn");
        }
        
        if (StringUtil.isNullOrEmpty(edmcNameEn)) {
            logger.error("EDM类信息中未找到类英文名！");
            throw new ServiceException("EDM类信息中未找到类英文名！");
        }
        
        
        // 循环查询资源信息
        OrmParam param = new OrmParam();
        
        for (int i = 0; i < nodes.size(); i++) {
            param.reset();
            // 资源id集合
            List<MoniMoniResSetaEntity> reIds = new ArrayList<MoniMoniResSetaEntity>();
            
            List<String> ids = new ArrayList<String>();
            
            if(type == 1){
                if(edmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
                    param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
                    
                    param.setWhereExp(OrmParam.and(param.getEqualXML("classname", edmcNameEn.split("\\.")[0]),
                                                   param.getEqualXML(Constant.PID, nodes.get(i))));
                    
                    List<MoniMoniHresSetbEntity> hRes = ormService.selectBeanList(MoniMoniHresSetbEntity.class, param);
                    if(hRes != null && !hRes.isEmpty())
                        hRes.stream().forEach(s->{
                            ids.add(s.getMoni_hres_id());
                            MoniMoniResSetaEntity re = new MoniMoniResSetaEntity();
                            re.setId(s.getId());
                            re.setMoni_res_id(s.getMoni_hres_id());
                            reIds.add(re);
                        
                        });
                    
                }else{
                    param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
                    param.setWhereExp(OrmParam.and(param.getEqualXML("classname", edmcNameEn.split("\\.")[0]),
                                                   param.getEqualXML(Constant.PID, nodes.get(i))));
                    
                    List<MoniMoniResSetaEntity> hRes = ormService.selectBeanList(MoniMoniResSetaEntity.class, param);
                    if(hRes != null && !hRes.isEmpty())
                        hRes.stream().forEach(s->{
                            ids.add(s.getMoni_res_id());
                            MoniMoniResSetaEntity re = new MoniMoniResSetaEntity();
                            re.setId(s.getId());
                            re.setMoni_res_id(s.getMoni_res_id());
                            reIds.add(re);
                        
                        });
                }
            }else if(type == 2){
                param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
                param.setWhereExp(param.getEqualXML(Constant.PID, nodes.get(i)));
                
                List<MtorMtorResSetbEntity> res = ormService.selectBeanList(MtorMtorResSetbEntity.class, param);
                
                if(res != null && !res.isEmpty())
                    res.stream().forEach(s->{
                        ids.add(s.getMtor_res_id());
                        MoniMoniResSetaEntity re = new MoniMoniResSetaEntity();
                        re.setId(s.getId());
                        re.setMoni_res_id(s.getMtor_res_id());
                        reIds.add(re);
                    });
            }else
                new ServiceException("查询标志type不正确");
            
            if(ids == null || ids.isEmpty())
                continue;
            
            JSONArray nodeRes = getResourceTxt(edmId, ids, null, nodes.get(i));
            
            if(nodeRes == null || nodeRes.isEmpty())
                continue;
            
            for(MoniMoniResSetaEntity rr : reIds){
                String res_id = rr.getMoni_res_id();
                for(int j = 0; j < nodeRes.size(); j++){
                    JSONObject nn = nodeRes.getJSONObject(j);
                    String r_id = nn.getString("id");
                    if(res_id.equals(r_id)){
                        JSONObject obj = (JSONObject)nn.clone();
                        obj.put("oid", rr.getId());
                        resources.add(obj);
                        break;
                    }
                }
            }
        }
        return resources;
    }

    
    
    @SuppressWarnings("rawtypes")
    private JSONArray getResourceTxt(String edmId, List<String> ids, String name, String nodeId) throws Exception {
        
        if(ids == null || ids.isEmpty())
            return null;
        
        JSONArray resources = new JSONArray();
        
        // 根据资源ID查询资源，先找对应的资源表
        Result resourcesResult = modelerClient.getPropertyValue(edmId, Constant.MONITOR_CLASS_PROP_RESOURCES);

        if (resourcesResult.getRetCode() != Result.RECODE_SUCCESS) {
            logger.error("根据监管类查询从属资源类失败！请检查Modeler服务！");
            throw new ServiceException("根据监管类查询从属资源类失败！请检查Modeler服务！");
        }
        
        if (resourcesResult.getData() == null) {
            logger.error("未找到资监管类的从属资源类型，请检查是否已设置！");
            throw new ServiceException("未找到资监管类的从属资源类型，请检查是否已设置！");
        }

        JSONObject resourceEdmObj = JSONObject.parseObject(JSON.toJSONString(resourcesResult.getData()));
        String dataType = resourceEdmObj.getString("dataType");
        
        if (!"class".equals(dataType)) {
            logger.error("资监管类的从属资源值不是EDM类，请检查！");
            throw new ServiceException("资监管类的从属资源值不是EDM类，请检查！");
        }
        
        JSONObject value = resourceEdmObj.getJSONObject("value");
        String resourcesEdmName = value.getString("edmcNameEn");

        Result result = modelerClient.getCharacterAndFormat(value.getString("id"));
        
        if (result.getRetCode() != Result.RECODE_SUCCESS) {
            logger.error("modelerClient getCharacterAndFormat fail");
            throw new ServiceException("调用 Modeler 类特征值显示格式查询接口失败！");
        }
        
        JSONObject characterObj = (JSONObject) JSONObject.toJSON(result.getData());
        
        if(characterObj != null && characterObj.containsKey("character") && characterObj.containsKey("format")){
            JSONArray characterArray = characterObj.getJSONArray("character");
            String format = characterObj.getString("format");
            String[] resourceFields = new String[characterArray.size()];
            characterArray.toArray(resourceFields);
            
            OrmParam ormParam = new OrmParam();
            ormParam.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            ormParam.setWhereExp(ormParam.getInXML(Constant.ID, ids.toArray()));
            
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(resourcesEdmName));
            
            @SuppressWarnings("unchecked")
            List resourceObjs = ormService.selectBeanList(cls, ormParam);
            
            // 根据字段名 组装资源信息
            for(Object obj : resourceObjs){
                
                JSONObject resourceObj = JSONObject.parseObject(JSON.toJSONString(obj));
                String edmObjName = format.toLowerCase();
                
                for (String fieldName : resourceFields) {
                    String f_str = StringUtil.isNullOrEmpty(resourceObj.getString(fieldName))?"":resourceObj.getString(fieldName);
                    edmObjName = edmObjName.replace(fieldName,f_str);
                }
                
                if(!StringUtil.isNullOrEmpty(nodeId))
                    resourceObj.put("nodeId", nodeId);
                
                if(StringUtil.isNullOrEmpty(name) || edmObjName.contains(name))
                    resourceObj.put("text",edmObjName);
                resources.add(resourceObj);
            }
        }else{
            throw new ServiceException("未发现“"+value.getString("edmcName")+"”的显示特征值！请前往modeler设置！");
        }
        return resources;
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
    public JSONObject getNewMonitorTreeStartDate(String edmcNameEn) throws Exception{

        JSONObject resultData = new JSONObject();
        
        OrmParam param = new OrmParam();
        
        String lastDate = "";
        
        if(edmcNameEn.endsWith(Constant.MONITOR_HISTORY_SET)){
            param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            param.setWhereExp(OrmParam.and(param.getEqualXML("classname", edmcNameEn.split("\\.")[0]), 
                                           param.getEqualXML("moni_hlvl_code", Constant.ROOT_LVL_CODE),
                                           param.getEqualXML("moni_hlvl", Constant.ROOT_LVL)));
            
            param.setOrderExp(SQLSortEnum.DESC, "moni_hend");
            
            param.setPageNo(1);
            param.setPageSize(1);
            
            List<MoniMoniHisSetaEntity> hNode = ormService.selectBeanList(MoniMoniHisSetaEntity.class, param);
            
            // 当前edmName里没有树
            if(hNode == null || hNode.isEmpty()){
                resultData.put("type", 2);
                return resultData;
            }else{
                lastDate = (new SimpleDateFormat(Constant.YYYY_MM_DD).format(hNode.get(0).getMoni_hend()));
            }
        }else{
            param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
            param.setWhereExp(OrmParam.and(param.getEqualXML("moni_lvl_code", Constant.ROOT_LVL_CODE),
                                           param.getEqualXML("moni_lvl", Constant.ROOT_LVL)));
            
            param.setOrderExp(SQLSortEnum.DESC, "moni_end");
            
            param.setPageNo(1);
            param.setPageSize(1);
            
            @SuppressWarnings("rawtypes")
            Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(edmcNameEn));
            
            @SuppressWarnings("unchecked")
            List<? extends MonitorEntity> hNode = ormService.selectBeanList(cls, param);
            
            // 当前edmName里没有树
            if(hNode == null || hNode.isEmpty()){
                resultData.put("type", 2);
                return resultData;
            }else{
                lastDate = (new SimpleDateFormat(Constant.YYYY_MM_DD).format(hNode.get(0).getMoni_end()));
            }
        }
        
        if(StringUtil.isNullOrEmpty(lastDate)){
            resultData.put("type", 2);
            return resultData;
        }
        
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
            
        } catch (ParseException e){
            throw new ServiceException("日期格式不正确！");
        }
    }

    @Override
    public JSONArray searchResourceObj(String resourceClassId, String resourceValue) throws Exception{
        
        Result result = modelerClient.getEdmByid(resourceClassId);
        Result edmResult = modelerClient.getEdmByid(resourceClassId);
        
        if (edmResult.getRetCode() != Result.RECODE_SUCCESS) {
            logger.error("根据id查询EDM类信息失败！");
            throw new ServiceException("根据id查询EDM类信息失败！");
        }
   
        if (edmResult.getData() == null) {
            logger.error("未找到对应的EMD类信息！ID：{0}",resourceClassId);
            throw new ServiceException("未找到对应的EMD类信息！ID："+resourceClassId);
        }
   
        JSONObject edmInfo = JSONObject.parseObject(JSON.toJSONString(edmResult.getData()));
        String edmcNameEn = edmInfo.getString("edmcNameEn");
   
        if (StringUtil.isNullOrEmpty(edmcNameEn)) {
            logger.error("EDM类信息中未找到类英文名！");
            throw new ServiceException("EDM类信息中未找到类英文名！");
        }
   
        String resourceEdmName = edmcNameEn.toLowerCase().replace(" ","");
        
        String[] resourceFields = null;
        JSONArray resourceObjList = null;
        
        result = modelerClient.getCharacterAndFormat(edmInfo.getString("id"));
        
        if (result.getRetCode() != Result.RECODE_SUCCESS){
            logger.error("操作moderler:获取类的特征值及显示格式");
            throw new ServiceException("操作moderler:获取类的特征值及显示格式");
        }
        JSONObject characterObj = (JSONObject)JSONObject.toJSON(result.getData());
        JSONArray characterArray = characterObj.getJSONArray("character");
        String format = characterObj.getString("format");
        if (characterArray == null || characterArray.size() < 0){
            logger.error("未找到显示格式");
            throw new ServiceException("未找到显示格式");
        }
        resourceFields = new String[characterArray.size()];
        characterArray.toArray(resourceFields);
        Arrays.stream(resourceFields).map(field -> field.toLowerCase()).collect(Collectors.toList()).toArray(resourceFields);
        JSONArray resourceObjDetailList = selectEdmObjList(resourceEdmName, resourceFields, resourceValue);
        resourceObjList = new JSONArray();
        for (Object obj : resourceObjDetailList){
            JSONObject json=(JSONObject) JSONObject.toJSON(obj);
            String edmObjName = format.toLowerCase();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", json.getString("id"));
            for (String fieldName : resourceFields){
                edmObjName = edmObjName.replace(fieldName, json.getString(fieldName));
            }
            jsonObject.put("name", edmObjName);
            resourceObjList.add(jsonObject);
        }
        return resourceObjList.size() > 0 ? resourceObjList : null;
    }
    
    public JSONArray selectEdmObjList(String edmName, String[] fields, String fieldValue) throws Exception{
        //根据查询条件查询授权对象已授权的资源配置集合
        JSONArray resourceObjList = new JSONArray();
        
        OrmParam param = new OrmParam();
        
        param.addColumn(SQLSymbolEnum.ALLCOLUMNS.getSymbol());
        
        if(fields == null || fields.length <= 0) // 精确查找
            param.setWhereExp(param.getEqualXML(Constant.ID, fieldValue));
        else{ // 模糊查找
            for (String fieldName : fields){
                if(StringUtil.isNullOrEmpty(param.getWhereExp()))
                    param.setWhereExp(param.getMatchMiddleXML(fieldName, fieldValue));
                else
                    param.setWhereExp(OrmParam.or(param.getWhereExp(), param.getMatchMiddleXML(fieldName, fieldValue)));
            }
        }
        
        @SuppressWarnings("rawtypes")
        Class cls = Class.forName(Constant.ENTITY_PATH + EdmUtil.convertClassName(edmName));
        
        @SuppressWarnings("unchecked")
        List<?> objs = ormService.selectBeanList(cls, param);
        
        if(objs != null && !objs.isEmpty())
            resourceObjList.addAll(objs);
        
        return resourceObjList;
    }
    
}
