package com.example.config;

import com.example.constant.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AppConfig{
    @Autowired
    private JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((Authorize) -> Authorize
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.POST,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PUT,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.HEAD,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.OPTIONS,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PATCH,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.TRACE,"/api/admin/**").hasAuthority(RoleConstant.ADMIN)
                        .anyRequest().permitAll())
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
