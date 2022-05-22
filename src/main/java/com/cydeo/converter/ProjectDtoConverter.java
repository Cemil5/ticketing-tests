package com.cydeo.converter;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationPropertiesBinding   OPTIONAL
public class ProjectDtoConverter implements Converter<String, ProjectDTO> {

    @Autowired
    ProjectService projectService;

    @Override
    public ProjectDTO convert(String source) {
        return projectService.findByID(source);
    }
}
