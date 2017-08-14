package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;

@Component
public class ModelerClientFallback implements ModelerClient{

	@Override
	public Result queryEdmClassById(String id) {
		Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client queryEdmClassById fallback");
        return result;
	}

	@Override
	public Result queryEdmClassProperties(String id) {
		Result result = new Result();
		result.setRetCode(Result.RECODE_ERROR);
		result.setErrMsg("modeler client queryEdmClassProperties fallback");
		return result;
	}

	@Override
	public Result queryClassTree(String modelerId, String[] edmcNameEns) {
		Result result = new Result();
		result.setRetCode(Result.RECODE_ERROR);
		result.setErrMsg("modeler client queryClassTree fallback");
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
    public Result getEdmcNameEn(String classId, String edmpCode) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("modeler client getCharacterAndFormat fallback");
        return result;
    }

    @Override
    public Result getEntityByVersionAndEnglishName(String edmdVer, String edmcNameEn) {
		Result result = new Result();
		result.setRetCode(Result.RECODE_ERROR);
		result.setErrMsg("ModelerClient getEntityByVersionAndEnglishName fallback");
		return result;
    }
    
    @Override
    public Result checkIsChileNode(String id,String sid) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("biz monitor provider client checkIsChileNode fallback");
        return result;
    }


}

