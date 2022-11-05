package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
//    private UserMapper userMapper;
    private MapperUtil mapperUtil;

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAll(Sort.by("firstName"));
        return userList.stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return mapperUtil.convert(userRepository.findByUserName(username), new UserDTO());
    }

    @Override
    public void save(UserDTO user) {
        userRepository.save(mapperUtil.convert(user, new User()));
    }

    @Override
    public UserDTO update(UserDTO user) {
        User updatedUser = mapperUtil.convert(user, new User());
        User savedUser = userRepository.findByUserName(user.getUserName());
        updatedUser.setId(savedUser.getId());
        updatedUser.setInsertDateTime(savedUser.getInsertDateTime());
        userRepository.save(updatedUser);
        return findByUserName(user.getUserName());
    }

    // without @Transactional : No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call
    @Override
    //@Transactional : we put this annotation inside UserService
    public void deleteByUserName(String username) {
        userRepository.deleteByUserName(username);
    }
}
