package dev.battlesweeper.backend.objects.packet;

import dev.battlesweeper.backend.objects.Position;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class GameStartPacket extends Packet {

    public Position boardSize;
    public Position[] mines;
}
