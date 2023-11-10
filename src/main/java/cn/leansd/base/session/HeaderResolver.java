package cn.leansd.base.session;

import jakarta.servlet.http.HttpServletRequest;

public interface HeaderResolver {
    String resolveUserId(HttpServletRequest request);
}
