package com.anther.aspect;


import com.anther.annotation.GlobalInterceptor;
import com.anther.entity.dto.TokenUserInfoDto;
import com.anther.entity.enums.ResponseCodeEnum;
import com.anther.exception.BusinessException;
import com.anther.redis.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/2 23:27
 */
@Component
@Aspect
@Slf4j
public class GlobalOperationAspect {
    @Resource
    private RedisComponent redisComponent;

    @Before("@annotation(com.anther.annotation.GlobalInterceptor)")
    public void interceptorDo(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                return;
            }
            if (interceptor.checkLogin() || interceptor.checkAdmin()) {
                checkLogin(interceptor.checkAdmin());
            }
        } catch (Exception e) {
            log.error("全局拦截器异常", e);
            throw e;
        }catch (Throwable e){
            log.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    private void checkLogin(Boolean checkAdmin){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto(token);
        if (tokenUserInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }else if (checkAdmin && !tokenUserInfoDto.getAdmin()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
