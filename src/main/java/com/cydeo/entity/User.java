package com.cydeo.entity;

import com.cydeo.enums.Gender;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
//@Data // z2h since it creates problem, I commented out this and put getter / setter
@Entity
@Table(name = "users")
// if we delete an employee which has completed tasks, then try to list a project which has this employee task,
// spring can not find this employee since we deleted and
// throws javax.persistence.EntityNotFoundException: Unable to find com.cydeo.entity.User with id 4
// we can handle this exception by commenting out @Where annotation

// @Where(clause = "is_deleted=false") // adds this clause to all queries related to user entity
        // SELECT * FROM users WHERE id = 4 AND is_deleted = false;

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

//    public User(Long id, LocalDateTime insertDateTime, Long insertUserId, LocalDateTime lastUpdateDateTime, Long lastUpdateUserId, String firstName,
//                String lastName, String userName, String passWord, boolean enabled, String phone, Role role, Gender gender) {
//        super(id, insertDateTime, insertUserId, lastUpdateDateTime, lastUpdateUserId);
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.userName = userName;
//        this.passWord = passWord;
//        this.enabled = enabled;
//        this.phone = phone;
//        this.role = role;
//        this.gender = gender;
//    }
}
