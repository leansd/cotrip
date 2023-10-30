package cn.leansd.base.session;

import org.springframework.http.server.ServerHttpRequest;

public interface HeaderResolver {
    String resolveUserId(ServerHttpRequest request);
}
