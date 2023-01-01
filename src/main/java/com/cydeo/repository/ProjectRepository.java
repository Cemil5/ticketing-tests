package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String code);

    List<Project> findByAssignedManager_UserName(String username);

    List<Project> findAllByProjectStatusIsNotAndAssignedManager(Status status, User assignedManager);

}
