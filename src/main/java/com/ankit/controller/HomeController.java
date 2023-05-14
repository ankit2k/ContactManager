package com.ankit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.ankit.dao.UserRepository;
import com.ankit.entities.User;
import com.ankit.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder bEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Smart-Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About- Smart-Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model m ) {
		
		m.addAttribute("title","Register- Smart-Contact Manager");
		m.addAttribute("user",new User());
		return "signup";  
	}

	// handle for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid  @ModelAttribute("user") User user, BindingResult result, 
			@RequestParam(value="agreement",defaultValue = "false") boolean agreement,
			Model m, HttpSession session ) {
		
	try {
		if(!agreement) {
			System.out.println("You Have not agreed");
			throw new Exception("You Have not agreed");
		}
		
		if(result.hasErrors()) {
			System.out.println("error:"+result.toString());
			m.addAttribute("user",user);
			return "signup";	
		}
		
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(bEncoder.encode(user.getPassword()));
		User saveUser=userRepository.save(user);
		System.out.println(user);
		session.setAttribute("user",new User());
		session.setAttribute("message", new Message("Successfully Registered" , "alert-success"));
		
		return "signup";
	} catch (Exception e) {
	e.printStackTrace();
	m.addAttribute("user",user);
	session.setAttribute("message", new Message("Somthing Went Wrong!"+ e.getMessage() , "alert-danger"));
	return "signup";
	}
		
	}
	
	// handle for Custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
	}
	

	
}
