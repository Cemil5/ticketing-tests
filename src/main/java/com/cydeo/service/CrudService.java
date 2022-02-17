package com.cydeo.service;

import java.util.List;

public interface CrudService<T, ID> {

    //save
    //find user by username
    //give me all user list
    //delete

    T save(T object);
    T findByID(ID id);
    List<T> findAll();
    void delete(T object);
    void deleteByID(ID id);
    void update(T object);

}
