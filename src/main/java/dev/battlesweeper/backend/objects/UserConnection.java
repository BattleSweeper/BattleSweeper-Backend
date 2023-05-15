package dev.battlesweeper.backend.objects;

import dev.battlesweeper.backend.objects.user.User;
import jakarta.websocket.CloseReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@AllArgsConstructor
@Getter @Builder
@Slf4j
public class UserConnection {

    private final User user;
    private WebSocketSession session;

    public UserConnection(User user) {
        this(user, null);
    }

    public void setSession(WebSocketSession session) {
        setSession(session, false);
    }

    public void setSession(WebSocketSession session, boolean cancelPrev) {
        log.info("Session set: " + session);
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

    public void close(CloseStatus status) throws IOException {
        session.close(status);
    }
}