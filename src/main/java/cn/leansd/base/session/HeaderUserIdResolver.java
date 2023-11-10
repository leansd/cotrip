package cn.leansd.base.session;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static cn.leansd.base.session.HttpTest.USER_ID_HEADER;

@Component
@Profile("dev")
public class HeaderUserIdResolver implements UserIdResolver {
    @Override
    public String resolveUserId(HttpServletRequest request) {
        return request.getHeader(USER_ID_HEADER);
    }
}
