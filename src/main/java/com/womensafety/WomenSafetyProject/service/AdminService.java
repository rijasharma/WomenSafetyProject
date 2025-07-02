package com.womensafety.WomenSafetyProject.service;



import com.womensafety.WomenSafetyProject.entity.Admin;
import com.womensafety.WomenSafetyProject.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;


    @Autowired
    private AdminRepository adminRepository;

    // Register admin (optional)
    public Admin registerAdmin(Admin admin) {
        return adminRepository.save(admin);
    }


    public Admin loginAdmin(String email, String password) {
//        Admin admin = adminRepository.findByEmail(email);
//        if (admin != null && admin.getPassword().equals(password)) {
//            return admin;
//        }
//        return null;

        if (adminEmail.equals(email) && adminPassword.equals(password)) {
            return new Admin(email, password); // Dummy Admin object for session
        }
        return null;
    }


    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }


}

