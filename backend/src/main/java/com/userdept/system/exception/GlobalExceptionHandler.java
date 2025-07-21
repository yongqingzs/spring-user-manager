package com.userdept.system.exception;

import com.userdept.system.vo.ApiResponse;
import io.jsonwebtoken.MalformedJwtException;
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
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("请求参数验证失败: {}", errorMessage);
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数验证失败: " + errorMessage);
    }

    /**
     * 处理请求参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("请求参数绑定失败: {}", errorMessage);
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        
        if (e instanceof BadCredentialsException) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
        
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "认证失败: " + e.getMessage());
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.FORBIDDEN.value(), "权限不足，无法访问");
    }

    /**
     * 处理 JWT 格式错误异常
     */
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleMalformedJwtException(MalformedJwtException e) {
        log.warn("JWT 格式错误: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "无效的 Token");
    }

    /**
     * 处理缺失的请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiResponse<?> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return ApiResponse.error(400, "请求参数缺失: " + ex.getParameterName());
    }

    /**
     * 处理缺失或格式错误的请求体异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体缺失或格式错误: {}", ex.getMessage());
        return ApiResponse.error(400, "请求体缺失或格式错误，请检查提交的数据");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 处理其他所有未预期的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        StackTraceElement element = e.getStackTrace()[0];
        String location = element.getClassName() + "." + element.getMethodName() + "(line:" + element.getLineNumber() + ")";
        log.error("系统异常 at {}: {}", location, e.getMessage(), e);
        return ApiResponse.error("服务器内部错误，请联系管理员");
    }
}
