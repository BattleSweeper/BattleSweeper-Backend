package dev.battlesweeper.backend.utils;

import dev.battlesweeper.backend.auth.cypher.SHA256;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class StringUtils {

    public static String randomAlphanumeric(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String generateSHA256Hash(String original) throws NoSuchAlgorithmException {
        return new SHA256().encrypt(original);
    }
}
