package dev.battlesweeper.backend.objects.user;

import dev.battlesweeper.backend.objects.User;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@Builder
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class RegisteredUser implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String email;

    String pwHash;

    String name;

    Long joinMillis;

    @Override
    public RegisteredUser overwrite(User user) {
        id = user.getId();
        email = user.getEmail();
        pwHash = user.getPwHash();
        name = user.getName();
        if (user instanceof RegisteredUser)
            joinMillis = ((RegisteredUser) user).getJoinMillis();
        return this;
    }
}