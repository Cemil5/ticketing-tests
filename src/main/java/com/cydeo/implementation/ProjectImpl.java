package com.cydeo.implementation;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;

import java.util.List;

public class ProjectImpl extends AbstractMapService<ProjectDTO,String > implements ProjectService {

    @Override
    public ProjectDTO save(ProjectDTO object) {
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
        super.update(object.getProjectCode(), object);
    }

    @Override
    public void delete(ProjectDTO object) {
        super.delete(object);
    }
}
