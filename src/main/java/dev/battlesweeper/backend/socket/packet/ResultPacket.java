package dev.battlesweeper.backend.socket.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;

@JsonDeserialize(as = ResultPacket.class)
@AllArgsConstructor
public final class ResultPacket extends Packet {

    public ResultPacket(String result) {
        this(result, null);
    }

    public static final String RESULT_OK       = "OK";
    public static final String RESULT_BAD_DATA = "BAD_DATA";
    public static final String RESULT_FAILURE  = "FAILURE";

    public String result;

    public String message;
}
