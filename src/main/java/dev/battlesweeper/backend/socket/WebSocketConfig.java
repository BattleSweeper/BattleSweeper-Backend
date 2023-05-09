package dev.battlesweeper.backend.socket;

import dev.battlesweeper.backend.socket.game.RoomHandler;
import dev.battlesweeper.backend.socket.queue.QueueHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public static final String ROUTE_PREFIX = "/bs/api/v1";

    private final QueueHandler queueHandler;
    private final RoomHandler  roomHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(queueHandler, ROUTE_PREFIX + "/queue/register").setAllowedOrigins("*");
        registry.addHandler(roomHandler , ROUTE_PREFIX + "/room").setAllowedOrigins("*");
    }
}