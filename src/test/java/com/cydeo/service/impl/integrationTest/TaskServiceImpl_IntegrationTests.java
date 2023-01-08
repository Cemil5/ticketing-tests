package com.cydeo.service.impl.integrationTest;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.UserService;
import com.cydeo.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceImpl_IntegrationTests {

    @Autowired
    TaskServiceImpl taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserService userService;

    @Test
    @Transactional
    void listAllTasks() {
        //when
        List<TaskDTO> taskDTOS = taskService.listAllTasks();
        //then
        assertTrue(taskDTOS.size()>15);
    }

    @Test
    @Transactional
    void findById() {
        //when
        TaskDTO taskDTO = taskService.findById(1L);
        //then
        assertEquals("Injecting dependencies", taskDTO.getTaskDetail());
    }

    @Test
    @Transactional
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
    void save() {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(1L);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskDetail("test");
        taskDTO.setProject(projectDTO);
        taskDTO.setAssignedEmployee(userDTO);

        //when
        TaskDTO savedDto = taskService.save(taskDTO);

        //then
        TaskDTO actualDto = taskService.findById(savedDto.getId());
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(taskDTO);
    }

    @Test
    @Transactional
    void update() {
        //given
        TaskDTO taskDTO = taskService.findById(1L);
        taskDTO.setTaskDetail("test");

        //when
        TaskDTO actual = taskService.update(taskDTO);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(taskDTO);
    }

    @Test
    @Transactional
    @Commit
    void delete() {
        //when
        taskService.delete(2L);

        //then
        Task task = taskRepository.findById(2L).orElseThrow( ()-> new NoSuchElementException("task not found"));
        assertTrue(task.getIsDeleted());
    }

    @Test
    void totalNonCompletedTask() {
        //when
        int total = taskService.totalNonCompletedTask("SP02");
        //then
        assertEquals(1, total);
    }

    @Test
    void totalCompletedTask() {
        //when
        int total = taskService.totalCompletedTask("SP00");
        //then
        assertEquals(1, total);
    }

    @Test
    @Transactional
    @WithMockUser(username = "sameen@employee.com", password = "Abc1", roles = "EMPLOYEE")
    void findAllTasksByStatusIsNot() {
        //when
        List<TaskDTO> taskDTOS = taskService.findAllTasksByStatusIsNot(Status.COMPLETE);
        //then
        assertEquals(2, taskDTOS.size());
    }

    @Test
    @Transactional
    @WithMockUser(username = "sameen@employee.com", password = "Abc1", roles = "EMPLOYEE")
    void findAllTasksByStatusIs() {
        //when
        List<TaskDTO> taskDTOS = taskService.findAllTasksByStatusIsNot(Status.IN_PROGRESS);
        //then
        assertEquals(3, taskDTOS.size());
    }

    @Test
    @Transactional
    @WithMockUser(username = "sameen@employee.com", password = "Abc1", roles = "EMPLOYEE")
    void listAllNonCompletedByAssignedEmployee() {
        //given
        UserDTO employee = userService.findByUserName("sameen@employee.com");
        //when
        List<TaskDTO> taskDTOS = taskService.listAllNonCompletedByAssignedEmployee(employee);
        //then
        assertEquals(2, taskDTOS.size());
    }

    @Test
    @Transactional
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
    void completeByProject() {
        //given
        List<TaskDTO> tasks = taskService.getTasksByProjectCode("SP00");
        tasks.forEach(dto -> System.out.println(dto.getTaskStatus()));
        System.out.println("----------------");

        //when
        taskService.completeByProject("SP00");

        //then
        List<TaskDTO> taskDTOS = taskService.getTasksByProjectCode("SP00");
        taskDTOS.forEach(taskDTO -> assertEquals(Status.COMPLETE, taskDTO.getTaskStatus()));
    }

    @ParameterizedTest
    @Transactional
    @CsvSource({
            "'SP00', 4",
            "'SP20', 0"
    })
    void getTasksByProjectCode(String projectCode, int expected) {
        //when
        List<TaskDTO> tasks = taskService.getTasksByProjectCode(projectCode);
        //then
        assertEquals(expected, tasks.size());
    }

}