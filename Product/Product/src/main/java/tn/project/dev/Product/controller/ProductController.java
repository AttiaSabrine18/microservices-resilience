package tn.project.dev.Product.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @Bulkhead(name = "productService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "getAllProductsFallback")
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    public List<Product> getAllProducts() throws InterruptedException {
        // Simulation d'un traitement long
        Thread.sleep(100);
        return products;
    }

    @GetMapping("/{id}")
    @Bulkhead(name = "productService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "getProductByIdFallback")
    public Product getProductById(@PathVariable Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/category/{category}")
    @Bulkhead(name = "productService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "getProductsByCategoryFallback")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @GetMapping("/hello")
    @Bulkhead(name = "productService", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "helloFallback")
    public String hello() throws InterruptedException {
        Thread.sleep(50); // Simulation traitement
        return "Bonjour depuis Product 1 Service ! [BULKHEAD OK]";
    }

    // Fallback methods
    public List<Product> getAllProductsFallback(Throwable t) {
        return Arrays.asList(new Product(0L, "Produit indisponible", BigDecimal.ZERO, "N/A"));
    }

    public Product getProductByIdFallback(Long id, Throwable t) {
        return new Product(0L, "Service saturé", BigDecimal.ZERO, "BULKHEAD");
    }

    public List<Product> getProductsByCategoryFallback(String category, Throwable t) {
        return Arrays.asList(new Product(0L, "Catégorie indisponible", BigDecimal.ZERO, category));
    }

    public String helloFallback(Throwable t) {
        return "Product Service saturé - Bulkhead plein ! 🟡";
    }
}