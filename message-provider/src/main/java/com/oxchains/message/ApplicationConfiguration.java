package com.oxchains.message;

import com.oxchains.message.auth.AuthError;
import com.oxchains.message.auth.JwtAuthenticationProvider;
import com.oxchains.message.auth.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * @author ccl
 * @time 2017-10-12 11:21
 * @name ApplicationConfiguration
 * @desc:
 */

@EnableWebSecurity
@Configuration
public class ApplicationConfiguration extends WebSecurityConfigurerAdapter{

    private JwtAuthenticationProvider jwtAuthenticationProvider;
    private JwtTokenFilter jwtTokenFilter;
    private AuthError authError;

    ApplicationConfiguration(@Autowired JwtAuthenticationProvider jwtAuthenticationProvider, @Autowired JwtTokenFilter jwtTokenFilter, @Autowired AuthError authError){
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtTokenFilter = jwtTokenFilter;
        this.authError = authError;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST,"/login","/token","/register", "/message/**/*").permitAll()
                .antMatchers(HttpMethod.GET, "/message/**/*").permitAll().antMatchers("/**/*")
                .authenticated().and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authError)
                .accessDeniedHandler(authError);

//        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/**/*", "/**/*/*").permitAll();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    /**
     * allow cross origin requests
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}
