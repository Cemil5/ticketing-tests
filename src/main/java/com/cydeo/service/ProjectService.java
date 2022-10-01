package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Status;

import java.util.List;

public interface ProjectService extends CrudService<ProjectDTO, String> {

    void complete(ProjectDTO project);

    ProjectDTO save(ProjectDTO object, Status status);

    List<ProjectDTO> getCountedListOfProjectDTO(UserDTO manager);
}
