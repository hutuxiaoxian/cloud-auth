package com.zhishouwei.platform.cloud.auth.server;

import com.alibaba.fastjson.JSONObject;
import com.zhishouwei.common.model.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceFallbackImpl implements UserService {
    @Override
    public AuthUser findUserByUsername(String name) {
        log.info("调用{}失败，参数:{}", "findUserByUsername", name);
        return null;
    }

    @Override
    public AuthUser findUserByMobile(String phone) {
        log.info("调用{}失败，参数:{}", "通过手机号查询用户", phone);
        return null;
    }

    @Override
    public AuthUser findUserByOpenId(String openId) {
        log.info("调用{}失败，参数:{}", "通过OpenId查询用户", openId);
        return null;
    }

    @Override
    public AuthUser findUserByMobileAndCode(String mobile, String code) {
        log.info("调用{}失败，参数:{} code:{}", "通过mobile和identifyCode查询用户", mobile, code);
        return null;
    }
}
