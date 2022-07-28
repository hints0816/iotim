package org.hints.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/28 18:29
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 用户认证 Manager
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("isAuthenticated()");
    }

    //进行 Client 客户端的配置。
    //设置使用基于内存的 Client 存储器。实际情况下，最好放入数据库中，方便管理。
    /*
     *
     * 创建一个 Client 配置。如果要继续添加另外的 Client 配置，可以在 <4.3> 处使用 #and() 方法继续拼接。
     * 注意，这里的 .withClient("clientapp").secret("112233") 代码段，就是 client-id 和 client-secret。
     *补充知识：可能会有胖友会问，为什么要创建 Client 的 client-id 和 client-secret 呢？
     *通过 client-id 编号和 client-secret，授权服务器可以知道调用的来源以及正确性。这样，
     *即使“坏人”拿到 Access Token ，但是没有 client-id 编号和 client-secret，也不能和授权服务器发生有效的交互。
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // <4.1>
                .withClient("clientapp").secret("112233") // <4.2> Client 账号、密码。
                .authorizedGrantTypes("password") // <4.2> 密码模式
                .scopes("read_userinfo", "read_contacts") // <4.2> 可授权的 Scope
//                .and().withClient() // <4.3> 可以继续配置新的 Client
        ;
    }

}