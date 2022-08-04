package com.quickpaas.shop.config.jwt;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtKeyConfig {
    public static final BaseEncoding BASE64 = BaseEncoding.base64();

    @Value("${jwt.secret}")
    private String jwtKey;

    @Bean
    public SecretKey secretKey() {
        SecretKey key = Keys.hmacShaKeyFor(BASE64.decode(jwtKey));
//        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//        System.out.println("JWT Key:" + BASE64.encode(key.getEncoded()));
        return key;
    }

    public static void main(String[] argv) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        System.out.println("JWT Key:" + BASE64.encode(key.getEncoded()));
    }
}
