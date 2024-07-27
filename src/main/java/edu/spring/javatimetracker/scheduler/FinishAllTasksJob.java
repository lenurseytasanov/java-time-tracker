package edu.spring.javatimetracker.scheduler;

import edu.spring.javatimetracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinishAllTasksJob {

    private final TaskService taskService;

    @Scheduled(cron = "0 59 23 * * *", zone = "#{ appProperties.getTimeZone().toZoneId() }")
    public void finishAllTasks() {
        taskService.finishAllTasks();
    }
}
