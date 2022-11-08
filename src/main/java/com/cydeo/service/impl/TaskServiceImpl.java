package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final MapperUtil mapperUtil;

    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            return mapperUtil.convert(task, new TaskDTO());
        }
        return null;
    }

    //@Transactional
    @Override
    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        final Task task = mapperUtil.convert(dto, new Task());
        taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());
        Task convertedTask = mapperUtil.convert(dto, new Task());

        if (task.isPresent()) {
            convertedTask.setTaskStatus(dto.getTaskStatus() == null ? task.get().getTaskStatus() : dto.getTaskStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }

        // second option
//        Task savedTask = taskRepository.findById(dto.getId()).orElseThrow();
//        Task converted = mapperUtil.convert(dto, new Task());
//        converted.setAssignedDate(savedTask.getAssignedDate());
//        converted.setTaskStatus(savedTask.getTaskStatus());
//        taskRepository.save(converted);
    }

    @Override
    public void delete(Long id) {
        // this option is better than below for deletion operation
        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isPresent()) {
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }
//        Task task = taskRepository.findById(id).orElseThrow();
//        task.setIsDeleted(true);
//        taskRepository.save(task);
    }

    @Override
    public int totalNonCompletedTask(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTask(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public List<TaskDTO> findAllTasksByStatusIsNot(Status status) {
        UserDTO loggedInUser = userService.findByUserName("john@employee.com");
        User user = mapperUtil.convert(loggedInUser, new User());

        return taskRepository.findByTaskStatusIsNotAndAssignedEmployee(status, user).stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> findAllTasksByStatusIs(Status status) {
        UserDTO loggedInUser = userService.findByUserName("john@employee.com");
        User user = mapperUtil.convert(loggedInUser, new User());
        return taskRepository.findByTaskStatusIsAndAssignedEmployee(status, user).stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllNonCompletedByAssignedEmployee(UserDTO assignedEmployee) {
        List<Task> tasks = taskRepository
                .findByTaskStatusIsNotAndAssignedEmployee(Status.COMPLETE, mapperUtil.convert(assignedEmployee, new User()));
        return tasks.stream().map(task -> mapperUtil.convert(task, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public void completeByProject(String projectCode) {
        List<Task> tasks = taskRepository.findByProject_ProjectCode(projectCode);
        tasks.stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .forEach(taskDTO -> {
                    taskDTO.setTaskStatus(Status.COMPLETE);
                    update(taskDTO);
                });
    }
}