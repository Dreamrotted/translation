package com.room.interceptor;

import com.google.gson.Gson;
import com.room.common.Result;
import com.room.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = JwtUtils.validateToken(token);
            if (username != null) {
                return true;
            }
        }
        // 返回401，表示未登录或登录已过期
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        returnJson(response);
        return false;
    }

    /**
     * 返回格式化后的json到前端页面
     */
    private void returnJson(HttpServletResponse response) throws Exception {
        PrintWriter writer = null;
        // 配置为json格式
        response.setContentType("application/json;charset=UTF-8");
        try {
            Result<Object> result = new Result<>();
            result.setCode(401);
            result.setMessage("未登录或登录已过期");
            writer = response.getWriter();
            // 使用gson返回json格式
            Gson gson = new Gson();
            writer.print(gson.toJson(result));
        } catch (IOException e) {
            logger.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
