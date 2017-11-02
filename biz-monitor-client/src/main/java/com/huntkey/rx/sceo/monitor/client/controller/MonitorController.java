package com.huntkey.rx.sceo.monitor.client.controller;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

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
import com.huntkey.rx.sceo.monitor.client.service.MonitorClient;
import com.huntkey.rx.sceo.monitor.commom.constant.ValidBean;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;

/**
 * Created by zhaomj on 2017/8/11.
 */
@RestController
@Validated
@RequestMapping("/v1/monitors")
public class MonitorController {
    @Autowired
    MonitorClient monitorClient;

    /***
     * 查询监管树临时结构
     * @param tempId 监管树临时单id
     * @param hasResource 是否包含资源
     * @param validDate 日期
     * @return
     */
    @RequestMapping(value = "/tempTree")
    public Result tempTree(@RequestParam(value = "key") @NotBlank(message = "监管树临时单Key不能为空") String key,
                           @RequestParam(value = "validDate", required = false) 
                           @Pattern(regexp=ValidBean.DATE_REGX,message="日期格式不正确") String validDate,
                           @RequestParam(value = "type", defaultValue="1") int type,
                           @RequestParam(value = "flag", defaultValue="false") boolean flag) {
        return monitorClient.tempTree(key, validDate,type,flag);
    }

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
                             @RequestParam(value = "type") @Range(min=1,max=2)  int type){
        return monitorClient.checkOrder(classId,rootId,type);
    }
    
    /**
     * 是否进行上次操作
     * @param key redis的key值
     * @param flag 确认框选择
     * @return
     */
    @RequestMapping(value = "/edit")
    public Result editBefore(@RequestParam(value = "key") @NotBlank(message="redis的Key不能为空") String key,
                             @RequestParam(value = "flag",defaultValue="false") boolean flag){
        return monitorClient.editBefore(key,flag);
    }
    
    /**
     * @param type       1表示新增 2提示界面复制
     * @param beginDate
     * @param endDate
     * @param classId
     * @param rootId
     * @param edmcNameEn
     * @param rootEdmcNameEn
     * @return
     */
    @RequestMapping(value = "/addMonitorTree", method = RequestMethod.POST)
    public Result addMonitorTree(@RequestBody() @Valid AddMonitorTreeTo addMonitorTreeTo) {
        return monitorClient.addMonitorTree(addMonitorTreeTo);
    }

    /**
     * 监管树的维护
     *
     * @param classId    监管类ID
     * @param rootId     根节点
     * @param edmcNameEn edm类型英文名  即监管树实体类表名
     * @return
     */
    @RequestMapping(value = "/treeMaintaince", method = RequestMethod.GET)
    public Result treeMaintaince(@RequestParam(value = "classId") @NotBlank(message = "监管类ID不能为空") String classId,
                                 @RequestParam(value = "rootId") @NotBlank(message = "监管树根节点ID不能为空") String rootId,
                                 @RequestParam(value = "rootEdmcNameEn") @NotBlank(message = "EDM类英文名称不能为空") String rootEdmcNameEn) {
        return monitorClient.treeMaintaince(classId, rootId, rootEdmcNameEn);
    }
    
    
    /***
     * 查询节点详情
     * @param key redis key
     * @param levelCode 节点层及编码
     * @return 节点信息
     * @author fangkun 2017-10-21
     */
    @RequestMapping(value = "/nodeDetail")
    public Result nodeDetail(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
            @RequestParam(value = "lvlCode") @NotBlank(message = "节点层及编码不能为空") String lvlCode) {
        return monitorClient.nodeDetail(key,lvlCode);
    }

    /***
     * 保存节点详情
     * @param 节点详情
     * @return 节点的层级编码
     * @author fangkun
     */
    @RequestMapping(value = "/saveNodeDetail", method = RequestMethod.POST)
    public Result saveNodeDetail(@RequestBody() @Valid NodeTo nodeDetail) {
        return monitorClient.saveNodeDetail(nodeDetail);
    }

    /***
     * 删除节点资源
     * @param key redis key
     * @param lvlCode 节点层及编码
     * @param resourceId 资源ID
     * @return 被删除的节点ID
     */
    @RequestMapping(value = "/deleteNodeResource")
    public Result deleteNodeResource(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                                     @RequestParam(value = "lvlCode") @NotBlank(message = "节点层及编码不能为空")   String lvlCode,
                                     @RequestParam(value = "resourceId") @NotBlank(message = "资源ID不能为空") String resourceId) {
        return monitorClient.deleteNodeResource(key,lvlCode, resourceId);
    }

    /***
     * 添加节点资源
     * @param key redis key
     * @param lvlCode 节点层级编码
     * @param resourceId 资源ID
     * @param resourceText 资源名称  
     * @return 资源ID
     * @author fangkun 2017-10-24 
     */
    @RequestMapping(value = "/addResource")
    public Result addResource(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                              @RequestParam(value = "lvlCode") @NotBlank(message = "节点层级编码不能为空") String lvlCode,
                              @RequestParam(value = "resourceId") @NotBlank(message = "资源ID不能为空") String resourceId,
                              @RequestParam(value = "resourceText") String resourceText) {
        return monitorClient.addResource(key,lvlCode, resourceId,resourceText);
    }

    /****
     * 添加节点
     * @param key redis key
     * @param lvlCode 节点层级编码
     * @return type 添加节点类型
     * @author fangkun 2017-10-24
     */
    @RequestMapping(value = "/addNode")
    public Result addNode(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                          @RequestParam(value = "lvlCode") @NotBlank(message = "节点层及编码不能为空") String lvlCode,
                          @RequestParam(value = "type") @Range(min = 0, max = 2, message = "0：创建子节点 1：创建上节点 2：创建下节点") int type) {
        return monitorClient.addNode(key,lvlCode, type);
    }

    /**
     * 删除节点
     * @param key redis key
     * @param lvlCode 节点层级编码
     * @param type 0代表失效 1代表删除
     * @return
     */
    @RequestMapping(value = "/deleteNode", method = RequestMethod.GET)
    public Result deleteNode(
            @RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
            @RequestParam(value = "lvlCode") @NotBlank(message = "节点层级编码不能为空") String lvlCode,
            @RequestParam(value = "type") @Range(min = 0, max = 1, message = "1：节点删除 0：节点失效") int type) {
        return monitorClient.deleteNode(key,lvlCode, type);
    }

    @RequestMapping(value = "/moveNode", method = RequestMethod.GET)
    public Result moveNode(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                           @RequestParam(value = "moveLvlCode") @NotBlank(message = "节点层级编码不能为空") String moveLvlCode,
                           @RequestParam(value = "desLvlCode") String desLvlCode,
                           @RequestParam(value = "type") int type
    ) {
        return monitorClient.moveNode(key, moveLvlCode, desLvlCode, type);
    }
    
    @RequestMapping(value = "/formula", method = RequestMethod.POST)
    public Result formula(@RequestBody NodeTo node) {
        return monitorClient.formula(node);
    }

}