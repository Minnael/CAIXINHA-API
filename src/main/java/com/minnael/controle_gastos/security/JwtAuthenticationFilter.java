package com.minnael.controle_gastos.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta todas as requisições HTTP, valida o JWT e armazena o userId no contexto.
 * Executado uma vez por requisição.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String headerName;

    @Value("${jwt.prefix}")
    private String headerPrefix;

    /**
     * Extrai o token JWT do header Authorization
     * Formato esperado: "Authorization: Bearer <token>"
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(headerName);
        
        if (header == null || !header.startsWith(headerPrefix + " ")) {
            return null;
        }
        
        return header.substring(headerPrefix.length() + 1);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        
        // Endpoints públicos que não requerem autenticação
        if (isPublicEndpoint(requestURI)) {
            log.debug("Endpoint público acessado: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);
            
            if (token == null) {
                log.warn("Request sem token JWT para endpoint protegido: {}", requestURI);
                sendUnauthorizedResponse(response, "Token de autenticação não fornecido");
                return;
            }

            // Valida token e extrai userId
            String userId = jwtUtil.extractUserId(token);
            
            // Armazena userId no contexto do thread
            UserContext.setUserId(userId);
            
            log.debug("Requisição autenticada para userId: {} em {}", userId, requestURI);
            
            // Continua a cadeia de filtros
            filterChain.doFilter(request, response);
            
        } catch (JwtException e) {
            log.error("Token JWT inválido ou expirado: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Token inválido ou expirado");
        } catch (Exception e) {
            log.error("Erro inesperado durante autenticação: ", e);
            sendUnauthorizedResponse(response, "Erro na autenticação");
        } finally {
            // Limpa o contexto para evitar memory leaks
            UserContext.clear();
        }
    }

    /**
     * Define quais endpoints são públicos (não requerem autenticação)
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/swagger-ui") ||
               uri.startsWith("/v3/api-docs") ||
               uri.startsWith("/api-docs") ||
               uri.equals("/") ||
               uri.startsWith("/actuator") ||
               uri.startsWith("/h2-console");
    }

    /**
     * Envia resposta 401 Unauthorized com mensagem de erro
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
            java.time.LocalDateTime.now(),
            message
        );
        
        response.getWriter().write(jsonResponse);
    }
}
