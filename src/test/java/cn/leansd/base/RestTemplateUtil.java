package cn.leansd.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpHeaders;

import static cn.leansd.base.session.HttpTest.USER_ID_HEADER;

public class RestTemplateUtil {
    public static HttpHeaders buildHeaderWithUserId(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, userId);
        return headers;
    }

    public static String asJson(Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
