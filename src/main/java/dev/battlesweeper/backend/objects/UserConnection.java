package dev.battlesweeper.backend.objects;

import dev.battlesweeper.backend.objects.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@AllArgsConstructor
@Getter
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