package com.cydeo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskDTO {

    private String taskId;
    private ProjectDTO project;
    private UserDTO assignedEmployee;
    private String taskSubject;
    private String taskDetail;




}
