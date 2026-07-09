package com.uca.pncparcialfinalhotel.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración TEMPORAL (Parte III-IV).
 * Todavía no existe JWT ni roles activos: dejamos todo abierto (permitAll) para poder
 * probar el CRUD en Bruno sin autenticarse. Esta clase se reemplaza casi por completo
 * en la Parte V-VI, cuando entra el filtro JWT y las reglas de autorización por rol.
 * El bean PasswordEncoder sí se queda igual, porque UsuarioService ya lo necesita
 * desde ahora para no guardar contraseñas en texto plano.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
