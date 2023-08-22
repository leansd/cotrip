package cn.leansd.base;

import org.springframework.http.HttpHeaders;

import static cn.leansd.base.session.HttpTest.USER_ID_HEADER;

public class RestTemplateUtil {
    public static HttpHeaders buildHeaderWithUserId(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, userId);
        return headers;
    }
}
