package com.huntkey.rx.sceo.monitor.provider.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.method.register.plugin.annotation.MethodRegister;
import com.huntkey.rx.sceo.method.register.plugin.entity.ProgramCate;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;

/**
 * Created by zhaomj on 2017/8/9.
 */

@RestController
@RequestMapping("/monitors")
@Validated
public class MonitorTreeController {
    
    private static final String MONITOR_HISTORY_SET=".moni_his_set";
    
    @Autowired
    MonitorTreeService monitorTreeService;
    
    /**
     * 查询监管树类列表，并根据查询条件统计监管类下监管树的数量
     * @param treeName
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception 
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITOR,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = " 查询监管树类列表，并根据查询条件统计监管类下监管树的数量",
            getReqParamsNameNoPathVariable = {"treeName","beginTime","endTime"}
    )
    @RequestMapping(method = RequestMethod.GET)
    public Result getMonitors(@RequestParam(required = false) String treeName,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) @Pattern(regexp ="([0-9]{4}-[0-9]{2}-[0-9]{2}|$)",message = "日期格式不正确")String endTime) 
                              throws Exception{
        
        Result result = new Result();
        
        result.setRetCode(Result.RECODE_SUCCESS);
        
        result.setData(monitorTreeService.getEntityByVersionAndEnglishName(treeName,beginTime,endTime));
        
        return result;
    }
    
    /**
     * 根据监管类英文名查询监管类下的监管树
     * @param treeName 监管树名称
     * @param edmcNameEn 监管类英文名
     * @param edmId 监管类edmId
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     * @throws Exception 
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITOR,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "根据监管类英文名查询监管类下的监管树",
            getReqParamsNameNoPathVariable = {"treeName","edmcNameEn","edmId","beginTime","endTime"}
    )
    @RequestMapping(value = "/trees",method = RequestMethod.GET)
    public Result getMonitorTrees(@RequestParam(required = false) String treeName,
                                  @RequestParam @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                  @RequestParam @NotBlank(message = "edmId 不能为空") String edmId,
                                  @RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime) throws Exception{
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getMonitorTrees(treeName,edmcNameEn,edmId,beginTime,endTime));
        return result;
    }
    
    /**
     * 查询某个时间的指定监管树所有节点
     * 若有根节点ID则根据根节点ID查询
     * 无根节点ID则先根据时间查询出根节点ID
     * @param rootEdmcNameEn
     * @param searchDate
     * @param rootNodeId
     * @param edmId edm id 模型id
     * @param flag 是否包含节点 true代表包含
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITOR,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "查询某个时间的指定监管树所有节点",
            getReqParamsNameNoPathVariable = {"rootEdmcNameEn","searchDate","rootNodeId","edmId","flag"}
    )
    @RequestMapping(value = "/trees/nodes",method = RequestMethod.GET)
    public Result getMonitorTreeNodes(@RequestParam @NotBlank(message = "类英文名不能为空") String rootEdmcNameEn,
                                      @RequestParam @NotBlank(message = "查询日期不能为空") String searchDate,
                                      @RequestParam(required = false,defaultValue = "") String rootNodeId,
                                      @RequestParam(required = true) String edmId,
                                      @RequestParam(defaultValue = "false") boolean flag)  throws Exception{
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        JSONObject obj = monitorTreeService.getMonitorTreeNodes(rootEdmcNameEn,searchDate,"",rootNodeId);
        JSONArray nodes = obj == null ? null : obj.getJSONArray("nodes");
        
        if(nodes == null || nodes.isEmpty())
            return result;
        
        if(obj.getString("edmName").endsWith(MONITOR_HISTORY_SET)){
            for(int i = 0; i < nodes.size(); i++){
                JSONObject data = nodes.getJSONObject(i);
                data.put("moni_node_no", data.getString("moni_hnode_no"));
                data.put("moni_node_name", data.getString("moni_hnode_name"));
                data.put("moni_node_def", data.getString("moni_hnode_def"));
                data.put("moni_beg", data.get("moni_hbeg"));
                data.put("moni_end", data.get("moni_hend"));
                data.put("moni_index_conf", data.getString("moni_hindex_conf"));
                data.put("moni_lvl_code", data.getString("moni_hlvl_code"));
                data.put("moni_lvl", data.get("moni_hlvl"));
                data.put("moni_relate_cnd", data.getString("moni_hrelate_cnd"));
                data.put("moni_enum", data.get("moni_henum"));
                data.put("moni_major", data.getString("moni_hmajor"));
                data.put("moni_assit", data.getString("moni_hassit"));
                data.put("moni_seq", data.get("moni_hseq"));
                
                data.remove("moni_hnode_no");
                data.remove("moni_hnode_name");
                data.remove("moni_hnode_def");
                data.remove("moni_hbeg");
                data.remove("moni_hend");
                data.remove("moni_hindex_conf");
                data.remove("moni_hlvl_code");
                data.remove("moni_hlvl");
                data.remove("moni_hrelate_cnd");
                data.remove("moni_henum");
                data.remove("moni_hmajor");
                data.remove("moni_hassit");
                data.remove("moni_hseq");
            }
        }
        
        result.setData(obj);
        
        if(!flag)
            return result;
        
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++)
            ids.add(nodes.getJSONObject(i).getString(Constant.ID));
        
        obj.put("resources", monitorTreeService.getNodeResources(null, ids, edmId, obj.getString("edmName"),1));
        
        return result;
    }

    /**
     * 查询指定类的卷积属性清单
     * @param edmcNameEn
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITOR,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "查询指定类的卷积属性清单",
            getReqParamsNameNoPathVariable = {"edmcNameEn","enable"}
    )
    @RequestMapping(value = "/conproperties",method = RequestMethod.GET)
    public Result getConProperties(@RequestParam(value = "edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                   @RequestParam(value = "enable",defaultValue = "true") boolean enable) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getConProperties(edmcNameEn,enable));
        return result;
    }
    
    /**
     * 树新增 最大时间计算
     * @author lijie
     * @param edmcNameEn 树列表中的英文名
     * @param classId 类id
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITOR,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "树新增 最大时间计算",
            getReqParamsNameNoPathVariable = {"edmcNameEn"}
    )
    @RequestMapping(value = "/newDate",method = RequestMethod.GET)
    public Result getNewMonitorTreeStartDate(@RequestParam(value = "edmcNameEn") 
                                             @NotBlank(message = "类英文名不能为空") String edmcNameEn) throws Exception{
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getNewMonitorTreeStartDate(edmcNameEn));
        return result;
    }
    
    @MethodRegister(
            edmClass = Constant.EDM_EMPLOYEE,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "查询员工信息",
            getReqParamsNameNoPathVariable = {"resourceClassId","resourceValue"}
    )
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public Result searchResourceObj(@RequestParam(value = "resourceClassId") @NotBlank(message = "资源类ID不能为空") String resourceClassId,
                                    @RequestParam(value = "resourceValue") @NotBlank(message = "资源对象值不能为空")  String resourceValue) throws Exception{
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.searchResourceObj(resourceClassId,resourceValue));
        return result;
    }
}
