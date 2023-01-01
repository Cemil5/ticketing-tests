package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public UserServiceImpl(UserRepository userRepository,
                           MapperUtil mapperUtil,
                           @Lazy ProjectService projectService,
                           @Lazy TaskService taskService,
                           PasswordEncoder passwordEncoder, SecurityService securityService) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
        this.projectService = projectService;
        this.taskService = taskService;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(false);
        return userList.stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return mapperUtil.convert(userRepository.findByUserNameAndIsDeleted(username, false), new UserDTO());
    }

    @Override
    public void save(UserDTO dto) {
        User user = mapperUtil.convert(dto, new User());
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public UserDTO update(UserDTO user) {
        User updatedUser = mapperUtil.convert(user, new User());
        User savedUser = userRepository.findByUserNameAndIsDeleted(user.getUserName(), false);
        updatedUser.setId(savedUser.getId());
        updatedUser.setInsertDateTime(savedUser.getInsertDateTime());
        userRepository.save(updatedUser);
        return findByUserName(user.getUserName());
    }

    @Override
    public void delete(String username) {
       User user = userRepository.findByUserNameAndIsDeleted(username, false);
       if (checkIfUserCanBeDeleted(user)) {
           user.setIsDeleted(true);
           user.setUserName(user.getUserName() + "-" + user.getId());  // harold@manager.com-2
           userRepository.save(user);
       } else {
           throw new RejectedExecutionException("user cannot be deleted");
       }
    }

    @Override
    public List<UserDTO> listAllByRole(String description) {
        return userRepository.findByRole_DescriptionIgnoreCaseAndIsDeleted(description, false).stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public String getLoggedInUsername() {
        return securityService.getLoggedInUsername();
    }

    private boolean checkIfUserCanBeDeleted(User user) {
        final UserDTO userDTO = mapperUtil.convert(user, new UserDTO());

        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(userDTO);
                return projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(userDTO);
                return taskDTOList.size() == 0;
            default:
                return true;
        }

    }
}
