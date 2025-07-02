package com.womensafety.WomenSafetyProject.controller;


import com.womensafety.WomenSafetyProject.entity.Admin;
import com.womensafety.WomenSafetyProject.entity.Alert;
import com.womensafety.WomenSafetyProject.repository.UserRepository;
import com.womensafety.WomenSafetyProject.service.AdminService;
import com.womensafety.WomenSafetyProject.service.AlertService;
import com.womensafety.WomenSafetyProject.util.AlertPdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // Show admin dashboard with all alerts
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(required = false) String status,
                                HttpSession session,
                                Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";

        List<Alert> alerts;

        if (status != null && !status.isEmpty()) {
            alerts = alertService.getAlertsByStatus(status);
        } else {
            alerts = alertService.getAllAlerts();
        }

        // Count how many alerts are pending
        long pendingCount = alerts.stream()
                .filter(alert -> "Pending".equalsIgnoreCase(alert.getStatus()))
                .count();

        model.addAttribute("alerts", alerts);
        model.addAttribute("status", status);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("pendingCount", pendingCount);

        return "admin/dashboard"; // templates/admin/dashboard.html
    }




//    // Resolve alert by ID
//    @PostMapping("/resolve/{id}")
//    public String resolveAlert(@PathVariable Long id, HttpSession session) {
//        Admin admin = (Admin) session.getAttribute("admin");
//        if (admin == null) return "redirect:/login";
//
//        alertService.resolveAlert(id, admin.getEmail());
//        return "redirect:/admin/dashboard";
//    }


    // Resolve alert by ID
    @PostMapping("/resolve/{id}")
    public String resolveAlert(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";

        alertService.resolveAlert(id, admin.getEmail(), session); // Pass session

        return "redirect:/admin/dashboard";
    }


    // AdminController.java
    @GetMapping("/alerts/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=alerts_report.pdf";
        response.setHeader(headerKey, headerValue);

        List<Alert> alerts = alertService.getAllAlerts(); // Adjust this based on status if needed

        AlertPdfExporter exporter = new AlertPdfExporter(alerts);
        exporter.export(response);
    }


    @GetMapping("/alerts/stats")
    @ResponseBody
    public Map<String, Long> getAlertStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", alertService.countByStatus("Pending"));
        stats.put("resolved", alertService.countByStatus("Resolved"));
        return stats;
    }

    @GetMapping("/alerts/daily-stats")
    @ResponseBody
    public Map<String, Long> getDailyAlertStats() {
        List<Alert> alerts = alertService.getAllAlerts();
        Map<String, Long> dailyStats = alerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getTimestamp().toLocalDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));
        return dailyStats;
    }

    @GetMapping("/charts")
    public String showCharts(Model model) {
        // Optionally add admin email or any other data
        model.addAttribute("adminEmail", "rijakumari42@gmail.com");
        return "admin/charts"; // Thymeleaf resolves to charts.html in templates folder
    }


    @GetMapping("/users")
    public String manageUsers(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";

        model.addAttribute("users", userRepository.findAll());
        return "admin/manage-users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";

        if (!admin.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            return "admin/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "admin/change-password";
        }

        admin.setPassword(newPassword);
        adminService.saveAdmin(admin); // Make sure this method updates the password
        model.addAttribute("success", "Password changed successfully.");
        return "redirect:/admin/dashboard"; // Correct - mapped to @GetMapping("/dashboard")

    }

    @PostMapping("/alerts/clear-resolved")
    public String clearResolvedAlerts(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";
        alertService.deleteResolvedAlerts();
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users/{id}/alerts")
    public String viewUserAlerts(@PathVariable Long id, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) return "redirect:/login";

        List<Alert> userAlerts = alertService.getAlertsByUserId(id);
        model.addAttribute("alerts", userAlerts);
        model.addAttribute("userId", id);
        return "admin/user-alerts"; // templates/admin/user-alerts.html
    }



}
