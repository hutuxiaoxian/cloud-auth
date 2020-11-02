package com.zhishouwei.platform.cloud.auth.entity;

import lombok.Data;

@Data
public class OauthClientDetails {
    private String clientId;
    private String resourceIds;
    private String clientSecret;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String authorities;
    private Long accessTokenValidity;
    private Long refreshTokenValidity;
    private String additionalInformation;
    private Integer autoapprove;
}
