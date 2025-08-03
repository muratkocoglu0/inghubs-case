package com.muratkocoglu.inghubs.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR"),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND"),
    ACCESS_DENIED("ACCESS_DENIED"),
    BUSINESS_ERROR("BUSINESS_ERROR"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE"),
    ORDER_NOT_PENDING("ORDER_NOT_PENDING");

    private final String code;
}
