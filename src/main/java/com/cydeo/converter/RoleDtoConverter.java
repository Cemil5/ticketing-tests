package com.cydeo.converter;

import com.cydeo.dto.RoleDTO;
import com.cydeo.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
//@AllArgsConstructor
//@ConfigurationPropertiesBinding   OPTIONAL
public class RoleDtoConverter implements Converter<String, RoleDTO> {

    RoleService roleService;

    // If you encounter looping exception, put @Lazy annotation inside the constructor
    public RoleDtoConverter(@Lazy RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public RoleDTO convert(String source) {

        // for creating error if user selects "Select"
        if (source == null || source.isBlank())
            return null;

        Long id = Long.parseLong(source);
        return roleService.findById(id);
    }

}
