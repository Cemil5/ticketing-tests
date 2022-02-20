package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.UserService;
import com.cydeo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @GetMapping("/create")
    public String createProject(Model model){
        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());
        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(ProjectDTO projectDTO){
        projectDTO.setProjectStatus(Status.OPEN);
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
        model.addAttribute("project", projectService.findByID(projectCode));
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("managers", userService.findManagers());
        return "/project/update";
    }

    @PostMapping("/update/{projectCode}")
    public String updateProject(@PathVariable String projectCode, ProjectDTO projectDTO){
        projectDTO.setProjectStatus(projectService.findByID(projectCode).getProjectStatus());
        projectService.update(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectCode}")
    public String completeProject(@PathVariable String projectCode){
        projectService.complete(projectService.findByID(projectCode));
        return "redirect:/project/create";
    }

}
