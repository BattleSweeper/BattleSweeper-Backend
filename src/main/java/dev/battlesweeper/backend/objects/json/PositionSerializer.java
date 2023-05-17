package dev.battlesweeper.backend.objects.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.battlesweeper.backend.objects.Position;

import java.io.IOException;

public class PositionSerializer extends JsonSerializer<Position> {

    @Override
    public void serialize(Position value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        gen.writeNumber(value.x());
        gen.writeNumber(value.y());
        gen.writeEndArray();
    }
}
