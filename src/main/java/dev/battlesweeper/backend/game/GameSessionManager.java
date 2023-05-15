package dev.battlesweeper.backend.game;

import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.user.User;

import java.util.*;

public class GameSessionManager {

    private final Map<UUID, GameSession> sessions = new HashMap<>();

    public UUID createSession(Collection<User> users) {
        var sessionId = UUID.randomUUID();
        var connections = toConnectionMap(users);
        var nSession = new GameSession(sessionId, connections, new Date().getTime());

        sessions.put(sessionId, nSession);

        return sessionId;
    }

    public GameSession getSessionById(UUID id) {
        return sessions.get(id);
    }

    public GameSession getSessionOfUser(User user) {
        for (var session : sessions.values()) {
            if (session.hasUser(user))
                return session;
        }
        return null;
    }

    public Collection<GameSession> getSessions() {
        return sessions.values();
    }

    private Map<Long, UserConnection> toConnectionMap(Collection<User> users) {
        Map<Long, UserConnection> nMap = new HashMap<>();
        for (var user : users) {
            nMap.put(user.getId(), new UserConnection(user));
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
