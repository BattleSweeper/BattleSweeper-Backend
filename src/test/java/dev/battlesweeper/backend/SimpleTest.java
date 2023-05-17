package dev.battlesweeper.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.battlesweeper.backend.auth.EmailSender;
import dev.battlesweeper.backend.objects.Position;
import dev.battlesweeper.backend.utils.EmailUtils;
import dev.battlesweeper.backend.utils.StringUtils;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class SimpleTest {

    @Test
    void testSerializer() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var position = new Position(10, 20);
        var encoded = mapper.writeValueAsString(position);
        System.out.println(encoded);
        var decoded = mapper.readValue(encoded, Position.class);
        System.out.println(decoded);
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
