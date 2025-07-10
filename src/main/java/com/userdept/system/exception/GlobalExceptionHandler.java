package com.userdept.system.exception;

import com.userdept.system.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("请求参数验证失败: {}", errorMessage);
        return ResultVO.error(HttpStatus.BAD_REQUEST.value(), "参数验证失败: " + errorMessage);
    }

    /**
     * 处理请求参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("请求参数绑定失败: {}", errorMessage);
        return ResultVO.error(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultVO<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        
        if (e instanceof BadCredentialsException) {
            return ResultVO.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
        
        return ResultVO.error(HttpStatus.UNAUTHORIZED.value(), "认证失败: " + e.getMessage());
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultVO<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ResultVO.error(HttpStatus.FORBIDDEN.value(), "权限不足，无法访问");
    }

    /**
     * 处理缺失的请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResultVO<?> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return ResultVO.error(400, "请求参数缺失: " + ex.getParameterName());
    }

    /**
     * 处理缺失或格式错误的请求体异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体缺失或格式错误: {}", ex.getMessage());
        return ResultVO.error(400, "请求体缺失或格式错误，请检查提交的数据");
    }

    /**
     * 处理其他所有未预期的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultVO.error("服务器内部错误，请联系管理员");
    }
}
