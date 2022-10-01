package com.cydeo.converter;

import com.cydeo.dto.RoleDTO;
import com.cydeo.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
//@ConfigurationPropertiesBinding   OPTIONAL
public class RoleDtoConverter implements Converter<String, RoleDTO> {

    RoleService roleService;

    @Override
    public RoleDTO convert(String source) {
        Long id = Long.parseLong(source);
        return roleService.findByID(id);
    }

}
