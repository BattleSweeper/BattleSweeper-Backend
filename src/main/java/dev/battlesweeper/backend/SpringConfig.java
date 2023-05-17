package dev.battlesweeper.backend;

import dev.battlesweeper.backend.objects.json.PacketHandlerModule;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final EntityManager manager;

    @Bean
    public PacketHandlerModule packetHandlerModule() {
        return new PacketHandlerModule();
    }

    public SpringConfig(EntityManager manager) {
        this.manager = manager;
    }
}
