package com.huntkey.rx.sceo.monitor.provider.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/9.
 */

@RestController
@RequestMapping("/monitors")
@Validated
public class MonitorTreeController {

    @Autowired
    MonitorTreeService monitorTreeService;

    /**
     * 查询某个时间的指定监管树所有节点
     * 若有根节点ID则根据根节点ID查询
     * 无根节点ID则先根据时间查询出根节点ID
     * @param edmcNameEn
     * @param searchDate
     * @param rootNodeId
     * @return
     */
    @GetMapping("/trees/nodes")
    public Result getMonitorTreeNodes(@RequestParam @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                      @RequestParam @NotBlank(message = "查询日期不能为空") String searchDate,
                                      @RequestParam(required = false,defaultValue = "") String rootNodeId){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getMonitorTreeNodes(edmcNameEn,searchDate,rootNodeId));
        return result;
    }

    /**
     * 查询监管树类列表，并根据查询条件统计监管类下监管树的数量
     * @param treeName
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping
    public Result getMonitors(@RequestParam(required = false) String treeName,
                              @RequestParam(required = false) String beginTime,
                              @RequestParam(required = false) String endTime){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getEntityByVersionAndEnglishName(treeName,beginTime,endTime));
        return result;
    }

    /**
     * 根据监管类英文名查询监管类下的监管树
     * @param treeName
     * @param edmcNameEn
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/trees")
    public Result getMonitorTrees(@RequestParam(required = false) String treeName,
                                  @RequestParam @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                  @RequestParam(required = false) String beginTime,
                                  @RequestParam(required = false) String endTime){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getMonitorTrees(treeName,edmcNameEn,beginTime,endTime));
        return result;
    }

    /**
     * 查询监管树节点关联的资源清单
     * @param name
     * @param nodes
     * @param edmcId
     * @return
     */
    @GetMapping("/trees/resources")
    public Result getNodeResources(@RequestParam(required = false) String name,
                                   @RequestParam @NotEmpty(message = "树节点ID集合不能为空") List<String> nodes,
                                   @RequestParam @NotBlank(message = "监管类ID不能为空") String edmcId){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getNodeResources(name,nodes,edmcId));
        return result;
    }

    /**
     * 查询指定类的卷积属性清单
     * @param edmcNameEn
     * @return
     */
    @GetMapping("/conproperties")
    public Result getConProperties(@RequestParam(value = "edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
                                   @RequestParam(value = "enable",defaultValue = "true") boolean enable){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getConProperties(edmcNameEn,enable));
        return result;
    }

    @GetMapping("/newDate")
    public Result getNewMonitorTreeStartDate(@RequestParam(value = "edmcNameEn") @NotBlank(message = "类英文名不能为空") String edmcNameEn,
    		@RequestParam(value="classId") @NotBlank(message="监管类ID不能为空") String classId){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getNewMonitorTreeStartDate(edmcNameEn,classId));
        return result;
    }
    @GetMapping("/search")
    public Result searchResourceObj(@RequestParam(value = "resourceClassId") @NotBlank(message = "资源类ID不能为空") String resourceClassId,
                                    @RequestParam(value = "resourceValue") @NotBlank(message = "资源对象值不能为空")  String resourceValue){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.searchResourceObj(resourceClassId,resourceValue));
        return result;
    }
}
