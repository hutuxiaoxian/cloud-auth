package com.zhishouwei.platform.cloud.auth.configure;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableAuthorizationServer // /oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error
public class AuthServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Resource
    private DruidDataSource dataSource;

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private DefaultTokenServices tokenServices;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        // 允许表单认证
        security.allowFormAuthenticationForClients();
        // 允许check_token访问
        security.tokenKeyAccess("permitAll()");
        security.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //方式一授权码授权模式
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }



    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST)
                .tokenServices(tokenServices);

        endpoints.tokenGranter((new CompositeTokenGranter(Arrays.asList(
                endpoints.getTokenGranter(),
                tokenGranter(MyTokenGranter.GrantType.OPENID.getCode()
                        ,endpoints.getClientDetailsService(),
                        endpoints.getOAuth2RequestFactory() ), // 拦截openid方式
                tokenGranter(MyTokenGranter.GrantType.SMS_CODE.getCode()
                        ,endpoints.getClientDetailsService(),
                        endpoints.getOAuth2RequestFactory() ) // 拦截sms_code方式
        ))));
    }
    /**
     * 生成装饰器，用于处理sms_code和openid方式认证
     * @param grantType 认证方式：sms_code,openid
     * @param detailsService    客户端服务
     * @param factory   oauth2工厂
     * @return  装饰器
     */
    private MyTokenGranter tokenGranter(String grantType, ClientDetailsService detailsService, OAuth2RequestFactory factory) {
        return new MyTokenGranter(
                tokenServices,
                detailsService,
                factory,
                grantType,
                userDetailsService
        );
    }


}
