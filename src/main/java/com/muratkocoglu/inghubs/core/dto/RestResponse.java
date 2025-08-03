package com.muratkocoglu.inghubs.core.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestResponse<T> implements Serializable {

    private static final long serialVersionUID = 4393729396342214160L;

	private static final RestResponse<Void> EMPTY_RESPONSE = new RestResponse<>(null, null, null);

    private T data;
    
    private String errorCode;

    private String message;

    private RestResponse(T t, String errorCode, String message){
        this.data = t;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static <T> RestResponse<T> of(T t, String errorCode, String message){
        return new RestResponse<>(t, errorCode, message);
    }

    public static RestResponse<Void> empty() {
        return EMPTY_RESPONSE;
    }
}
