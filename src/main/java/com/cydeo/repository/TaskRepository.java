package com.cydeo.repository;

import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject_ProjectCode(String projectCode);

    @Query("select count(t) from Task t where t.project.projectCode =?1 and t.taskStatus <> 'COMPLETE'")
    int totalNonCompletedTasks(String projectCode);

    int findByProject_ProjectCodeAndTaskStatusNotContaining(String projectCode, Status status);
    int findByProject_ProjectCodeAndTaskStatusContaining(String projectCode, Status status);

//    @Query("select count(t) from Task t where t.project.projectCode =?1 and t.taskStatus = 'COMPLETE'")
    @Query(value = "SELECT COUNT(*) " +
            "FROM tasks t JOIN projects p on t.project_id=p.id " +
            "WHERE p.project_code=?1 AND t.task_status='COMPLETE'",nativeQuery = true)
    int totalCompletedTasks(String projectCode);


    List<Task> findByTaskStatusIsNotAndAssignedEmployee_UserName(Status status, String username);

    List<Task> findByTaskStatusIsAndAssignedEmployee_UserName(Status status, String username);
}
