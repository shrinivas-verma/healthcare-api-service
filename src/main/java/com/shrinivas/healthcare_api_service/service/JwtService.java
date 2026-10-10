package com.shrinivas.healthcare_api_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public SecretKey getSecretKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwt(String username){
        long now=System.currentTimeMillis();
        Map<String,Object> claims=new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now+ 1000*60*30 ))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    public boolean validateToken(String token, UserDetails userDetails){
//        final String username=extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && isUserEnabled(userDetails) );
//    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date())
                    && userDetails.isEnabled();
        } catch (JwtException | IllegalArgumentException exception) {
//            prevents malformed, expired, or incorrectly signed tokens from crashing the request with an unhandled exception.
            return false;
        }
    }

    private boolean isUserEnabled(UserDetails userDetails) {
        return userDetails.isEnabled();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

}
