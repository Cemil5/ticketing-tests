package com.cydeo.entity;

import com.cydeo.enums.Gender;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
// if we delete an employee which has completed tasks, then try to list a project which has this employee task,
// spring can not find this employee since we deleted and
// throws javax.persistence.EntityNotFoundException: Unable to find com.cydeo.entity.User with id 4
// we can handle this exception by commenting out @Where annotation

// @Where(clause = "is_deleted=false") // adds this clause to all queries related to user entity
        // SELECT * FROM users WHERE id = 4 AND is_deleted = false;
@Entity
public class User extends BaseEntity {

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String userName;

    private String passWord;

    private boolean enabled;
    private String phone;

    @ManyToOne
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
