package steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ru.komus.eventorplugin.processor.EventorProcessorService;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.*;

public class EventorProcessingSteps {

    private EventorProcessorService service;
    private String actionType;
    private Path tempJsonFile;

    @Before
    public void setUp() throws Exception {
        service = spy(new EventorProcessorService());
        doNothing().when(service).sendHttpEvent(any());
        doNothing().when(service).saveToDatabase(any());
        doNothing().when(service).sendToKafka(any());

        tempJsonFile = Paths.get("build/tmp/test-events.json");
        Files.createDirectories(tempJsonFile.getParent());
    }

    @Given("an event with actionType {string}")
    public void an_event_with_action_type(String actionType) {
        this.actionType = actionType;
    }

    @When("the event is processed")
    public void the_event_is_processed() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode event = mapper.createObjectNode()
                .put("id", 1)
                .put("eventName", "Test Event")
                .put("timestamp", "2024-01-01T00:00:00Z")
                .put("actionType", actionType)
                .set("payload", mapper.createObjectNode().put("test", "data"));


        Files.writeString(tempJsonFile, mapper.writeValueAsString(List.of(event)));


        service.processEvents(tempJsonFile);
    }

    @Then("it should be sent via HTTP")
    public void it_should_be_sent_via_http() {
        verify(service, atLeastOnce()).sendHttpEvent(any());
    }

    @Then("it should be saved to the database")
    public void it_should_be_saved_to_the_database() {
        verify(service, atLeastOnce()).saveToDatabase(any());
    }

    @Then("it should be sent to Kafka")
    public void it_should_be_sent_to_kafka() {
        verify(service, atLeastOnce()).sendToKafka(any());
    }
}
