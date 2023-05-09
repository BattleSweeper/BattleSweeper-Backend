package dev.battlesweeper.backend.socket.queue;

import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Getter
public class QueueData {
    private UserConnection connection;
    private int flags;

    public static QueueData fromUser(UserInfo user, WebSocketSession session, int flags) {
        return new QueueData(new UserConnection(user, session), flags);
    }

    public static QueueData fromUser(UserInfo user, WebSocketSession session) {
        return fromUser(user, session, 0);
    }
}
