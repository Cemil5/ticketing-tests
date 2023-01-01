package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.SecurityService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final TaskService taskService;
    private final SecurityService securityService;

    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code)
                .orElseThrow(()-> new NoSuchElementException("project not found"));
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        Project saved = projectRepository.save(mapperUtil.convert(dto, new Project()));
        return mapperUtil.convert(saved, new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) {
        Project savedProject = projectRepository.findByProjectCode(dto.getProjectCode()).orElseThrow();
        Project updatedProject = mapperUtil.convert(dto, new Project());
        updatedProject.setId(savedProject.getId());
        updatedProject.setProjectStatus(savedProject.getProjectStatus());
        Project saved = projectRepository.save(updatedProject);
        return mapperUtil.convert(saved, new ProjectDTO());
    }

    @Override
    @Transactional
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code).orElseThrow();
        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() + "-" + project.getId()); // SP03-4
        projectRepository.save(project);
        taskService.getTasksByProjectCode(code)
                .forEach(dto -> taskService.delete(dto.getId()));
    }

    @Override
    public void complete(String  code) {
        Project project = projectRepository.findByProjectCode(code).orElseThrow();
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);

        // if a project status is changed to "completed", tasks of this project should be also "completed"
        taskService.completeByProject(code);
    }

    // to list all projects belongs to logged in manager
    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        UserDTO currentUserDto = userService.findByUserName(securityService.getLoggedInUsername());
        final List<Project> projectList = projectRepository.findByAssignedManager_UserName(currentUserDto.getUserName());

        return projectList.stream()
                .map(project -> {
                    ProjectDTO dto = mapperUtil.convert(project, new ProjectDTO());
                    int completeTasks = taskService.totalCompletedTask(dto.getProjectCode());
                    int inCompleteTasks = taskService.totalNonCompletedTask(dto.getProjectCode());
                    dto.setCompleteTaskCounts(completeTasks);
                    dto.setInCompleteTaskCounts(inCompleteTasks);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager) {
        return projectRepository.findAllByProjectStatusIsNotAndAssignedManager(Status.COMPLETE,
                mapperUtil.convert(assignedManager, new User()))
                .stream().map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());
    }
}
