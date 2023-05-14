package com.ankit.controller;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ankit.dao.ContactRepo;
import com.ankit.dao.UserRepository;
import com.ankit.entities.Contact;
import com.ankit.entities.User;
import com.ankit.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepo contactRepo;
	// Run for all handler
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("UserName:" + userName);

		// get the user using username(Email)
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);

	}
	
	// Dashboard home
	//	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Dashboard");

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contacts");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,
			HttpSession httpSession) {
		
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		//processing and uploading file
		
		if(file.isEmpty()) {
			//If the file is empty then try our message
			System.out.println("File is empty");
			contact.setImageUrl("contact.png");
			
		}else {
		//file the file to folder and update the name to contact
			contact.setImageUrl(file.getOriginalFilename());
			
			File savefile=new ClassPathResource("static/img").getFile();
			
			Path path=Paths.get(savefile.getAbsolutePath()+File.separator+ file.getOriginalFilename());
			
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image is uploaded");
		}
		
		
		
		//adding contact into list
		contact.setUser(user);
		
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("Data Added");
		
		httpSession.setAttribute("message", new Message("Your contact is added! Add More.", "success") );
		
		
		} catch (Exception e) {
			System.out.println("Error"+e.getMessage());
			httpSession.setAttribute("message", new Message("Something went wrong", "danger") );
		}
		return "normal/add_contact_form";
	}
	
	//__________________Showing all Details___________
	//per page= 5[n]
	//curent page=0[current]
	/*not for pagination @GetMapping("/show-contacts") */
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page ,Model model
			,Principal principal ) {
		model.addAttribute("title", "All Contacts");
		//Send contact list
		
//		String name=principal.getName();
//		User user=this.userRepository.getUserByUserName(name);
//		List contacts=user.getContacts();
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		Pageable pageable =PageRequest.of(page, 8);
		
		Page<Contact> contacts=this.contactRepo.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	/* Show Particular Contact Details */
	@GetMapping("/{cId}/contact")
	public String showChontactDetail(@PathVariable("cId") Integer cId
			,Model model
			,Principal principal) {
		System.out.println(cId);
		
		Optional<Contact> contactOpt=this.contactRepo.findById(cId);
		Contact contact=contactOpt.get();
		
		String userName= principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) { 
			model.addAttribute("contact",contact);
			model.addAttribute("title", contact.getName());
		}
		
		
		return "normal/contact_detail"; 	
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteChontact(@PathVariable("cId") Integer cId
	,Model model,Principal principal) {
		
		Optional<Contact> contactOpt=this.contactRepo.findById(cId);
		Contact contact=contactOpt.get();
		
		//Check...
		String userName= principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) { 
			model.addAttribute("title", "Delete");
			
			//Remove Image
//			File savefile=new ClassPathResource("static/img").getFile();
//			Path path=Paths.get(savefile.getAbsolutePath()+"/"+ contact.getImageUrl());
//			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
//			System.out.println("Image is uploaded");
			
//			contact.setUser(null);this.contactRepo.delete(contact);
			//Not working work without service class which have 
			//Transactional Anotation so we are using orphanRemoval=true
			//in User class @One to Many anotation and override equal method of contact
			user.getContacts().remove(contact);
			this.userRepository.save(user);
		}
		
		return "redirect:/user/show-contacts/0";	
		
	}
	
	//Open Update form
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId
	,Model model) {
		
		Contact contact=this.contactRepo.findById(cId).get();
		
			model.addAttribute("title", "Update");
			model.addAttribute("contact",contact);
			System.out.println(contact.getImageUrl());			
			return "normal/update_contact_form";	
		
	}
	
	//Update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@Valid @ModelAttribute Contact contact
	,Model model,
	@RequestParam("profileImage") MultipartFile file,
	Principal principal) {
		
		try {
		
			//old contact details
			Contact oldContact =this.contactRepo.findById(contact.getcId()).get();
			
			//image..
			if(!file.isEmpty()) {
			//file Work rewirte
			
			/* delete old photo */
			File deletefile=new ClassPathResource("static/img").getFile();
			File file1=new File(deletefile,oldContact.getImageUrl());
			file1.delete();
			//update new photo
			contact.setImageUrl(file.getOriginalFilename());
			File savefile=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(savefile.getAbsolutePath()+File.separator+ contact.getImageUrl());
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println(contact.getImageUrl());
			}
			
			User user=userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
			System.out.println(contact.getImageUrl());
		} catch (Exception e) {
			
		}
		
		/*
		 * System.out.println(contact.getcId()); System.out.println(contact.getName());
		 */
		model.addAttribute("title", "Update");
						
		return "redirect:/user/"+contact.getcId()+"/contact";	
		
	}
	
//Your Profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "normal/profile";
	}
	
	//Open Setting haNDLER
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}
	//Change Setting haNDLER
		@PostMapping("/change-password")
		public String changeSettings(@RequestParam("oldPassword") String oldPassword,
				@RequestParam("newPassword") String newPassword, Principal principal) {
			
			System.out.println("old Pass: "+oldPassword);
			System.out.println("new Pass: "+newPassword);
			
			String name=principal.getName();
			User user=this.userRepository.getUserByUserName(name);
			if(this.encoder.matches(oldPassword, user.getPassword())) {
				//Change the Password
				user.setPassword(this.encoder.encode(newPassword));
				this.userRepository.save(user);
			}else {
				//error
				return "redirect:/user/settings";
			}
			
			
			return "redirect:/user/index";
		}
		
}
