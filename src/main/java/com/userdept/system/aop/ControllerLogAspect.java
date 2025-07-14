package com.userdept.system.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 通用 Controller 日志切面
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {
    /**
     * 过滤对象字段，忽略字段名包含 "list" 的内容，只输出类型和数量
     */
    private String filterObjectFields(Object obj) {
        return filterObjectFields(obj, 0);
    }

    // 递归输出对象字段，遇到字段名包含 list 且为 Collection 时只输出类型和数量
    private String filterObjectFields(Object obj, int depth) {
        if (obj == null) return "null";
        if (depth > 3) return obj.getClass().getSimpleName() + "{...}"; // 防止死循环
        Class<?> clazz = obj.getClass();
        if (isSimpleType(obj)) return obj.toString();
        if (obj instanceof java.util.Collection) {
            return "Collection(size=" + ((java.util.Collection<?>) obj).size() + ")";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append("(");
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                sb.append(name).append("=unaccessible, ");
                continue;
            }
            if (name.toLowerCase().contains("list") && value instanceof java.util.Collection) {
                sb.append(name).append("=[size=").append(((java.util.Collection<?>) value).size()).append("]");
            } else if (isSimpleType(value)) {
                sb.append(name).append("=").append(value);
            } else if (value instanceof java.util.Collection) {
                sb.append(name).append("=Collection(size=").append(((java.util.Collection<?>) value).size()).append(")");
            } else if (value != null) {
                sb.append(name).append("=").append(filterObjectFields(value, depth + 1));
            } else {
                sb.append(name).append("=null");
            }
            sb.append(", ");
        }
        if (fields.length > 0) sb.setLength(sb.length() - 2);
        sb.append(")");
        return sb.toString();
    }

    /**
     * 判断是否为简单类型（包装类、String、Number、Boolean、Character、Enum）
     */
    private boolean isSimpleType(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz.isEnum();
    }

    // 切 Controller 层所有方法
    @Pointcut("within(com.userdept.system.controller..*)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        StringBuilder paramStr = new StringBuilder();
        for (Object arg : args) {
            if (arg == null) {
                paramStr.append("null, ");
            } else {
                String argStr = arg.toString();
                // 脱敏处理
                if (argStr.toLowerCase().contains("password")) {
                    argStr = argStr.replaceAll("(?i)password.?=.?[^,;\s]+", "password=******");
                }
                paramStr.append(argStr).append(", ");
            }
        }
        if (paramStr.length() > 0) paramStr.setLength(paramStr.length() - 2);

        int resultID;
        String resultStr;
        if (result == null) {
            resultID = 1;
            resultStr = "null";
        } else if (result instanceof java.util.Collection) {
            resultID = 2;
            resultStr = "Collection(size=" + ((java.util.Collection<?>) result).size() + ")";
        } else {
            resultID = 3;
            try {
                resultStr = filterObjectFields(result);
            } catch (Exception e) {
                String full = result.toString();
                resultStr = full.length() > 200 ? full.substring(0, 200) + "..." : full;
            }
        }
        log.debug("Result ID: {}", resultID);
        log.debug("[Controller] {}.{} called, params: [{}], return: {}", className, methodName, paramStr, resultStr);
    }
}
