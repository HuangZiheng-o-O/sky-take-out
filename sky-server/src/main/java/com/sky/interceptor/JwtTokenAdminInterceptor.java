package com.sky.interceptor;

import com.alibaba.fastjson.JSON;
import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
//        令牌的名称和令牌的值是两个不同的概念。
//
//        令牌的名称：jwtProperties.getAdminTokenName()="token" 令牌的名称是一个标识符，用于在请求中指示令牌的位置。
//        通常，它位于请求的头部（HTTP 请求头中的一个字段）或请求的参数中。它告诉服务器在哪里可以找到令牌值。
//        在您的配置中，admin-token-name: token 意味着令牌的名称是 "token"，服务器会在请求头中查找名为 "token" 的字段来获取令牌值。

//        令牌的值：令牌的值是一个包含身份验证或授权信息的字符串。
//        eyJhbGciOiJIUzI1NiJ9.eyJlbXBJZCI6MSwiZXhwIjoxNjk0NjA1MzkwfQ.Mg2mWgKRIZAPWJC5FhHf9abky-lE_XCOR8JtdP6PraI
//        在身份验证流程中，令牌的值通常是一个经过签名的令牌，其中包含了用户信息或其他相关信息。令牌的值是客户端用来向服务器证明身份或获得授权的凭据。
//        在您的代码中，token = request.getHeader(jwtProperties.getAdminTokenName()) 用于获取请求头中名为 "token" 的字段的值，这个值应该是一个 JWT（
//        JSON Web Token）字符串，其中包含了身份验证或授权信息。所以，令牌的名称是 "token"，而令牌的值是 JWT 字符串。

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：", empId);
            BaseContext.setCurrentId(empId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
