package com.cydeo.converter;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
//@ConfigurationPropertiesBinding   OPTIONAL
public class ProjectDtoConverter implements Converter<String, ProjectDTO> {

    ProjectService projectService;

    @Override
    public ProjectDTO convert(String source) {

        if (source == null || source.equals("")) {
            return null;
        }

        return projectService.findById(source);

    }
}
