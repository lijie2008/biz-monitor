/**
 * Project Name:security-center-provider
 * File Name:HbaseClientFallback.java
 * Package Name:com.huntkey.rx.sceo.security.center.provider.controller.client
 * Date:2017年6月30日下午5:39:00
 * Copyright (c) 2017 嘉源锐信 All Rights Reserved.
 *
*/

package com.huntkey.rx.sceo.monitor.provider.controller.client;

import org.springframework.stereotype.Component;

import com.huntkey.rx.commons.utils.rest.Result;

/**
 * ClassName:HbaseClientFallback
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 * Date:     2017年6月30日 下午5:39:00
 * @author   lijie
 * @version  
 * @see 	 
 */
@Component
public class HbaseClientFallback implements HbaseClient{

    @Override
    public Result queryFromEsAndHbase(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client queryFromEsAndHbase fallback");
        return result;
    }

    @Override
    public Result addDatasToEsAndHbase(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client addDatasToEsAndHbase fallback");
        return result;
    }

    @Override
    public Result deleteEsAndHbase(String datas) {
        Result result = new Result();
        result.setRetCode(Result.RECODE_ERROR);
        result.setErrMsg("hbase client deleteEsAndHbase fallback");
        return result;
    }

}

