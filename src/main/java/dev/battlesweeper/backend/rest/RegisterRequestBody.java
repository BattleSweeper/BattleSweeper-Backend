package dev.battlesweeper.backend.rest;

import lombok.*;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class RegisterRequestBody {

    public String email;
    public String code;
    public String password;
    public String name;
}
