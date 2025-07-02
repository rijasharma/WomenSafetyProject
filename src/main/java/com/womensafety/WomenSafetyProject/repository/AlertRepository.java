package com.womensafety.WomenSafetyProject.repository;


import com.womensafety.WomenSafetyProject.entity.Alert;
import com.womensafety.WomenSafetyProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUser(User user);

    List<Alert> findByStatus(String status);

    Long countByStatus(String status);

    void deleteAllByStatus(String status);


    List<Alert> findByUserId(Long userId);
}

