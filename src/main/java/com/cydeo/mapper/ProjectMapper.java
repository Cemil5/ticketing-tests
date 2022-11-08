package com.cydeo.mapper;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

// we use mapperUtil instead of this mapper
@Component
public class ProjectMapper {

    private final ModelMapper modelMapper;


    public ProjectMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Project convertToEntity(UserDTO dto) {
        return modelMapper.map(dto, Project.class);
    }

    public ProjectDTO convertToDto(User entity) {
        return modelMapper.map(entity, ProjectDTO.class);
    }
}
