package dev.battlesweeper.backend.objects;

public record UserInfo(Long id, String name) {

    @Override
    public String toString() {
        return "User($name:$id))";
    }
}
