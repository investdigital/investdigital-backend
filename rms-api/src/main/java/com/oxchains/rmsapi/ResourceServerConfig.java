package com.oxchains.rmsapi;

/**
 * @author ccl
 * @time 2018-01-11 13:33
 * @name ResoutceServerConfig
 * @desc:
 */
//@EnableResourceServer
public class ResourceServerConfig /*extends ResourceServerConfigurerAdapter*/{
    /*@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
    }*/
/*
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/api/**").access("#oauth2.hasScope('write')");
    }*/
}
