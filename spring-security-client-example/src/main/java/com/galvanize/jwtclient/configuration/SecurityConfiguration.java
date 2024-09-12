package com.galvanize.jwtclient.configuration;

import com.galvanize.jwtclient.security.JwtProperties;
import com.galvanize.jwtclient.security.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtProperties jwtProperties;

    public SecurityConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    @Order(1)
    public JwtProperties getJwtProperties() {
        return new JwtProperties();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain getSecurityFilter(HttpSecurity http) throws Exception {

        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/ping").permitAll()
                .requestMatchers(HttpMethod.GET, "/cardme").hasRole("USER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtTokenAuthenticationFilter(jwtProperties),  UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(
              sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
          .build();
    }
}
