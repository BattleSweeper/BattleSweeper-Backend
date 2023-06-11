package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.user.RegisteredUser;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<RegisteredUser, Long> {

    //User save(User user);

    public @Nonnull Optional<RegisteredUser> findById(@Nonnull Long id);

    public Optional<RegisteredUser> findByEmail(String email);

    public List<RegisteredUser> findByName(String name);

    @Query(value = "SELECT * FROM battlesweeper.users u WHERE u.clear_time > 0 ORDER BY u.clear_time LIMIT :limit", nativeQuery = true)
    public List<RegisteredUser> findAllByOrderByClearTimeAsc(@Param("limit") int limit);
}
