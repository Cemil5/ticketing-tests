package com.cydeo.controller;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor // autowires all beans
@Controller
@RequestMapping("/user")
public class UserController {

    private final RoleService roleService;
    private final UserService userService;

    @GetMapping({"/create", "/add", "/initialize"})
    public String  createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userService.findAll());

        return "/user/create";
    }


//    @PostMapping("/create")
//    public String insertUser(@Valid @ModelAttribute("user") UserDTO user, Model model){
//    // public String insertUser(UserDTO user, Model model){
//
//        userService.save(user);
//
////        model.addAttribute("user", new UserDTO());
////        model.addAttribute("roles", roleService.findAll());
////        model.addAttribute("users", userService.findAll());
////        return "user/create";
//
//        // instead of writing codes above, we use redirect keyword.
//        return "redirect:/user/create";
//    }

    // model should be last parameter
    @PostMapping("/create")
    public String insertUser(@Valid @ModelAttribute("user") UserDTO user, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("users", userService.findAll());
            model.addAttribute("roles", roleService.findAll());
            return "/user/create";
        }
        userService.save(user);
        return "redirect:/user/create";
    }

    @GetMapping("/update/{username}")
    public String editUser(@PathVariable String username, Model model){
        model.addAttribute("user", userService.findById(username));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "user/update";
    }

    @PostMapping("/update")
  //  public String updateUser(@Valid @ModelAttribute UserDTO user, Model model){
    public String updateUser(@Valid UserDTO user, Model model){

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
