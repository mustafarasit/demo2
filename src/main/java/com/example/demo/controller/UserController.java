package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() {
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return userService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return userService.findById(id)
                .filter(u -> u.getUsername().equals(authentication.getName()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(403).build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(userService.update(id, user));
        }
        return userService.findById(id)
                .filter(u -> u.getUsername().equals(authentication.getName()))
                .map(u -> ResponseEntity.ok(userService.update(id, user)))
                .orElseGet(() -> ResponseEntity.status(403).build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String username = authentication.getName();
        return userService.findByUsername(username).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMe(Authentication authentication, @RequestBody User user) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .map(u -> ResponseEntity.ok(userService.update(u.getId(), user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
