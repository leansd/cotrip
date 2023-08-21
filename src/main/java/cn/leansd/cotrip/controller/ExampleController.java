package cn.leansd.cotrip.controller;

import cn.leansd.user.session.SessionDTO;
import cn.leansd.user.session.UserSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @GetMapping("/hello")
    public String hello(@UserSession SessionDTO session) {
        return "<h1>Hello, " +  session.getName() + "!</h1>";
    }
}