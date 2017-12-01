package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;

@Component
public class ModelerClientFallback implements ModelerClient{

    @Override
    public Result getEntityByVersionAndEnglishName(String edmdVer, String edmcNameEn) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getEntityByVersionAndEnglishName fallback");
        return result;
    }

    @Override
    public Result getPropertyValue(String classId, String edmpCode) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getPropertyValue fallback");
        return result;
    }

    @Override
    public Result getEdmByid(String edmId) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getEdmByid fallback");
        return result;
    }

    @Override
    public Result getCharacterAndFormat(String classId) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getCharacterAndFormat fallback");
        return result;
    }

    @Override
    public Result getConProperties(String edmdVer, String edmcNameEn) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getConProperties fallback");
        return result;
    }

    @Override
    public Result getPropertyFormula(String id) {
        
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getPropertyFormula fallback");
        return result;
    }

    @Override
    public Result checkIsChileNode(String id, String sid) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client checkIsChileNode fallback");
        return result;
    }

}

