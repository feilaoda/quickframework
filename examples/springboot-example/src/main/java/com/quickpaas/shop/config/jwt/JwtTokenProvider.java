package com.quickpaas.shop.config.jwt;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quickpaas.framework.exception.ErrorCode;
import com.quickpaas.framework.exception.WebException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtTokenProvider implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final long serialVersionUID = -2550185165626007488L;

//    @Value("${jwt.secret}")
//    private String secret;
    @Value("${jwt.validTime}")
    private Long validTime;
    @Autowired
    private SecretKey secretKey;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private Cache<String, Long> tokenCache;
    private Cache<String, JwtUserDetails> userCache;
    public JwtTokenProvider() {
        tokenCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
        userCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();
    }

    public JwtUserDetails getUserFromToken(String token) {
        JwtUserDetails details = userCache.getIfPresent(token);
        if(details != null) {
            return details;
        }else {
            String username = getClaimFromToken(token, Claims::getSubject);
            JwtUserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if(userDetails != null) {
                userCache.put(token, userDetails);
            }
            return userDetails;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            LOG.error(e.getMessage(), e);
        }
        return new DefaultClaims();
    }

    public Boolean validate(String token) {
        try {
            if(tokenCache.getIfPresent(token) == null) {
                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
                final Date expiration = getExpirationDateFromToken(token);
                boolean ok = !expiration.before(new Date());
                if (!ok) {
                    throw new WebException(ErrorCode.Code(401, "需要重新登录"));
                }
                tokenCache.put(token, expiration.getTime());
                return ok;
            }else {
                return true;
            }
        } catch (JwtException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }


    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validTime * 1000))
                .signWith(secretKey).compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return userDetails != null && username != null && (username.equals(userDetails.getUsername()) && !validate(token));
    }
}