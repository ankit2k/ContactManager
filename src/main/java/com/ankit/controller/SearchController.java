package com.ankit.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ankit.dao.ContactRepo;
import com.ankit.dao.UserRepository;
import com.ankit.entities.User;
import com.ankit.entities.Contact;

@RestController
public class SearchController {
	@Autowired
	private ContactRepo contactRepo;
	@Autowired
	private UserRepository userRepo;
	
	// search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query ,Principal principal) {
	System.out.println(query);
	User user= this.userRepo.getUserByUserName(principal.getName());
	List<Contact> contact=this.contactRepo.findByNameContainingAndUserOrderByNameAsc(query,user);
	return ResponseEntity.ok(contact);
	}

}
