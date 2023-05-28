package dev.battlesweeper.backend.objects.packet;

import dev.battlesweeper.backend.objects.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor @Builder @Getter
@PacketType(type = "event_player_update")
public final class PlayerUpdatePacket extends Packet {

    private UserInfo user;
    private Packet packet;
}
