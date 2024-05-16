package com.xbxie.mall.admin.config;

import com.xbxie.mall.admin.component.MyAccessDeniedHandler;
import com.xbxie.mall.admin.component.MyAuthenticationEntryPoint;
import com.xbxie.mall.admin.fliter.JwtFilter;
import org.assertj.core.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Spring Security 配置类
 * created by xbxie on 2024/5/12
 */
@Configuration
// @EnableWebSecurity
public class SecurityConfig {
    @Resource
    private JwtFilter jwtFilter;

    @Resource
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Resource
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement().disable()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/login", "/auth/logout").permitAll()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(myAuthenticationEntryPoint)
            .accessDeniedHandler(myAccessDeniedHandler)
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // @Bean
    // CorsConfigurationSource corsConfigurationSource() {
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowCredentials(true);
    //     configuration.setAllowedOrigins(Collections.singletonList("*"));
    //     configuration.setAllowedMethods(Collections.singletonList("*"));
    //     configuration.setAllowedHeaders(Collections.singletonList("*"));
    //     source.registerCorsConfiguration("/**",configuration);
    //     return source;
    // }

    // @Bean
    // CorsConfigurationSource corsConfigurationSource(){
    //     CorsConfiguration corsConfiguration = new CorsConfiguration();
    //     //允许从百度站点跨域
    //     corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
    //     //允许GET和POST方法
    //     corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
    //     //允许携带凭证
    //     corsConfiguration.setAllowCredentials(true);
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     //对所有URL生效
    //     source.registerCorsConfiguration("/**",corsConfiguration);
    //     return source;
    // }

    // @Bean
    // CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(Collections.singletonList("*"));
    //     configuration.setAllowedMethods(Collections.singletonList("*"));
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }
}
