package info.investdigital;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author luoxuri
 * @create 2017-11-28 15:58
 **/
@EnableWebSecurity
@Configuration
public class ApplicationConfiguration extends WebSecurityConfigurerAdapter {

    //    private final JwtAuthenticationProvider jwtAuthenticationProvider;
//    private final JwtTokenFilter jwtTokenFilter;
//    private AuthError authError;
//
//    public UserApplicationConfiguration(@Autowired JwtTokenFilter jwtTokenFilter, @Autowired JwtAuthenticationProvider jwtAuthenticationProvider, @Autowired AuthError authError) {
//        this.jwtTokenFilter = jwtTokenFilter;
//        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
//        this.authError = authError;
//    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/user/*","/token","/account/*").permitAll()
//                //.antMatchers("/user/phone").authenticated()
//                .antMatchers("/**/*")
//                .authenticated().and()
//                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling()
//                .authenticationEntryPoint(authError)
//                .accessDeniedHandler(authError);
        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/**/*","/**/*/*").permitAll().and().exceptionHandling();
    }



    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        //auth.authenticationProvider(jwtAuthenticationProvider);
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
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}
