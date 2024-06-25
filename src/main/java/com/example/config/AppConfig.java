package com.example.config;

import com.example.constant.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppConfig {
    @Autowired
    private JwtValidator jwtValidator;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((Authorize) -> Authorize
                        // USER
                        .requestMatchers(HttpMethod.GET, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.POST, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.PUT, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.HEAD, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.OPTIONS, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.PATCH, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.TRACE, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        .requestMatchers(HttpMethod.DELETE, RoleConstant.API_USER).hasAuthority(RoleConstant.USER)
                        // ADMIN
                        .requestMatchers(HttpMethod.GET, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.POST, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PUT, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.HEAD, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.OPTIONS, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.TRACE, RoleConstant.API_ADMIN).hasAuthority(RoleConstant.ADMIN)
                        .anyRequest().permitAll())
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
