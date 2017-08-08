package com.huntkey.rx.sceo.monitor.provider.controller.client;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorService;

@RestController
@RequestMapping("/monitor")
@Validated
public class MonitorController1 {
	@Autowired 
	MonitorService service;
	/***
	 * 查询监管树临时结构
	 * @param tempId 监管树临时单id
	 * @param hasResource 是否包含资源
	 * @param validDate 日期
	 * @return
	 */
	@RequestMapping(value="/tempTree")
	public Result tempTree(@RequestParam(value="tempId") @NotBlank(message="监管树临时单ID不能为空") String tempId,
			@RequestParam(value="containResource",required=false,defaultValue="0") int containResource,
			@RequestParam(value="validDate",required=false) String validDate){
		return service.tempTree(tempId,containResource,validDate);
	}
	/**
	 * 监管树临时单预览 是否需要包含资源
	 * @param nodes
	 * @return
	 */
	@RequestMapping(value="/containResource")
	public Result containResource(@RequestParam(value="nodes") @NotBlank(message="监管树临时单节点ID数组不能为空") 
	@Size(min=1) String[] nodes){
		return service.containResource(nodes);
	}
	/**
	 * 查询节点详情
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/nodeDetail")
	public Result nodeDetail(@RequestParam(value="nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId){
		return service.nodeDetail(nodeId);
	}
	/**
	 * 查询节点关联资源
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/nodeResource")
	public Result nodeResource(@RequestParam(value="nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId){
		return service.nodeResource(nodeId);
	}
	/**
	 * 保存节点详情
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/saveNodeDetail")
	public Result saveNodeDetail(@RequestParam(value="nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId){
		return service.nodeResource(nodeId);
	}
	
	
}
