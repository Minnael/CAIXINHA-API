package com.minnael.controle_gastos.security;

import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocal para armazenar o userId autenticado durante a requisição.
 * Garante isolamento multi-tenant sem passar userId por toda a cadeia de chamadas.
 */
@Slf4j
public class UserContext {

    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    /**
     * Define o userId do usuário autenticado para o thread atual
     */
    public static void setUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            log.warn("Tentativa de setar userId vazio ou nulo");
            return;
        }
        currentUserId.set(userId);
        log.debug("UserId setado no contexto: {}", userId);
    }

    /**
     * Retorna o userId do usuário autenticado no thread atual
     * @throws SecurityException se não houver usuário autenticado
     */
    public static String getUserId() {
        String userId = currentUserId.get();
        if (userId == null) {
            log.error("Tentativa de acessar userId sem autenticação");
            throw new SecurityException("Usuário não autenticado");
        }
        return userId;
    }

    /**
     * Retorna o userId se existir, caso contrário null
     */
    public static String getUserIdOrNull() {
        return currentUserId.get();
    }

    /**
     * Verifica se há usuário autenticado no contexto
     */
    public static boolean hasUser() {
        return currentUserId.get() != null;
    }

    /**
     * Limpa o userId do contexto (importante para evitar memory leaks)
     */
    public static void clear() {
        String userId = currentUserId.get();
        if (userId != null) {
            log.debug("Limpando userId do contexto: {}", userId);
        }
        currentUserId.remove();
    }
}
