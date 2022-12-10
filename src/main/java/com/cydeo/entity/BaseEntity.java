package com.cydeo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Data // z2h since it creates problem, I commented out this and put getter / setter
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isDeleted = false;

    //  @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime insertDateTime;
    @Column(nullable = false, updatable = false)
    private Long insertUserId;
    //  @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdateDateTime;
    @Column(nullable = false)
    private Long lastUpdateUserId;

//    @PrePersist
//    private void onPrePersist() {
//        this.insertDateTime = LocalDateTime.now();
//        this.lastUpdateDateTime = LocalDateTime.now();
//        this.insertUserId = 1L;
//        this.lastUpdateUserId = 1L;
//    }
//
//    @PreUpdate
//    private void onPreUpdate() {
//        this.lastUpdateDateTime = LocalDateTime.now();
//        this.lastUpdateUserId = 1L;
//    }

}
