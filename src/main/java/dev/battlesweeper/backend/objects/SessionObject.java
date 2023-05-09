package dev.battlesweeper.backend.objects;

import org.springframework.web.socket.WebSocketSession;

public interface SessionObject {

    public abstract WebSocketSession getSession();
}
