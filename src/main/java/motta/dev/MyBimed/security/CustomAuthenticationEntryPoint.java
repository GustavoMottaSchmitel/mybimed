package motta.dev.MyBimed.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.security.exception.BadCredentialsException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Falha na autenticação - Detalhes: {}", authException.getMessage());
        log.debug("Headers: {}", Collections.list(request.getHeaderNames()));
        log.debug("Método: {} | Path: {}", request.getMethod(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Não autorizado");
        body.put("message", determineErrorMessage(authException));
        body.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private String determineErrorMessage(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return "E-mail ou senha incorretos";
        } else if (ex instanceof InsufficientAuthenticationException) {
            return "Token de autenticação ausente ou inválido";
        }
        return "Falha na autenticação";
    }
}