package dev.battlesweeper.backend.objects.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class AnonymousUser implements User {

    Long id;

    String name;

    @Override
    public User overwrite(User user) {
        id = user.getId();
        name = user.getName();
        return this;
    }
}
