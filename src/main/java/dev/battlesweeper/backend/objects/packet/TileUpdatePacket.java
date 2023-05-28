package dev.battlesweeper.backend.objects.packet;

import dev.battlesweeper.backend.objects.Position;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor @Getter
@PacketType(type = "event_tile_update")
public final class TileUpdatePacket extends Packet {

    public static final int ACTION_TILE_OPEN   = 0;
    public static final int ACTION_FLAG_PLACE  = 1;
    public static final int ACTION_FLAG_REMOVE = 2;

    private Position position;
    private int action;
    private int bombLeft;
}
