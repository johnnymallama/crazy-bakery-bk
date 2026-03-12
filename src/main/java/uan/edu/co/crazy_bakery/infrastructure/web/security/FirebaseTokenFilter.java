package uan.edu.co.crazy_bakery.infrastructure.web.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        return ("POST".equalsIgnoreCase(method) && "/usuarios".equalsIgnoreCase(uri))
                || ("GET".equalsIgnoreCase(method) && "/receta/ultimas-imagenes".equalsIgnoreCase(uri))
                || ("GET".equalsIgnoreCase(method) && uri.startsWith("/geografia/"))
                || ("GET".equalsIgnoreCase(method) && uri.startsWith("/tamanos/tipo-receta/"))
                || ("GET".equalsIgnoreCase(method) && uri.startsWith("/ingredientes/search"))
                || ("GET".equalsIgnoreCase(method) && uri.startsWith("/usuarios/"))
                || ("POST".equalsIgnoreCase(method) && "/generate-image/custom-cake".equalsIgnoreCase(uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            rejectUnauthorized(response, "Token de autenticación requerido");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    decodedToken.getUid(), null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (FirebaseAuthException e) {
            rejectUnauthorized(response, "Token inválido o expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void rejectUnauthorized(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}
