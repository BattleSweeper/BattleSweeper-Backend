package dev.battlesweeper.backend.auth;

import dev.battlesweeper.backend.objects.user.AnonymousUser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AnonymousUserManager {

    private final AtomicLong incrementalId = new AtomicLong(0L);
    private final Map<Long, String> users = new HashMap<>();

    public boolean nameExists(String name) {
        return users.containsValue(name);
    }

    public long getIncrementalID() {
        return -incrementalId.incrementAndGet();
    }

    public void registerUser(AnonymousUser user) {
        users.put(user.getId(), user.getName());
    }

    public void invalidateUser(Long id) {
        users.remove(id);
    }

    private AnonymousUserManager() {}
    private static AnonymousUserManager INSTANCE;
    public static AnonymousUserManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AnonymousUserManager();
        return INSTANCE;
    }
}
