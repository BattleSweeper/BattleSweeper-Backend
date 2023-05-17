package dev.battlesweeper.backend.objects.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.battlesweeper.backend.objects.packet.Packet;
import dev.battlesweeper.backend.objects.packet.PacketType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PacketDeserializer extends JsonDeserializer<Packet> {

    private final Map<String, Class<Packet>> classMap = new HashMap<>();

    public PacketDeserializer() {
        var subclasses = Packet.class.getPermittedSubclasses();
        for (var subclass : subclasses) {
            var regularizedName = regularizeClassName(subclass);
            classMap.put(regularizedName, (Class<Packet>) subclass);
        }
    }

    @Override
    public Packet deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        if (!root.has("type"))
            return null;

        var type = root.get("type").asText();
        if (classMap.containsKey(type)) {
            var clazz = classMap.get(type);
            root.remove("type");
            return mapper.readValue(root.toString(), clazz);
        }

        return null;
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
