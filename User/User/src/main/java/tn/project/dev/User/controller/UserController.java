package tn.project.dev.User.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;
import tn.project.dev.User.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final List<User> users = Arrays.asList(
            new User(1L, "Alice Martin", "alice@email.com"),
            new User(2L, "Bob Dupont", "bob@email.com"),
            new User(3L, "Charlie Durant", "charlie@email.com")
    );

    @GetMapping
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    public List<User> getAllUsers() {
        // Simulation d'erreur aléatoire (50% de chance)
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Service User 1 indisponible !");
        }
        return users;
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    public User getUserById(@PathVariable Long id) {
        if (new Random().nextInt(10) < 3) { // 30% d'erreur
            throw new RuntimeException("Erreur lors de la récupération !");
        }
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/hello")
    @CircuitBreaker(name = "userService", fallbackMethod = "helloFallback")
    public String hello() {
        // Simule une panne après quelques appels
        if (Math.random() > 0.7) {
            throw new RuntimeException("Service User 1 en panne !");
        }
        return "Bonjour depuis User 1 Service ! [CIRCUIT FERME]";
    }

    // Fallback methods
    public List<User> getAllUsersFallback(Throwable t) {
        return Arrays.asList(new User(0L, "Fallback User", "fallback@email.com"));
    }

    public User getUserByIdFallback(Long id, Throwable t) {
        return new User(0L, "Service temporairement indisponible", "N/A");
    }

    public String helloFallback(Throwable t) {
        return "User 1 Service dégradé - Mode Circuit Breaker ! ";
    }
}