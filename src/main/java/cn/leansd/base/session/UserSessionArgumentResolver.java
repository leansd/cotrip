package cn.leansd.base.session;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserSessionArgumentResolver implements HandlerMethodArgumentResolver {

    UserIdResolver userIdResolver;
    @Autowired
    public UserSessionArgumentResolver(UserIdResolver userIdResolver){
        this.userIdResolver = userIdResolver;
    }

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
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String userId = userIdResolver.resolveUserId(servletRequest);
        return new SessionDTO(userId);
    }
}
