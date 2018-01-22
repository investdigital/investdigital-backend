package com.oxchains.rmsuser.service;

import com.oxchains.rmsuser.common.RegexUtils;
import com.oxchains.rmsuser.dao.UserRepo;
import com.oxchains.rmsuser.entity.SSOUser;
import com.oxchains.rmsuser.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ccl
 * @time 2018-01-12 10:13
 * @name UserAuthService
 * @desc:
 */
@Service
public class UserAuthService implements UserDetailsService{
    @Resource
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = null;
        if(RegexUtils.match(s,RegexUtils.REGEX_EMAIL)){
            user = userRepo.findByEmail(s);
        }else if(RegexUtils.match(s,RegexUtils.REGEX_MOBILEPHONE)){
            user = userRepo.findByMobilephone(s);
        }else {
            user = userRepo.findByLoginname(s);
        }

        if(null == user){
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
        return new SSOUser(user);
    }

}
