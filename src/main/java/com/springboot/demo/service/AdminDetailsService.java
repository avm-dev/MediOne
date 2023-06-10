package com.springboot.demo.service;

import com.springboot.demo.model.Admin;
import com.springboot.demo.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public UserDetails validateUser(String username, String password) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(password)) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();
    }

}
