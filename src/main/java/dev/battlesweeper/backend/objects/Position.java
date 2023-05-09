package dev.battlesweeper.backend.objects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = Position.class)
public record Position(int x, int y) {
}
