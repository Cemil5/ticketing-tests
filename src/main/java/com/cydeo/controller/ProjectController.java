package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.ProjectService;
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

    private final ProjectService projectService;
    private final UserService userService;
   // TaskService taskService;

    // manager should see only his/her projects, so we need to limit projects to be shown
    @GetMapping("/create")
    public String createProject(Model model){
        model.addAttribute("project", new ProjectDTO());
//        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("projects", projectService.listAllProjectDetails());
        model.addAttribute("managers", userService.listAllByRole("manager"));
        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(@Valid @ModelAttribute ("project") ProjectDTO projectDTO, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", userService.listAllByRole("manager"));
            model.addAttribute("projects", projectService.listAllProjectDetails());
            return "/project/create";
        }

       // projectDTO.setProjectStatus(Status.OPEN);
        projectService.save(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectCode}")
    public String deleteProject(@PathVariable String projectCode){
        projectService.delete(projectCode);
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectCode}")
    public String editProject(@PathVariable String projectCode, Model model){
        model.addAttribute("project", projectService.getByProjectCode(projectCode));
        model.addAttribute("projects", projectService.listAllProjectDetails());
        model.addAttribute("managers", userService.listAllByRole("manager"));
        return "/project/update";
    }

    @PostMapping("/update")
    public String updateProject(@Valid @ModelAttribute("project") ProjectDTO project, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", userService.listAllByRole("manager"));
            model.addAttribute("projects", projectService.listAllProjectDetails());
            return "/project/update";
        }
        projectService.update(project);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectCode}")
    public String completeProject(@PathVariable String projectCode){
        projectService.complete(projectCode);
        return "redirect:/project/create";
    }

    // manager should see only his/her project details
    @GetMapping("/manager")
    public String getProjectByManager(Model model){
        model.addAttribute("projects",  projectService.listAllProjectDetails());
        return "/manager/project-status";
    }

}
