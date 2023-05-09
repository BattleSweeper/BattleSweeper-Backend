package dev.battlesweeper.backend.objects.user;

import dev.battlesweeper.backend.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class AnonymousUser implements User {

    Long id;

    String email;

    String pwHash;

    String name;

    @Override
    public User overwrite(User user) {
        id = user.getId();
        email = user.getEmail();
        pwHash = user.getPwHash();
        name = user.getName();
        return this;
    }
}
