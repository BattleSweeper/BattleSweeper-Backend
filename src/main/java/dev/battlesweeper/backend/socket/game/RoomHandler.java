package dev.battlesweeper.backend.socket.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.game.BoardGenerator;
import dev.battlesweeper.backend.game.GameSession;
import dev.battlesweeper.backend.game.GameSessionManager;
import dev.battlesweeper.backend.objects.Position;
import dev.battlesweeper.backend.objects.json.PacketHandlerModule;
import dev.battlesweeper.backend.objects.packet.GameStartPacket;
import dev.battlesweeper.backend.objects.packet.ResultPacket;
import dev.battlesweeper.backend.objects.packet.UserJoinPacket;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Component
public class RoomHandler extends TextWebSocketHandler {

    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        var gameSession = retrieveSession(session);
        if (gameSession == null) {
            var packet = new ResultPacket(
                    ResultPacket.RESULT_BAD_DATA,
                    "No session associated with connection found"
            );
            session.sendMessage(new TextMessage(objMapper.writeValueAsString(packet)));
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        String payload = message.getPayload();

        if (payload.equals("test")) {
            Position[] mines = { new Position(1, 2), new Position(5, 3), new Position(10, 6) };
            var packet = new GameStartPacket(new Position(16, 16), mines);
            gameSession.broadcast(objMapper.writeValueAsString(packet));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var headers = session.getHandshakeHeaders();

        var token  = headers.getFirst(HttpHeaders.AUTHORIZATION);
        var roomID = headers.getFirst("room-id");

        if (token == null || roomID == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (!token.startsWith("Bearer ")) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        token = token.substring("Bearer ".length());

        if (!AuthTokenManager.getInstance().isTokenValid(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        var user = AuthTokenManager.getInstance().getUserFromToken(token);
        if (user.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        var gameSession = GameSessionManager.getInstance().getSessionById(UUID.fromString(roomID));
        if (gameSession == null) {
            var msg = objMapper.writeValueAsString(new ResultPacket(ResultPacket.RESULT_FAILURE, "Invalid room ID"));
            session.sendMessage(new TextMessage(msg));
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        gameSession.markJoined(user.get().getId(), session);
        var msg = objMapper.writeValueAsString(new ResultPacket(ResultPacket.RESULT_OK, null));
        session.sendMessage(new TextMessage(msg));

        gameSession.broadcast(new UserJoinPacket(user.get()));

        if (gameSession.allUserJoined()) {
            var boardSize = new Position(16, 16);
            var mines = BoardGenerator.generateMines(boardSize, 40);

            gameSession.broadcast(new GameStartPacket(boardSize, mines));
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {

    }

    private GameSession retrieveSession(WebSocketSession session) {


        var user = AuthTokenManager.getInstance().getUserFromToken("");
        return user
                .map(value -> GameSessionManager.getInstance().getSessionOfUser(value))
                .orElse(null);
    }

    public RoomHandler() {
        objMapper.registerModule(new PacketHandlerModule());
    }
}
