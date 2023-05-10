package dev.battlesweeper.backend.rest;

import dev.battlesweeper.backend.auth.AuthTokenManager;
import dev.battlesweeper.backend.auth.cypher.SHA256;
import dev.battlesweeper.backend.db.UserService;
import dev.battlesweeper.backend.socket.WebSocketConfig;
import dev.battlesweeper.backend.socket.packet.ResultPacket;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class AuthHandler {

    private final UserService userService;

    public AuthHandler(UserService service) {
        userService = service;
    }

    @PostMapping(WebSocketConfig.ROUTE_PREFIX + "/auth")
    public ResultPacket requestLogin(@RequestBody AuthRequestBody form) {
        switch (form.type) {
            case AuthRequestBody.TYPE_ANONYMOUS -> {
                return new ResultPacket(ResultPacket.RESULT_FAILURE, "NOT_SUPPORTED");
            }
            case AuthRequestBody.TYPE_REGISTERED -> {
                var query = userService.findByEmail(form.info.email);
                if (query.isEmpty())
                    return new ResultPacket(ResultPacket.RESULT_FAILURE, "NOT_FOUND");

                var user = query.get();
                String pwHash;
                try {
                    pwHash = SHA256.encrypt(form.info.password);
                } catch (NoSuchAlgorithmException e) {
                    return new ResultPacket(ResultPacket.RESULT_FAILURE, "ENCRYPT_FAILURE");
                }

                if (!user.getPwHash().equals(pwHash))
                    return new ResultPacket(ResultPacket.RESULT_FAILURE, "PASSWORD_MISMATCH");

                var accessToken = AuthTokenManager.getInstance().createAuthToken(user);

                return new ResultPacket(ResultPacket.RESULT_OK, accessToken.toJsonString());
            }
        }

        return new ResultPacket(ResultPacket.RESULT_BAD_DATA, "UNKNOWN_TYPE");
    }
}
