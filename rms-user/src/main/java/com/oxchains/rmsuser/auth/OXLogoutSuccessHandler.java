package com.oxchains.rmsuser.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ccl
 * @time 2018-01-10 14:43
 * @name OXLogoutSuccessHandler
 * @desc:
 */
@Slf4j
public class OXLogoutSuccessHandler  implements LogoutSuccessHandler{
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        log.info("退出成功");
        String redirectUrl = httpServletRequest.getParameter("redirectUrl");
        httpServletResponse.sendRedirect(redirectUrl);
    }
}
