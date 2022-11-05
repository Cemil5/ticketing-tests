package com.cydeo.service;

import com.cydeo.dto.ProjectDTO;
import org.springframework.stereotype.Service;


public interface ProjectService {

    ProjectDTO findById(String id);
}
