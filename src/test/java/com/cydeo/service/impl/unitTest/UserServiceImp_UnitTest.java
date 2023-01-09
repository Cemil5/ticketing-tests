package com.cydeo.service.impl.unitTest;

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
import com.cydeo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImp_UnitTest {

    @Mock
    private UserRepository userRepository;

    /**
     * When Mockito creates a mock, it does so from the Class of a Type, not from an actual instance.
     * The mock simply creates a bare-bones shell instance of the Class, entirely instrumented to track interactions with it.
     * On the other hand, the spy will wrap an existing instance. It will still behave in the same way as the normal instance;
     * the only difference is that it will also be instrumented to track all the interactions with it.
     */
    @Spy
    private MapperUtil mapperUtil = new MapperUtil(new ModelMapper());

    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * @InjectMocks is the Mockito Annotation. It allows you to mark a field on which an injection is to be performed.
     * @InjectMocks will only inject mocks/spies created using the @Spy or @Mock annotation.
     * Injection allows you to,
     * •	Enable shorthand mock and spy injections.
     * •	Minimize repetitive mock and spy injection.
     * Always remember that the @InjectMocks annotation will only inject mocks/spies created using @Mock or @Spy annotations.
     * Mockito will try to inject mocks only either by constructor injection, setter injection, or property injection, in order.
     * If any of the following strategies fail, then Mockito won't report a failure.
     */
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskService taskService;

    static User user;
    static UserDTO userDTO;

    // this method run before each @Test method.
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setUserName("user");
        user.setPassWord("Abc1");
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
        when(userRepository.findAllByIsDeleted(false)).thenReturn(getUsers());
        List<UserDTO> expectedList = getUserDtos();
        expectedList.sort(Comparator.comparing(UserDTO::getFirstName).reversed());
        List<UserDTO> actualList = userService.listAllUsers();
        // then
        /**
         * AssertJ assertThat :
         * Objects can be compared in various ways either to determine equality of two objects or to examine the fields of an object.
         * for more info : https://assertj.github.io/doc/
         */
        assertThat(actualList).usingRecursiveComparison()
                .ignoringExpectedNullFields() // this method can be used if needed
                //  .ignoringFields("id") // this method can be used if needed
                .isEqualTo(expectedList);
    }

    @Test
    void findByUserName_happyPath() {
        //when
        when(userRepository.findByUserNameAndIsDeleted(anyString(), any()))
                .thenReturn(user);
        //then
        UserDTO actual = userService.findByUserName("John");
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(userDTO);
    }

    @Test
    void findByUserName_throws_exception() {
        //when
        Throwable throwable = catchThrowable(() -> userService.findByUserName("John"));
        //then
        assertInstanceOf(NoSuchElementException.class, throwable);
        assertEquals("user not found", throwable.getMessage());
    }

    @Test
    void save() {
        //when
        when(userRepository.save(any())).thenReturn(user);
        UserDTO actualDto = userService.save(userDTO);
        //then
        verify(mapperUtil).convert(any(UserDTO.class), any(User.class));
        verify(mapperUtil).convert(any(User.class), any(UserDTO.class));
        verify(passwordEncoder).encode(anyString());
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(userDTO);
    }

    @Test
    void update() {
        //when
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean()))
                .thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        UserDTO actualDto = userService.update(userDTO);
        //then
        verify(mapperUtil).convert(any(UserDTO.class), any(User.class));
        verify(passwordEncoder).encode(anyString());
        assertEquals(userDTO.getUserName(), actualDto.getUserName());
        assertThat(actualDto).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(userDTO);
    }

    @ParameterizedTest
    /**
     *@ValueSource
     * •	It is used to provide a single parameter per test method.
     * •	It lets you specify an array of literals or primitive types.
     */
    @ValueSource(strings = {"Manager", "Employee", "Admin"})
    void delete_happyPath(String roleDescription) {
        //given
        User user = getUser(roleDescription);
        //when
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean()))
                .thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        /**
         * Mockito throws an UnsupportedStubbingException when an initialized mock is not called by one of the test methods
         * during execution. We can avoid this strict stub checking by using lenient() method when initializing the mocks.
         */
        lenient().when(projectService.listAllNonCompletedByAssignedManager(any()))
                .thenReturn(new ArrayList<>());
        lenient().when(taskService.listAllNonCompletedByAssignedEmployee(any()))
                .thenReturn(new ArrayList<>());
        userService.delete(userDTO.getUserName());
        //then
        assertTrue(user.getIsDeleted());
        assertNotEquals("user", user.getUserName());
        if (roleDescription.equals("Manager")) {
            verify(projectService).listAllNonCompletedByAssignedManager(any());
        } else if (roleDescription.equals("Employee")) {
            verify(taskService).listAllNonCompletedByAssignedEmployee(any());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Manager", "Employee"})
    void delete_throws_exception(String roleDescription) {
        //given
        User user = getUser(roleDescription);
        //when
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean()))
                .thenReturn(user);
        lenient().when(projectService.listAllNonCompletedByAssignedManager(any()))
                .thenReturn(List.of(new ProjectDTO(), new ProjectDTO()));
        lenient().when(taskService.listAllNonCompletedByAssignedEmployee(any()))
                .thenReturn(List.of(new TaskDTO(), new TaskDTO()));
        Throwable throwable = catchThrowable(() -> userService.delete(userDTO.getUserName()));
        //then
        assertInstanceOf(RejectedExecutionException.class, throwable);
        assertEquals("user cannot be deleted", throwable.getMessage());
    }

    @Test
    void listAllByRole(){
        //when
        when(userRepository.findByRole_DescriptionIgnoreCaseAndIsDeleted(anyString(), anyBoolean()))
                .thenReturn(getUsers());
        List<UserDTO> actualList = userService.listAllByRole("Manager");
        //then
        assertEquals(2, actualList.size());
        verify(mapperUtil, times(2))
                .convert(any(User.class), any(UserDTO.class));
    }

    static User getUser(String roleDescription) {
        User user = new User();
        user.setUserName("user");
        user.setIsDeleted(false);
        Role role = new Role();
        role.setDescription(roleDescription);
        user.setRole(role);
        return user;
    }

    private static List<User> getUsers() {
        User user2 = new User();
        user2.setId(1L);
        user2.setFirstName("Emily");
        return List.of(user, user2);
    }

    private static List<UserDTO> getUserDtos() {
        UserDTO user2 = new UserDTO();
        user2.setId(1L);
        user2.setFirstName("Emily");
        return Arrays.asList(userDTO, user2);
    }

}
