package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@AllArgsConstructor // autowires all beans.
@RequestMapping("/project")
public class ProjectController {

    ProjectService projectService;
    UserService userService;
    TaskService taskService;

    @GetMapping("/create")
    public String createProject(Model model){
        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());
        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(@Valid @ModelAttribute ("project") ProjectDTO projectDTO, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", userService.findManagers());
            model.addAttribute("projects", projectService.findAll());
            return "/project/create";
        }

       // projectDTO.setProjectStatus(Status.OPEN);
        projectService.save(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectCode}")
    public String deleteProject(@PathVariable String projectCode){
        projectService.deleteByID(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectCode}")
    public String editProject(@PathVariable String projectCode, Model model){
        model.addAttribute("project", projectService.findById(projectCode));
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());
        return "/project/update";
    }

    @PostMapping("/update")
    public String updateProject(@Valid @ModelAttribute("project") ProjectDTO project, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", userService.findManagers());
            model.addAttribute("projects", projectService.findAll());
            return "/project/update";
        }
        projectService.update(project);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectCode}")
    public String completeProject(@PathVariable String projectCode){
        projectService.complete(projectService.findById(projectCode));
        return "redirect:/project/create";
    }

    @GetMapping("/manager")
    public String getProjectByManager(Model model){
        UserDTO manager = userService.findById("john@cybertek.com");
        model.addAttribute("projects",  projectService.getCountedListOfProjectDTO(manager));
        return "/manager/project-status";
    }

}
