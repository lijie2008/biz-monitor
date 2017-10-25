package com.huntkey.rx.sceo.monitor.client.controller;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.MonitorClient;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

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
	 * 查询节点详情
	 * @param key redis key
	 * @param levelCode 节点层及编码
	 * @return 节点信息
	 * @author fangkun 2017-10-21
	 */
    @RequestMapping(value = "/nodeDetail")
    public Result nodeDetail(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
    		@RequestParam(value = "levelCode") @NotBlank(message = "节点层及编码不能为空") String levelCode) {
        return monitorClient.nodeDetail(key,levelCode);
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
	 * @param levelCode 节点层及编码
	 * @param resourceId 资源ID
	 * @return 被删除的节点ID
	 */
    @RequestMapping(value = "/deleteNodeResource")
    public Result deleteNodeResource(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
    								 @RequestParam(value = "levelCode") @NotBlank(message = "节点层及编码不能为空")	String levelCode,
                                     @RequestParam(value = "resourceId") @NotBlank(message = "资源ID不能为空") String resourceId) {
        return monitorClient.deleteNodeResource(key,levelCode, resourceId);
    }

	/***
	 * 添加节点资源
	 * @param key redis key
	 * @param levelCode 节点层级编码
	 * @param resourceId 资源ID
	 * @param resourceText 资源名称  
	 * @return 资源ID
	 * @author fangkun 2017-10-24 
	 */
    @RequestMapping(value = "/addResource")
    public Result addResource(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
    						  @RequestParam(value = "levelCode") @NotBlank(message = "节点层级编码不能为空") String levelCode,
    						  @RequestParam(value = "resourceId") @NotBlank(message = "资源ID不能为空") String resourceId,
                              @RequestParam(value = "resourceText") String resourceText) {
        return monitorClient.addResource(key,levelCode, resourceId,resourceText);
    }

	/****
	 * 添加节点
	 * @param key redis key
	 * @param levelCode 节点层级编码
	 * @return type 添加节点类型
	 * @author fangkun 2017-10-24
	 */
    @RequestMapping(value = "/addNode")
    public Result addNode(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                          @RequestParam(value = "levelCode") @NotBlank(message = "节点层及编码不能为空") String levelCode,
                          @RequestParam(value = "type") @Range(min = 0, max = 2, message = "0：创建子节点 1：创建上节点 2：创建下节点") int type) {
        return monitorClient.addNode(key,levelCode, type);
    }

    /**
     * 删除节点
     * @param key redis key
     * @param levelCode 节点层级编码
     * @param type 0代表失效 1代表删除
     * @return
     */
    @RequestMapping(value = "/deleteNode", method = RequestMethod.GET)
    public Result deleteNode(
    		@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
    		@RequestParam(value = "levelCode") @NotBlank(message = "节点层级编码不能为空") String levelCode,
            @RequestParam(value = "type") @Range(min = 0, max = 1, message = "0：节点失效 1：节点删除") int type) {
        return monitorClient.deleteNode(key,levelCode, type);
    }

    @RequestMapping(value = "/moveNode", method = RequestMethod.GET)
    public Result moveNode(@RequestParam(value = "key") @NotBlank(message = "redis key不能为空") String key,
                           @RequestParam(value = "moveLvlCode") @NotBlank(message = "节点层级编码不能为空") String moveLvlCode,
                           @RequestParam(value = "desLvlCode") String desLvlCode,
                           @RequestParam(value = "type") int type
    ) {
        return monitorClient.moveNode(key, moveLvlCode, desLvlCode, type);
    }
}
