package com.cydeo.service.impl;

import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends AbstractMapService<TaskDTO, Long> implements TaskService {
    @Override
    public TaskDTO save(TaskDTO object) {
        object.setTaskId(UUID.randomUUID().getLeastSignificantBits());
      //  System.out.println("task Id" + object.getTaskId());
        return super.save(object.getTaskId(), object);
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
    public void delete(TaskDTO object) {
        super.delete(object);
    }

    @Override
    public void deleteByID(Long aLong) {
        super.deleteById(aLong);
    }

    @Override
    public void update(TaskDTO object) {
        TaskDTO temp = findByID(object.getTaskId());
        object.setTaskStatus(temp.getTaskStatus());
        object.setAssignedDate(temp.getAssignedDate());
        System.out.println("task Id" + object.getTaskId());
        super.update(object.getTaskId(), object);
    }

    @Override
    public List<TaskDTO> findTaskByManager(UserDTO manager) {
        return super.findAll().stream().filter(task -> task.getProject().getAssignedManager().equals(manager)).collect(Collectors.toList());
    }
}
