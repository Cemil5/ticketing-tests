package com.cydeo.implementation;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends AbstractMapService<UserDTO, String> implements UserService {
    @Override
    public UserDTO save(UserDTO object) {
        return super.save(object.getUserName(), object);
    }

    @Override
    public UserDTO findByID(String s) {
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
}
