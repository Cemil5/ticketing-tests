package com.cydeo.service.impl.unitTest;

import com.cydeo.dto.TaskDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.UserService;
import com.cydeo.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImpl_UnitTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    static Task task;
    static TaskDTO taskDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTaskDetail("Test Task");
        task.setTaskStatus(Status.OPEN);
        taskDto = new TaskDTO();
        taskDto.setId(1L);
        taskDto.setTaskDetail("Test Task");
        taskDto.setTaskStatus(Status.OPEN);
    }

    @Test
    void listAllTasks() {
        // given
        List<TaskDTO> expectedList = getTaskDtos().stream()
                .sorted(Comparator.comparing(TaskDTO::getTaskStatus).reversed())
                .collect(Collectors.toList());
        // when
        when(taskRepository.findAll()).thenReturn(getTasks());
        List<TaskDTO> actualList = taskService.listAllTasks();
        // then
            // option 1 :
        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expectedList.get(0).getId(), actualList.get(0).getId());
            // option 2 :
        assertThat(actualList).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedList);
    }

    @Test
    void findById() {
        // when
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));
        TaskDTO actualTask = taskService.findById(1L);
        // then
            // option 1 :
        assertNotNull(actualTask);
        assertEquals(taskDto.getTaskDetail(), actualTask.getTaskDetail());
            // option 2 :
        assertThat(actualTask).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(taskDto);
    }

    @Test
    void findById_throws_exception() {
        //when
        when(taskRepository.findById(any())).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(()-> taskService.findById(1L));
        //then
        assertInstanceOf(NoSuchElementException.class, throwable);
        assertEquals("Task not found", throwable.getMessage());
    }

    @Test
    void save(){
        // given
        taskDto.setId(null);
        // when
        when(taskRepository.save(any())).thenReturn(task);
        TaskDTO actualTask = taskService.save(taskDto);
        // then
            // option 1:
        assertEquals(taskDto.getTaskDetail(), actualTask.getTaskDetail());
        assertNotNull(actualTask.getId());
            // option 2:
        assertThat(actualTask).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(task);
    }

    @Test
    void update_happyPath(){
        // when
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        TaskDTO actualTask = taskService.update(taskDto);
        // then
            // option 1:
        assertEquals(taskDto.getTaskDetail(), actualTask.getTaskDetail());
        assertEquals(task.getId(), actualTask.getId());
            // option 2:
        assertThat(actualTask).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(task);
    }

    @Test
    void update_throws_exception(){
        // when
        when(taskRepository.findById(any())).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(()-> taskService.update(taskDto));
        //then
        assertInstanceOf(NoSuchElementException.class, throwable);
        assertEquals("task not found", throwable.getMessage());
    }

    @Test
    void delete() {
        //given
        task.setIsDeleted(false);
        //when
        when(taskRepository.findById(any())).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);
        taskService.delete(1L);
        //then
        assertTrue(task.getIsDeleted());
    }



    private List<Task> getTasks() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTaskStatus(Status.COMPLETE);
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTaskStatus(Status.OPEN);
        return List.of(task1, task2);
    }

    private List<TaskDTO> getTaskDtos() {
        TaskDTO task1 = new TaskDTO();
        task1.setId(1L);
        task1.setTaskStatus(Status.COMPLETE);
        TaskDTO task2 = new TaskDTO();
        task2.setId(2L);
        task2.setTaskStatus(Status.OPEN);
        return List.of(task1, task2);
    }
}
