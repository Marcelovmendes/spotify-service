package com.example.spotify.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession
public class SessionConfig {

    @Value("${session.cookie.name:SESSIONID}")
    private String cookieName;

    @Value("${session.cookie.domain:}")
    private String cookieDomain;

    @Value("${session.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${session.cookie.secure:false}")
    private boolean cookieSecure;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(cookieName);
        serializer.setCookiePath("/");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite(cookieSameSite);
        serializer.setUseSecureCookie(cookieSecure);
        if (!cookieDomain.isEmpty()) {
            serializer.setDomainNamePattern("^.+?(\\w+\\.\\w+)$");
        }
        return serializer;
    }
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
          return new GenericJackson2JsonRedisSerializer();
      }


}
