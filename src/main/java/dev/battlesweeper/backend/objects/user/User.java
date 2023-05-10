package dev.battlesweeper.backend.objects.user;

public interface User {

        public Long getId();

        public String getName();

        public User overwrite(User user);
}
