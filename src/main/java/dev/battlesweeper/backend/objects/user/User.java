package dev.battlesweeper.backend.objects.user;

import dev.battlesweeper.backend.objects.UserInfo;

public interface User {

        public Long getId();

        public String getName();

        public User overwrite(User user);

        public UserInfo toUserInfo();
}
