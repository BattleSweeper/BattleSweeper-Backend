package dev.battlesweeper.backend.socket.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@JsonDeserialize(as = GameFoundPacket.class)
//@JsonSerialize(as = GameFoundPacket.class)
@AllArgsConstructor
@NoArgsConstructor
public final class GameFoundPacket extends Packet {

    public UUID roomID;
    public String authToken;
}