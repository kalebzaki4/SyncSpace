package com.br.syncspace.infra.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = """
                {
                    "status": 403,
                    "error": "Acesso Negado",
                    "message": "Você não possui permissão para acessar este recurso.",
                    "path": "%s"
                }
                """.formatted(request.getRequestURI());

        response.getWriter().write(jsonResponse);
    }
}