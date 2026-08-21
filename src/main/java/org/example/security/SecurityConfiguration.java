package org.example.security;

import lombok.AllArgsConstructor;
import org.example.enums.Permission;
import org.example.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ===== PUBLIC =====
                        .requestMatchers(HttpMethod.GET, "/cars", "/cars/*", "/cars/power/*", "/cars/producer/*", "/car-brands", "/car-brands/*/models", "/regions"
                        ).permitAll()

                        // ===== AUTH =====
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // ===== USER'S OWN ADS =====
                        .requestMatchers(HttpMethod.PATCH, "/cars/reviews/*")
                        .hasAuthority(Permission.CAR_EDIT_OWN.name())

                        .requestMatchers(HttpMethod.DELETE, "/cars/*")
                        .hasAuthority(Permission.CAR_DELETE_OWN.name())

                        .requestMatchers(HttpMethod.DELETE, "/cars/reviews/current")
                        .hasAuthority(Permission.CAR_EDIT_OWN.name())

                        .requestMatchers(HttpMethod.PATCH, "/car/*/sell")
                        .hasAuthority(Permission.CAR_SELL_OWN.name())

                        // ===== MODERATION =====
                        .requestMatchers(HttpMethod.GET, "/cars/reviews/manager", "/cars-in-review")
                        .hasAuthority(Permission.REVIEW_MODERATE.name())

                        .requestMatchers(HttpMethod.PATCH, "/cars/reviews/*/approve")
                        .hasAuthority(Permission.REVIEW_MODERATE.name())

                        .requestMatchers(HttpMethod.DELETE, "/cars/reviews/*")
                        .hasAuthority(Permission.REVIEW_MODERATE.name())

                        // ===== BRAND / MODEL MANAGEMENT =====
                        .requestMatchers(HttpMethod.GET, "/car/get-all-requests-adding-new-brands-and-models")
                        .hasAuthority(Permission.BRAND_MODEL_MANAGE.name())

                        .requestMatchers(HttpMethod.POST, "/car/*/add-new-brand-and-model")
                        .hasAuthority(Permission.BRAND_MODEL_MANAGE.name())

                        .requestMatchers(HttpMethod.DELETE, "/car/*/remove-request-adding-new-brand-and-model")
                        .hasAuthority(Permission.BRAND_MODEL_MANAGE.name())

                        // ===== CAR CREATION =====
                        .requestMatchers(HttpMethod.POST, "/cars", "/cars/request-add-new-car")
                        .hasAuthority(Permission.CAR_CREATE.name())

                        // ===== DELETE ANY PUBLISHED CAR =====
                        .requestMatchers(HttpMethod.DELETE, "/cars/*")
                        .hasAuthority(Permission.CAR_DELETE_ANY.name())

                        // ===== USER MANAGEMENT =====
                        .requestMatchers(HttpMethod.PATCH, "/user/block/*", "/user/unblock/*")
                        .hasAuthority(Permission.USER_BLOCK.name())

                        .requestMatchers(HttpMethod.PATCH, "/user/make-manager/*", "/user/make-admin/*")
                        .hasAuthority(Permission.MANAGER_CREATE.name())

                        // ===== PREMIUM =====
                        .requestMatchers(HttpMethod.PATCH, "/user/account/premium")
                        .hasAuthority(Permission.PREMIUM_BUY.name())

                        // ===== READING CARS =====
                        .requestMatchers(HttpMethod.GET, "/cars/**", "/car-brands/**")
                        .hasAuthority(Permission.CAR_READ.name())

                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
