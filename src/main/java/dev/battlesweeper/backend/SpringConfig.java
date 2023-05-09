package dev.battlesweeper.backend;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final EntityManager manager;

    public SpringConfig(EntityManager manager) {
        this.manager = manager;
    }
}
