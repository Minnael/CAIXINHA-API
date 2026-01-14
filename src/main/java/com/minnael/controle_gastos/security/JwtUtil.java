package com.minnael.controle_gastos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitário para validação de JWT gerados pelo microserviço Node.js.
 * Usa a MESMA secret key para garantir interoperabilidade.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Gera a chave secreta a partir da string configurada
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valida o token JWT e retorna os claims se válido
     * @param token JWT token
     * @return Claims contendo payload do token
     * @throws JwtException se token for inválido, expirado ou malformado
     */
    public Claims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            log.debug("Token validado com sucesso para userId: {}", claims.get("id"));
            return claims;
        } catch (JwtException e) {
            log.error("Erro ao validar token JWT: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extrai o userId do token JWT
     * O microserviço Node.js armazena como "id" no payload
     */
    public String extractUserId(String token) {
        Claims claims = validateToken(token);
        Object userId = claims.get("id");
        
        if (userId == null) {
            log.error("Token JWT não contém campo 'id'");
            throw new JwtException("Token inválido: campo 'id' não encontrado");
        }
        
        return userId.toString();
    }

    /**
     * Extrai o login do usuário do token
     */
    public String extractLogin(String token) {
        Claims claims = validateToken(token);
        return claims.get("login", String.class);
    }

    /**
     * Verifica se o token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Extrai a data de expiração do token
     */
    public Date getExpirationDate(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration();
    }
}
