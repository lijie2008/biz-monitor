package com.huntkey.rx.sceo.monitor.provider.controller.client;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
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
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		result.setData(service.tempTree(tempId,containResource,validDate));
		return result;
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
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		result.setData(service.nodeDetail(nodeId));
		return result;
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
	@RequestMapping(value="/saveNodeDetail",method=RequestMethod.POST)
	public Result saveNodeDetail(@RequestBody() @Valid NodeTo nodeDetail){
		return service.saveNodeDetail(nodeDetail);
	}
	/**
	 * 删除节点资源
	 * @param nodeId 节点ID
	 * @param resourceId 临时单ID
	 * @return
	 */
	@RequestMapping(value="/deleteNodeResource",method=RequestMethod.DELETE)
	public Result deleteNodeResource(@RequestParam("nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId,
		@RequestParam("resourceId") @NotBlank(message="资源ID不能为空") String resourceId){
		return service.deleteNodeResource(nodeId,resourceId);
	}
	
	/**
	 * 变更公式接口
	 * @param nodeId 节点ID
	 * @param resourceId 临时单ID
	 * @return
	 */
	@RequestMapping(value="/changeFormula",method=RequestMethod.GET)
	public Result changeFormula(@RequestParam("nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId,
		@RequestParam("formularId") String formularId){
		return service.changeFormula(nodeId,formularId);
	}
	
	/**
	 * 新增资源
	 * @param nodeId 节点ID
	 * @param resourceIds 资源id集合
	 * @return
	 */
	@RequestMapping(value="/addResource",method=RequestMethod.GET)
	public Result addResource(@RequestParam("nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId,
		@RequestParam("resourceIds") @NotBlank(message="资源ID不能为空") String[] resourceIds){
		return service.addResource(nodeId,resourceIds);
	}
	
	/**
	 * 新增节点
	 * @param nodeId 节点ID
	 * @param nodeType 创建节点的类型
	 * @return
	 */
	@RequestMapping(value="/addNode",method=RequestMethod.GET)
	public Result addNode(@RequestParam("nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId,
		@RequestParam("nodeType") @NotBlank(message="资源ID不能为空") String nodeType){
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		result.setData(service.addNode(nodeId,nodeType));
		return result;
	}
	/**
	 * 删除节点
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/deleteNode",method=RequestMethod.GET)
	public Result deleteNode(@RequestParam("nodeId") @NotBlank(message="监管树节点ID不能为空") String nodeId){
		Result result=new Result();
		result.setRetCode(Result.RECODE_SUCCESS);
		result.setData(service.deleteNode(nodeId));
		return result;
	}		
	
	
	
	
	
	
	
}
