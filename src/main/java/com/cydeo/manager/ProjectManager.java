package com.cydeo.manager;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectManager {

    @Autowired
    ProjectService projectService;

    @Autowired
    TaskService taskService;

    public List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager){
        return projectService.findAll()
                .stream().filter(project -> project.getAssignedManager().equals(manager)).
                map(project -> {
                    List<TaskDTO> taskList = taskService.findTaskByManager(manager);
                    int completeCount = (int) taskList.stream().
                            filter(t->t.getProject().equals(project) && t.getTaskStatus() == Status.COMPLETE).count();
                    int inCompleteCount = (int) taskList.stream().
                            filter(t->t.getProject().equals(project) && t.getTaskStatus() != Status.COMPLETE).count();
                    project.setCompleteTaskCounts(completeCount);
                    project.setInCompleteTaskCounts(inCompleteCount);
                    return project;
                }).collect(Collectors.toList());
    }


}
