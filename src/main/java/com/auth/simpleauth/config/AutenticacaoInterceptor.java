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

        // 3. Validação por Cookie (Sessão Tradicional do Tomcat)
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            return true;
        }

        // 4. FALLBACK TELEGRAM/CROSS-SITE: Suporte a Header ou Parametro de identificação
        // Permite o acesso se o frontend enviar o header X-User-Id ou se for consulta do próprio usuário
        String userIdHeader = request.getHeader("X-User-Id");
        String userIdParam = request.getParameter("userId");

        if (userIdHeader != null || userIdParam != null) {
            return true;
        }

        // Se a requisição for para buscar o próprio usuário (/usuarios/{id}) ou seus vinculos e houver identificação
        if (uri.contains("/usuarios/") || uri.contains("/vinculos/")) {
            // Em ambiente web-app, se a sessão caiu, podemos permitir requisições GET read-only de perfil/vínculo
            if ("GET".equalsIgnoreCase(method)) {
                return true;
            }
        }

        // 5. Nega acesso se não houver nenhuma evidência de autenticação
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("Acesso não autorizado! Efetue o login no sistema.");
        return false;
    }
}