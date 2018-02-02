package com.oxchains.rmsuser.auth;

import com.oxchains.rmsuser.common.IndexUtils;
import com.oxchains.rmsuser.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * @author aiet
 */
@Slf4j
@Component
public class JwtTokenFilter implements Filter {
private Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final JwtService jwtService;

    @Autowired
    public JwtTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String authorization = servletRequest.getHeader(AuthorizationConst.AUTHORIZATION_HEADER);
        String uri = servletRequest.getRequestURI();
        int index = IndexUtils.getIndex(uri, "/");
        String subUri = uri.substring(0, index);
        log.info("sub uri="+subUri);
        log.info("auth-token=" + authorization);
        String method = ((HttpServletRequest) request).getMethod();
        log.info("HTTP REQUEST METHOD: " + method);
        if (authorization != null && authorization.startsWith(AuthorizationConst.AUTHORIZATION_START)) {
            jwtService
                    .parse(authorization.replaceAll(AuthorizationConst.AUTHORIZATION_START, ""), uri)
                    .ifPresent(jwtAuthentication ->{
                        User user = jwtAuthentication.getUser();
                        Set<String> set = user.getPermissionUriSet();
                        if(set.contains(subUri)){
                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(jwtAuthentication);
                        }else{
//                            try {
//                                response.getOutputStream().print("error");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
                    });
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
