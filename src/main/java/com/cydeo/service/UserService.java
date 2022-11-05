package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import javax.transaction.Transactional;
import java.util.List;


public interface UserService {

    List<UserDTO> listAllUsers();

    UserDTO findByUserName(String username);

    void save(UserDTO user);

    UserDTO update(UserDTO user);

    @Transactional
    void deleteByUserName(String username);
}
