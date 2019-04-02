package com.example.tuancan.utils;

import com.example.tuancan.config.ProjectUrlConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiaoqianyong
 * @description
 * @create 2019-04-01-21:25
 */
@Component
@Aspect
@Slf4j
public class AuthorizedAspect {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Before("execution(* com.example.tuancan.controller.*.*(..)) && @annotation(com.example.tuancan.utils.AuthorizedAnnotation)")
    public void wxAuthorizedCheck(JoinPoint joinPoint){
        log.info("参数：{}",JsonUtil.toJson(joinPoint.getArgs()));

        Object[] args = joinPoint.getArgs();
        String openId="";
        for (Object arg : args) {
            if(arg instanceof HttpServletRequest){
                HttpServletRequest arg1 = (HttpServletRequest) arg;
                openId= arg1.getParameter("openId");
                break;
            }
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        if(!StringUtil.isNullOrEmpty(openId)){
            request.getSession().setAttribute("openId",openId);
            log.info("ASpect_request--openid:{}",openId);
        }
        if (StringUtil.isNullOrEmpty(openId)){
            openId = request.getSession().getAttribute("openId").toString();
            log.info("ASpect_session--openid:{}",openId);
        }
        if (StringUtil.isNullOrEmpty(openId)){
            try {
                if (response != null) {
                    response.sendRedirect("/wechat/authorize");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
