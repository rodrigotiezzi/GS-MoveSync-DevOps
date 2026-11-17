package br.com.fiap.movesync.config;

import br.com.fiap.movesync.security.JwtAuthFilter;
import br.com.fiap.movesync.security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Cadeia 1: páginas Thymeleaf (/web/**) — login por formulário + sessão (CSRF ativo)
    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/web/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/login").permitAll()
                        .requestMatchers("/web/css/**", "/web/js/**", "/web/images/**").permitAll()

                        // Cadastro da malha (cruzamentos, semáforos e dispositivos) é do ADMIN
                        .requestMatchers("/web/cruzamento/**").hasRole("ADMIN")
                        .requestMatchers("/web/semaforo/**").hasRole("ADMIN")
                        .requestMatchers("/web/dispositivo/**").hasRole("ADMIN")

                        // Operação do dia a dia: leituras e ajustes
                        .requestMatchers("/web/leitura/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers("/web/ajuste/**").hasAnyRole("ADMIN", "OPERADOR")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/web/login")
                        .loginProcessingUrl("/web/login")
                        .defaultSuccessUrl("/web", true)
                        .failureUrl("/web/login?erro")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/web/logout")
                        .logoutSuccessUrl("/web/login?logout")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/web/acesso-negado")
                );

        return http.build();
    }

    // Cadeia 2: API REST — JWT stateless
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()

                        // Leitura dos cadastros liberada para o operador; alteração só ADMIN
                        .requestMatchers(HttpMethod.GET, "/cruzamento/**", "/semaforo/**", "/dispositivo/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers("/cruzamento/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/semaforo/*/ajustar-tempo").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers("/semaforo/**").hasRole("ADMIN")
                        .requestMatchers("/dispositivo/**").hasRole("ADMIN")

                        .requestMatchers("/leitura/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers("/ajuste/**").hasAnyRole("ADMIN", "OPERADOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
