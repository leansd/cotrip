package cn.leansd.cotrip.controller;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Logger;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    Logger logger = Logger.getLogger(CustomHandshakeHandler.class.getName());
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return (Principal) attributes.get("PRINCIPAL");
    }


}