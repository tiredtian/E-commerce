package com.asiainfo.mall.exception;

import com.asiainfo.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理统一异常的类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
        log.error("default exception:", e);
        return ApiRestResponse.error(MallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(MallException.class)
    @ResponseBody
    public Object handleMallException(MallException e) {
        log.error("mall exception:", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException",e);
        return handleBindingResult(e.getBindingResult());
    }

//    BindingResult用于封装参数验证过程中的错误信息
    private ApiRestResponse handleBindingResult(BindingResult result) {
        List<String> list = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError allError : allErrors) {
                String message = allError.getDefaultMessage();
                list.add(message);
            }
        }
        if (list.size()==0) {
            return ApiRestResponse.error(MallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(MallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
