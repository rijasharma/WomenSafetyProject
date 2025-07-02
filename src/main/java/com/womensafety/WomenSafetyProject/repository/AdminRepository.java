package com.womensafety.WomenSafetyProject.repository;

import com.womensafety.WomenSafetyProject.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByEmail(String email);
}

