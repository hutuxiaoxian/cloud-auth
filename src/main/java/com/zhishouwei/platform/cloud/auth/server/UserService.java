package com.zhishouwei.platform.cloud.auth.server;

import com.zhishouwei.common.model.AuthUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${authserver.load-service:auth-service}",
        fallback = UserServiceFallbackImpl.class
)
public interface UserService {
    /**
     * 用户名方式验证用户是否合法
     * @param name  需要验证的用户名
     * @return AuthUser，用户的基本信息，包含用户名、权限、角色。当存在角色时会覆盖用户权限
     */
    @PostMapping({"/account/findUserByUsername"})
    AuthUser findUserByUsername(@RequestParam("username") String name);

    /**
     * 用手机号方式验证用户是否合法
     * @param phone  需要验证的手机号
     * @return AuthUser，用户的基本信息，包含用户名、权限、角色。当存在角色时会覆盖用户权限
     */
    @PostMapping({"/account/findUserByMobile"})
    AuthUser findUserByMobile(@RequestParam("mobile") String phone);

    /**
     * OpenId方式验证用户是否合法
     * @param openId  需要验证的OpenId
     * @return AuthUser，用户的基本信息，包含用户名、权限、角色。当存在角色时会覆盖用户权限
     */
    @PostMapping({"/account/findUserByOpenId"})
    AuthUser findUserByOpenId(@RequestParam("openId") String openId);

    /**
     * 手机号+验证码方式验证用户是否合法
     * @param mobile  需要验证的手机号
     * @param code 验证码
     * @return AuthUser，用户的基本信息，包含用户名、权限、角色。当存在角色时会覆盖用户权限
     */
    @PostMapping({"/account/findUserByMobileAndCode"})
    AuthUser findUserByMobileAndCode(@RequestParam("mobile") String mobile, @RequestParam("code") String code);
}

