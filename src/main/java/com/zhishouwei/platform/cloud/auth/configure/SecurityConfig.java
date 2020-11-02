package com.zhishouwei.platform.cloud.auth.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import com.zhishouwei.platform.cloud.auth.server.UserDetailService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
//@@EnableFeignClients()
@Component
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    /**
     * 声明Auth管理器
     * @return  当前使用的Auth管理器
     * @throws Exception 无法自动创建时抛出
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * 声明加密方式
     * @return 加密器
     */
    @Bean  //password加密的方式 相当于把PasswordEncoder类对象 注册到容器中
    PasswordEncoder passwordEncoder() {
        // 加密方式
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
    /**
     * 声明 TokenService服务
     * 用于载入token认证相关的服务
     * @return TokenService服务
     */
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);

        return defaultTokenServices;
    }
    /**
     * token存储方式,使用Redis方式，需要在配置文件中声明Redis配置且需要与Resources服务共用Redis
     * @return Redis存储
     */
    @Bean
    public RedisTokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("uaa/**")
                .fullyAuthenticated().and().httpBasic();  //拦截所有请求 通过httpBasic进行认证
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }


    /**
     * 设置添加用户信息,正常应该从数据库中读取
     * @return 用户管理服务，用于回调和装载用户Oauth
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }


}
