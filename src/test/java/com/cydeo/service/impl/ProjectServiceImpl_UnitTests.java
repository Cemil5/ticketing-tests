package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.SecurityService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImpl_UnitTests {

    @Mock
    ProjectRepository projectRepository;
    @Spy
    MapperUtil mapperUtil = new MapperUtil(new ModelMapper());
    @Mock
    UserService userService;
    @Mock
    TaskService taskService;
    @Mock
    SecurityService securityService;
    @InjectMocks
    ProjectServiceImpl projectService;

    static Project project;
    static ProjectDTO projectDto;

    @BeforeEach
    void setUp(){
        project = new Project();
        project.setId(1L);
        project.setProjectCode("P001-en");
        project.setProjectStatus(Status.OPEN);
        projectDto = new ProjectDTO();
        projectDto.setId(5L);
        projectDto.setProjectCode("P001-dto");
    }

    @Test
    void getByProjectCode_happyPath() {
        //when
        when(projectRepository.findByProjectCode(anyString())).thenReturn(Optional.ofNullable(project));
        ProjectDTO actual = projectService.getByProjectCode("P001");
        //then
        assertEquals(project.getProjectCode(), actual.getProjectCode());
    }

    @Test
    void getByProjectCode_throws_exception() {
        //when
        when(projectRepository.findByProjectCode(anyString())).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(()-> projectService.getByProjectCode(anyString()));
        //then
        assertInstanceOf(NoSuchFieldError.class, throwable);
        assertEquals("project not found", throwable.getMessage());
    }

    @Test
    void save() {
        //when
        when(projectRepository.save(any())).thenReturn(project);
        ProjectDTO actual = projectService.save(projectDto);
        //then
        verify(projectRepository).save(any(Project.class));
        assertEquals(project.getProjectCode(), actual.getProjectCode());
        assertEquals(Status.OPEN, actual.getProjectStatus());
    }

    @Test
    void update() {
        //given
        project.setProjectDetail("test");
        projectDto.setProjectDetail("test");
        //when
        when(projectRepository.findByProjectCode(anyString())).thenReturn(Optional.ofNullable(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        ProjectDTO actual = projectService.update(projectDto);
        //then
        assertEquals(projectDto.getProjectDetail(), actual.getProjectDetail());
        assertEquals(project.getId(), actual.getId());
        assertEquals(project.getProjectStatus(), actual.getProjectStatus());
    }

    @Test
    void delete() {
        //given
        project.setIsDeleted(false);
        //when
        when(projectRepository.findByProjectCode(anyString())).thenReturn(Optional.ofNullable(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(taskService.getTasksByProjectCode(anyString())).thenReturn(getTasks());
        projectService.delete(project.getProjectCode());
        //then
        assertTrue(project.getIsDeleted());
        assertNotEquals("P001", project.getProjectCode());
        verify(taskService).getTasksByProjectCode(anyString());
        verify(taskService, times(getTasks().size())).delete(anyLong());
    }

    @Test
    void complete() {
        //when
        when(projectRepository.findByProjectCode(anyString())).thenReturn(Optional.ofNullable(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        projectService.complete("P001");
        //then
        assertEquals(Status.COMPLETE, project.getProjectStatus());
        verify(taskService).completeByProject(anyString());
    }

    @ParameterizedTest
    @MethodSource(value = "input")
    void listAllProjectDetails(List<Project> projects) {
        //given
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("user");
        //when
        when(securityService.getLoggedInUsername()).thenReturn("user");
        when(userService.findByUserName("user")).thenReturn(userDTO);
        when(projectRepository.findByAssignedManager_UserName("user")).thenReturn(projects);
        lenient().when(taskService.totalCompletedTask(anyString())).thenReturn(5).thenReturn(10);
        lenient().when(taskService.totalNonCompletedTask(anyString())).thenReturn(3).thenReturn(6);
        List<ProjectDTO> actualList = projectService.listAllProjectDetails();
        //then
        if (actualList.size()>1) {
            assertEquals(5, actualList.get(0).getCompleteTaskCounts());
            assertEquals(10, actualList.get(1).getCompleteTaskCounts());
            assertEquals(3, actualList.get(0).getInCompleteTaskCounts());
            assertEquals(6, actualList.get(1).getInCompleteTaskCounts());
            verify(taskService, times(actualList.size())).totalCompletedTask(anyString());
            verify(taskService, times(actualList.size())).totalNonCompletedTask(anyString());
        }

        assertEquals(projects.size(), actualList.size());
    }

    static Stream<Arguments> input(){
        project = new Project();
        project.setId(1L);
        project.setProjectCode("P001");
        project.setProjectStatus(Status.OPEN);
        return Stream.of(
                Arguments.arguments(getProjects()),
                Arguments.arguments(new ArrayList<>())
        );
    }

    @ParameterizedTest
    @MethodSource(value = "input")
    void listAllNonCompletedByAssignedManager(List<Project> projects) {
        //given
        UserDTO user = new UserDTO();
        user.setUserName("user");
        //when
        when(projectRepository.findAllByProjectStatusIsNotAndAssignedManager(any(), any()))
                .thenReturn(projects);
        List<ProjectDTO> actualList = projectService.listAllNonCompletedByAssignedManager(user);
        //then
        assertEquals(projects.size(), actualList.size());
    }

    private static List<Project> getProjects() {
        Project project1 = new Project();
        project1.setId(2L);
        project1.setProjectCode("P002");
        project1.setProjectStatus(Status.OPEN);
        return List.of(project, project1);
    }

    private List<TaskDTO> getTasks(){
        TaskDTO task1 = new TaskDTO();
        task1.setId(1L);
        task1.setTaskStatus(Status.OPEN);
        TaskDTO task2 = new TaskDTO();
        task2.setId(2L);
        task2.setTaskStatus(Status.OPEN);
        return List.of(task1, task2);
    }
}