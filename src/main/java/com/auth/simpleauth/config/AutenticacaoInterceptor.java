package com.auth.simpleauth.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Libera requisições PREFLIGHT (OPTIONS) do CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 2. Libera a rota do QR Code para não exigir cookie de sessão no navegador
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/qrcode")) {
            return true;
        }

        // 3. Validação normal de sessão para as demais rotas da aplicação
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Acesso não autorizado! Efetue o login no sistema.");
        return false;
    }
}