package likelion.Spring_Like_Farmer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion.Spring_Like_Farmer.config.AppProperties;
import likelion.Spring_Like_Farmer.validation.ExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AppProperties appProperties;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String jwt = getJwtFromRequest(request);
            System.out.println(getJwtFromRequest(request) + "---------" + LocalDate.now());
            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.isTokenExpired(jwt)) {
                    createResponse(ExceptionCode.EXPIRED_JWT_TOKEN, response);
                } else {
                    Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(jwt);

                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                            userDetails.getPassword(), userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("TokenAuthenticationFilter.doFilterInternal---------");
                    filterChain.doFilter(request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            createResponse(ExceptionCode.INVALID_JWT_SIGNATURE, response);
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            createResponse(ExceptionCode.INVALID_JWT_TOKEN, response);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            createResponse(ExceptionCode.EXPIRED_JWT_TOKEN, response);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            createResponse(ExceptionCode.UNSUPPORTED_JWT_TOKEN, response);
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void createResponse(ExceptionCode exceptionCode, HttpServletResponse response) throws IOException {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("state", HttpServletResponse.SC_UNAUTHORIZED);
        json.put("code", exceptionCode.getCode());

        //String message = exceptionCode.getMessage();
        //String escapedDescription = UriUtils.encode(message, "UTF-8");
        json.put("message", exceptionCode.getMessage());

        String newResponse = new ObjectMapper().writeValueAsString(json);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(newResponse.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(newResponse);
    }
}
