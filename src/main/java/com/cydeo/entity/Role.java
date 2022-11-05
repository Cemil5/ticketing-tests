package com.cydeo.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Data // z2h since it creates problem, I commented out this and put getter / setter
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    private String description;
}
