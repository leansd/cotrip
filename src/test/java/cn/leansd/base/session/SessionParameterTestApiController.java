package cn.leansd.base.session;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionParameterTestApiController {

    @GetMapping("/test/session")
    public String getUserId(@UserSession SessionDTO session) {
        return session.getUserId();
    }
}