package com.cydeo.implementation;

import com.cydeo.dto.TaskDTO;
import com.cydeo.service.TaskInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskImpl extends AbstractMapService<TaskDTO, String > implements TaskInterface {
    @Override
    public TaskDTO save(TaskDTO object) {
        return super.save(object.getTaskId(), object);
    }

    @Override
    public TaskDTO findByID(String s) {
        return super.findById(s);
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
    public void deleteByID(String s) {
        super.deleteById(s);
    }

    @Override
    public void update(TaskDTO object) {
        super.update(object.getTaskId(), object);
    }
}
