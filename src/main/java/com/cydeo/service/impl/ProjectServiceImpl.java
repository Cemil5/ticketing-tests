package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Status;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProjectServiceImpl extends AbstractMapService<ProjectDTO, String> implements ProjectService {

    private final TaskService taskService;

    @Override
    public ProjectDTO save(ProjectDTO project) {

        if (project.getProjectStatus() == null)
            project.setProjectStatus(Status.OPEN);

        return super.save(project.getProjectCode(), project);
    }

    public ProjectDTO save(ProjectDTO object, Status status) {
        return super.save(object.getProjectCode(), object);
    }

    @Override
    public ProjectDTO findById(String s) {
        return super.findById(s);
    }

    @Override
    public List<ProjectDTO> findAll() {
        return super.findAll();
    }

    @Override
    public void deleteByID(String s) {
        super.deleteById(s);
    }

    @Override
    public void update(ProjectDTO project) {
        if (project.getProjectStatus() == null) {
            ProjectDTO foundProject = this.findById(project.getProjectCode());
            project.setProjectStatus(foundProject.getProjectStatus());
        }
        super.update(project.getProjectCode(), project);
    }

    @Override
    public void delete(ProjectDTO object) {
        super.delete(object);
    }

    @Override
    public void complete(ProjectDTO project) {
        project.setProjectStatus(Status.COMPLETE);
        save(project);
    }

//    public List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager) {
//        return findAll()
//                .stream()
//                .filter(project -> project.getAssignedManager().equals(manager))
//                .map(project -> {
//                    List<TaskDTO> taskList = taskService.findTaskByManager(manager);
//                    int completeCount = (int) taskList.stream().
//                            filter(t -> t.getProject().equals(project) && t.getTaskStatus() == Status.COMPLETE).count();
//                    int inCompleteCount = (int) taskList.stream().
//                            filter(t->t.getProject().equals(project) && t.getTaskStatus() != Status.COMPLETE).count();
//
//                    project.setCompleteTaskCounts(completeCount);
//                    project.setInCompleteTaskCounts(inCompleteCount);
//                    return project;
//                }).collect(Collectors.toList());
//    }

    public List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager) {
        return findAll()
                .stream()
                .filter(project -> project.getAssignedManager().equals(manager))
                // I changed to peek from map with the recommendation of Intellij
                .peek(project -> {
                    List<TaskDTO> taskList = taskService.findTaskByManager(manager);
                    int completeTaskCount = (int) taskList.stream().
                            filter(t -> t.getProject().equals(project) && t.getTaskStatus() == Status.COMPLETE)
                            .count();
                    int inCompleteTaskCount = (int) taskList.stream().
                            filter(t -> t.getProject().equals(project) && t.getTaskStatus() != Status.COMPLETE)
                            .count();

                    project.setCompleteTaskCounts(completeTaskCount);
                    project.setInCompleteTaskCounts(inCompleteTaskCount);
                }).collect(Collectors.toList());
    }


}
