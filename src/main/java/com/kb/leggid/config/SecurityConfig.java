package com.kb.leggid.config;

import com.kb.leggid.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity// Indique à SPring que cette classe est une classe qui configure la sécurité de l'application web
@AllArgsConstructor // Pour l'injection par contructeur ( ex: le UserDetailService )
public class SecurityConfig extends WebSecurityConfigurerAdapter { // Fournit les bases de la configuration de la securité

    private final UserDetailsService userDetailsService; // Implique l'ajout d'une implémentation dans le package Service

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // On désactive la securité CSRF car les attaques CSRF ne sont possible que lorsqu'on utilise des cookies de session
        // Les apis REST étant stateless ( cela signifie que chaque requêtes est isolé des autres, toute les informations
        // sont contenues dans la requêtes en cours et ne dépendent pas des requêtes précedentes , ou d'informations dans
        // les cookies )
        http.csrf().disable()
                // On autorise toute les requeêtes qui matche le patten "/api/auth**" au tilisateurs autentifiés
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .permitAll()
                // Exclu ces Urls de la contrainte d'authentifications
                .antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        // Ajout d'un filtre de la requete HTTP
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Il est impératif de fournir une impélmentation, sinon Spring remonte une exception.
    // Création d'un Authentification manager builder à partir des détails en base de l'utilisateur à authentifier .
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // Voir l'implémentation : UserDetailsServiceImpl
                .userDetailsService(userDetailsService)
                // On va chercher les details de l'utilisateur en base ( via  UserRepository )  on mappe les données avec
                // le user au sens Spring Security, avec un niveau d'accès que l'ont définit
                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
