package edu.spring.javatimetracker.scheduler;

import edu.spring.javatimetracker.configuration.AppProperties;
import edu.spring.javatimetracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteOldTasksJob {

    private final TaskService taskService;

    private final AppProperties appProperties;

    @Scheduled(cron = "0 0 0 * * *", zone = "#{ appProperties.getTimeZone().toZoneId() }")
    public void deleteOldTasks() {
        taskService.deleteOldTasks(appProperties.getTaskTtl());
    }
}
