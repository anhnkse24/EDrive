package com.swp391.edrive.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Bỏ qua các endpoint public (dùng startsWith để khớp cả query params)
        if (path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/forgot-password")
                || path.startsWith("/api/auth/reset-password")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // ✅ Chỉ xử lý nếu có header Bearer hợp lệ
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                if (tokenProvider.validateToken(token)) {
                    username = tokenProvider.getUsernameFromToken(token);
                }
            } catch (Exception ex) {
                // Nếu token invalid (expired, revoked, sai format) thì trả về 401 luôn
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // ✅ Nếu có username hợp lệ, set vào SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails ud = userDetailsService.loadUserByUsername(username);
            if (tokenProvider.validateToken(token)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}