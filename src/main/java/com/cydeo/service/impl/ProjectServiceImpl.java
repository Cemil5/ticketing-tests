package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.entity.Project;
import com.cydeo.enums.Status;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MapperUtil mapperUtil;

    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectRepository.findAll().stream()
                .map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {
        return mapperUtil.convert(projectRepository.findByProjectCode(code), new ProjectDTO());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        projectRepository.save(mapperUtil.convert(dto, new Project()));
    }

    @Override
    public void update(ProjectDTO dto) {
        Project savedProject = projectRepository.findByProjectCode(dto.getProjectCode());
        Project updatedProject = mapperUtil.convert(dto, new Project());
        updatedProject.setId(savedProject.getId());
        updatedProject.setProjectStatus(savedProject.getProjectStatus());
        projectRepository.save(updatedProject);
    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);
        projectRepository.save(project);
    }

    @Override
    public void complete(String  code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }
}
