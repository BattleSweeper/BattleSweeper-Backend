package dev.battlesweeper.backend.objects.packet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.battlesweeper.backend.objects.user.User;
import lombok.AllArgsConstructor;

@JsonDeserialize(as = ResultPacket.class)
@AllArgsConstructor
public final class UserJoinPacket extends Packet {

    public UserJoinPacket(User user) {
        this(user.getId(), user.getName());
    }

    public Long id;
    public String name;
}
