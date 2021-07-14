package com.example.probation.repository;

import com.example.probation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    User getById(Long id);

    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT 2", nativeQuery = true)
    List<User> getRandomUsers();
}
