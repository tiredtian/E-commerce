package com.asiainfo.mall.filter;

import com.asiainfo.mall.common.ApiRestResponse;
import com.asiainfo.mall.common.Constant;
import com.asiainfo.mall.exception.MallException;
import com.asiainfo.mall.exception.MallExceptionEnum;
import com.asiainfo.mall.model.pojo.Category;
import com.asiainfo.mall.model.pojo.User;
import com.asiainfo.mall.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 管理员校验过滤器
 */
public class AdminFilter implements Filter {

    public User currentUser;

    @Autowired
    private UserService userService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(Constant.JWT_TOKEN);
        if (token == null) {
            PrintWriter out = new HttpServletResponseWrapper(
                    (HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "    \"status\": 10007,\n"
                    + "    \"msg\": \"NEED_JWT_TOKEN\",\n"
                    + "    \"data\": null\n"
                    + "}");
            out.flush();
            out.close();
            return;
        }
        Algorithm algorithm = Algorithm.HMAC256(Constant.JWT_KEY);
        //jwt解码器
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = jwtVerifier.verify(token);
            currentUser = new User();
            currentUser.setUsername(jwt.getClaim(Constant.USER_NAME).asString());
            currentUser.setId(jwt.getClaim(Constant.USER_ID).asInt());
            currentUser.setRole(jwt.getClaim(Constant.USER_ROLE).asInt());
        }catch(TokenExpiredException e){
            throw new MallException(MallExceptionEnum.TOKEN_EXPIRED);
        }catch(JWTDecodeException e){
            throw new MallException(MallExceptionEnum.TOKEN_WRONG);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
