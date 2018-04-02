package com.cjsz.tech.system.aop.aspect;
import com.cjsz.tech.system.annotation.SysActionLogAnnotation;
import com.cjsz.tech.system.domain.SysActionLog;
import com.cjsz.tech.system.domain.SysLog;
import com.cjsz.tech.system.domain.SysUser;
import com.cjsz.tech.system.service.SysActionLogService;
import com.cjsz.tech.system.service.SysLogService;
import com.cjsz.tech.system.service.UserService;
import com.cjsz.tech.system.utils.SysActionLogType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
/**
 * Created by Bruce on 2016/11/15.
 */
@Component
@Aspect
public class ServiceLogAspect {

    @Autowired
    private SysActionLogService sysActionLogService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserService userService;


    @Pointcut("@annotation(com.cjsz.tech.system.annotation.SysActionLogAnnotation)")
    public void serviceLog() {
        }

    @Before("serviceLog()")
    public void before(JoinPoint joinPoint) {

    }



    @Around("serviceLog()")
    public Object around(JoinPoint joinPoint) {
        ProceedingJoinPoint proceedingJoinPoint = joinPoint instanceof ProceedingJoinPoint ? (ProceedingJoinPoint)joinPoint : null;
        try {
            Object rvt =   proceedingJoinPoint.proceed();
            return rvt;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
            return null;
    }

    @AfterReturning(value = "serviceLog()", returning="rtv")
    public Object  afterReturn(JoinPoint joinPoint, Object rtv) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        SysActionLogAnnotation sysActionLogAnnoation = method.getAnnotation(SysActionLogAnnotation.class);
        if(sysActionLogAnnoation != null) {
            SysActionLogType sysActionLogType = sysActionLogAnnoation.action_type();
            String action_log_module_name = sysActionLogAnnoation.action_log_module_name();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(null == attributes){
                return null;
            }
            HttpServletRequest request = attributes.getRequest();

            Object[] os = joinPoint.getArgs();
            String content = "";
            if(os != null) {
                ObjectMapper  gson = new ObjectMapper();
                content = gson.writeValueAsString(os);
            }
            SysUser sysUser = null;
            if(StringUtils.isNotEmpty(request.getHeader("token"))) {
                sysUser = userService.findByToken(request.getHeader("token"));
            }

            String ip_address = "无";
            String ip_num = "无";
            if(StringUtils.isNotEmpty(request.getHeader("IP"))){
                ip_num =  request.getHeader("IP");
            }

            if(StringUtils.isNotEmpty(request.getHeader("Address"))){
                ip_address =  request.getHeader("Address");
            }
            String action_ip = ip_num + "-" + ip_address;
//            String action_ip = IpUtils.getIpAddr(request);
            if(sysUser != null) {
                SysActionLog sysActionLog = new SysActionLog();
                String action_user_name = sysUser.getUser_name();
                Long org_id = sysUser.getOrg_id();
                sysActionLog.setAction_user_name(action_user_name);
                sysActionLog.setAction_type(sysActionLogType.getAction_type());
                sysActionLog.setAction_log_module_name(action_log_module_name);
                sysActionLog.setAction_ip(action_ip);
                sysActionLog.setOrg_id(org_id);

                sysActionLog.setAction_log_content(content);
                Integer rows = sysActionLogService.saveLog(sysActionLog);
                System.out.println("save action log finish:" + rows );
            }
        }
        return rtv;
    }
    @After("serviceLog()")
    public void after(JoinPoint joinPoint) {

    }
    @AfterThrowing(pointcut = "serviceLog()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        SysLog sysLog = new SysLog();
        sysLog.setSys_log_code(100L);
        String exClassName = joinPoint.getTarget().getClass().getName();
        String exMethod = joinPoint.getSignature().getName();
        StringBuffer content = new StringBuffer();
        content.append(exClassName).append(".").append(exMethod);
        content.append(":");
        content.append(ex.getMessage());
        sysLog.setSys_log_content(content.toString());
        sysLogService.saveLog(sysLog);
    }

}

