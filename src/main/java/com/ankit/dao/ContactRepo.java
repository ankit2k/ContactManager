package com.ankit.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ankit.entities.Contact;
import com.ankit.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer> {

	//	@Query("from Contact as c where c.user.id =:userId")
	//	public List <Contact> findContactsByUser(@Param("userId") int userId);

	// pagination
	// pageable: currentPage & contact page page
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

	// Search
	public List<Contact> findByNameContainingAndUserOrderByNameAsc(String name, User user);
}
