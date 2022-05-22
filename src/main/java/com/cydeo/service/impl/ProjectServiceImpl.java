package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.enums.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl extends AbstractMapService<ProjectDTO, String> implements ProjectService {

    @Override
    public ProjectDTO save(ProjectDTO object) {
        return super.save(object.getProjectCode(), object);
    }

    public ProjectDTO save(ProjectDTO object, Status status) {
        return super.save(object.getProjectCode(), object);
    }

    @Override
    public ProjectDTO findByID(String s) {
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
    public void update(ProjectDTO object) {
        if (object.getProjectStatus() == null) {
            ProjectDTO temp = findByID(object.getProjectCode());
            object.setProjectStatus(temp.getProjectStatus());
        }
        super.update(object.getProjectCode(), object);
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


}
