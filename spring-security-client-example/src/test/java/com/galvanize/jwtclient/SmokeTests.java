package com.galvanize.jwtclient;

import com.galvanize.jwtclient.security.JwtProperties;
import com.galvanize.jwtclient.security.UserPrinciple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 These sets of tests are collected together because they represent
 simple tests for common features, such as server loading, database
 connections, and security mechanisms.
 */

@Profile("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	JwtProperties jwtProperties;

	TestingUtilities util;

	@BeforeEach
	void setup() {
		this.util = new TestingUtilities(testRestTemplate, jwtProperties);
	}

	// This will fail if the server fails to boot
	@Test
	@DisplayName("Server Should Start")
	void contextLoads() {
	}

	@Test
	@DisplayName("Ping should public and return Pong")
	void basicPingTest() {
		ResponseEntity<String> response = util.quickGet("/ping");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Pong", response.getBody());
	}

	@Test
	@DisplayName("Id Check should require a user")
	void failIdCheck() {
		ResponseEntity<String> response = this.util.quickGet("/cardme");

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	@Test
	@DisplayName("Id Check should reply with user information")
	void okIdCheck() {
		// Setup
		UserPrinciple user = new UserPrinciple(1L, "Alex", "User", "Dude", "123", "dude@example.com");
		user.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
		String token = this.util.getToken(user);

		// Execute
		ResponseEntity<String> response = this.util.quickGet("/cardme", token);
		String body = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(body, containsString(user.getUsername()));
		assertThat(body, containsString(user.getId().toString()));
	}

	// Test the database connection somehow? Print out some connection information?
}
