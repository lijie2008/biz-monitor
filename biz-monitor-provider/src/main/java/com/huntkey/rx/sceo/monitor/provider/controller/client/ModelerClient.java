package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huntkey.rx.commons.utils.rest.Result;

//@FeignClient(value = "MODELER-PROVIDER", url = "http://192.168.13.34:2002", fallback = ModelerClientFallback.class) //单机调试使用(注意不要提交此行)
@FeignClient(value = "modeler-provider", fallback = ModelerClientFallback.class)
public interface ModelerClient {

    /**
     * 根据id查询EDM类
     * @author 方坤
     * @version
     * @see
     */
    @RequestMapping(value = "/classes/{id}")
    Result queryEdmClassById(@PathVariable(value = "id") String id);

    /**
     * 查询属性
     * @param id
     * @return
     */
    @RequestMapping(value = "/classes/{id}/properties")
    Result queryEdmClassProperties(@PathVariable(value = "id") String id);

    /**
     * 根据模型id + 类英文名称数组查询类树
     * @param modelerId
     * @param edmcNameEns
     * @return
     */
    @RequestMapping(value = "/classes/classTree", method = RequestMethod.GET)
    Result queryClassTree(@RequestParam(value = "modelerId") String modelerId,
                          @RequestParam(value = "edmcNameEns") String[] edmcNameEns);

    /**
     * 根据类id 查询特征值字段集合和格式化样式
     * @param classId
     * @return
     */
    @RequestMapping(value = { "/classFormats/getCharacterAndFormat" }, method = {
            RequestMethod.GET })
    Result getCharacterAndFormat(@RequestParam(value = "classId") String classId);

    /**
     * 
     * getEdmcCode: 查询资源类信息
     * @author lijie
     * @param classId 监管树类ID
     * @param edmpCode 属性编码
     * @return
     */
    @RequestMapping(value = "/properties/values", method = RequestMethod.GET)
    Result getEdmcNameEn(@RequestParam(value = "classId") String classId,
                         @RequestParam(value = "edmpCode") String edmpCode);

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
     * checkIsChileNode:根据父类id 子类id来确认 子类是否为父类的  子孙类
     * @author caozhenx
     * @param id 父类Id
     * @param sid 子类id
     * @return
     */
    @RequestMapping(value = "/classes/child/{id}/{sid}", method = RequestMethod.GET)
    Result checkIsChileNode(@PathVariable(value = "id") String id,
                            @PathVariable(value = "sid") String sid);

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
     * 将指定id的所有属性的is_visible字段更改为指定数值
     * @param ids
     * @param b （0或1）
     */
    @RequestMapping(value = "/properties/changeVisible", method = RequestMethod.POST)
    Result changePropertiesVisible(@RequestParam(value = "ids") String[] ids,
                                          @RequestParam(value = "b") byte b);
    
    /**
     * getPropertyFormula:根据卷积属性id查询公式
     * @author caozhenx
     * @param id
     * @return
     */
    @RequestMapping(value = "/convolutes/formula/{id}", method = RequestMethod.GET)
    Result getPropertyFormula(@PathVariable(value = "id") String id);    

}
