package com.kb.leggid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity// Indique à SPring que cette classe est une classe qui configure la sécurité de l'application web
public class SecurityConfig extends WebSecurityConfigurerAdapter { // Fournit les bases de la configuration de la securité

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // On désactive la securité CSRF car les attaques CSRF ne sont possible que lorsqu'on utilise des cookies de session
        // Les apis REST étant stateless ( cela signifie que chaque requêtes est isolé des autres, toute les informations
        // sont contenues dans la requêtes en cours et ne dépendent pas des requêtes précedentes , ou d'informations dans
        // les cookies )
        http.csrf().disable()
                // On autorise toute les requeêtes qui matche le patten "/api/auth**" au tilisateurs autentifiés
                .authorizeRequests()
                .antMatchers("/api/auth**")
                .permitAll()
                .anyRequest()
                .authenticated();

        super.configure(http);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
