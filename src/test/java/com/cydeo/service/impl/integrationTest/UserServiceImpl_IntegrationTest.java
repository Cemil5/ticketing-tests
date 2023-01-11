package com.cydeo.service.impl.integrationTest;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.RoleRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.SecurityService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
public class UserServiceImpl_IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    /**
     * We can use the @MockBean to add mock objects to the Spring application context.
     * The mock will replace any existing bean of the same type in the application context.
     * If no bean of the same type is defined, a new one will be added.
     * This annotation is useful in integration tests where a particular bean, like an external service, needs to be mocked.
     * Use @Mock when unit testing your business logic (only using JUnit and Mockito).
     *  Use @MockBean when you write a test that is backed by a Spring Test Context and you want to add or replace a bean with a mocked version of it.
     */

    static User user;
    static UserDTO userDTO;
    @Autowired
    private RoleRepository roleRepository;

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
    @Transactional    // InvalidDataAccessApiUsageException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing
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
    @Transactional // DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [last_update_user_id" of relation "users]
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
    @WithMockUser(username = "admin@admin.com", password = "Abc1", roles = "ADMIN")
//    @Commit   // spring by default rollback after each test. it really deletes if we uncomment this annotation.
    /**
     *@ValueSource
     * •	It is used to provide a single parameter per test method.
     * •	It lets you specify an array of literals or primitive types.
     */
    @ValueSource(strings = {"admin@admin.com", "john@employee.com"})
    void delete_happyPath(String username) {
        // given
        UserDTO expected = userService.findByUserName(username);
        // when
        userService.delete(username);
        // then
        User result = userRepository.findById(expected.getId()).orElseThrow();
        assertTrue(result.getIsDeleted());
        assertNotEquals(expected.getUserName(),result.getUserName());
    }

    @ParameterizedTest
    @Transactional
    @WithMockUser(username = "admin@admin.com", password = "Abc1", roles = "ADMIN")
    @ValueSource(strings = {"samantha@manager.com", "harold@manager.com"})
    void delete_throws_exception(String username) {
        // when
        Throwable throwable = catchThrowable(() -> userService.delete(username));
        //then
        assertInstanceOf(RejectedExecutionException.class, throwable);
        assertEquals("user cannot be deleted", throwable.getMessage());
    }

    @ParameterizedTest
    /**
     * @MethodSource
     * •	It is used to specify a factory method for test arguments.
     * •	This method can be present in the same class or any other class too.
     * •	The factory method should be static and should return a Stream, an Iterable or an array of elements.
     */
    @MethodSource(value = "description")
    void listAllByRole(String roleDescription, int expectedSize) {
        // when
        List<UserDTO> actualList = userService.listAllByRole(roleDescription);
        // then
        assertEquals(expectedSize, actualList.size());
        actualList.forEach(
                userDTO1 -> assertEquals(roleDescription, userDTO1.getRole().getDescription())
        );
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
