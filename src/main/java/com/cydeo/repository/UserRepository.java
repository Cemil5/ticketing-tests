package com.cydeo.repository;

import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // since we commented @where deleted is false condition from user entity :
    List<User> findAllByIsDeleted(Boolean isDeleted);

    User findByUserNameAndIsDeleted(String username, Boolean isDeleted);

//    @Transactional  // we can also put it inside service
//    void deleteByUserName(String username);

    List<User> findByRole_DescriptionIgnoreCaseAndIsDeleted(String roleDescription, Boolean isDeleted);



}
