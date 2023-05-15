package dev.battlesweeper.backend.rest.body;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RefreshRequestBody {
    public String token;
}
