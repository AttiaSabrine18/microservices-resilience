package tn.project.dev.User.controller;

import org.springframework.web.bind.annotation.*;
import tn.project.dev.User.model.User;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final List<User> users = Arrays.asList(
            new User(1L, "Alice Martin", "alice@email.com"),
            new User(2L, "Bob Dupont", "bob@email.com"),
            new User(3L, "Charlie Durant", "charlie@email.com")
    );

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Bonjour depuis User2 Service !";
    }
}