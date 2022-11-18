package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import java.util.List;


public interface UserService {

    List<UserDTO> listAllUsers();

    UserDTO findByUserName(String username);

    void save(UserDTO user);

    UserDTO update(UserDTO user);

    // deletes from database
//    void deleteByUserName(String username);

    // makes boolean true, if deleted
    void delete(String username);

    List<UserDTO> listAllByRole(String description);

    String getLoggedInUsername();

}
