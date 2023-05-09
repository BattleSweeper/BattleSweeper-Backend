package dev.battlesweeper.backend.rest;

import dev.battlesweeper.backend.auth.EmailSender;
import dev.battlesweeper.backend.db.UserService;
import dev.battlesweeper.backend.objects.User;
import dev.battlesweeper.backend.socket.WebSocketConfig;
import dev.battlesweeper.backend.socket.packet.ResultPacket;
import dev.battlesweeper.backend.utils.EmailUtils;
import dev.battlesweeper.backend.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RegistrationHandler {

    private final Map<String, String> pendingRegs = new HashMap<>();
    private final UserService userService;

    public RegistrationHandler(UserService service) {
        userService = service;
    }

    @PostMapping(WebSocketConfig.ROUTE_PREFIX + "/register")
    public ResultPacket requestRegistration(@RequestParam(value = "email") String email) {
        if (pendingRegs.containsKey(email))
            return new ResultPacket(ResultPacket.RESULT_FAILURE, "ALREADY_SENT");

        var code  = StringUtils.randomAlphanumeric(8);
        EmailSender.getInstance().sendMail(email, EmailUtils.buildMailContent(code));

        pendingRegs.put(email, code);
        return new ResultPacket(ResultPacket.RESULT_OK, email);
    }

    @PostMapping(WebSocketConfig.ROUTE_PREFIX + "/register/verify")
    public ResultPacket verifyAndRegister(@RequestBody RegisterRequestBody form) {
        if (!pendingRegs.containsKey(form.email) || !pendingRegs.get(form.email).equals(form.code))
            return new ResultPacket(ResultPacket.RESULT_BAD_DATA, "CODE_MISMATCH");

        String pwHash;
        try {
            pwHash = StringUtils.generateSHA256Hash(form.password);
        } catch (NoSuchAlgorithmException e) {
            return new ResultPacket(ResultPacket.RESULT_FAILURE, "HASH_FAILURE");
        }

        var user = User.builder()
                .email(form.email)
                .name(form.name)
                .pwHash(pwHash)
                .build();
        userService.join(user);
        return new ResultPacket(ResultPacket.RESULT_OK, user.getId().toString());
    }
}
