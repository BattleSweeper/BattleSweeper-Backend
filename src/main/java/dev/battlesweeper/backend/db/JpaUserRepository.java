package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.User;
import jakarta.persistence.EntityManager;

public class JpaUserRepository implements UserRepository {

    private EntityManager manager;

    public JpaUserRepository(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public User save(User user) {
        manager.persist(user);
        return user;
    }
}
