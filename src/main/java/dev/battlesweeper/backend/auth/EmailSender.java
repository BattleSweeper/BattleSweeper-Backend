package dev.battlesweeper.backend.auth;

import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    private final Session session;

    public boolean sendMail(String dest, EmailData data) {
        return sendMail(dest, data.title(), data.content(), data.type());
    }

    public boolean sendMail(String dest, String title, String content, String subtype) {
        var message = new MimeMessage(session);
        try {
            message.setFrom("battlesweeper@mooner.dev");
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
            message.setSubject(title);
            message.setText(content, "utf-8", subtype);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private EmailSender() {
        var props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587");

        var env = Dotenv.load();
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(env.get("EMAIL_ADDR"), env.get("EMAIL_PSWD"));
            }
        });
        session.setDebug(true);
    }
    private static EmailSender INSTANCE;
    public static EmailSender getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EmailSender();
        return INSTANCE;
    }
}
