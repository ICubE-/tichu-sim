package com.icube.sim.tichu.auth;

import com.icube.sim.tichu.auth.jwt.JwtAuthenticationFilter;
import com.icube.sim.tichu.users.TichuSimUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TichuSimUserDetailsService tichuSimUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var daoAuthenticationProvider = new DaoAuthenticationProvider(tichuSimUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .sessionManagement(c -> c
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class)
                .authorizeHttpRequests(c -> c
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login", "/login.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(c -> c
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                PathPatternRequestMatcher.pathPattern("/api/**")
                        )
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.setStatus(HttpStatus.FORBIDDEN.value())
                        )
                );

        return http.build();
    }
}
