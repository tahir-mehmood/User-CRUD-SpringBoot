package com.tm.tdd.domain.repository;

import com.tm.tdd.domain.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
    boolean existsByEmail(String email);
}
