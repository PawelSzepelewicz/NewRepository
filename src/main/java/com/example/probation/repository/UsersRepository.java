package com.example.probation.repository;

import com.example.probation.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT 2", nativeQuery = true)
    List<User> getRandomUsers();

    List<User> findAllByOrderByRatingDesc();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
