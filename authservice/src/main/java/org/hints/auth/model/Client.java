package org.hints.auth.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Client {
    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 授权码模式
     */
    private String authorizedGrantTypes;

    /**
     * 回调地址
     */
    private String redirectUri;

    /**
     * TOKEN失效时长（秒）
     */
    private Double accessTokenValiditySeconds;

    /**
     * REFRESH_TOKEN失效时长（秒）
     */
    private Double refreshTokenValiditySeconds;

    /**
     * 作用域
     */
    private String scopes;

    /**
     * 是否授权页面
     */
    private String autoApproveScopes;

    /**
     * 资源id
     */
    private String resourceId;

    /**
     * 资源id
     */
    private String clientName;

    /**
     * 资源id
     */
    private Integer applicationType;

    /**
     * 资源id
     */
    private String introduction;

    /**
     * 资源id
     */
    private Date createTime;

    /**
     * 资源id
     */
    private Date updateTime;
}