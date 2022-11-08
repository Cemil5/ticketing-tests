package com.cydeo.mapper;

import com.cydeo.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@AllArgsConstructor
public class MapperUtil {
    // we implemented this, instead of RoleMapper, UserMapper, etc.

    private final ModelMapper modelMapper;

    // option 1
    public <T>  T convert(Object objectToConverted, T convertedObject) {
        return modelMapper.map(objectToConverted, (Type) convertedObject.getClass());
    }
// we can use the method above like this :
//    ProjectDTO dto = mapperUtil.convert(project, new ProjectDTO());



        // option 2
//    public <T>  T convert(Object objectToConverted, Class<T> convertedObject){
//        return modelMapper.map(objectToConverted, convertedObject);
//    }
// we can use the method above like this :
//    ProjectDTO dto = mapperUtil.convert(project, ProjectDTO.class);

        // option 3
//    public <T>  T convert(Object objectToConverted, T convertedObject){
//        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());

}
