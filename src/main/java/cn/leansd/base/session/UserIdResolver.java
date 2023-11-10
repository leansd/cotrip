package cn.leansd.base.session;

import jakarta.servlet.http.HttpServletRequest;

public interface UserIdResolver {
    String resolveUserId(HttpServletRequest request);
}
