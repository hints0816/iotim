package org.hints.auth.service;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hints.auth.mapper.ClientMapper;
import org.hints.auth.mapper.UserMapper;
import org.hints.auth.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Data
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {
    private static final Logger log = LogManager.getLogger(ClientDetailsServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClientMapper clientMapper;

    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ClientDetails clientDetails = (ClientDetails) redisTemplate.opsForValue().get(clientRedisKey(clientId));
        if (clientDetails == null) {
            clientDetails = cacheAndGetClient(clientId);
        }
        return clientDetails;
    }

    private ClientDetails cacheAndGetClient(String clientId) {
        // 从数据库读取
        ClientDetails clientDetails = null;
        try {
            clientDetails = getBaseClientDetails(clientId);
            if (clientDetails != null) {
                // 写入redis缓存
                redisTemplate.opsForValue().set(clientRedisKey(clientId), clientDetails, clientDetails.getAccessTokenValiditySeconds(), TimeUnit.SECONDS);
                log.info("缓存clientId:{},{}", clientId, clientDetails);
            }
        } catch (NoSuchClientException e) {
            log.error("clientId:{},{}", clientId, clientId);
        } catch (InvalidClientException e) {
            log.error("cacheAndGetClient-invalidClient:{}", clientId, e);
        }
        return clientDetails;
    }

    private BaseClientDetails getBaseClientDetails(String clientId) {
        Client client = clientMapper.findByClientId(clientId);
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        if (client != null) {
            baseClientDetails.setClientId(client.getClientId());
            baseClientDetails.setClientSecret(client.getClientSecret());
            baseClientDetails.setAuthorizedGrantTypes(Arrays.asList(client.getAuthorizedGrantTypes().split(",")));
            baseClientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds().intValue());
            baseClientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds().intValue());
            baseClientDetails.setScope(Arrays.asList(client.getScopes().split(",")));
            HashSet hs=new HashSet();
            hs.add(client.getRedirectUri());
            baseClientDetails.setRegisteredRedirectUri(hs);
            // 获取是否有授权页面开关
            baseClientDetails.setAutoApproveScopes(Arrays.asList(client.getAutoApproveScopes()));
            baseClientDetails.setResourceIds(Arrays.asList(client.getResourceId()));
        }
        return baseClientDetails;
    }

    private String clientRedisKey(String clientId) {
        return "resource" + ":" + clientId;
    }
}

