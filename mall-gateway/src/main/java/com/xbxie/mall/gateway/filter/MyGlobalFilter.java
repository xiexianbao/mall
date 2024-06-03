package com.xbxie.mall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xbxie.mall.common.utils.R;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * created by xbxie on 2024/5/22
 */
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 不需要 token 的接口
    private static final List<String> tokenWhiteList = new ArrayList<>(Arrays.asList(
        "/auth/login/admin",
        "/auth/login/member",
        "/auth/register/member",
        "/product/homepage/detail",
        "/product/spu/*"
    ));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 不需要 token 的路径
        if (tokenWhiteList.stream().anyMatch(path -> antPathMatcher.match(path, request.getPath().toString()))) {
            System.out.println("不需要 token");
            return chain.filter(exchange);
        }


        // 需要 token 的路径
        String authorization = request.getHeaders().getFirst("Authorization");
        System.out.println("authorization = " + authorization);
        if (!StringUtils.hasLength(authorization)) {
            System.out.println("未登录");

            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            R<Void> failR = R.fail(401, "token验证失败");
            byte[] bytes = JSONObject.toJSONString(failR).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));
        }


        System.out.println("已登录");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
