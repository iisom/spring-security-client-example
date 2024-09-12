package com.galvanize.jwtclient.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("security.jwt")
public class JwtProperties {
    private String uri;
    private String header = "Authorization";
    private String prefix = "Bearer";
    // Used in the gLab-Identity-Provider 
    private int expiration = 24 * 60 * 60; //Default to 24 hours
    private String secret = "secret";

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getHeaderValue(String token) {
        return String.format("%s %s", this.getPrefix(), token);
    }
}
