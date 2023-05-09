package dev.battlesweeper.backend.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import dev.battlesweeper.backend.socket.packet.Packet;
import dev.battlesweeper.backend.socket.packet.PacketType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PacketSerializer extends JsonSerializer<Packet> {
    private final Map<Class<Packet>, String> classMap = new HashMap<>();

    public PacketSerializer() {
        var subclasses = Packet.class.getPermittedSubclasses();
        for (var subclass : subclasses) {
            var regularizedName = regularizeClassName(subclass);
            classMap.put((Class<Packet>)subclass, regularizedName);
        }
    }

    @Override
    public void serialize(Packet value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!classMap.containsKey(value.getClass()))
            throw new IllegalArgumentException("Unknown class: " + value);

        gen.writeStartObject();

        gen.writeFieldName("type");
        gen.writeString(classMap.get(value.getClass()));

        var jType = serializers.constructType(value.getClass());
        var beanDesc = serializers.getConfig().introspect(jType);

        var serializer = BeanSerializerFactory.instance.findBeanOrAddOnSerializer(serializers, jType, beanDesc, false);
        serializer.unwrappingSerializer(null).serialize(value, gen, serializers);

        gen.writeEndObject();
    }

    private String regularizeClassName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(PacketType.class))
            return clazz.getAnnotation(PacketType.class).type();

        var name = clazz.getSimpleName();
        if (!name.endsWith("Packet"))
            return null;

        return name.substring(0, name.length() - "Packet".length())
                .replaceAll("([A-Z])", "_$1")
                .toLowerCase()
                .substring(1);
    }
}
