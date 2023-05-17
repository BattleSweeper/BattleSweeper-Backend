package dev.battlesweeper.backend.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@JsonDeserialize(as = ResultPacket.class)
@AllArgsConstructor
public final class ResultPacket extends Packet {

    public ResultPacket(int result) {
        this(result, null);
    }

    public ResultPacket(HttpStatus status, String message) {
        this(status.value(), message);
    }

    public ResultPacket(HttpStatus status) {
        this(status.value());
    }

    public static final int RESULT_OK       = HttpStatus.OK.value();
    public static final int RESULT_BAD_DATA = HttpStatus.NOT_ACCEPTABLE.value();
    public static final int RESULT_FAILURE  = HttpStatus.BAD_REQUEST.value();

    public int result;

    public String message;
}
