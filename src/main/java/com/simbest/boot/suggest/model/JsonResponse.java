package com.simbest.boot.suggest.model;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class JsonResponse<T> {
    /**
     * 成功提示信息
     */
    public static final String MSG_SUCCESS = "操作成功";

    /**
     * 失败提示信息
     */
    public static final String MSG_ERRO = "操作失败";

    /**
     * 未授权访问提示信息
     */
    public static final String ACCESS_FORBIDDEN = "无权限访问";

    /**
     * 成功响应的错误码
     */
    public static final int SUCCESS_CODE = 0;

    /**
     * 失败响应的错误码
     */
    public static final int ERROR_CODE = -1;

    /**
     * 成功响应的HTTP状态码
     */
    public static final int SUCCESS_STATUS = 200;

    /**
     * 失败响应的HTTP状态码
     */
    public static final int ERROR_STATUS = 500;

    /**
     * 错误码，0表示成功，非0表示失败
     */
    @NonNull
    private Integer errcode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timestamp;

    /**
     * 响应时间戳
     */
    private int status;

    /**
     * 错误类型描述
     */
    private String error;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 创建默认成功响应
     *
     * @return 成功响应对象，errcode=0，status=200
     */
    @SuppressWarnings("unchecked")
    public static <T> JsonResponse<T> defaultSuccessResponse() {
        return (JsonResponse<T>) JsonResponse.builder().errcode(SUCCESS_CODE).timestamp(new Date())
                .status(SUCCESS_STATUS).build();
    }

    /**
     * 创建默认失败响应
     *
     * @return 失败响应对象，errcode=-1，status=500，包含默认错误信息
     */
    @SuppressWarnings("unchecked")
    public static <T> JsonResponse<T> defaultErrorResponse() {
        JsonResponse<T> jsonResponse = (JsonResponse<T>) JsonResponse.builder().errcode(ERROR_CODE).message("未知错误")
                .timestamp(new Date()).status(ERROR_STATUS).build();
        return jsonResponse;
    }

    /**
     * 创建带数据的成功响应
     *
     * @param obj 响应数据
     * @return 成功响应对象，包含响应数据
     */
    public static <T> JsonResponse<T> success(T obj) {
        JsonResponse<T> response = defaultSuccessResponse();
        response.setData(obj);
        return response;
    }

    /**
     * 创建带数据的失败响应
     *
     * @param obj 响应数据
     * @return 失败响应对象，包含响应数据
     */
    public static <T> JsonResponse<T> fail(T obj) {
        JsonResponse<T> response = defaultErrorResponse();
        response.setData(obj);
        return response;
    }

    /**
     * 创建带提示信息的成功响应
     *
     * @param message 提示信息
     * @return 成功响应对象，包含提示信息
     */
    public static <T> JsonResponse<T> success(String message) {
        JsonResponse<T> response = defaultSuccessResponse();
        response.setMessage(message);
        return response;
    }

    /**
     * 创建带提示信息的失败响应
     *
     * @param message 提示信息
     * @return 失败响应对象，包含提示信息
     */
    public static <T> JsonResponse<T> fail(String message) {
        JsonResponse<T> response = defaultErrorResponse();
        response.setMessage(message);
        return response;
    }

    /**
     * 创建带数据和提示信息的成功响应
     *
     * @param obj     响应数据
     * @param message 提示信息
     * @return 成功响应对象，包含响应数据和提示信息
     */
    public static <T> JsonResponse<T> success(T obj, String message) {
        JsonResponse<T> response = defaultSuccessResponse();
        response.setData(obj);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建带数据和提示信息的失败响应
     *
     * @param obj     响应数据
     * @param message 提示信息
     * @return 失败响应对象，包含响应数据和提示信息
     */
    public static <T> JsonResponse<T> fail(T obj, String message) {
        JsonResponse<T> response = fail(obj);
        response.setMessage(message);
        return response;
    }

    /**
     * 创建带数据、提示信息和错误码的失败响应
     *
     * @param obj     响应数据
     * @param message 提示信息
     * @param errcode 错误码（参见ErrorCodeConstants）
     * @return 失败响应对象，包含响应数据、提示信息和错误码
     */
    public static <T> JsonResponse<T> fail(T obj, String message, Integer errcode) {
        JsonResponse<T> response = fail(obj);
        response.setMessage(message);
        response.setErrcode(errcode);
        return response;
    }

    /**
     * 创建未授权响应（无参数）
     *
     * @return 未授权响应对象，status=401
     */
    @SuppressWarnings("unchecked")
    public static <T> JsonResponse<T> unauthorized() {
        JsonResponse<T> response = (JsonResponse<T>) JsonResponse.builder().errcode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.name())
                .message(ACCESS_FORBIDDEN)
                .timestamp(new Date())
                .build();
        log.warn("无权限访问，即将返回【{}】", toJson(response));
        return response;
    }

    /**
     * 创建未授权响应（带请求和异常信息）
     *
     * @param request   HTTP请求对象
     * @param exception 导致未授权的异常
     * @return 未授权响应对象，包含异常信息
     */
    @SuppressWarnings("unchecked")
    public static <T> JsonResponse<T> unauthorized(HttpServletRequest request, Exception exception) {
        JsonResponse<T> response = (JsonResponse<T>) JsonResponse.builder().errcode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(exception.getMessage())
                .timestamp(new Date())
                .message(ACCESS_FORBIDDEN)
                /**
                 * 注释该代码，避免系统路径信息泄露
                 *
                 */
                // .path(request.getRequestURI())
                .build();
        log.warn("无权限访问，即将返回【{}】", toJson(response));
        return response;
    }

    /**
     * 创建已授权响应
     *
     * @return 已授权响应对象，errcode=0，status=200
     */
    @SuppressWarnings("unchecked")
    public static <T> JsonResponse<T> authorized() {
        return (JsonResponse<T>) JsonResponse.builder().errcode(SUCCESS_CODE)
                .status(HttpStatus.OK.value())
                .error(HttpStatus.OK.name())
                .build();
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 要转换的对象
     * @return JSON字符串
     */
    private static String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("转换对象到JSON失败", e);
            return obj.toString();
        }
    }
}
