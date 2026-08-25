package br.com.nichesdev.userAuth.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.key}")
    private String key;

    //Gerar Token
    public String gerarToken (Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername());
    }

    public String buildToken(String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigninKey())
                .compact();
    }

    // Validação Token
    public boolean isTokenValid (String token) {
        try {
            getClaims(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        return getClaims(token).getSubject();
    }

    private Claims getClaims (String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSigninKey(){
        return Keys.hmacShaKeyFor(key.getBytes());
    }
}
