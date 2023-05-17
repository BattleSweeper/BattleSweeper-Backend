package dev.battlesweeper.backend.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.user.User;
import dev.battlesweeper.backend.objects.packet.Packet;
import lombok.Builder;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Builder
public class GameSession {

    private UUID id;
    // userId, connection
    private Map<Long, UserConnection> connections;
    private Long startedAt;

    private final ObjectMapper objMapper = new ObjectMapper();

    public GameSession(UUID id, Map<Long, UserConnection> connections, Long startedAt) {
        this.id          = id;
        this.connections = connections;
        this.startedAt   = startedAt;
    }

    public boolean allUserJoined() {
        return connections
                .values()
                .stream()
                .allMatch(conn -> conn.getSession() != null);
    }

    public void markJoined(Long userId, WebSocketSession session) {
        connections.get(userId).setSession(session);
    }

    public boolean hasUser(Long userId) {
        return connections.containsKey(userId);
    }

    public boolean hasUser(User user) {
        return hasUser(user.getId());
    }

    public void broadcast(WebSocketMessage<?> message) throws IOException {
        for (var conn : connections.values()) {
            conn.sendMessage(message);
        }
    }

    public void broadcast(String message) throws IOException {
        broadcast(new TextMessage(message));
    }

    public void broadcast(Packet packet) throws IOException {
        broadcast(objMapper.writeValueAsString(packet));
    }
}