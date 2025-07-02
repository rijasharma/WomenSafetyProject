package com.womensafety.WomenSafetyProject.controller;


import com.womensafety.WomenSafetyProject.entity.Alert;
import com.womensafety.WomenSafetyProject.entity.User;
import com.womensafety.WomenSafetyProject.repository.UserRepository;
import com.womensafety.WomenSafetyProject.service.AlertService;
import com.womensafety.WomenSafetyProject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Show user dashboard with alert history
//    @GetMapping("/dashboard")
//    public String showDashboard(HttpSession session, Model model) {
//        User user = (User) session.getAttribute("user");
//        if (user == null) return "redirect:/login";
//
//        List<Alert> alerts = alertService.getAlertsByUser(user);
//        model.addAttribute("alerts", alerts);
//        model.addAttribute("user", user);
//        return "user/dashboard";  // templates/user/dashboard.html
//    }


    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Load alert history
        List<Alert> alerts = alertService.getAlertsByUser(user);
        model.addAttribute("alerts", alerts);
        model.addAttribute("user", user);

        // Check for session-based notification
        String notificationKey = "notification_user_" + user.getId();
        String notification = (String) session.getServletContext().getAttribute(notificationKey);
        if (notification != null) {
            model.addAttribute("notification", notification);
            session.getServletContext().removeAttribute(notificationKey); // show once
        }

        return "user/dashboard"; // templates/user/dashboard.html
    }



    // Handle alert submission from frontend
    @PostMapping("/sendAlert")
    @ResponseBody
    public String sendAlert(@RequestParam double latitude,
                            @RequestParam double longitude,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "NOT_LOGGED_IN";

        alertService.sendAlert(user, latitude, longitude);

        return "ALERT_SENT";
    }

    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        User user = userRepository.findById(sessionUser.getId()).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "user/edit";
    }

    // Handle form submission
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User updatedUser, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        User user = userRepository.findById(sessionUser.getId()).orElse(null);
        if (user != null) {
            user.setName(updatedUser.getName());
            user.setPhone(updatedUser.getPhone());
            user.setLocation(updatedUser.getLocation());
            user.setPassword(updatedUser.getPassword());
            userRepository.save(user);
            session.setAttribute("user", user); // Update session data
        }
        return "redirect:/user/edit?success";
    }

    @GetMapping("/view")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/view-profile"; // This will map to templates/user/view-profile.html
    }

    @GetMapping("/delete")
    public String deleteAccount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            userService.deleteUserById(user.getId());
            session.invalidate(); // Log out the user
        }
        return "redirect:/register"; // Redirect to register page
    }

    @PostMapping("/notification/viewed")
    @ResponseBody
    public void clearNotification(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String notificationKey = "notification_user_" + user.getId();
            session.getServletContext().removeAttribute(notificationKey);
        }
    }



}

