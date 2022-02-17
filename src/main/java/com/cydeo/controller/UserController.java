package com.cydeo.controller;

import com.cydeo.bootstrap.DataGenerator;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.implementation.RoleServiceImpl;
import com.cydeo.implementation.UserServiceImpl;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    RoleService roleService;
    @Autowired
    UserService userService;

    @GetMapping({"/create", "/add", "/initialize"})
    public String  createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userService.findAll());

        for (UserDTO user : userService.findAll()){
            System.out.println(user.getUserName());
        }

//        List<String> roleList = new ArrayList<>();
//        for (RoleDTO role : roleService.findAll()){
//            roleList.add(role.getDescription());
//        }
//        model.addAttribute("roleList", roleList);
//        System.out.println(roleList);
//        System.out.println(roleService.findByID(1L).getDescription());

        return "user/create";
    }


    @PostMapping("/create")
    public String insertUser(UserDTO user, Model model){

        userService.save(user);

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAll());

        model.addAttribute("users", userService.findAll());

        for (UserDTO user1 : userService.findAll()){
            System.out.println(user1.getUserName());
        }

        return "user/create";
    }

    @GetMapping("/update/{username}")
    public String editUser(@PathVariable String username, Model model){
        model.addAttribute("user", userService.findByID(username));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "user/update";
    }

    @PostMapping("/update/{username}")
    public String updateUser(@PathVariable String username, UserDTO user, Model model){

        userService.update(user);

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userService.findAll());
        return "user/create";
    }


}
