package com.jieming.ad.advise;

import com.jieming.ad.exception.AdException;
import com.jieming.ad.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionAdvise  {

    @ExceptionHandler(value = AdException.class)
    public CommonResponse<String> handlerAdException(HttpServletRequest request,
                                                     AdException adexception){
        //优化：定义不同类型的异常枚举（异常吗和异常信息）
        CommonResponse<String> response = new CommonResponse<>(-1,"business error");
        response.setData(adexception.getMessage());
        return response;
    }
}
