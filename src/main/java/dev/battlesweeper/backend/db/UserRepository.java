package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.user.RegisteredUser;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<RegisteredUser, Long> {

    //User save(User user);

    public @Nonnull Optional<RegisteredUser> findById(@Nonnull Long id);

    public Optional<RegisteredUser> findByEmail(String email);

    public List<RegisteredUser> findByName(String name);
}
