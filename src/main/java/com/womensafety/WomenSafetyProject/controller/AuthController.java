package com.womensafety.WomenSafetyProject.controller;



import com.womensafety.WomenSafetyProject.entity.Admin;
import com.womensafety.WomenSafetyProject.entity.User;
import com.womensafety.WomenSafetyProject.service.AdminService;
import com.womensafety.WomenSafetyProject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    // Show user registration page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); //  Add empty user for th:object
        return "user/register";
    }



    // Handle user registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }
        userService.registerUser(user);
        model.addAttribute("message", "Registration successful. Please login.");
        return "login";  // Redirect to login page
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // templates/login.html
    }

    // Handle login (both user and admin)
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String role,
                        HttpSession session,
                        Model model) {

        if ("user".equals(role)) {
            User user = userService.loginUser(email, password);
            if (user != null) {
                session.setAttribute("user", user);
                return "redirect:/user/dashboard";
            } else {
                model.addAttribute("error", "Invalid user credentials");
                return "login";
            }
        } else if ("admin".equals(role)) {
            Admin admin = adminService.loginAdmin(email, password);
            if (admin != null) {
                session.setAttribute("admin", admin);
                return "redirect:/admin/dashboard";
            } else {
                model.addAttribute("error", "Invalid admin credentials");
                return "login";
            }
        }

        model.addAttribute("error", "Invalid role selected");
        return "login";
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

