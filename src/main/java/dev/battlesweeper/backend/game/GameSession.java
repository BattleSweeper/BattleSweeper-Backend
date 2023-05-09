package dev.battlesweeper.backend.game;

import dev.battlesweeper.backend.objects.UserConnection;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import java.io.IOException;
import java.util.*;

public class GameSession {

    private UUID id;
    private Map<String, UserConnection> connections;
    private Long startedAt;
    private Set<String> joined = new HashSet<>();

    public GameSession(UUID id, Map<String, UserConnection> connections, Long startedAt) {
        this.id          = id;
        this.connections = connections;
        this.startedAt   = startedAt;
    }

    public GameSession(UUID id, Map<String, UserConnection> connections) {
        this(id, connections, System.currentTimeMillis());
    }

    public boolean markJoined(String token) {
        joined.add(token);
        return false;
    }

    public boolean hasUser(String token) {
        return connections.containsKey(token);
    }

    public boolean hasUser(UUID userId) {
        for (var conn : connections.values()) {
            if (conn.user().id() == userId)
                return true;
        }
        return false;
    }

    public UserConnection getUserByToken(String token) {
        if (!hasUser(token))
            return null;
        return connections.get(token);
    }

    public void broadcast(WebSocketMessage<?> message) throws IOException {
        for (var conn : connections.values()) {
            conn.sendMessage(message);
        }
    }

    public void broadcast(String message) throws IOException {
        broadcast(new TextMessage(message));
    }
}