package com.cydeo.entity;

import com.cydeo.entity.common.BaseEntity;
import com.cydeo.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "projects")
@NoArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Project extends BaseEntity {

    // Whenever the unique constraint is based only on one field, we can use @Column(unique=true) on that column.
    // if this field is not unique, it will throw error that should be handled.
    @Column(unique = true)
    private String projectName;

    private String projectCode;

    //@Column(columnDefinition = "DATE")
    private LocalDate startDate;
    //@Column(columnDefinition = "DATE")
    private LocalDate endDate;

    private String projectDetail;

    @Enumerated(EnumType.STRING)
    private Status projectStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User assignedManager;
}
