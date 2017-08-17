package com.huntkey.rx.sceo.monitor.client.service;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.monitor.client.service.hystrix.MonitorClientFallback;
import com.huntkey.rx.sceo.monitor.commom.model.AddMonitorTreeTo;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by zhaomj on 2017/8/11.
 */
@FeignClient(value = "biz-monitor-provider",fallback = MonitorClientFallback.class)
public interface MonitorClient {
	/***
	 * 查询监管树临时结构
	 * @param tempId 监管树临时单id
	 * @param hasResource 是否包含资源
	 * @param validDate 日期
	 * @return
	 */
	@RequestMapping(value="/monitors/tempTree")
	Result tempTree(@RequestParam(value="tempId")  String tempId,
			@RequestParam(value="validDate",required=false) String validDate);
	/**
	 * 监管树临时单预览 是否需要包含资源
	 * @param nodes
	 * @return
	 */
	@RequestMapping(value="/monitors/containResource")
	Result containResource(@RequestParam(value="nodes") 
	String[] nodes,
	@RequestParam(value="classId")  String classId);
	/**
	 * 查询节点详情
	 * @param nodeId 节点ID  
	 * @return
	 */
	@RequestMapping(value="/monitors/nodeDetail")
	Result nodeDetail(@RequestParam(value="nodeId") String nodeId);
	/**
	 * 查询节点关联资源
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/monitors/nodeResource")
	Result nodeResource(@RequestParam(value="nodeId") String nodeId,
			@RequestParam(value="classId") String classId);  
	/**
	 * 保存节点详情
	 * @param nodeId 节点ID
	 * @return
	 */
	@RequestMapping(value="/monitors/saveNodeDetail",method=RequestMethod.POST)
	Result saveNodeDetail(@RequestBody NodeTo nodeDetail);
	/**
	 * 删除节点资源
	 * @param nodeId 节点ID
	 * @param resourceId 临时单ID
	 * @return
	 */
	@RequestMapping(value="/monitors/deleteNodeResource")
	Result deleteNodeResource(@RequestParam(value="nodeId")  String nodeId,
		@RequestParam(value="resourceId")  String resourceId);
	
	/**
	 * 变更公式接口
	 * @param nodeId 节点ID
	 * @param resourceId 临时单ID
	 * @return
	 */
	@RequestMapping(value="/monitors/changeFormula",method=RequestMethod.GET)
	Result changeFormula(@RequestParam(value="nodeId") String nodeId,
		@RequestParam(value="formularId") String formularId);
	
	/**
	 * 新增资源
	 * @param nodeId 节点ID
	 * @param resourceIds 资源id集合
	 * @return
	 */
	@RequestMapping(value="/monitors/addResource",method=RequestMethod.GET)
	Result addResource(@RequestParam(value="nodeId") String nodeId,
		@RequestParam(value="resourceIds") String[] resourceIds);
	
	/**
	 * 新增节点
	 * @param nodeId 节点ID
	 * @param nodeType 创建节点的类型
	 * @return
	 */
	@RequestMapping(value="/monitors/addNode",method=RequestMethod.GET)
	Result addNode(@RequestParam(value="nodeId") String nodeId,
		@RequestParam(value="nodeType")  int nodeType);
	/**
	 * 删除节点
	 * @param nodeId 节点ID
	 * @param type 0代表失效 1代表删除
	 * @return
	 */
	@RequestMapping(value="/monitors/deleteNode",method=RequestMethod.GET)
	Result deleteNode(@RequestParam(value="nodeId")  String nodeId,
			@RequestParam(value="type")  int type);
	
	@RequestMapping(value="/monitors/moveNode",method=RequestMethod.GET)
	Result moveNode(@RequestParam(value="nodeId") String nodeId,
			@RequestParam(value="nodeParentId") String nodeParentId,
			@RequestParam(value="nodeLeftId") String nodeLeftId,
			@RequestParam(value="nodeRightId") String nodeRightId
			);
	
	@RequestMapping(value="/monitors/addMonitorTree",method=RequestMethod.POST)
	Result addMonitorTree(@RequestBody AddMonitorTreeTo addMonitorTreeTo);
	
	//监管树维护
	@RequestMapping(value="/monitors/treeMaintaince")
	Result treeMaintaince(@RequestParam(value="classId") String classId,
			@RequestParam(value="rootId") String rootId,
			@RequestParam(value="edmcNameEn") String edmcNameEn);
}
