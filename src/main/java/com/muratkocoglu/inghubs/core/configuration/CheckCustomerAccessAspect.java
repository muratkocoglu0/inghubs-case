package com.muratkocoglu.inghubs.core.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.muratkocoglu.inghubs.core.dto.CustomUserDetails;

@Aspect
@Component
public class CheckCustomerAccessAspect {

	@Before("@annotation(checkCustomerAccess)")
    public void checkAccess(JoinPoint joinPoint, CheckCustomerAccess checkCustomerAccess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        if ("CUSTOMER".equals(principal.getRole())) {
	        Long currentCustomerId = principal.getCustomerId();
	        Long targetCustomerId = extractTargetCustomerId(joinPoint, checkCustomerAccess.value());
	
	        if (!currentCustomerId.equals(targetCustomerId)) {
	            throw new AccessDeniedException("You are not allowed to access this resource");
	        }
        }
    }

    private Long extractTargetCustomerId(JoinPoint joinPoint, String paramName) {
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName) && args[i] instanceof Long) {
                return (Long) args[i];
            }
            
            if (args[i] != null) {
                try {
                    var field = args[i].getClass().getDeclaredField(paramName);
                    field.setAccessible(true);
                    Object value = field.get(args[i]);
                    if (value instanceof Long) {
                        return (Long) value;
                    }
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("Parameter " + paramName + " is not a Long type");
    }
}
