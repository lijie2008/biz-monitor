package com.huntkey.rx.sceo.monitor.provider.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.huntkey.rx.commons.utils.string.StringUtil;
import com.huntkey.rx.sceo.monitor.commom.model.LoginResponseTO;
import com.huntkey.rx.sceo.monitor.commom.model.SysUser;
import com.huntkey.rx.sceo.monitor.commom.utils.BCryptUtil;
import com.huntkey.rx.sceo.monitor.provider.service.SysUserService;

/**
 * Created by clarkzhao on 2017/3/28.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private String prefixHeader = "Basic ";

    @Autowired
    private SysUserService userService;

    /**
     * cas server 将凭证 转为ascII 码后用 Base64 加密，然后将加密的凭证放在请求头的 Authorization 参数中，
     * 在验证时，用Base64解密 得到 凭证的ASCII码，再解析ACSCII码。
     * @param authorization
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public LoginResponseTO createLoginResponseTO (@RequestHeader("Authorization") String authorization,
                                                  HttpServletResponse response){

        System.out.println("authorization: "+authorization);

        LoginResponseTO to = new LoginResponseTO();

        //去掉加密字符串的前缀
        authorization = authorization.substring(prefixHeader.length());

        //base64反编码
        byte [] credentials = Base64Utils.decodeFromString(authorization);

        //ACSCII码转字符串
        String asc = ":";
        try {
            asc = new String(credentials,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int splitCharIndex = asc.indexOf(':');
        String username = asc.substring(0,splitCharIndex);
        String password = asc.substring(splitCharIndex+1);

        SysUser sysUser = userService.selectSysUserByAccount(username);

        if(sysUser != null&& !StringUtil.isNullOrEmpty(password)){
            if ("0".equals(sysUser.getState())){
                boolean isPass = BCryptUtil.matches(password,sysUser.getPassword());
                if (isPass){
                    to.setId(sysUser.getId());
                }else {
                    response.setStatus(401);
                }
            }else {
                response.setStatus(403);
            }

        }else {
            response.setStatus(404);
        }

        return to;
    }

}
