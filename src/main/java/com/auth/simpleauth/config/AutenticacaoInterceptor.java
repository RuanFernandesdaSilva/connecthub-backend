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

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 2. Libera rotas públicas de autenticação, cadastro e QR Code
        if (uri.startsWith("/api/qrcode") ||
                uri.startsWith("/login") ||
                (uri.startsWith("/usuarios") && "POST".equalsIgnoreCase(method))) {
            return true;
        }

        // 3. Validação normal de sessão para as rotas protegidas
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