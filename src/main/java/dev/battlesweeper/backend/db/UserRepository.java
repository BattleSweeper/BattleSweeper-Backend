package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.User;

public interface UserRepository {

    User save(User user);
}
