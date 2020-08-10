package com.shalpha.app.client;

import org.springframework.context.annotation.Bean;

import com.shalpha.app.security.oauth2.AuthorizationHeaderUtil;

import feign.RequestInterceptor;

public class OAuth2InterceptedFeignConfiguration {

    @Bean(name = "oauth2RequestInterceptor")
    public RequestInterceptor getOAuth2RequestInterceptor(AuthorizationHeaderUtil authorizationHeaderUtil) {
        return new TokenRelayRequestInterceptor(authorizationHeaderUtil);
    }
}
