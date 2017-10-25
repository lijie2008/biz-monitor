package com.huntkey.rx.sceo.monitor.provider.controller;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;

@RestController
@RequestMapping("/monitors")
@Validated
public class MonitorController {
    
    @Autowired
    MonitorService service;
    
    /**
     * 在树新增、树维护时必须校验临时单中是否存在失效的临时树
     * 如果存在，需要将临时树、节点全部删除
     * @param classId
     * @param rootId
     * @param type 1 - 树新增 、 2 - 树维护 
     * @return
     */
    @RequestMapping(value = "/checkOrder")
    public Result checkOrder(@RequestParam(value = "classId") @NotBlank(message="监管类ID不能为空") String classId,
                             @RequestParam(value = "rootId",defaultValue="") String rootId,
                             @RequestParam(value = "type") @Range(min=1,max=2) @NotBlank(message="操作类型不能为空") int type){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.checkOrder(classId, rootId, type));
        return result;
    }
    
    
    /**
     * 是否进行上次操作
     * @param key redis的key值
     * @param flag 确认框选择
     * @return
     */
    @RequestMapping(value = "/edit")
    public Result editBefore(@RequestParam(value = "key") @NotBlank(message="redis的Key不能为空") String key,
                             @RequestParam(value = "flag",defaultValue="false") @NotBlank(message="用户选择不能为空") boolean flag){
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.editBefore(key, flag));
        return result;
    }
    
    /***
     * 查询监管树临时结构
     * @param tempId 监管树临时单id
     * @param flag 是否包含资源
     * @param validDate 日期
     * @param type 1 - 从redis中查询 2 - 从临时单表查询
     * @return
     */
    @RequestMapping(value = "/tempTree")
    public Result tempTree(@RequestParam(value = "key") String key,
                           @RequestParam(value = "validDate") String validDate, 
                           @RequestParam(value = "type", defaultValue="1") int type,
                           @RequestParam(value = "flag", defaultValue="false") boolean flag) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.tempTree(key, validDate, type, flag));
        return result;
    }
    
    
    /**
     * @param type       1表示新增 2提示界面复制
     * @param beginDate
     * @return
     */
    @RequestMapping(value = "/addMonitorTree", method = RequestMethod.POST)
    public Result addMonitorTree(@RequestBody AddMonitorTreeTo addMonitorTreeTo) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addMonitorTree(addMonitorTreeTo));
        return result;
    }
    
    
    /**
     * 监管树的维护
     *
     * @param classId    监管类ID
     * @param rootId     根节点
     * @param rootEdmcNameEn edm类型英文名  即监管树实体类表名
     * @return
     */
    @RequestMapping(value = "/treeMaintaince")
    public Result treeMaintaince(@RequestParam(value = "classId") String classId,
                                 @RequestParam(value = "rootId") String rootId,
                                 @RequestParam(value = "rootEdmcNameEn") String rootEdmcNameEn) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.treeMaintaince(classId, rootId, rootEdmcNameEn));
        return result;
    }
}
