package dev.battlesweeper.backend.utils;

import dev.battlesweeper.backend.auth.EmailData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EmailUtils {

    private static String MAIL_TEMPLATE = null;

    public static EmailData buildMailContent(String authCode) {
        if (MAIL_TEMPLATE == null) {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            try (InputStream is = classloader.getResourceAsStream("static/mail_template.html")) {
                assert is != null;
                MAIL_TEMPLATE = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        var content = MAIL_TEMPLATE.replace("{CODE}", authCode);
        return new EmailData("[BattleSweeper] 이메일 인증을 완료해주세요", content, "html");
    }
}
