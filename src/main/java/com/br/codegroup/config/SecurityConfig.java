package com.br.codegroup.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Documentação pública
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api-docs/**"
                        ).permitAll()

                        // Autenticação pública
                        .requestMatchers("/api/auth/**").permitAll()

                        // Relatórios - apenas admin e manager
                        .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "MANAGER")

                        // Projetos - leitura para todos, escrita para admin/manager
                        .requestMatchers("/api/projects", "/api/projects/search", "/api/projects/status", "/api/projects/manager")
                        .hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MANAGER")

                        // Membros - apenas admin
                        .requestMatchers("/api/members/**").hasRole("ADMIN")

                        // Todos os outros endpoints requerem autenticação
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // HTTP Basic authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager123"))
                .roles("MANAGER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, manager, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}