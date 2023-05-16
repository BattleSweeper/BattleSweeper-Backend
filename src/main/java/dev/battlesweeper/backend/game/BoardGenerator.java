package dev.battlesweeper.backend.game;

import dev.battlesweeper.backend.objects.Position;

import java.util.HashSet;
import java.util.Set;

// TODO: Possible improvement: Reduce mine density
public class BoardGenerator {
    private BoardGenerator() {}

    public static Position[] generateMines(Position boardSize, int mines) {
        Set<Position> positions = new HashSet<>();

        for (var i = 0; i < mines; ++i) {
            Position nPos;
            do {
                nPos = getRandomPosition(boardSize.x(), boardSize.y());
            } while (positions.contains(nPos));
            positions.add(nPos);
        }

        return positions.toArray(new Position[0]);
    }

    private static Position getRandomPosition(int limX, int limY) {
        return new Position(randomIn(limX), randomIn(limY));
    }

    private static int randomIn(int max) {
        return (int) Math.floor(Math.random() * max);
    }
}
