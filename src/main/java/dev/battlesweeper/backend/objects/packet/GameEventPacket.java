package dev.battlesweeper.backend.objects.packet;

public interface GameEventPacket {

    public default boolean isBroadcast() {
        return false;
    }
}
