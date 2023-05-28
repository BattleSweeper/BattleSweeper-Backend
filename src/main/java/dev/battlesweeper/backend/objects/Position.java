package dev.battlesweeper.backend.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.battlesweeper.backend.objects.json.PositionDeserializer;
import dev.battlesweeper.backend.objects.json.PositionSerializer;
import lombok.Builder;

//@JsonSerialize(as = Position.class, using = PositionSerializer.class)
//@JsonDeserialize(as = Position.class, using = PositionDeserializer.class)
@Builder
public record Position(int x, int y) {

    public boolean isNearby(Position other, int radius) {
        return Math.abs(other.x - x) <= radius && Math.abs(other.y - y) <= radius;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Position pos))
            return false;

        return pos.x == x() && pos.y == y();
    }
}
