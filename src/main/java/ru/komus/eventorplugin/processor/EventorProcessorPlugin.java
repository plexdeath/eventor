package ru.komus.eventorplugin.processor;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public class EventorProcessorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        EventorProcessorExtention extention = project.getExtensions().create(
                "eventorProcessor",
                EventorProcessorExtention.class,
                project.getObjects()
        );

        TaskProvider<EventorProcessorTask> processEventsTask = project.getTasks().register(
                "processEvents",
                EventorProcessorTask.class,
                task -> {
                    task.setGroup("processing");
                    task.setDescription("Process events from JSON file");
                    task.setJsonFilePath(extention.getJsonFilePath().get());
                }
        );

        extention.getJsonFilePath().finalizeValueOnRead();
    }
}