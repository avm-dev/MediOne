package com.springboot.demo.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.servlet.http.HttpSession;
import com.springboot.demo.service.AdminDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.demo.model.Admin;
import com.springboot.demo.model.Doctor;
import com.springboot.demo.model.Receptionist;
import com.springboot.demo.repository.AdminRepository;
import com.springboot.demo.repository.DoctorRepository;
import com.springboot.demo.repository.ReceptionistRepository;

import java.util.Date;

@RestController
@RequestMapping("/master")
public class AdDrRecepController {
	private final DoctorRepository doctorrepository;
	private final ReceptionistRepository receptionistrepository;
	private final AdminDetailsService adminDetailsService;

	@Autowired
	public AdDrRecepController(AdminDetailsService userDetailsService, DoctorRepository doctorrepository, ReceptionistRepository receptionistrepository, AdminRepository adminRepository) {
		this.adminDetailsService = userDetailsService;
		this.doctorrepository = doctorrepository;
		this.receptionistrepository = receptionistrepository;
	}

	@PostMapping(value = "/login", produces = "application/json")
	public ResponseEntity<String> adminLogin(@RequestBody Admin admin, HttpSession session) {
		UserDetails userDetails;
		try {
			userDetails = adminDetailsService.validateUser(admin.getUsername(), admin.getPassword());
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

        // Generate JWT token
        String jwtToken = generateJwtToken(userDetails.getUsername());

        // Store JWT token in session
        session.setAttribute("jwtToken", jwtToken);

        return ResponseEntity.ok(jwtToken);
	}

	@PostMapping("/regdoc")
	@PreAuthorize("hasRole('ADMIN')")
	public Doctor createDoctor(@RequestBody Doctor doctor) {
		return doctorrepository.save(doctor);
	}

	@PostMapping("/regrecep")
	@PreAuthorize("hasRole('ADMIN')")
	public Receptionist createReceptionist(@RequestBody Receptionist receptionist) {
		return receptionistrepository.save(receptionist);
	}

    private String generateJwtToken(String username) {
        // Define token expiration time
        long tokenValidityInMillis = 86400000; // 24 hour

        // Generate the JWT token
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidityInMillis))
                .signWith(SignatureAlgorithm.HS512, "NDyrkAa0I1APzCcugSeRUQlhEvpoSX+kqiLbU/S28hZbnWkX67Ixe7uRHiRtnvwA" +
						"KstWgupE3izpAWEOeX5vhg==")
                .compact();
    }
}
