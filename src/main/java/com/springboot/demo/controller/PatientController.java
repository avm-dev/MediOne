package com.springboot.demo.controller;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import com.springboot.demo.exception.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.demo.model.AppointmentRecord;
import com.springboot.demo.model.Doctor;
import com.springboot.demo.model.MedicalReport;
import com.springboot.demo.model.PPrescription;
import com.springboot.demo.model.Patient;
import com.springboot.demo.model.Receptionist;
import com.springboot.demo.repository.AppointmentRecordRepository;
import com.springboot.demo.repository.DoctorRepository;
import com.springboot.demo.repository.PatientRepository;
import com.springboot.demo.repository.ReceptionistRepository;

@RestController
@RequestMapping("/patient")
public class PatientController {
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private AppointmentRecordRepository appointmentrecordRepository;
	
	@Autowired
	private DoctorRepository doctorrepository;
	
	@Autowired
	private ReceptionistRepository receptionistrepository; 
	
	//registration of patients
	@PostMapping("/register")
	public Patient createPatient(@RequestBody Patient patient) {
		return patientRepository.save(patient);
	}

	//patient login
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<String> patientLogin(@RequestBody Patient p, HttpSession Session)
	{
		Patient dt=patientRepository.findByEmailIdAndPassword(p.getEmailId(),p.getPassword());
		if(Objects.nonNull(dt))
		{
			return ResponseEntity.ok(dt.toString());
		}
		else
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}
	}

	@PutMapping("/reqForAppointment")
	public ResponseEntity<String> reqForAppointments(@RequestBody AppointmentRecord ar) {
		try {
			ar.setAppointmentStatus("Requested");
			Patient patient = patientRepository.findByPid2(ar.getPatientId());

			if (patient == null) {
				// Patient not found, handle the exception
				throw new PatientNotFoundException("Patient not found with ID: " + ar.getPatientId());
			}

			ar.setPatientId(patient.getPid2());
			appointmentrecordRepository.save(ar);

			return ResponseEntity.ok("Success");
		} catch (PatientNotFoundException e) {
			// Handle the exception and return an appropriate response
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			// Handle any other generic exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}


	@GetMapping("/doctorDetails")
	public List<Doctor> getAllDoctors(){
		return doctorrepository.findAll();
	}

	@GetMapping("/receptionistDetails")
	public List<Receptionist> getAllReceptionist(){
		return receptionistrepository.findAll();
	}

	@GetMapping("/getPatientById/{id}")
	public Patient getPatientById(@PathVariable long id){
	   return patientRepository.findByPid2(id);
	}

}
