package dev.battlesweeper.backend.socket.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.game.GameSessionManager;
import dev.battlesweeper.backend.objects.UserConnection;
import dev.battlesweeper.backend.objects.json.PacketHandlerModule;
import dev.battlesweeper.backend.objects.user.User;
import dev.battlesweeper.backend.objects.packet.GameFoundPacket;
import dev.battlesweeper.backend.objects.packet.Packet;
import dev.battlesweeper.backend.rest.Message;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class QueueHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(QueueHandler.class);

    private static final String QUEUE_TYPE_ATTR  = "BS.QUEUE.TYPE";
    private static final int    QUEUE_BLOCK_SIZE = 4;
    private static final Queue<QueueData> duoQueue   = new ConcurrentLinkedQueue<>();
    private static final Queue<QueueData> multiQueue = new ConcurrentLinkedQueue<>();

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
        boolean isDuo = Objects.requireNonNull(session.getUri()).getPath().endsWith("/duo");

        logger.info("New connection to queue pool");
        HttpHeaders headers = session.getHandshakeHeaders();
        var token = headers.getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            logger.info("Queue rejected: ${Message.TOKEN_EXPIRED}");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(Message.TOKEN_EXPIRED));
            return;
        }

        token = token.substring("Bearer ".length());
        if (!AuthTokenManager.getInstance().isTokenValid(token)) {
            logger.info("Queue rejected: ${Message.TOKEN_EXPIRED}");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(Message.TOKEN_EXPIRED));
            return;
        }

        var nUser = AuthTokenManager.getInstance().getUserFromToken(token);
        if (nUser.isEmpty()) {
            logger.info("Queue rejected: SERVER_ERROR");
            session.close(CloseStatus.SERVER_ERROR);
            return;
        }
        var user = nUser.get();
        String typeStr;
        if (isDuo)
            typeStr = "DUO";
        else
            typeStr = "BTL";
        logger.info("New user joined the ${typeStr} queue pool: ${user.getName()}#${user.getId()}");
        session.getAttributes().put(QUEUE_TYPE_ATTR, typeStr);

        int blockSize;
        if (headers.containsKey("BYPASS_SIZE") && Objects.equals(headers.getFirst("BYPASS_SIZE"), "true"))
            blockSize = 1;
        else if (isDuo)
            blockSize = 2;
        else
            blockSize = QUEUE_BLOCK_SIZE;

        if (isDuo)
            handleQueue(duoQueue, user, session, blockSize);
        else
            handleQueue(multiQueue, user, session, blockSize);
    }

    private void handleQueue(Queue<QueueData> queue, User user, WebSocketSession session, int blockSize) throws IOException {
        queue.offer(QueueData.fromUser(user, session));

        if (queue.size() >= blockSize) {
            UserConnection[] connections = new UserConnection[blockSize];
            for (var i = 0; i < blockSize; ++i)
                connections[i] = Objects.requireNonNull(queue.poll()).getConnection();

            var users  = Arrays.stream(connections).map(UserConnection::getUser).toList();
            var roomID = GameSessionManager.getInstance().createSession(users);

            for (var connection : connections) {
                var packet = new GameFoundPacket(roomID);
                connection.sendText(objMapper.writeValueAsString(packet));
                connection.close(CloseStatus.NORMAL);
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        logger.info("Connection closed with status $status");
        if (status != CloseStatus.NORMAL) {
            Queue<QueueData> queue;
            if (!session.getAttributes().containsKey(QUEUE_TYPE_ATTR))
                return;
            if (session.getAttributes().get(QUEUE_TYPE_ATTR) == "DUO")
                queue = duoQueue;
            else
                queue = multiQueue;

            User user = null;
            for (Iterator<QueueData> it = queue.iterator(); it.hasNext();) {
                var data = it.next();
                if (data.getConnection().getSession() == session) {
                    user = data.getConnection().getUser();
                    it.remove();
                    break;
                }
            }
            var isRemoved = user != null;
            if (isRemoved)
                logger.info("${user.toString()} disconnected abnormally");
        }
    }

    public QueueHandler() {
        objMapper.registerModule(new PacketHandlerModule());
    }
}