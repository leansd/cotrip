package cn.leansd.base.security;

import org.springframework.http.server.ServerHttpRequest;

public interface HeaderResolver {
    String resolveUserId(ServerHttpRequest request);
}
