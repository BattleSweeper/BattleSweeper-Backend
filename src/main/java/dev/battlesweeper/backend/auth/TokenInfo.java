package dev.battlesweeper.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {

    private String grantType;
    private String accessToken;
    private String refreshToken;

    public String toJsonString() {
        var builder = new StringBuilder("{ ");
        for (var field : this.getClass().getDeclaredFields()) {
            try {
                builder.append("\"")
                        .append(field.getName())
                        .append("\": \"")
                        .append(field.get(this))
                        .append("\", ");
            } catch (IllegalAccessException ignored) {}
        }
        var res = builder.toString();
        res = res.substring(0, res.length() - 2);
        return res + " }";
    }
}
