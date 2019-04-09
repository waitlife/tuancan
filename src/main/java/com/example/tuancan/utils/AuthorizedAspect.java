package com.example.tuancan.utils;

import com.example.tuancan.config.ProjectUrlConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

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

    @Before(value = "@annotation(com.example.tuancan.utils.AuthorizedAnnotation)")
    public void wxAuthorizedCheck(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String name = signature.getMethod().getName();
        log.info("切面方法：{}", name);

       /* Object[] args = joinPoint.getArgs();
        log.info("参数：{}", JsonUtil.toJson(args));
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        String name = signature.getMethod().getName();
        /*
        for (int i = 0; i < args.length; i++) {
            if ("openId".equals(parameterNames[i]) && Objects.nonNull(args[i])) {
                openId = args[i].toString();
                return openId;
            }
        }*/
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        if (!isLogin(request)) {
            try {
                if (response != null) {
                    response.sendRedirect(projectUrlConfig.getWhchatAuthorize() + "/wechat/authorize");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      /*  Object result = null;
        try {
            result = joinPoint.proceed();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            result = JsonUtil.toJson("{异常}" + throwable.getMessage());
        }
        return result;*/
    }

    private Boolean isLogin(HttpServletRequest request) {
        String openId = null;
        /*if (!StringUtil.isNullOrEmpty(openId)) {
            request.getSession().setAttribute("openId", "o81TH1TH9szWjykZQcY2-1S1_eWI");
            log.info("ASpect_request--openid:{}", openId);
        }*/
        openId = request.getSession().getAttribute("openId").toString();
        log.info("ASpect_session--openid:{}", openId);
        return Objects.nonNull((openId));

    }

}
