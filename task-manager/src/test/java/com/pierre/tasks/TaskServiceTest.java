package com.pierre.tasks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Test
    void createsTaskWithTrimmedTitleAndPendingStatus() {
        TaskService service = new TaskService();

        Task task = service.createTask("  Study Java  ");

        assertEquals(1L, task.getId());
        assertEquals("Study Java", task.getTitle());
        assertFalse(task.isCompleted());
        assertEquals(1, service.getTasks().size());
        assertSame(task, service.getTasks().get(0));
    }

    @Test
    void assignsConsecutiveIds(){
        TaskService service = new TaskService();

        Task task_1 = service.createTask("  Study Java  ");
        Task task_2 = service.createTask("  Study Java  ");

        assertEquals(1L, task_1.getId());
        assertEquals(2L, task_2.getId());
    }

    @Test
    void rejectsNullTitle() {
        TaskService service = new TaskService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createTask(null)
        );

        assertTrue(service.getTasks().isEmpty());
    }
}