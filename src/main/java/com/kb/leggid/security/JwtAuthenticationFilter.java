package com.kb.leggid.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // On lit le le token depuis la requête
        String jwtFromRequest = getJwtFromRequest(request);
        // On valide le token
        // Si un token est bien renseignée ( non nullité, chaine de longueur supérieur à 0 et au moins un caractère de texte
        // Et le Token est valide (création d'un parser avec la clé public, puis on parse le token si pas d'anomalie alors le token est valie)
        if(StringUtils.hasText(jwtFromRequest) && jwtProvider.validateToken(jwtFromRequest)){
            // Lee nom d'utilisateur est compris dans la claim
            String username = jwtProvider.getUsernameFromJwt(jwtFromRequest);
            // Avec le nom d'utilisateur qu'on vient de récuperer, on va lire les détails
            // Le service UserDetailService est un implementation d'un service qui fournit une interface pour gérer des
            // détail utilisateur au sens de Spring avec les données de l'utilisateur en base de données
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Création d'un objet qui sert à représenter le couple utilisateur/mot de passe dans le contexte Spring Security
            UsernamePasswordAuthenticationToken usernamePasswordAuthentication =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            usernamePasswordAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // On met l'objet ainsi créer dans le context.
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthentication);
        }
        // Filtre les resultats autorisés à l'utilisateur
        filterChain.doFilter(request,response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken  = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
