package dev.battlesweeper.backend.game;

import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.user.User;

import java.util.*;

public class GameSessionManager {

    private final Map<UUID, GameSession> sessions = new HashMap<>();

    public UUID createSession(Map<String, User> users) {
        var sessionId = UUID.randomUUID();
        var connections = toConnectionMap(users);
        var nSession = new GameSession(sessionId, connections, System.currentTimeMillis());

        sessions.put(sessionId, nSession);

        return sessionId;
    }

    public GameSession getSessionById(UUID id) {
        return sessions.get(id);
    }

    public GameSession getSessionByToken(String token) {
        for (var session : sessions.values()) {
            if (session.hasUser(token))
                return session;
        }
        return null;
    }

    public Collection<GameSession> getSessions() {
        return sessions.values();
    }

    private Map<String, UserConnection> toConnectionMap(Map<String, User> org) {
        Map<String, UserConnection> nMap = new HashMap<>();
        for (var key : org.keySet()) {
            nMap.put(key, new UserConnection(org.get(key)));
        }
        return nMap;
    }

    private GameSessionManager() {}
    private static GameSessionManager INSTANCE;
    public static GameSessionManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GameSessionManager();
        return INSTANCE;
    }
}
