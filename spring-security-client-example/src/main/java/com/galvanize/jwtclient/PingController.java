package com.galvanize.jwtclient;

import com.galvanize.jwtclient.security.User;
import com.galvanize.jwtclient.security.UserPrinciple;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

  @GetMapping("/ping")
  public String ping() {
    return "Pong";
  }

  @GetMapping("/cardme")
  public String idCheck(@AuthenticationPrincipal User user) {
    return user.getUsername() + " with an id of " + user.getId() + " is the currently authenticated user.";
  }
}
