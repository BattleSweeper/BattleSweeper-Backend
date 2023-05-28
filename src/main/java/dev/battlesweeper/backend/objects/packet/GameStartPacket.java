package dev.battlesweeper.backend.objects.packet;

import dev.battlesweeper.backend.objects.Position;
import dev.battlesweeper.backend.objects.UserInfo;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public final class GameStartPacket extends Packet {

    public List<UserInfo> users;
    public Position boardSize;
    public Position[] mines;
}
