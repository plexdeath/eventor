package ru.komus.eventorplugin.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;

public class EventorProcessorService {

    private final ObjectMapper mapper = new ObjectMapper();

    public void processEvents(Path jsonPath) {
        try {
            JsonNode root = mapper.readTree(jsonPath.toFile());

            for (JsonNode event : root) {
                String actionType = event.path("actionType").asText();
                switch (actionType) {
                    case "HTTP" -> sendHttpEvent(event);
                    case "DB" -> saveToDatabase(event);
                    case "KAFKA" -> sendToKafka(event);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process events", e);
        }
    }

    public void sendHttpEvent(JsonNode event) { /* ... */ }
    public void saveToDatabase(JsonNode event) { /* ... */ }
    public void sendToKafka(JsonNode event) { /* ... */ }
}