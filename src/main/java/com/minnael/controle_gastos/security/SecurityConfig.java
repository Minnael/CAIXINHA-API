package com.minnael.controle_gastos.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.List;

/**
 * Configuração de segurança da aplicação.
 * Define políticas de CORS e registra o filtro de autenticação JWT.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Registra o filtro JWT para autenticação em todas as requisições
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthenticationFilter);
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1); // Executa antes de outros filtros
        return registrationBean;
    }

    /**
     * Configuração de CORS para permitir requisições do app React Native
     * e possíveis frontends web
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir origens específicas (ajuste conforme seu ambiente)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",      // Frontend web dev
            "http://localhost:19006",     // Expo web
            "exp://localhost:19000",      // Expo app
            "http://192.168.*.*:*",       // Rede local para testes mobile
            "https://seu-dominio.com"     // Produção
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));
        
        // Headers expostos ao cliente
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type"
        ));
        
        // Não permite credenciais (cookies) pois usamos header-based auth
        configuration.setAllowCredentials(false);
        
        // Cache da configuração CORS por 1 hora
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Filtro de CORS que aplica a configuração acima
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(0); // Executa primeiro (antes do JWT filter)
        return bean;
    }
}
