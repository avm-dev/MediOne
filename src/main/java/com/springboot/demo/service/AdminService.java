package com.springboot.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.demo.model.Admin;
import com.springboot.demo.repository.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin registerAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
}

