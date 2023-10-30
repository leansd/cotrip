package cn.leansd.base.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
public class JwtHeaderResolver implements HeaderResolver {
    Logger logger = LoggerFactory.getLogger(JwtHeaderResolver.class);
    @Override
    public String resolveUserId(ServerHttpRequest request) {
        logger.trace("enter resolveUserId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            logger.warn("authentication is not JwtAuthenticationToken");
            return null;
        }

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        logger.info("jwt: {}", jwt.getClaims());
        return jwt.getClaimAsString("sub");
    }
}
