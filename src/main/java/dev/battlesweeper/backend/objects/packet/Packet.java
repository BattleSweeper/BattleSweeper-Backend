package dev.battlesweeper.backend.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.battlesweeper.backend.objects.json.PacketDeserializer;
import dev.battlesweeper.backend.objects.json.PacketSerializer;

@JsonDeserialize(using = PacketDeserializer.class)
@JsonSerialize(using = PacketSerializer.class)
public abstract sealed class Packet permits GameFoundPacket, GameStartPacket, ResultPacket, TestPacket, UserJoinPacket {}
