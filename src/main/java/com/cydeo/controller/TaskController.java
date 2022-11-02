package com.cydeo.controller;

import com.cydeo.dto.TaskDTO;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import com.cydeo.enums.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/task")
@AllArgsConstructor
public class TaskController {

    private final ProjectService projectService;
    private final UserService userService;
    private final TaskService taskService;

    @GetMapping("/create")
    public String createTask(Model model){
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("employees", userService.findEmployees());
        model.addAttribute("tasks", taskService.findAll());
        return "/task/create";
    }

    @PostMapping("/create")
    public String insertTask(@Valid @ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("projects", projectService.findAll());
            model.addAttribute("employees", userService.findEmployees());
            model.addAttribute("tasks", taskService.findAll());
            return "/task/create";
        }
        taskService.save(task);
        return "redirect:/task/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id){
        taskService.deleteByID(id);
        return "redirect:/task/create";
    }

    @GetMapping("/update/{id}")
    public String editTask(@PathVariable Long id, Model model){
        model.addAttribute("task", taskService.findById(id));
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("employees", userService.findEmployees());
        model.addAttribute("tasks", taskService.findAll());
        return "/task/update";
    }

//    @PostMapping("update/{id}")
//    public String updateTask(@PathVariable Long id, TaskDTO task){
//        task.setId(id);
//        taskService.update(task);
//        return "redirect:/task/create";
//    }

    // Spring finds and match id implicitly if variable names equal and post mapping
    @PostMapping("/update/{id}")
    public String updateTask(@Valid @ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("projects", projectService.findAll());
            model.addAttribute("employees", userService.findEmployees());
            model.addAttribute("tasks", taskService.findAll());
            return "/task/update";
        }

        taskService.update(task);
        return "redirect:/task/create";
    }

    @GetMapping("/employee/pending-tasks")
    public String employeePendingTasks(Model model){
        model.addAttribute("tasks", taskService.findAllTasksByStatusIsNot(Status.COMPLETE));
        return "/task/pending-tasks";
    }

    @GetMapping("/employee/archive")
    public String employeeArchive(Model model){
        model.addAttribute("tasks", taskService.findAllTasksByStatusIs(Status.COMPLETE));
        return "/task/archive";
    }

    @GetMapping("/employee/edit/{id}")
    public String employeeEditTask(@PathVariable Long id, Model model){
        model.addAttribute("task", taskService.findById(id));
        //we don't want employee to change project and employee part
//        model.addAttribute("projects", projectService.findAll());
//        model.addAttribute("employees", userService.findEmployees());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("tasks", taskService.findAllTasksByStatusIsNot(Status.COMPLETE));
        return "/task/status-update";
    }

    @PostMapping("/employee/update/{id}")
    public String employeeUpdateTask(@Valid  @ModelAttribute("task") TaskDTO task, BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", Status.values());
            model.addAttribute("tasks", taskService.findAllTasksByStatusIsNot(Status.COMPLETE));
            return "/task/status-update";
        }

        taskService.updateStatus(task);
        return "redirect:/task/employee/pending-tasks";
    }

}
