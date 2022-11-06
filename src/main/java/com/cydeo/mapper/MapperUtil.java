package com.cydeo.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MapperUtil {
    // we implemented this, instead of RoleMapper, UserMapper

    private final ModelMapper modelMapper;

//    public <T> T convertToEntity(Object objectToConverted, T convertedObject) {
//        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
//    }
//
//    public <T> T convertToDTO(Object objectToConverted, T convertedObject) {
//        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
//    }

    public <T>  T convert(Object objectToConverted, T convertedObject){
        return (T) modelMapper.map(objectToConverted, convertedObject.getClass());
    }
}
