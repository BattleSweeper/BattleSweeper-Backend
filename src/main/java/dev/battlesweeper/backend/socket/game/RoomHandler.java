package dev.battlesweeper.backend.socket.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.game.BoardGenerator;
import dev.battlesweeper.backend.game.GameSession;
import dev.battlesweeper.backend.game.GameSessionManager;
import dev.battlesweeper.backend.objects.Position;
import dev.battlesweeper.backend.objects.UserInfo;
import dev.battlesweeper.backend.objects.json.PacketHandlerModule;
import dev.battlesweeper.backend.objects.packet.*;
import dev.battlesweeper.backend.rest.Message;
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
            var packet = new ResultPacket(ResultPacket.RESULT_BAD_DATA, Message.NO_SESSION_FOUND);
            session.sendMessage(new TextMessage(objMapper.writeValueAsString(packet)));
            return;
        }

        var userInfo = retrieveUserInfo(session);
        if (userInfo == null) {
            var packet = new ResultPacket(ResultPacket.RESULT_BAD_DATA, Message.TOKEN_INVALID);
            session.sendMessage(new TextMessage(objMapper.writeValueAsString(packet)));
            return;
        }

        String payload = message.getPayload();
        var packet = objMapper.readValue(payload, Packet.class);

        if (packet instanceof TileUpdatePacket || packet instanceof GameUpdatePacket) {
            var nPacket = PlayerUpdatePacket.builder()
                    .user(userInfo)
                    .packet(packet)
                    .build();
            gameSession.broadcast(nPacket);
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
        //var msg = objMapper.writeValueAsString(new ResultPacket(ResultPacket.RESULT_OK, null));
        //session.sendMessage(new TextMessage(msg));

        gameSession.broadcast(new UserJoinPacket(user.get()));

        if (gameSession.allUserJoined()) {
            var boardSize = new Position(16, 16);
            var mines = BoardGenerator.generateMines(boardSize, 40);
            var users = gameSession.getAllUserInfo();

            gameSession.broadcast(new GameStartPacket(users, boardSize, mines));
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {

    }

    private GameSession retrieveSession(WebSocketSession session) {
        var headers = session.getHandshakeHeaders();
        var roomID = headers.getFirst("room-id");
        if (roomID == null)
            return null;
        return GameSessionManager.getInstance().getSessionById(UUID.fromString(roomID));
    }

    private UserInfo retrieveUserInfo(WebSocketSession session) {
        var headers = session.getHandshakeHeaders();
        var token = headers.getFirst("Authorization");
        if (token == null)
            return null;
        token = token.substring("Bearer ".length());
        var info = AuthTokenManager.getInstance().getUserInfoFromToken(token);
        return info.orElse(null);
    }

    public RoomHandler() {
        objMapper.registerModule(new PacketHandlerModule());
    }
}
