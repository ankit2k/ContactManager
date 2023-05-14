package com.ankit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ankit.helper.EmailDetails;

import jakarta.mail.internet.MimeMessage;

@Service
public class GEmailSender {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(EmailDetails em) {
	
	try {
	
		MimeMessage message= mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(message, true);
		
		helper.setFrom("ankit00xi@gmail.com");
		helper.setTo(em.getTo());
		helper.setSubject(em.getSubject());
		helper.setText("text/html",em.getBody());
		
		mailSender.send(message);
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
	}
}
