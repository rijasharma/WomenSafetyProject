package com.womensafety.WomenSafetyProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/register",        //  Public route
                                "/login",           //  Login page
                                "/css/**",          //  Static resources
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .anyRequest().permitAll() //  Allow everything else
                )
                .formLogin(form -> form.disable())   //  Disable default login
                .logout(logout -> logout.disable()); //  Disable default logout

        return http.build();
    }
}


