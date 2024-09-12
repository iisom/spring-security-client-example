package com.galvanize.jwtclient;

import com.galvanize.jwtclient.security.JwtProperties;
import com.galvanize.jwtclient.security.UserPrinciple;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TestingUtilities {

  private final TestRestTemplate testRestTemplate;
  private final JwtProperties jwtProperties;

  TestingUtilities(TestRestTemplate restTemplate, JwtProperties jwtProperties) {
    this.testRestTemplate = restTemplate;
    this.jwtProperties = jwtProperties;
  }

  public RequestEntity<Void> getRequestEntity(String path) {
    return RequestEntity
      .get(path)
      .accept(MediaType.ALL)
      .build();
  }

  public RequestEntity<Void> getRequestEntity(String path, String token) {
    return RequestEntity
      .get(path)
      .accept(MediaType.ALL)
      .header(this.jwtProperties.getHeader(), this.jwtProperties.getHeaderValue(token))
      .build();
  }

  public <T> ResponseEntity<T> getRequest(String path, Class<T> bodyType) {
    return this.testRestTemplate.exchange(this.getRequestEntity(path), bodyType);
  }

  public <T> ResponseEntity<T> getRequest(String path, Class<T> bodyType, String token) {
    return this.testRestTemplate.exchange(this.getRequestEntity(path, token), bodyType);
  }

  public ResponseEntity<String> quickGet(String path) {
     return this.getRequest(path, String.class);
  }

  public ResponseEntity<String> quickGet(String path, String token) {
    return this.getRequest(path, String.class, token);
  }

  public String getToken(String username, Integer userId, List<String> roles) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
      .setHeaderParam("typ","JWT")
      .setSubject(username)
      .claim("name", username)
      .claim("guid", userId)
      .claim("authorities", roles)
      .setIssuedAt(new Date(now))
      .setExpiration(new Date(now + this.jwtProperties.getExpiration() * 1000L))
      .signWith(SignatureAlgorithm.HS512, this.jwtProperties.getSecret().getBytes())
      .compact();
  }

  public String getToken(UserPrinciple user) {
    List<String> roles = user.getAuthorities().stream().map(Object::toString).toList();
    long now = System.currentTimeMillis();

    return Jwts.builder()
      .setHeaderParam("typ","JWT")
      .setSubject(user.getUsername())
      .claim("name", user.getUsername())
      .claim("guid", user.getId())
      .claim("authorities", roles)
      .setIssuedAt(new Date(now))
      .setExpiration(new Date(now + this.jwtProperties.getExpiration() * 1000L))
      .signWith(SignatureAlgorithm.HS512, this.jwtProperties.getSecret().getBytes())
      .compact();
  }

  public String getToken(String username, List<String> roles) {
    return this.getToken(username, 1, roles);
  }
}
