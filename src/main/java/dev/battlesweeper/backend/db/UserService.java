package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.User;
import jakarta.transaction.Transactional;

@Transactional
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Long join(User user) {
        repository.save(user);
        return user.getId();
    }
}
