package dev.battlesweeper.backend.objects;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class UserConnection {

    private final UserInfo user;
    private WebSocketSession session;

    public UserConnection(UserInfo user, WebSocketSession session) {
        this.user    = user;
        this.session = session;
    }

    public UserConnection(UserInfo user) {
        this(user, null);
    }

    public UserInfo user() {
        return user;
    }

    public WebSocketSession session() {
        return this.session;
    }

    public void setSession(WebSocketSession session) {
        setSession(session, false);
    }

    public void setSession(WebSocketSession session, boolean cancelPrev) {
        if (cancelPrev && this.session != null && this.session.isOpen()) {
            try {
                this.session.close();
            } catch (IOException ignored) {}
        }
        this.session = session;
    }

    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        session.sendMessage(message);
    }

    public void sendText(String message) throws IOException {
        sendMessage(new TextMessage(message));
    }
}