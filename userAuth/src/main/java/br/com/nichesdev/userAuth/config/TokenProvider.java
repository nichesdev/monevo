package br.com.nichesdev.userAuth.config;


import br.com.nichesdev.userAuth.domain.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.key}")
    private String key;

    //Gerar Token
    public String gerarToken (Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        return buildToken(user.getEmail(), user.getId());
    }

    public String buildToken(String email, Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigninKey(), Jwts.SIG.HS256)
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
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}
