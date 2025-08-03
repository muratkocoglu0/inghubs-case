package com.muratkocoglu.inghubs.core.configuration;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCustomerAccess {
    String value() default "id";
}
