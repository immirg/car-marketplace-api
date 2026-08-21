package org.example.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.example.client.AppUser;
import org.example.services.UserService;
import org.example.views.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @JsonView(Views.PlatformManager.class)
    @PatchMapping("/block/{id}")
    public ResponseEntity<Void> blockUser(@PathVariable Integer id) {
        return userService.blockUser(id);
    }

    @JsonView(Views.PlatformManager.class)
    @PatchMapping("/unblock/{id}")
    public ResponseEntity<Void> unblockUser(@PathVariable Integer id) {
        return userService.unblockUser(id);
    }

    @JsonView(Views.PlatformAdmin.class)
    @PatchMapping("/make-manager/{id}")
    public ResponseEntity<Void> makeManager(@PathVariable Integer id) {
        return userService.makeManager(id);
    }

    @JsonView(Views.PlatformAdmin.class)
    @PatchMapping("/make-admin/{id}")
    public ResponseEntity<Void> makeAdmin(@PathVariable Integer id) {
        return userService.makeAdmin(id);
    }

    @PatchMapping("/account/premium")
    public ResponseEntity<Void> createPremiumAccount(@AuthenticationPrincipal AppUser appUser) {
        return userService.createPremiumAccount(appUser.getEmail());
    }
}
