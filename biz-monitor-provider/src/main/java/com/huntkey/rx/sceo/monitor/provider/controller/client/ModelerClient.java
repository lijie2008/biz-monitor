package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huntkey.rx.commons.utils.rest.Result;

@FeignClient(value = "MODELER-PROVIDER", url = "http://10.3.98.154:2002", fallback = ModelerClientFallback.class) //单机调试使用(注意不要提交此行)
//@FeignClient(value = "modeler-provider", fallback = ModelerClientFallback.class)
public interface ModelerClient {
    
    /**
     * 根据modeler版本和类英文名查询类的所有实体子孙类
     * @param edmdVer
     * @param edmcNameEn
     * @return
     */
    @RequestMapping(value = "/classes/entity", method = RequestMethod.GET)
    Result getEntityByVersionAndEnglishName(@RequestParam(value = "edmdVer") String edmdVer,
                                            @RequestParam(value = "edmcNameEn") String edmcNameEn);
    
    
    /**
     * 查询资源类信息
     *
     * @param classId  监管树类ID
     * @param edmpCode 属性编码
     * @return
     */
    @RequestMapping(value = "/properties/values", method = RequestMethod.GET)
    Result getPropertyValue(@RequestParam(value = "classId") String classId, @RequestParam(value = "edmpCode") String edmpCode);
    
    /**
     * 根据EDM ID 查询edm详细信息
     *
     * @param edmId
     * @return
     */
    @RequestMapping(value = "/classes/{edmId}", method = RequestMethod.GET)
    Result getEdmByid(@PathVariable(value = "edmId") String edmId);
    
    
    /**
     * 根据类id 查询特征值字段集合和格式化样式
     * @param classId
     * @return
     */
    @RequestMapping(value = { "/classFormats/getCharacterAndFormat" }, method = {
            RequestMethod.GET })
    Result getCharacterAndFormat(@RequestParam(value = "classId") String classId);
    
    /**
     * 根据类英文名查询类的卷积属性
     * @param edmdVer
     * @param edmcNameEn
     * @return
     */
    @RequestMapping(value = "/properties/getConProperties", method = RequestMethod.GET)
    Result getConProperties(@RequestParam(value = "edmdVer") String edmdVer,
                            @RequestParam(value = "edmcNameEn") String edmcNameEn);
    
    /**
     * getPropertyFormula:根据卷积属性id查询公式
     * @author caozhenx
     * @param id
     * @return
     */
    @RequestMapping(value = "/convolutes/formula/{id}", method = RequestMethod.GET)
    Result getPropertyFormula(@PathVariable(value = "id") String id);  
    
    /**
     * checkIsChileNode:根据父类id 子类id来确认 子类是否为父类的  子孙类
     * @author caozhenx
     * @param id 父类Id
     * @param sid 子类id
     * @return
     */
    @RequestMapping(value = "/classes/child/{id}/{sid}", method = RequestMethod.GET)
    Result checkIsChileNode(@PathVariable(value = "id") String id,
                            @PathVariable(value = "sid") String sid);
}
