package cn.leansd.user.session;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserSessionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(SessionDTO.class) &&
                parameter.hasParameterAnnotation(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            /* 返回一个空Session，不抛出异常 */
            return new SessionDTO();
        }

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String userId = jwt.getClaimAsString("sub");
        String name = jwt.getClaimAsString("name");
        String userName = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String phoneNumber = jwt.getClaimAsString("phone_number");

        SessionDTO session = new SessionDTO(userId, phoneNumber, name, userName, email);
        return session;
    }
}
