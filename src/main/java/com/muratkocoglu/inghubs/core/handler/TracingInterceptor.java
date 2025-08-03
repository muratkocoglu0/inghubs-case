package com.muratkocoglu.inghubs.core.handler;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class TracingInterceptor implements HandlerInterceptor {

    private final Tracer tracer;

    public TracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
    						 @NonNull HttpServletResponse response, 
    						 @NonNull  Object handler) {
        if (tracer.currentSpan() != null) {
            response.addHeader("X-Trace-Id", tracer.currentSpan().context().traceId());
        }
        return true;
    }
}

