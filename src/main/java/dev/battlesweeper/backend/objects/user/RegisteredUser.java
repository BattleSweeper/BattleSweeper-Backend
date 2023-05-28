package dev.battlesweeper.backend.objects.user;

import dev.battlesweeper.backend.objects.UserInfo;
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
        name = user.getName();
        if (user instanceof RegisteredUser nUser) {
            email = nUser.getEmail();
            pwHash = nUser.getPwHash();
            joinMillis = nUser.getJoinMillis();
        }
        return this;
    }

    @Override
    public UserInfo toUserInfo() {
        return new UserInfo(id, name);
    }
}