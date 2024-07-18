package edu.spring.javatimetracker.db.repository;

import edu.spring.javatimetracker.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskJpaRepository extends JpaRepository<Task, Long> {

    /**
     * Возвращает список задач пользователя за период c N (включительно) по M (не включительно), отсортированный
     * по длительности выполнения
     * @param username имя пользователя
     * @param from дата начала периода
     * @param to дата конца периода
     * @return список задач
     */
    @Query("SELECT task FROM Task task JOIN User user ON task.assignee.id = user.id " +
            "WHERE user.username = :username AND task.startedAt IS NOT NULL AND task.finishedAt IS NOT NULL AND " +
            "task.startedAt >= :from AND task.finishedAt < :to " +
            "ORDER BY task.finishedAt - task.startedAt DESC")
    List<Task> findUserTasks(@Param("username") String username, @Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    /**
     * Возвращает список интервалов пользователя за период c N (включительно) по M (не включительно), отсортированный
     * по началу выполнения задач
     * @param username имя пользователя
     * @param from дата начала периода
     * @param to дата конца периода
     * @return список задач
     */
    @Query("SELECT task FROM Task task JOIN User user ON task.assignee.id = user.id " +
            "WHERE user.username = :username AND task.startedAt IS NOT NULL AND task.finishedAt IS NOT NULL AND " +
            "task.startedAt >= :from AND task.finishedAt < :to " +
            "ORDER BY task.startedAt")
    List<Task> findUserIntervals(@Param("username") String username, @Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    /**
     * Возвращает суммарное рабочее время пользователя
     * @param username имя пользователя
     * @param from дата начала периода
     * @param to дата конца периода
     * @return рабочее время в наносекундах
     */
    @Query("SELECT SUM(task.finishedAt - task.startedAt) FROM Task task JOIN User user ON task.assignee.id = user.id " +
            "WHERE user.username = :username AND task.startedAt IS NOT NULL AND task.finishedAt IS NOT NULL AND " +
            "task.startedAt >= :from AND task.finishedAt < :to ")
    Long getUserTimeSum(@Param("username") String username, @Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    /**
     * Удаляет все задачи пользователя
     * @param username имя пользователя
     */
    @Modifying
    @Query("DELETE FROM Task task JOIN User user ON task.assignee.id = user.id WHERE user.username = :username")
    void deleteUserTasks(@Param("username") String username);

    Optional<Task> findByDescription(String description);
}
