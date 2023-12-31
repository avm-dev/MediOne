package com.springboot.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.springboot.demo.model.AppointmentRecord;
import com.springboot.demo.model.Doctor;
import com.springboot.demo.model.Patient;
import com.springboot.demo.model.Receptionist;
import com.springboot.demo.repository.AppointmentRecordRepository;
import com.springboot.demo.repository.DoctorRepository;
import com.springboot.demo.repository.PatientRepository;
import com.springboot.demo.repository.ReceptionistRepository;

@RestController
@RequestMapping("/appointment")
public class AppointmentRecordController {
	
	@Autowired
	private AppointmentRecordRepository appointmentrecordrepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private ReceptionistRepository receptionistRepository; 
	
	@Autowired
	  private JavaMailSender javaMailSender;
	
	// Getting Appointment Record with status Confirmed
	@GetMapping("/AppoinmentDetails/{id}")
	   public List<AppointmentRecord> getAppointmentDetailsByIdAndStatus(@PathVariable Long id){
		 
		 List<AppointmentRecord> ApRecord = (List<AppointmentRecord>) appointmentrecordrepository.findByReceptionistIdAndAppointmentStatus(id,"Confirmed");
		 return ApRecord;
	}
	
	// Getting Appointment Record with status Requested
	@GetMapping("/RequestedAppoinmentDetails/{id}")
	   public List<AppointmentRecord> getPendingAppointmentDetailsByIdAndStatus(@PathVariable Long id){
		 
		 List<AppointmentRecord> ApRecord = (List<AppointmentRecord>) appointmentrecordrepository.findByReceptionistIdAndAppointmentStatus(id,"Requested");
		 	return ApRecord;
	}
	
	
	//Getting Record from AppointmentRecord Table for Particular ReceptionistID 
	@GetMapping("/PatientRecord/{id}")
	   public List<Patient> getPatientDetailsById(@PathVariable Long id){
	    	//List<Patient> patient1;
		 List<AppointmentRecord> ApRecord = (List<AppointmentRecord>) appointmentrecordrepository.findByReceptionistId(id);
		 List<Patient> patients = new ArrayList<Patient>(); 
		 for (AppointmentRecord a:ApRecord) {
			 patients.add(patientRepository.findByPid2(a.getPatientId()));
		}
		 return patients;
	}

	//get doctors list by receptionist
	@GetMapping("/DoctorDetails/{id}")
	   public List<Doctor> getDoctortDetailsById(@PathVariable Long id){
		List<AppointmentRecord> ApRecord = (List<AppointmentRecord>) appointmentrecordrepository.findByReceptionistId(id);
		List <Doctor> doctors = new ArrayList<Doctor>();
		for(AppointmentRecord d:ApRecord){
			doctors.add(doctorRepository.findByDoctorId(d.getDoctorId()));
		}
		   return doctors;
	    }
	
	
	//Get A single Record from Appointment Record Table
	@GetMapping("/getAppointmentRecord/{id}")
	public AppointmentRecord getRecordById(@PathVariable Long id) {
		AppointmentRecord apr = appointmentrecordrepository.findByRecordId(id);
		return apr;
	}
	
	//Update Record of Appointment Record Table
	@PutMapping("/updateAppointmentRecord/{id}")
	public ResponseEntity<AppointmentRecord> updateAppointmentRecord(@PathVariable Long id,@RequestBody AppointmentRecord apr){
	    apr.setRecordId(id);
	    AppointmentRecord updateappoitmentrecord = appointmentrecordrepository.save(apr);
        return ResponseEntity.ok(updateappoitmentrecord);
 }

    //Rest API for Deleting Record using Record Id
	@DeleteMapping("/DeleteRecord/{id}")
		public ResponseEntity <Map<String, Boolean>> deleteRecord(@PathVariable Long id){
			AppointmentRecord record = appointmentrecordrepository.findByRecordId(id);
					
			appointmentrecordrepository.delete(record);
			Map<String,Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return ResponseEntity.ok(response);
		}

	//  Sending E-mails to Patients
	@PostMapping("/sendEmail/{id}")
	public void sendEmail(@PathVariable Long id) {
		AppointmentRecord apr = appointmentrecordrepository.findByRecordId(id);
		AppointmentRecord record = appointmentrecordrepository.findByRecordId(id);
		Patient patient = patientRepository.findByPid2(record.getPatientId());
		SimpleMailMessage msg = new SimpleMailMessage();
		if(record.getPatientId() == patient.getPid2() && record.getAppointmentStatus().equals("Confirmed") ) {
			String mail = patient.getEmailId();
			msg.setTo(mail);
			msg.setSubject("MediOne HEALTH CARE APPOINTMENT APPROVAL");
			msg.setText("Hello, "+patient.getFirstName()+" "+patient.getLastName()+" Your Appointment is Approved for Date "+apr.getAppointmentDate()+"\n Your Regards - MediOne HEALTHCARE");
			javaMailSender.send(msg);
			System.out.println("Inside Confirmed loop");
		}else if(record.getPatientId() == patient.getPid2() && record.getAppointmentStatus().equals("Rejected")) {
			String mail = patient.getEmailId();
			msg.setTo(mail);
			msg.setSubject("MediOne HEALTH CARE APPOINTMENT Rejected");
			msg.setText(patient.getFirstName()+" "+patient.getLastName()+" Your Appointment is Rejected Please request for other Date"+"\n Your Regards - MediOne HEALTHCARE");
			javaMailSender.send(msg);
			System.out.println("Inside Rejected loop");
		}
		
	}
	
	//Getting Record from Receptionist Table for Particular ReceptionistID
	@GetMapping("/ReceptionistDetails/{id}")
	   public Receptionist getReceptionisttDetailsById(@PathVariable Long id){
		   
		   return  receptionistRepository.findByReceptionistId(id);
	   }
	
	@GetMapping("/PatientDetails/{id}")
	   public Patient getDetailsById(@PathVariable Long id){
		   
		   return patientRepository.findByPid2(id);
	    }
	
	
	/*@GetMapping("/DoctorDetails/{id}")
	   public Doctor getDoctorDetailsById(@PathVariable Long id){
		List<AppointmentRecord> ApRecord = (List<AppointmentRecord>) appointmentrecordrepository.findByReceptionistId(id);
				   return  doctorRepository.findByDoctorId(id);
	  }*/
	

}
