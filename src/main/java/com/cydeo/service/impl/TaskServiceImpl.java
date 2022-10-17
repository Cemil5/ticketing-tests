package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.TaskService;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends AbstractMapService<TaskDTO, Long> implements TaskService {
    @Override
    public TaskDTO save(TaskDTO task) {

        if (task.getTaskStatus() == null)
            task.setTaskStatus(Status.OPEN);

        if (task.getAssignedDate() == null)
            task.setAssignedDate(LocalDate.now());

        if (task.getId() == null)
            task.setId(UUID.randomUUID().getMostSignificantBits());

        return super.save(task.getId(), task);
    }

    @Override
    public TaskDTO findByID(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public List<TaskDTO> findAll() {
        return super.findAll();
    }

    @Override
    public void delete(TaskDTO task) {
        super.delete(task);
    }

    @Override
    public void deleteByID(Long id) {
        super.deleteById(id);
    }

    @Override
    public void update(TaskDTO task) {
        TaskDTO foundTask = findByID(task.getId());
        task.setTaskStatus(foundTask.getTaskStatus());
        task.setAssignedDate(foundTask.getAssignedDate());
        //   System.out.println("task Id" + task.getId());
        super.update(task.getId(), task);
    }

    @Override
    public List<TaskDTO> findTaskByManager(UserDTO manager) {
        return super.findAll().stream().filter(task -> task.getProject().getAssignedManager().equals(manager)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> findAllTasksByStatusIsNot(Status status) {
        return findAll().stream()
                .filter(task -> !task.getTaskStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> findAllTasksByStatusIs(Status status) {
        return findAll().stream()
                .filter(task -> task.getTaskStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO task) {
        TaskDTO founded = findByID(task.getId());
        founded.setTaskStatus(task.getTaskStatus());
        super.update(task.getId(), founded);
    }
}
