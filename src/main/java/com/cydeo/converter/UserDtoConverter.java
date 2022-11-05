package com.cydeo.converter;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
//@ConfigurationPropertiesBinding   OPTIONAL
public class UserDtoConverter implements Converter<String, UserDTO> {

    UserService userService;

    @Override
    public UserDTO convert(String source) {

        if (source == null || source.equals("")) {
            return null;
        }

        return userService.findByUserName(source);
    }

}
