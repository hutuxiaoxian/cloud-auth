package com.zhishouwei.platform.cloud.auth.configure;

import com.zhishouwei.platform.cloud.auth.server.UserDetailService;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 增加对openid，sms_code的支持
 *
 */
public class MyTokenGranter extends AbstractTokenGranter {

    private UserDetailsService userDetailsService;
    private String grantType;
    public enum GrantType {
        SMS_CODE("sms_code"),
        OPENID("openid"),
        PASSWORD("password");
        private String code;
        private GrantType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public MyTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType, UserDetailsService userDetailsService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.grantType = grantType;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        String res = "";

        if (GrantType.SMS_CODE.code.equals(this.grantType)) {
            String mobile = tokenRequest.getRequestParameters().get("mobile");
            String code = tokenRequest.getRequestParameters().get("code");
            res = mobile + UserDetailService.SEPARATE + code;
        } else if(GrantType.OPENID.code.equals(this.grantType)) {
            String openid = tokenRequest.getRequestParameters().get("openid");
            res = "openid" + UserDetailService.SEPARATE + openid;
        } else {
            throw new InvalidGrantException("不是SMS_CODE或OPENID模式，无法对用户进行身份验证");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(res);

        Authentication userAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        ((AbstractAuthenticationToken) userAuth).setDetails(tokenRequest.getRequestParameters());
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("无法对用户进行身份验证");
        }
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
