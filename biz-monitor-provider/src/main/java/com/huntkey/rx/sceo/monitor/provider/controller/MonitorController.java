package com.huntkey.rx.sceo.monitor.provider.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.provider.config.Revoked;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;

@RestController
@RequestMapping("/monitors")
@Validated
public class MonitorController {
    @Autowired
    MonitorService service;

    /***
     * 查询监管树临时结构
     * @param tempId 监管树临时单id
     * @param hasResource 是否包含资源
     * @param validDate 日期
     * @return
     */
    @RequestMapping(value = "/tempTree")
    public Result tempTree(@RequestParam(value = "tempId") String tempId,
                           @RequestParam(value = "validDate") String validDate) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.tempTree(tempId, validDate));
        return result;
    }

    /**
     * 监管树临时单预览 是否需要包含资源
     *
     * @param nodes
     * @return
     */
    @RequestMapping(value = "/resource")
    public Result resource(@RequestParam(value = "nodes") @Size(min = 1)
                                   String[] nodes, @RequestParam(value = "classId") String classId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.resource(nodes, classId));
        return result;
    }

    /**
     * 查询节点详情
     *
     * @param nodeId 节点ID
     * @return
     */
    @RequestMapping(value = "/nodeDetail")
    public Result nodeDetail(@RequestParam(value = "nodeId") String nodeId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.nodeDetail(nodeId));
        return result;
    }

    /**
     * 查询节点关联资源
     *
     * @param nodeId 节点ID
     * @return
     */
    @RequestMapping(value = "/nodeResource")
    public Result nodeResource(@RequestParam(value = "nodeId") String nodeId,
                               @RequestParam(value = "classId") String classId) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.nodeResource(nodeId, classId));
        return result;
    }

    /**
     * 保存节点详情
     *
     * @param nodeId 节点ID
     * @return
     */
    @RequestMapping(value = "/saveNodeDetail", method = RequestMethod.POST)
    public Result saveNodeDetail(@RequestBody NodeTo nodeDetail) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.saveNodeDetail(nodeDetail));
        return result;
    }

    /**
     * 删除节点资源
     *
     * @param nodeId     节点ID
     * @param resourceId 临时单ID
     * @return
     */
    @RequestMapping(value = "/deleteNodeResource")
    public Result deleteNodeResource(@RequestParam(value = "nodeId") String nodeId,
                                     @RequestParam(value = "resourceId") String resourceId) {
        return service.deleteNodeResource(nodeId, resourceId);
    }

    /**
     * 变更公式接口
     *
     * @param nodeId     节点ID
     * @param resourceId 临时单ID
     * @return
     */
    @RequestMapping(value = "/changeFormula", method = RequestMethod.GET)
    public Result changeFormula(@RequestParam(value = "nodeId") String nodeId,
                                @RequestParam(value = "formularId") String formularId) {
        return service.changeFormula(nodeId, formularId);
    }

    /**
     * 新增资源
     *
     * @param nodeId      节点ID
     * @param resourceIds 资源id集合
     * @return
     */
    @RequestMapping(value = "/addResource")
    public Result addResource(@RequestParam(value = "nodeId") @NotBlank(message = "节点ID不能为空")
                                      String nodeId,
                              @RequestParam(value = "resourceIds") @Size(min = 1)
                                      String[] resourceIds) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addResource(nodeId, resourceIds));
        return result;
    }

    /**
     * 新增节点
     *
     * @param nodeId   节点ID
     * @param nodeType 创建节点的类型
     * @return
     */
    @RequestMapping(value = "/addNode", method = RequestMethod.GET)
    public Result addNode(@RequestParam(value = "nodeId") String nodeId,
                          @RequestParam(value = "nodeType")
                                  int nodeType) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addNode(nodeId, nodeType,""));
        return result;
    }

    /**
     * 删除节点
     *
     * @param nodeId 节点ID
     * @param type   0代表失效 1代表删除
     * @return
     */
    @RequestMapping(value = "/deleteNode", method = RequestMethod.GET)
    public Result deleteNode(@RequestParam(value = "nodeId") String nodeId,
                             @RequestParam(value = "type") int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.deleteNode(nodeId, type));
        return result;
    }

    @RequestMapping(value = "/moveNode", method = RequestMethod.GET)
    public Result moveNode(@RequestParam(value = "nodeId") String nodeId,
                           @RequestParam(value = "nodeParentId") String nodeParentId,
                           @RequestParam(value = "nodeLeftId") String nodeLeftId,
                           @RequestParam(value = "nodeRightId") String nodeRightId
    ) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.moveNode(nodeId, nodeParentId, nodeLeftId, nodeRightId));
        return result;
    }

    /**
     * @param type       1表示新增 2提示界面复制
     * @param beginDate
     * @param endDate
     * @param classId
     * @param treeId
     * @param edmcNameEn
     * @return
     */
    @Revoked(type=OperateType.INITIALIZE)
    @RequestMapping(value = "/addMonitorTree", method = RequestMethod.POST)
    public Result addMonitorTree(@RequestBody AddMonitorTreeTo addMonitorTreeTo
    ) {
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
     * @param edmcNameEn edm类型英文名  即监管树实体类表名
     * @return
     */
    @RequestMapping(value = "/treeMaintaince")
    public Result treeMaintaince(@RequestParam(value = "classId") String classId,
                                 @RequestParam(value = "rootId") String rootId,
                                 @RequestParam(value = "edmcNameEn") String edmcNameEn) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.treeMaintaince(classId, rootId, edmcNameEn));
        return result;
    }

}
