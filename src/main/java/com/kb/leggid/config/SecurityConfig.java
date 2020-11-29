package com.kb.leggid.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity// Indique à SPring que cette classe est une classe qui configure la sécurité de l'application web
public class SecurityConfig extends WebSecurityConfigurerAdapter { // Fournit les bases de la configuration de la securité

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // On désactive la securité CSRF car les attaques CSRF ne sont possible que lorsqu'on utilise des cookies de session
        // Les apis REST étant stateless ( cela signifie que chaque requêtes est isolé des autres, toute les informations
        // sont contenues dans la requêtes en cours et ne dépendent pas des requêtes précedentes , ou d'informations dans
        // les cookies )
        http.csrf().disable();

        super.configure(http);
    }
}
