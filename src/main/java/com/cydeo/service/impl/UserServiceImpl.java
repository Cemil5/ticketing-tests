package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends AbstractMapService<UserDTO, String> implements UserService {
    @Override
    public UserDTO save(UserDTO object) {
        return super.save(object.getUserName(), object);
    }

    @Override
    public UserDTO findById(String s) {
        return super.findById(s);
    }

    @Override
    public List<UserDTO> findAll() {
        return super.findAll();
    }

    @Override
    public void deleteByID(String s) {
        super.deleteById(s);
    }

    @Override
    public void update(UserDTO object) {
        super.update(object.getUserName(), object);
    }

    @Override
    public void delete(UserDTO object) {
        super.delete(object);
    }

    @Override
    public List<UserDTO> findManagers() {
        List<UserDTO> list = new ArrayList<>();
        /*
        for ( UserDTO user : super.findAll()){
            if (user.getRole().getId() == 2L)
                list.add(user);
        }
        return list;
        */
        return super.findAll().stream().filter(user -> user.getRole().getId() == 2).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findEmployees() {
        return super.findAll().stream().filter(user -> user.getRole().getId() == 3).collect(Collectors.toList());
    }
}
