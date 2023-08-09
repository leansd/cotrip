package cn.leancoding.cotrip.controller;

import cn.leancoding.cotrip.session.SessionDTO;
import cn.leancoding.cotrip.session.UserSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {
    @GetMapping("/hello")
    public String hello(@UserSession SessionDTO session) {
        return "<h1>Hello, " +  session.getName() + "!</h1>";
    }
}