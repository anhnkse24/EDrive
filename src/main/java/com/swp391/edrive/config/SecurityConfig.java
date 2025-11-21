package com.swp391.edrive.config;


import com.swp391.edrive.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
//@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationService authenticationService;
    private final Filter filter;

    @Lazy
    @Autowired
    public SecurityConfig(AuthenticationService authenticationService, Filter filter) {
        this.authenticationService = authenticationService;
        this.filter = filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
          )
            throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Cho phép truy cập các endpoint công khai
                        .requestMatchers(CorsUtils::isPreFlightRequest)
                        .permitAll() // Cho phép CORS pre-flight requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/refresh-token",
                                "/api/auth/google-login",
                                "/api/auth/facebook-login",
                                "/api/auth/reset-password",
                                "/api/auth/forgot-password",
                                "/api/auth/payments/vnpay-return",
                                "/api/auth/verify",
                                "/api/testdrives",
                                "/api/vehicles/**",
                                "/api/vehicles/search/**",
                                "/chat",
                                "/api/admin/unverified-accounts",
                                "/api/admin/verify-account/*",
                                "/api/payments/vnpay-return",
                                "/api/admin/verify-account/*",
                                "/api/dealers/**",
                                "/api/manufacturer-inventory/**",
                                "/api/feedbacks/**",
                                "/api/customer-orders/**",
                                "/api/manufacturer-inventory/**",
                                "/api/colors/**",
                                "/api/notifications/**",
                                "/api/testdrives/**",
                                "/api/contracts/**"
                        )
                        .permitAll() // Các endpoint không cần xác thực
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
                        .permitAll() // Cho phép truy cập Swagger
                        // Tất cả các request khác cần xác thực
                        .anyRequest()
                        .authenticated())
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
