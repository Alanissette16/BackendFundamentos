package ec.edu.ups.icc.fundamentos01.security.filters;



import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import ec.edu.ups.icc.fundamentos01.security.config.JwtProperties;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsServiceImpl;
import ec.edu.ups.icc.fundamentos01.security.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * Dependencias inyectadas por Spring
     */
    private final JwtUtil jwtUtil; // Para validar y extraer datos del JWT
    private final UserDetailsServiceImpl userDetailsService; // Para cargar usuario desde BD
    private final JwtProperties jwtProperties; // Configuración JWT (header, prefix)

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            JwtProperties jwtProperties) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            /**
             * PASO 1: Extraer token del header Authorization
             */
            String jwt = getJwtFromRequest(request);

            /**
             * PASO 2: Validar y autenticar SOLO si hay token
             */
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {

                /**
                 * PASO 3: Extraer email del token
                 */
                String email = jwtUtil.getEmailFromToken(jwt);

                /**
                 * PASO 4: Cargar usuario desde base de datos
                 */
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // Principal (el usuario)
                        null, // Credentials (no necesarias)
                        userDetails.getAuthorities() // Authorities (roles/permisos)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Usuario autenticado: {}", email);
            }
            } catch (Exception ex) {
            /**
             * Manejo de errores: Solo loguear, NO lanzar excepción
             * 
             * ¿Por qué no lanzar la excepción?
             * - Si lanzamos excepción, la request se aborta completamente
             * - Mejor: Dejar que continúe sin autenticación
             * - Spring Security se encargará de rechazarla con 401
             * 
             */
            logger.error("No se pudo establecer la autenticación del usuario", ex);
        }

        /**
         * PASO 7: Continuar con la cadena de filtros
         */
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getPrefix())) {
            return bearerToken.substring(jwtProperties.getPrefix().length());
        }

        return null;
    }
}
