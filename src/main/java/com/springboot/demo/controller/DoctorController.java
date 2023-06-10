package com.springboot.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.springboot.demo.model.Doctor;

import com.springboot.demo.repository.DoctorRepository;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

	@Autowired
	private DoctorRepository doctorRepository;
	
	@GetMapping("/getdoctors")
	public List<Doctor> getAllDoctors() {
		return doctorRepository.findAll();
	}

	@GetMapping("/getDoctorDetails/{id}")
	public Doctor getDoctor(@PathVariable Long id) {
		Doctor dtr = doctorRepository.findByDoctorId(id);
		return dtr;
	}

}
