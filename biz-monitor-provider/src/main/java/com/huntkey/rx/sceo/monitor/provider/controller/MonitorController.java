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
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.provider.config.Revoked;
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
                             @RequestParam(value = "type") @Range(min=1,max=2)  int type){
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
    @Revoked
    @RequestMapping(value = "/tempTree")
    public Result tempTree(@RequestParam(value = "key") @Revoked(key="key") String key,
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
    
	/***
	 * 查询节点详情
	 * @param key redis key
	 * @param lvlCode 节点层及编码
	 * @return 节点信息
	 * @author fangkun 2017-10-21
	 */
    @RequestMapping(value = "/nodeDetail")
    public Result nodeDetail(@RequestParam(value = "key") String key,
    		@RequestParam(value = "lvlCode") String lvlCode) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.nodeDetail(key,lvlCode));
        return result;
    }

	/***
	 * 保存节点详情
	 * @param 节点详情
	 * @return 节点的层级编码
	 * @author fangkun
	 */
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value = "/saveNodeDetail", method = RequestMethod.POST)
    public Result saveNodeDetail(@RequestBody @Revoked NodeTo nodeDetail) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.saveNodeDetail(nodeDetail));
        return result;
    }

	/***
	 * 删除节点资源
	 * @param key redis key
	 * @param levelCode 节点层及编码
	 * @param resourceId 资源ID
	 * @return 被删除的节点ID
	 */
    @Revoked(type=OperateType.DETAIL)
    @RequestMapping(value = "/deleteNodeResource")
    public Result deleteNodeResource(@RequestParam(value = "key")  @Revoked(key="key") String key,
    								 @RequestParam(value = "lvlCode")  @Revoked(key="lvlCode") String lvlCode,
                                     @RequestParam(value = "resourceId")   String resourceId) {
    	Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.deleteNodeResource(key, lvlCode,resourceId));
        return result;
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
    @Revoked(type=OperateType.DETAIL)
    @RequestMapping(value = "/addResource")
    public Result addResource(
    		@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") @Revoked(key="key") String key,
            @RequestParam(value = "lvlCode") @NotBlank(message = "节点层及编码不能为空") @Revoked(key="lvlCode") String lvlCode,        
            @RequestParam(value = "resourceId") String resourceId,
            @RequestParam(value = "resourceText") String resourceText) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addResource(key,lvlCode, resourceId,resourceText));
        return result;
    }

	/****
	 * 添加节点
	 * @param key redis key
	 * @param levelCode 节点层级编码
	 * @param type 新增节点类型
	 * @return levelCode 节点层级编码
	 * @author fangkun 2017-10-24
	 */
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value = "/addNode", method = RequestMethod.GET)
    public Result addNode(@RequestParam(value = "key") @Revoked(key="key") String key,
                          @RequestParam(value = "lvlCode")  String lvlCode,
                          @RequestParam(value = "type") int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addNode(key, lvlCode,type));
        return result;
    }

	/****
	 * 删除节点
	 * @param key redis key
	 * @param levelCode 节点层级编码
	 * @param type 删除类型 0 失效 1删除
	 * @return levelCode 节点层级编码
	 * @author fangkun 2017-10-24 
	 */
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value = "/deleteNode", method = RequestMethod.GET)
    public Result deleteNode(@RequestParam(value = "key") @Revoked(key="key") String key,
    						 @RequestParam(value = "lvlCode") String lvlCode,
                             @RequestParam(value = "type") int type) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.deleteNode(key,lvlCode, type));
        return result;
    }
    
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value = "/moveNode", method = RequestMethod.GET)
    public Result moveNode(@RequestParam(value = "key") @Revoked(key="key") String key,
                           @RequestParam(value = "moveLvlCode") String moveLvlCode,
                           @RequestParam(value = "desLvlCode") String desLvlCode,
                           @RequestParam(value = "type") int type
    ) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.moveNode(key, moveLvlCode, desLvlCode, type));
        return result;
    }
}
