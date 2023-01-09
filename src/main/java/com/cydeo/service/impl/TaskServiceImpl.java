package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
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
                .sorted(Comparator.comparing(Task::getTaskStatus).reversed())
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow( ()-> new NoSuchElementException("Task not found"));
        return mapperUtil.convert(task, new TaskDTO());
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        final Task task = mapperUtil.convert(dto, new Task());
        task.setTaskStatus(Status.OPEN);
        task.setAssignedDate(LocalDate.now());
        return mapperUtil.convert(taskRepository.save(task), new TaskDTO());
    }

    @Override
    public TaskDTO update(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());
        Task convertedTask = mapperUtil.convert(dto, new Task());

        if (task.isPresent()) {
            convertedTask.setTaskStatus(dto.getTaskStatus() == null ? task.get().getTaskStatus() : dto.getTaskStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            return mapperUtil.convert(taskRepository.save(convertedTask), new TaskDTO());
        } else {
            throw new NoSuchElementException("task not found");
        }
    }

    @Override
    public void delete(Long id) {
        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isPresent()) {
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }
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
        String username = userService.getLoggedInUsername();
        return taskRepository.findByTaskStatusIsNotAndAssignedEmployee_UserName(status, username).stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> findAllTasksByStatusIs(Status status) {
        String username = userService.getLoggedInUsername();
        return taskRepository.findByTaskStatusIsAndAssignedEmployee_UserName(status, username).stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllNonCompletedByAssignedEmployee(UserDTO assignedEmployee) {
        List<Task> tasks = taskRepository
                .findByTaskStatusIsNotAndAssignedEmployee_UserName(Status.COMPLETE, userService.getLoggedInUsername());
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

    @Override
    public List<TaskDTO> getTasksByProjectCode(String projectCode) {
        return taskRepository.findByProject_ProjectCode(projectCode).stream()
                .map(task -> mapperUtil.convert(task, new TaskDTO()))
                .collect(Collectors.toList());
    }
}
