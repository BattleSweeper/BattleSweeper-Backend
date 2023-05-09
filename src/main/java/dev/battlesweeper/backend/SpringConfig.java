package dev.battlesweeper.backend;

import dev.battlesweeper.backend.db.JpaUserRepository;
import dev.battlesweeper.backend.db.UserRepository;
import dev.battlesweeper.backend.db.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final EntityManager manager;

    public SpringConfig(EntityManager manager) {
        this.manager = manager;
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository());
    }

    @Bean
    public UserRepository userRepository() {
        return new JpaUserRepository(manager);
    }
}
