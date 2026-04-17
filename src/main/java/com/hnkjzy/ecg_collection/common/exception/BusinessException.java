package com.hnkjzy.ecg_collection.common.exception;

import com.hnkjzy.ecg_collection.common.result.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
