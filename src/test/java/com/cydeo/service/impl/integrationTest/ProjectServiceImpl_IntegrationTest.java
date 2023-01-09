package com.cydeo.service.impl.integrationTest;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectServiceImpl_IntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Test
    @Transactional // ModelMapper mapping errors: Error mapping com.cydeo.entity.Project to com.cydeo.dto.ProjectDTO
    void getByProjectCode_happyPath() {
        // when
        ProjectDTO actualDto = projectService.getByProjectCode("SP01");
        // then
        assertEquals("SP01", actualDto.getProjectCode());
        assertEquals("Spring Boot Project", actualDto.getProjectDetail());
        assertThat(actualDto).usingRecursiveComparison()
                .isNotNull();
    }

    @Test
    void getByProjectCode_throws_exception() {
        // when
        Throwable throwable = catchThrowable(() -> projectService.getByProjectCode("SP0155"));
        // then
        assertInstanceOf(NoSuchElementException.class, throwable);
        assertEquals("project not found", throwable.getMessage());
    }

    @Test
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
        // could not execute statement; SQL [n/a]; constraint [insert_user_id" of relation "projects];
    void save() {
        //given
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectCode("TT001");
        projectDTO.setProjectDetail("Testing");

        //when
        projectService.save(projectDTO);
        //then
        ProjectDTO actualDto = projectService.getByProjectCode("TT001");
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(projectDTO);
    }

    @Test
    @Transactional   // MappingException: ModelMapper mapping errors
    void update() {
        // given
        ProjectDTO projectDTO = projectService.getByProjectCode("SP01");
        projectDTO.setProjectDetail("testing project");

        // when
        ProjectDTO savedDto = projectService.update(projectDTO);

        // then
        assertThat(savedDto).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(projectDTO);
    }

    @Test
    @Transactional //NoSuchElementException: task not found
    void delete() {
        // when
        projectService.delete("SP01");

        // then
        Project project = projectRepository.findById(2L).orElseThrow(() -> new NoSuchElementException("project not found"));
        assertTrue(project.getIsDeleted());
    }

    @Test
    @Transactional  // MappingException: ModelMapper mapping errors
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
    void complete() {
        // when
        projectService.complete("SP01");
        // then
        Project project = projectRepository.findByProjectCode("SP01").orElseThrow();
        assertEquals(Status.COMPLETE, project.getProjectStatus());
        List<TaskDTO> taskDTOS = taskService.getTasksByProjectCode("SP01");
        taskDTOS.forEach(task -> assertEquals(Status.COMPLETE, task.getTaskStatus()));
    }

    @Test
    @Transactional     // MappingException: ModelMapper mapping errors
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
    void listAllProjectDetails() {
        //when
        List<ProjectDTO> projectDTOS = projectService.listAllProjectDetails();
        //then
        assertEquals(2, projectDTOS.size());
    }

    @Test
    @Transactional     // MappingException: ModelMapper mapping errors
    @WithMockUser(username = "samantha@manager.com", password = "Abc1", roles = "MANAGER")
    void listAllNonCompletedByAssignedManager() {
        UserDTO user = userService.findByUserName("samantha@manager.com");
        //when
        List<ProjectDTO> projectDTOS = projectService.listAllNonCompletedByAssignedManager(user);
        //then
        assertEquals(2, projectDTOS.size());
    }

}
