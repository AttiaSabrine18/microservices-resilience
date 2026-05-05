package tn.project.dev.Product.controller;


import org.springframework.web.bind.annotation.*;
import tn.project.dev.Product.model.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final List<Product> products = Arrays.asList(
            new Product(1L, "Laptop Pro", new BigDecimal("1299.99"), "Électronique"),
            new Product(2L, "Smartphone X", new BigDecimal("799.99"), "Électronique"),
            new Product(3L, "Casque Audio", new BigDecimal("149.99"), "Audio"),
            new Product(4L, "Souris Sans Fil", new BigDecimal("49.99"), "Accessoires")
    );

    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Bonjour depuis Product 2 Service !";
    }
}
