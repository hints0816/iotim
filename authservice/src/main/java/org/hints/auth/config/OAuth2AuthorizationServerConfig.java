package org.hints.auth.config;

import org.hints.auth.service.ClientDetailsServiceImpl;
import org.hints.auth.service.UserServiceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Arrays;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/7/28 18:29
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore jwtTokenStore;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceDetail userServiceDetail;

    @Autowired
    private JwtTokenConfig jwtTokenConfig;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CustomOAuth2RequestFactory customOAuth2RequestFactory;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //1.认证信息从数据库获取
        ClientDetailsServiceImpl clientDetailsService = new ClientDetailsServiceImpl();
        clientDetailsService.setRedisTemplate(redisTemplate);
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //Token的存储方式为内存
                .tokenStore(jwtTokenStore)
                //WebSecurity配置好的
                .authenticationManager(authenticationManager)
                //读取用户的验证信息
                .userDetailsService(userServiceDetail)
                .requestFactory(customOAuth2RequestFactory);
        //新建一个令牌增强链(payload)
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(JwtTokenEnhancer(), jwtTokenConfig.jwtAccessTokenConverter()));
        endpoints
                .accessTokenConverter(jwtTokenConfig.jwtAccessTokenConverter())
                .tokenEnhancer(enhancerChain)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                //重定义授权页面
                .pathMapping("/oauth/confirm_access", "/custom/confirm_access");
    }

    //目前来说是固定格式
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许表单认证
        security
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()");
    }

    @Bean
    public TokenEnhancer JwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

}