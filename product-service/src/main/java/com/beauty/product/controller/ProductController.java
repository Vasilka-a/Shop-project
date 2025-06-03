package com.beauty.product.controller;

import com.beauty.product.dto.request.ProductRequest;
import com.beauty.product.dto.response.ProductResponse;
import com.beauty.product.kafka.producer.KafkaLogProducer;
import com.beauty.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final KafkaLogProducer loggerService;


    public ProductController(ProductService productService, KafkaLogProducer loggerService) {
        this.productService = productService;
        this.loggerService = loggerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProductsForAuthorizeUsers() {
        List<ProductResponse> products = productService.getAllProducts();

        loggerService.sendLogInfo("Product-Service", String.format("Product list successfully received. Size: %d ", products.size()));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<ProductResponse>> getAllProductsForUnAuthorizeUsers() {
        List<ProductResponse> products = productService.getAllProducts();

        loggerService.sendLogInfo("Product-Service", String.format("Product list successfully received. Size: %d ", products.size()));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest product) {
        productService.addProduct(product);

        loggerService.sendLogInfo("Product-Service", String.format("Success added product: %s ", product.getProductName()));
        return ResponseEntity.status(HttpStatus.CREATED).body("Success created");
    }

    @PutMapping("/quantity")
    public ResponseEntity<?> updateProductQuantityByAdmin(@RequestParam Long id, @RequestParam int newQuantity) {
        productService.updateProductQuantity(id, newQuantity);

        loggerService.sendLogInfo("Product-Service", String.format("Admin success update product count by id: %d", id));
        return ResponseEntity.ok("Success updated");
    }

    @PutMapping("/price")
    public ResponseEntity<?> updateProductPrice(@RequestParam Long id, @RequestParam String newPrice) {
        productService.updateProductPrice(id, new BigDecimal(newPrice));

        loggerService.sendLogInfo("Product-Service", String.format("Success update product price by id: %d", id));
        return ResponseEntity.ok("Price updated");
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteProduct(@RequestParam Long id) {
        productService.deleteProduct(id);

        loggerService.sendLogInfo("Product-Service", String.format("Success deleted product by id: %d", id));
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/check-store")
    public ResponseEntity<Integer> checkStoreForQuantity(@RequestParam String code) {
        int quantity = productService.checkStoreForQuantity(code);
        return new ResponseEntity<>(quantity, HttpStatus.OK);
    }
}
