
package ru.komus.eventorplugin.processor;

import org.gradle.api.provider.Property;
import org.gradle.api.model.ObjectFactory;

public abstract class EventorProcessorExtention {
    private final Property<String> jsonFilePath;

    public EventorProcessorExtention(ObjectFactory objects) {
        this.jsonFilePath = objects.property(String.class);
        this.jsonFilePath.set("./events.json");
    }

    public Property<String> getJsonFilePath() {
        return jsonFilePath;
    }
}