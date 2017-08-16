package com.huntkey.rx.sceo.monitor.provider.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaomj on 2017/8/9.
 */

@RestController
@RequestMapping("/monitors")
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
    public Result getMonitorTreeNodes(@RequestParam String edmcNameEn,
                                      @RequestParam String searchDate,
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
                                  @RequestParam String edmcNameEn,
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
                                   @RequestParam List<String> nodes,
                                   @RequestParam String edmcId){
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
    public Result getConProperties(@RequestParam(value = "edmcNameEn") String edmcNameEn,
                                   @RequestParam(value = "enable",defaultValue = "true") boolean enable){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getConProperties(edmcNameEn,enable));
        return result;
    }

    @GetMapping("/{edmcNameEn}/newDate")
    public Result getNewMonitorTreeStartDate(@PathVariable(value = "edmcNameEn") String edmcNameEn){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(monitorTreeService.getNewMonitorTreeStartDate(edmcNameEn));
        return result;
    }
}
