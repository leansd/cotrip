package cn.leansd.base.session;

import org.springframework.context.annotation.Profile;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;

import static cn.leansd.base.session.HttpTest.USER_ID_HEADER;

@Component
@Profile("dev")
public class UserIdHeaderResolver implements HeaderResolver {
    @Override
    public String resolveUserId(ServerHttpRequest request) {
        return request.getHeaders().getFirst(USER_ID_HEADER);
    }
}
