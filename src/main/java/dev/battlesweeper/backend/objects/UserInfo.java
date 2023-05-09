package dev.battlesweeper.backend.objects;

import java.util.UUID;

public record UserInfo(UUID id, String name) {

    @Override
    public String toString() {
        return "User($name:$id))";
    }
}
