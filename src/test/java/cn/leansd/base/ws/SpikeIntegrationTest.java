package cn.leansd.base.ws;

import cn.leansd.base.ws.check_conn.HelloMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 技术探针：验证WebSocket的消息发送能力 。
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class SpikeIntegrationTest {
    @Value(value="${local.server.port}")
    private int port;

    @BeforeEach
    public void setUp(){
    }

    @Autowired  SimpMessagingTemplate template;
    Logger logger = Logger.getLogger(SpikeIntegrationTest.class.getName());
    @DisplayName("验证WebSocket发送给单一用户通知的能力")
    @Test
    public void testWebSocketSendToSingleUser() throws Exception {
        WebSocketTestTemplate testTemplate = new WebSocketTestTemplate("ws://localhost:" + this.port + WebSocketConfig.WS_ENDPOINT,
                "user-id-1",
                "/queue/status",
                "/topic/demo",
                HelloMessage.class,
                new Consumer<Object>() {
                    @Override
                    public void accept(Object payload) {
                        logger.info("received payload: " + payload);
                        assertNotNull(payload);
                    }
                });

        testTemplate.execute(()->{
            logger.info("send message");
            template.convertAndSendToUser("user-id-1", "/queue/status",
                    new HelloMessage("Hello, user-id-1!"));
        });
        testTemplate.verify(null);
    }

}
