package dev.battlesweeper.backend.db;

import dev.battlesweeper.backend.objects.user.RegisteredUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository repository;

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

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean updateById(Long id, RegisteredUser user) {
        var org = findById(id);

        org.ifPresent(value -> value.overwrite(user));
        return org.isPresent();
    }
}
