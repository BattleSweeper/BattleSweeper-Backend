package dev.battlesweeper.backend.objects;

public interface User {

        public Long getId();

        public String getEmail();

        public String getPwHash();

        public String getName();

        public User overwrite(User user);
}
