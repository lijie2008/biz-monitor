/**
 * Project Name:biz-monitor-provider
 * File Name:MonitorTreeOrderController.java
 * Package Name:com.huntkey.rx.sceo.monitor.provider.controller.client
 * Date:2017年8月8日下午8:10:11
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.sceo.method.register.plugin.annotation.MethodRegister;
import com.huntkey.rx.sceo.method.register.plugin.entity.ProgramCate;
import com.huntkey.rx.sceo.monitor.commom.constant.Constant;
import com.huntkey.rx.sceo.monitor.commom.constant.ValidBean;
import com.huntkey.rx.sceo.monitor.commom.enums.OperateType;
import com.huntkey.rx.sceo.monitor.commom.model.NodeTo;
import com.huntkey.rx.sceo.monitor.provider.config.Revoked;
import com.huntkey.rx.sceo.monitor.provider.service.MonitorTreeOrderService;

/**
 * ClassName:MonitorTreeOrderController 临时单据类
 * Date:     2017年8月8日 下午8:10:11
 * @author   lijie
 * @version  
 * @see 	 
 */
@RestController
@RequestMapping("/nodes")
@Validated
public class MonitorTreeOrderController {
    
    @Autowired
    private MonitorTreeOrderService service;
    
    @Resource(name="redisTemplate")
    private ListOperations<String, NodeTo> opsList;
    
    /**
     * 
     * queryNotUsingResource: 查询节点未使用的资源信息
     * @author lijie
     * @param orderId 临时单ID
     * @param lvlCode 节点层级编码
     * @param currentPage 当前页
     * @param pageSize 页大小
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "查询节点未使用的资源信息",
            getReqParamsNameNoPathVariable = {"key","lvlCode","currentPage","pageSize"}
    )
    @RequestMapping(value = "/resource",method = RequestMethod.GET)
    public Result queryNotUsingResource(@RequestParam @NotBlank(message = "临时单Key不能为空") String key,
                                        @RequestParam @NotBlank(message = "节点层级编码不能为空") String lvlCode,
                                        @RequestParam(defaultValue = "1",required=false) int currentPage, 
                                        @RequestParam(defaultValue="20",required=false) int pageSize) throws Exception{
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.queryNotUsingResource(key, lvlCode, currentPage, pageSize));
        return result;
    }
    
    /**
     * 
     * checkNodeResource: 节点时间区间修改检查
     * @author lijie
     * @param key 临时单key
     * @param lvlCode 节点层级编码
     * @param startDate 生效时间
     * @param endDate 失效时间
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "节点时间区间修改检查",
            getReqParamsNameNoPathVariable = {"key","lvlCode","startDate","endDate"}
    )
    @RequestMapping(value = "/checkDate",method = RequestMethod.GET)
    public Result checkNodeResource(@RequestParam @NotBlank(message = "临时单Key不能为空") String key,
                                    @RequestParam @NotBlank(message = "节点层级编码不能为空") String lvlCode,
                                    @RequestParam @NotBlank(message = "生效日期不能为空") @Pattern(regexp=ValidBean.DATE_REGX,message="生效日期格式不正确") String startDate, 
                                    @RequestParam @NotBlank(message = "失效日期不能为空") @Pattern(regexp=ValidBean.DATE_REGX,message="失效日期格式不正确") String endDate){
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.checkDate(key, lvlCode, startDate, endDate));
        return result;
    }
    
    /**
     * 
     * checkAvailableResource:校验是否存在资源未分配
     * @author lijie
     * @param key 临时单Key
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "校验是否存在资源未分配",
            getReqParamsNameNoPathVariable = {"key"}
    )
    @RequestMapping(value = "/other/resource",method = RequestMethod.GET)
    public Result checkAvailableResource(@RequestParam @NotBlank(message="临时单Key不能为空") String key) throws Exception{
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        
        List<?> datas = service.queryAvailableResource(key);
       
        if(datas == null || datas.isEmpty())
            result.setData(false);
        else
            result.setData(true);
        
       return result;
    }
    
    /**
     * 
     * addOtherNode: 将未分配的资源归类到其他节点上
     * @author lijie
     * @param key 临时单Key
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "将未分配的资源归类到其他节点上",
            getReqParamsNameNoPathVariable = {"key"}
    )
    @Revoked(type=OperateType.NODE)
    @RequestMapping(value = "/other",method = RequestMethod.GET)
    public Result addOtherNode(@RequestParam @NotBlank(message="临时单Key不能为空") @Revoked(key="key") String key) throws Exception{
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.addOtherNode(key));
        return result;
    }
    
    /**
     * 
     * save: 临时单保存
     * @author lijie
     * @param key 临时单key
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "临时单保存"
    )
    @RequestMapping(value = "/save/{key}",method = RequestMethod.GET)
    public Result save(@PathVariable(value="key") @NotBlank(message="临时单Key不能为空") String key) throws Exception{
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.save(key));
        return result;
    }
    
    /**
     * 
     * revoked: 撤销操作
     * @author lijie
     * @param key 临时单Key
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "临时单撤销"
    )
    @RequestMapping(value = "/revoke/{key}",method = RequestMethod.GET)
    public Result revoked(@PathVariable(value="key") @NotBlank(message="临时单Key不能为空") String key){
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.revoke(key));
        return result;
    }
    
    /**
     * 
     * store: 临时单入库
     * @author lijie
     * @param orderId 临时单orderId
     * @return
     */
    @MethodRegister(
            edmClass = Constant.EDM_MONITORTREEORDER,
            methodCate = "监管类方法",
            programCate = ProgramCate.Java,
            methodDesc = "临时单入库"
    )
    @RequestMapping(value = "/{orderId}",method = RequestMethod.GET)
    public Result store(@PathVariable(value="orderId") @NotBlank(message="临时单Id不能为空") String orderId) throws Exception{
       
        Result result = new Result();
        result.setRetCode(Result.RECODE_SUCCESS);
        result.setData(service.store(orderId));
        return result;
    }
}

