package com.xbxie.mall.admin.fliter;

import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * created by xbxie on 2024/5/14
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Resource
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "*");


        String token = request.getHeader("token");
        System.out.println("doFilterInternal:" + token);
        // 没有传 token，直接放行
        if (!StringUtils.hasLength(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity userEntity;
        try {
            userEntity = JwtUtils.parseToken(token);
        } catch (Exception e) {
            System.out.println("解析 token 失败");
            filterChain.doFilter(request, response);
            return;
        }

        Long id = userEntity.getId();
        if (id == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity userEntity1 = userService.getById(id);
        if (userEntity1 == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEntity1.getAccount(), userEntity1.getPassword(), null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
