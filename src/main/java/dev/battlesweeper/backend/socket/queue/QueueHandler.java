package dev.battlesweeper.backend.socket.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.game.GameSessionManager;
import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.UserInfo;
import dev.battlesweeper.backend.socket.packet.GameFoundPacket;
import dev.battlesweeper.backend.socket.packet.Packet;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class QueueHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(QueueHandler.class);

    private static final int QUEUE_BLOCK_SIZE = 1;
    private static final Queue<QueueData> userQueue = new ConcurrentLinkedQueue<>();

    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        logger.info("Received: " + message);
        var deserialized = objMapper.readValue(payload, Packet.class);
        logger.info("Des: " + deserialized.toString());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("New connection established");
        HttpHeaders headers = session.getHandshakeHeaders();
        var userId = headers.getFirst("user-id");
        var userName = headers.getFirst("user-name");
        if (userId == null || userName == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        var nUser = new UserInfo(UUID.fromString(userId), userName);
        logger.info(String.valueOf(nUser));

        userQueue.offer(QueueData.fromUser(nUser, session));

        if (userQueue.size() >= QUEUE_BLOCK_SIZE) {
            UserConnection[] connections = new UserConnection[QUEUE_BLOCK_SIZE];
            for (var i = 0; i < QUEUE_BLOCK_SIZE; ++i)
                connections[i] = userQueue.poll().getConnection();

            var users  = Arrays.stream(connections).map(UserConnection::user).toList();
            var roomID = GameSessionManager.getInstance().createSession(new HashMap<>());

            for (var user : connections) {
                var mSession = user.session();
                var token  = AuthTokenManager.getInstance().createAndRegisterToken();
                var packet = new GameFoundPacket(roomID, token);
                mSession.sendMessage(new TextMessage(objMapper.writeValueAsString(packet)));
                mSession.close(CloseStatus.NORMAL);

            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        logger.info("Connection closed with status $status");
        if (status != CloseStatus.NORMAL) {
            UserInfo user = null;
            for (Iterator<QueueData> it = userQueue.iterator(); it.hasNext();) {
                var data = it.next();
                if (data.getConnection().session() == session) {
                    user = data.getConnection().user();
                    it.remove();
                    break;
                }
            }
            var isRemoved = user != null;
            if (isRemoved)
                logger.info("${user.toString()} disconnected abnormally");
        }
    }
}