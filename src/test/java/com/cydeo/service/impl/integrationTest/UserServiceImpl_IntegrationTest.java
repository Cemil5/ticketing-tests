package com.cydeo.service.impl.integrationTest;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImpl_IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MapperUtil mapperUtil;

    @Autowired
    UserService userService;

    @MockBean
    TaskService taskService;

    @MockBean
    ProjectService projectService;

    static User user;
    static UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setUserName("user");
        user.setPassWord("Abc1");
        user.setEnabled(true);
        Role role = new Role();
        role.setDescription("Manager");
        user.setRole(role);
        userDTO = new UserDTO();
        userDTO.setId(null);
        userDTO.setFirstName("John");
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setDescription("Manager");
        userDTO.setRole(roleDTO);
    }

    @Test
    void listAllUsers() {
        // when
        List<UserDTO> userDTOS = userService.listAllUsers();
        // then
        assertEquals(7, userDTOS.size());
        assertEquals("sameen@employee.com", userDTOS.get(0).getUserName());
    }

    @Test
    void findByUserName_happyPath() {
        // when
        UserDTO userDTO = userService.findByUserName("sameen@employee.com");
        // then
        assertEquals("Sameen", userDTO.getFirstName());
        assertEquals("Shaw", userDTO.getLastName());
        assertThat(userDTO).usingRecursiveComparison()
                .ignoringFields("confirmPassWord")
                .isNotNull();
    }

    @Test
    void findByUserName_throws_exception() {
        // when
        Throwable throwable = catchThrowable(() -> userService.findByUserName("sameen@employee.co"));
        // then
        assertInstanceOf(NoSuchElementException.class, throwable);
        assertEquals("user not found", throwable.getMessage());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin@admin.com", password = "Abc1", roles = "ADMIN")
    void save() {
        // when
        UserDTO savedDto = userService.save(userDTO);
        // then
        User actual = userRepository.findById(savedDto.getId()).orElse(null);
        assertThat(actual).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("passWord")
                .isEqualTo(user);
    }

    @Test
    @Transactional
    void update() {
        // given
        UserDTO dto = userService.findByUserName("admin@admin.com");
        dto.setFirstName("test");
        // when
        UserDTO actual = userService.update(dto);
        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("passWord")
                .isEqualTo(dto);
    }

    @ParameterizedTest
    @Transactional
    @Commit
    @ValueSource(strings = {"admin@admin.com", "samantha@manager.com", "john@employee.com"})
    void delete_happyPath(String username) {
        // when
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());
        Long id = userService.findByUserName(username).getId();
        userService.delete(username);
        // then
        User result = userRepository.findById(id).orElseThrow();
        assertTrue(result.getIsDeleted());
    }

    @ParameterizedTest
    @Transactional
    @Commit
    @ValueSource(strings = {"harold@manager.com", "grace@employee.com"})
    void delete_throws_exception(String username) {
        // when
        lenient().when(projectService.listAllNonCompletedByAssignedManager(any()))
                .thenReturn(List.of(new ProjectDTO(), new ProjectDTO()));
        lenient().when(taskService.listAllNonCompletedByAssignedEmployee(any()))
                .thenReturn(List.of(new TaskDTO(), new TaskDTO()));
        Throwable throwable = catchThrowable(() -> userService.delete(username));
        //then
        assertInstanceOf(RejectedExecutionException.class, throwable);
        assertEquals("user cannot be deleted", throwable.getMessage());
    }

    @ParameterizedTest
    @Transactional
    @MethodSource(value = "description")
    void listAllByRole(String roleDescription, int expectedSize) {
        // when
        List<UserDTO> actualList = userService.listAllByRole(roleDescription);
        // then
        assertEquals(expectedSize, actualList.size());
    }

    static Stream<Arguments> description() {
        return Stream.of(arguments("Admin", 1),
                arguments("Manager", 2),
                arguments("Employee", 4)
        );
    }

    @Test
    @WithMockUser(username = "harold@manager.com", password = "Abc1", roles = "MANAGER")
    void getLoggedInUsername(){
        // when
        String actual = userService.getLoggedInUsername();
        // then
        assertEquals("harold@manager.com", actual);
    }


}
