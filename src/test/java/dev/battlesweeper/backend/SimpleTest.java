package dev.battlesweeper.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.EmailSender;
import dev.battlesweeper.backend.socket.packet.GameFoundPacket;
import dev.battlesweeper.backend.socket.packet.Packet;
import dev.battlesweeper.backend.socket.packet.TestPacket;
import dev.battlesweeper.backend.utils.EmailUtils;
import dev.battlesweeper.backend.utils.StringUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.util.UUID;

@Testable
public class SimpleTest {

    @Test
    void testSerializer() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var serialized = mapper.writeValueAsString(new GameFoundPacket(UUID.randomUUID(), "123123"));
        System.out.println(serialized);
        GameFoundPacket packet = (GameFoundPacket) mapper.readValue(serialized, Packet.class);
        System.out.println(packet.authToken);
        System.out.println(mapper.writeValueAsString(new TestPacket("World!")));
    }

    @Test
    void testMail() {
        var result = EmailSender
                .getInstance()
                .sendMail("siwol@mooner.dev", EmailUtils.buildMailContent(StringUtils.randomAlphanumeric(8)));
        System.out.println(result);
    }

    @Test
    void testDotEnv() {
        var dotenv = Dotenv.load();
        System.out.println(dotenv.get("EMAIL_ADDR"));
    }
}
