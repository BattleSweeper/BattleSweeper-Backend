package dev.battlesweeper.backend.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.battlesweeper.backend.objects.Position;
import lombok.AllArgsConstructor;

@JsonDeserialize(as = ResultPacket.class)
@AllArgsConstructor
public final class GameStartPacket extends Packet {

    public Position boardSize;
    public Position[] mines;
}
