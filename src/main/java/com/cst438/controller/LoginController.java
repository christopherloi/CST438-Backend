package com.cst438.controller;

import com.cst438.domain.User;
import com.cst438.domain.UserRepository;
import com.cst438.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

@RestController
@CrossOrigin
public class LoginController {
	
	@Autowired
	TokenService tokenService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;


	@PostMapping("/login")
	public LoginDTO login(@RequestBody Map<String, String> loginRequest) {
		// Extract email and password from the request body
		String email = loginRequest.get("email");
		String password = loginRequest.get("password");

		// Fetch user details
		User user = userRepository.findByEmail(email);
		if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid login credentials");
		}

		// Generate JWT token
		String token = tokenService.generateToken(email, user.getType());

		// Return token and user type
		return new LoginDTO(token, user.getType());
		/*String name = authentication.getName();
		System.out.println("login authentication "+name);
		User user = userRepository.findByEmail(name);
		String token = tokenService.generateToken(authentication);
		return new LoginDTO(token, user.getType());*/
	}

}
