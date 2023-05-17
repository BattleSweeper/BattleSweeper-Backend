package dev.battlesweeper.backend.objects.packet;

import dev.battlesweeper.backend.objects.Position;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
//@JsonDeserialize(using = PacketDeserializer.class, as = FlagPlacePacket.class)
//@JsonTypeName("event_flag_place")
@PacketType(type = "event_flag_place")
public final class FlagPlacePacket extends Packet implements GameEventPacket {

    /**
     * ID of game room session
     */
    public UUID roomId;

    /**
     * Position of placed flag
     */
    public Position position;

    /**
     * nth count of flag
     */
    public int count;

    @Override
    public boolean isBroadcast() {
        return true;
    }
}
