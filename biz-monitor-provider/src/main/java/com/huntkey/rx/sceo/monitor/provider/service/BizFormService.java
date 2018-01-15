package com.huntkey.rx.sceo.monitor.provider.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huntkey.rx.commons.utils.rest.Result;
import com.huntkey.rx.edm.service.OrderRegService;
import com.huntkey.rx.edm.service.PeopleRegService;
import com.huntkey.rx.sceo.method.register.plugin.entity.ParamsVo;
import com.huntkey.rx.sceo.monitor.commom.exception.ApplicationException;
import com.huntkey.rx.sceo.monitor.commom.model.CurrentSessionEntity;
import com.huntkey.rx.sceo.serviceCenter.common.model.NodeConstant;

@Component
public class BizFormService {
    private static Logger logger = LoggerFactory.getLogger(BizFormService.class);
    public static final String SUBMIT_METHOD_ARG_DEF = "orderDefId";
    public static final String SUBMIT_METHOD_ARG_INST = "orderInstanceId";
    public static final String AUDIT_METHOD_ARG_ACT_INSTANCE_ID = "actInstanceId";
    public static final String AUDIT_METHOD_ARG_OPINION = "opinion";
    public static final String AUDIT_METHOD_ARG_AUDIT_KEY = "auditKey";
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private OrderRegService orderRegService;
    @Autowired
    private PeopleRegService peopleRegService;

    /**
     * 获取当前登录的用户对应的员工和岗位信息
     */
    public CurrentSessionEntity getCurrentSessionInfo() {
        CurrentSessionEntity sessionEntity = new CurrentSessionEntity();
        String token = request.getHeader("Authorization");
        logger.info("token: " + token);
        ParamsVo paramsVo = new ParamsVo();
        paramsVo.setAuthorization(token);
        Result sessionResult = peopleRegService.getEcosystemSession(paramsVo);

        if (!Result.RECODE_SUCCESS.equals(sessionResult.getRetCode())) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，错误信息" + sessionResult.getErrMsg());
        }
        if (sessionResult.getData() == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，session数据为空");
        }
        JSONObject resultObj = JSON.parseObject(JSONObject.toJSONString(sessionResult.getData()));
        JSONObject currentStatus = resultObj.getJSONObject("currentStatus");
        if (currentStatus == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录信息为空");
        }
        //取当前登录用户的员工信息
        JSONObject currentStaff = currentStatus.getJSONObject("currentStaff");
        if (currentStaff == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录员工信息为空");
        }
        String employeeId = currentStaff.getString(NodeConstant.ID);
        if (employeeId == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录员工ID为空");
        }
        sessionEntity.setEmployeeId(employeeId);
        //取当前登录用户的岗位信息
        JSONObject currentPosition = currentStatus.getJSONObject("currentPosition");
        if (currentPosition == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录岗位信息为空");
        }
        String positionId = currentPosition.getString(NodeConstant.ID);
        if (positionId == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录岗位ID为空");
        }
        sessionEntity.setPositionId(positionId);
        //取当前登录用户的企业信息
        JSONObject currentEnterprise = currentStatus.getJSONObject("currentEnterprise");
        if (currentEnterprise == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录企业信息为空");
        }
        String enterpriseId = currentEnterprise.getString(NodeConstant.ID);
        if (enterpriseId == null) {
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, "获取用户Session信息出错，当前登录企业ID为空");
        }
        sessionEntity.setEnterpriseId(enterpriseId);

        return sessionEntity;
    }

    /**
     * 单据提交流程接口，会启动一个流程实例
     * @param orderDefId
     * @param orderInstanceId
     */
    public void submitWorkFlow(String orderDefId, String orderInstanceId) {
        ParamsVo params = new ParamsVo();
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put(SUBMIT_METHOD_ARG_DEF, orderDefId);
        map.put(SUBMIT_METHOD_ARG_INST, orderInstanceId);
        params.setParamObj(map);
        Result submitResult = orderRegService.submit(params);
        if (!Result.RECODE_SUCCESS.equals(submitResult.getRetCode())) {
            logger.error("提交流程失败，单据定义ID: {},单据实例ID:{}", orderDefId, orderInstanceId);
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, submitResult.getErrMsg());
        }
    }

    /**
     * 单据审批接口
     * @param actInstanceId 流程实例ID
     * @param opinion 审批意见
     * @param auditKey 审批类型
     */
    public void audit(String actInstanceId,String opinion,String auditKey){
        ParamsVo params = new ParamsVo();
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put(AUDIT_METHOD_ARG_ACT_INSTANCE_ID, actInstanceId);
        map.put(AUDIT_METHOD_ARG_OPINION, opinion);
        map.put(AUDIT_METHOD_ARG_AUDIT_KEY, auditKey);
        params.setParamObj(map);
        Result auditResult = orderRegService.audit(params);
        if (!Result.RECODE_SUCCESS.equals(auditResult.getRetCode())) {
            logger.error("审批失败，流程实例ID:{}", actInstanceId);
            ApplicationException.throwCodeMesg(Result.RECODE_ERROR, auditResult.getErrMsg());
        }
    }

}
