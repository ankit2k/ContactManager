package com.ankit.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ankit.config.GEmailSender;
import com.ankit.dao.UserRepository;
import com.ankit.entities.User;
import com.ankit.helper.EmailDetails;
import com.ankit.helper.OtpProvider;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	@Autowired
	private GEmailSender emailSender;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	// Open Setting haNDLER
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Forgot password");
		return "forgot_email_form";
	}

	// Open Setting haNDLER
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, Model model,
			HttpSession session) {
		
		model.addAttribute("title", "Email verification");
		System.out.println(email);

		// generate otp of 4 digit
		OtpProvider newOtp= new OtpProvider();
		int otp=newOtp.generateOtp();
		System.out.println("Otp:" + otp);
		try {
			// Write code for send OTP
//			EmailDetails emailDetails = new EmailDetails();
//			emailDetails.setTo(email);
//			emailDetails.setSubject("Testing Opt send function");
//			emailDetails.setBody("Hello,User<br> <b>Your Otp for verification is:" + otp + "</b>");
//			emailSender.sendEmail(emailDetails);
			System.out.println("Opt send successfully");
			// OTP should store in database this is form temporary use
			session.setAttribute("myOpt", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		} catch (Exception e) {
			return "forgot";
		}

		}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") Integer otp,
			HttpSession session) {
		Integer myOtp= (Integer) session.getAttribute("myOpt");
		String email=(String) session.getAttribute("email");
		System.out.println(myOtp+" "+otp);
		
		if(myOtp.equals(otp)) {
			//Change Password change
			User user=this.userRepository.getUserByUserName(email);
			System.out.println(session.getAttribute("email"));
			if(user==null) {
//				send error msg
				return "forgot_email_form";
			}else {
				return "password_change_form";	
			}
			
		}else {
			session.setAttribute("message", "You have entered Wrong opt");
			return "verify_otp";
		}
	}
	
	
	// Open Change Password haNDLER
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword
			,Model model,HttpSession session) {
		model.addAttribute("title", "Forgot password");
		String email=(String) session.getAttribute("email");
		User user=this.userRepository.getUserByUserName(email);
		user.setPassword(this.encoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=Password Changed Successfull..";
	}
}
