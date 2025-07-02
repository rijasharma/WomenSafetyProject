package com.womensafety.WomenSafetyProject.service;

import com.womensafety.WomenSafetyProject.entity.Alert;
import com.womensafety.WomenSafetyProject.entity.User;
import com.womensafety.WomenSafetyProject.repository.AlertRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MailService mailService;

    // Send a new alert
    public Alert sendAlert(User user, double latitude, double longitude) {
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setLatitude(latitude);
        alert.setLongitude(longitude);
        alert.setTimestamp(LocalDateTime.now());
        alert.setStatus("Pending");

        // Save to DB
        Alert savedAlert = alertRepository.save(alert);

        //  Send SMS to Admin
        String message = " Emergency Alert!\n"
                + "Name: " + user.getName() + "\n"
                + "Phone: " + user.getPhone() + "\n"
                + "Location: https://www.google.com/maps?q=" + latitude + "," + longitude;

        // ⚠️ Replace with actual verified admin number
        notificationService.sendSMS("+918340712218", message);

        // Send email to admin or contact
        String subject = "🚨 Emergency Alert from " + user.getName();
        String body = "Location: https://www.google.com/maps?q=" + latitude + "," + longitude;
        mailService.sendAlertEmail("rijakumari42@gmail.com", subject, body);

        return savedAlert;
    }

    public List<Alert> getAlertsByStatus(String status) {
        return alertRepository.findByStatus(status);
    }

    // Get alerts sent by a specific user
    public List<Alert> getAlertsByUser(User user) {
        return alertRepository.findByUser(user);
    }

    // Admin: Get all alerts
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // Admin: Mark alert as resolved
//    public Alert resolveAlert(Long alertId, String adminNameOrEmail) {
//        Alert alert = alertRepository.findById(alertId).orElse(null);
//        if (alert != null && !alert.getStatus().equals("Resolved")) {
//            alert.setStatus("Resolved");
//            alert.setResolvedBy(adminNameOrEmail);
//            return alertRepository.save(alert);
//        }
//        return null;
//    }

    public Alert resolveAlert(Long alertId, String adminNameOrEmail, HttpSession session) {
        Alert alert = alertRepository.findById(alertId).orElse(null);
        if (alert != null && !"Resolved".equalsIgnoreCase(alert.getStatus())) {
            alert.setStatus("Resolved");
            alert.setResolvedBy(adminNameOrEmail);
            alertRepository.save(alert);

            // Add a session-based notification for the user
            User user = alert.getUser();
            if (user != null) {
                // Set notification message in user's session using their userId as key
                String notificationKey = "notification_user_" + user.getId();
                session.getServletContext().setAttribute(notificationKey,
                        "Your alert (ID: " + alertId + ") has been resolved by Admin: " + adminNameOrEmail);
            }

            return alert;
        }
        return null;
    }



    public Long countByStatus(String status) {
        return alertRepository.countByStatus(status);
    }

    @Transactional
    public void deleteResolvedAlerts() {
        alertRepository.deleteAllByStatus("Resolved");
    }

    public List<Alert> getAlertsByUserId(Long userId) {
        return alertRepository.findByUserId(userId);
    }


}
