package com.oxchains.rmsuser;


import com.oxchains.rmsuser.auth.*;
import com.oxchains.rmsuser.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;

import com.oxchains.rmsuser.auth.AuthError;
import com.oxchains.rmsuser.auth.JwtAuthenticationProvider;
import com.oxchains.rmsuser.auth.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @author ccl
 * @create 2018-01-09 10:58
 **/
@EnableWebSecurity
@Configuration
public class ApplicationConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtTokenFilter jwtTokenFilter;
    private AuthError authError;

    public ApplicationConfiguration(@Autowired JwtTokenFilter jwtTokenFilter, @Autowired JwtAuthenticationProvider jwtAuthenticationProvider, @Autowired AuthError authError) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.authError = authError;
    }

    @Resource
    private UserAuthService userAuthService;

    /**
     * 1. 配置认证管理器
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("oxchains").password("oxchains").authorities("*");
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.userDetailsService(userAuthService).passwordEncoder(passwordEncoder());
    }

    /**
     * 配置安全策略
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .requestMatchers()
                .antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
        // 权限验证使用下面
//        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/user/*").permitAll()
//                .antMatchers("/**/*").authenticated().and()
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling()
//                .authenticationEntryPoint(authError)
//                .accessDeniedHandler(authError);

//         或者 验证规则中加, "/menu/**/*", "/permission/**/*"，然后最后面加上下面的
//        .and()
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling()
//                .authenticationEntryPoint(authError)
//                .accessDeniedHandler(authError)
    }


    /**
     * allow cross origin requests
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("login").setViewName("login");
                registry.addViewController("/").setViewName("index");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MPasswordEncoder();
    }
}
