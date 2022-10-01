package com.cydeo.controller;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor // autowires all beans
@Controller
@RequestMapping("/user")
public class UserController {

    RoleService roleService;
    UserService userService;

    @GetMapping({"/create", "/add", "/initialize"})
    public String  createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userService.findAll());

        return "user/create";
    }


    @PostMapping("/create")
    public String insertUser(@ModelAttribute("user") UserDTO user, Model model){
    // public String insertUser(UserDTO user, Model model){

        userService.save(user);

//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/update/{username}")
    public String editUser(@PathVariable String username, Model model){
        model.addAttribute("user", userService.findByID(username));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "user/update";
    }

    @PostMapping("/update")
    public String updateUser(UserDTO user, Model model){

        userService.update(user);
//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username){
        userService.deleteByID(username);
        return "redirect:/user/create";
    }
}
