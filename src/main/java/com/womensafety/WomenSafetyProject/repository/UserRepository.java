package com.womensafety.WomenSafetyProject.repository;



import com.womensafety.WomenSafetyProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

