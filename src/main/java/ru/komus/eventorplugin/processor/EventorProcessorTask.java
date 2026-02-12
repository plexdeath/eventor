package ru.komus.eventorplugin.processor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class EventorProcessorTask extends DefaultTask {
    private String jsonFilePath;
    private final EventorProcessorService service = new EventorProcessorService();

    public void setJsonFilePath(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    @TaskAction
    public void processEvents() {
        try {
            File file = new File(jsonFilePath);

            if (!file.exists()) {
                throw new RuntimeException("JSON file not found: " + jsonFilePath);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(file);

            for (JsonNode event : root) {
                String actionType = event.path("actionType").asText();
                switch (actionType) {
                    case "HTTP" -> sendHttpEvent(event);
                    case "DB" -> saveToDatabase(event);
                    case "KAFKA" -> sendToKafka(event);
                    default -> System.out.println("Unknown actionType: " + actionType);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process events", e);
        }
    }

    public void sendHttpEvent(JsonNode event) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/events"))
                    .POST(HttpRequest.BodyPublishers.ofString(event.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP event sent. Status: " + response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToDatabase(JsonNode event) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:h2:mem:testdb", "sa", "")) {

            String sql = "INSERT INTO events (id, event_name, timestamp, payload) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, event.path("id").asLong());
            stmt.setString(2, event.path("eventName").asText());
            stmt.setString(3, event.path("timestamp").asText());
            stmt.setString(4, event.path("payload").toString());
            stmt.executeUpdate();
            System.out.println("Event saved to DB: id=" + event.path("id").asLong());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToKafka(JsonNode event) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>("events-topic", event.toString());
            producer.send(record);
            System.out.println("Event sent to Kafka: id=" + event.path("id").asText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}