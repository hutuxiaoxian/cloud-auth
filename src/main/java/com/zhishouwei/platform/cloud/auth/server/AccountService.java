package com.zhishouwei.platform.cloud.auth.server;

import com.zhishouwei.platform.cloud.auth.entity.OauthClientDetails;
import com.zhishouwei.platform.cloud.auth.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountService {

    @Resource
    private AccountMapper mapper;

    /**
     * 认证企业客户id是否存在
     * @param clientId 企业客户id
     * @return 是否存在
     */
    public boolean checkClientId(String clientId) {
        boolean checked = false;
        OauthClientDetails item = mapper.findByClientId(clientId);
        if (item == null) {
            checked = true;
        }
        return checked;
    }

    /**
     * 新增企业认证数据
     * @param details 认证数据
     * @return 是否插入成功
     */
    public boolean insert(OauthClientDetails details) {
        details.setAuthorizedGrantTypes(details.getAuthorizedGrantTypes().replace(" ", ""));
        return mapper.insert(details) > 0 ? true : false;
    }
}
