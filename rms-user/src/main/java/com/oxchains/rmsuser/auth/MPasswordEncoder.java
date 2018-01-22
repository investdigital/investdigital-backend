package com.oxchains.rmsuser.auth;

import com.oxchains.rmsuser.common.EncryptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author ccl
 * @time 2018-01-12 15:55
 * @name MPasswordEncoder
 * @desc:
 */
public class MPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        String password = charSequence.toString();
        password = EncryptUtils.encodeSHA256(password);
        return password;
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence).equals(s);
    }
}
