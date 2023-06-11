package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.rank.RankData;
import dev.battlesweeper.backend.objects.user.RegisteredUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Long put(RegisteredUser user) {
        repository.save(user);
        return user.getId();
    }

    public Optional<RegisteredUser> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<RegisteredUser> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<RankData> findAllByOrderByClearTimeAsc(int limit) {
        return repository.findAllByOrderByClearTimeAsc(limit)
                .stream()
                .map(v -> new RankData(v.getId(), v.getName(), v.getClearTime()))
                .toList();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean updateById(Long id, RegisteredUser user) {
        var org = findById(id);

        org.ifPresent(value -> value.overwrite(user));
        return org.isPresent();
    }
}
