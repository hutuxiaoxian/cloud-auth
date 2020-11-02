package com.zhishouwei.platform.cloud.auth.server;

import com.zhishouwei.common.model.AuthUser;
import com.zhishouwei.common.utils.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userDetailService")
public class UserDetailService implements UserDetailsService {

    public static final String SEPARATE = ":_@4@_:";

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthUser userDetails = null;
        String errMsg = "账号不存在或密码不正确";
        if (s.contains(SEPARATE)) {
            String[] arr = s.split(SEPARATE);
            final int length = 2;
            if (arr.length == length && StringUtils.isMobile(arr[0])) {
                userDetails = userService.findUserByMobileAndCode(arr[0], arr[1]);
                return userDetailsByAuthUser(userDetails);
            }
            errMsg = "手机号或验证码不存在";
        } else if (s.startsWith("openid" + SEPARATE)) {
            s = s.replace("openid" + SEPARATE, "");
            if (StringUtils.isOpenID(s)) {
                userDetails = userService.findUserByOpenId(s);
                errMsg = "第三方OpenId不存在";
            }
        } else if (StringUtils.isMobile(s)) {
            userDetails = userService.findUserByMobile(s);
            errMsg = "手机号不存在";
        }
        if (userDetails == null) {
            userDetails = userService.findUserByUsername(s);
        }
        if (userDetails == null) {
            throw new UsernameNotFoundException(errMsg);
        }

        return userDetailsByAuthUser(userDetails);
    }

    /**
     * 查询到的AuthUser数据装载到UserDetails
     * @param authUser 查询到的AuthUser数据
     * @return UserDetails
     */
    private UserDetails userDetailsByAuthUser(AuthUser authUser) {
        User.UserBuilder builder =  AuthUser.builder()
                .username(authUser.getUsername())
                .password(authUser.getPassword())
                .accountExpired(!authUser.isAccountNonExpired())
                .credentialsExpired(!authUser.isCredentialsNonExpired())
                .accountLocked(!authUser.isAccountNonLocked())
                .disabled(!authUser.isEnabled())
                .authorities(authUser.getResources());
        if (authUser.getResources() == null && authUser.getResources().length == 0) {
            builder.roles(authUser.getRoles());
        }
        return builder.build();
    }

}
