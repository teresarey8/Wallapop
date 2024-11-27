package org.example.wallapop.Config;

import org.example.wallapop.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/registro", "/imagesAnuncios/**").permitAll() // Rutas públicas
                        .requestMatchers("/anuncios/editar/**").authenticated() // Solo usuarios autenticados pueden editar
                        .anyRequest().authenticated() // Otras rutas requerirán autenticación
                )

                .formLogin(login -> login
                        .loginPage("/login") // Página de login
                        .defaultSuccessUrl("/") // Redirige a la página principal al iniciar sesión
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/login") // URL a la que redirige después de cerrar sesión
                        .invalidateHttpSession(true) // Invalida la sesión
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(5 * 24 * 60 * 60) // 5 días
                        .userDetailsService(customUserDetailsService)
                );
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build(); // Construcción del AuthenticationManager
    }
}
