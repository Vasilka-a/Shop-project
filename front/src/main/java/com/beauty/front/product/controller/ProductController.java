package com.beauty.front.product.controller;

import com.beauty.front.model.RequestProduct;
import com.beauty.front.product.service.ProductService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public String getCatalog(Model model, HttpServletRequest request, @ModelAttribute("error") String error,
                             @ModelAttribute("errorProductCode") String errorProductCode) {
        String token = extractTokenFromSession(request);
        boolean role = false;
        boolean isAuthenticated = false;
        if (token != null) {
            role = checkAdmin(token);
            isAuthenticated = true;
            model.addAttribute("products", productService.getAllProducts(token));// форма с авторизацией
        } else {
            model.addAttribute("products", productService.getAllProducts()); // форма без авторизации
        }

        model.addAttribute("authenticated", isAuthenticated);
        model.addAttribute("isAdmin", role);

        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
            model.addAttribute("errorProductCode", errorProductCode);
        }
        return "products";
    }

    @GetMapping("/create")
    public String createProductForm(Model model) {
        model.addAttribute("product", new RequestProduct());
        return "create";
    }

    @PostMapping("/save")
    public String createProduct(
            @RequestParam("productName") String productName,
            @RequestParam("productCode") String productCode,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "productImage", required = false) MultipartFile imageFile,
            HttpServletRequest request) throws IOException {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }

        RequestProduct requestProduct = new RequestProduct();
        requestProduct.setProductCode(productCode);
        requestProduct.setProductName(productName);
        requestProduct.setDescription(description);
        requestProduct.setPrice(new java.math.BigDecimal(price));
        requestProduct.setQuantity(quantity);

        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            Path uploadPath = Paths.get("uploads/images/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            requestProduct.setProductImage(uniqueFileName);
        }
        productService.createProduct(requestProduct, token);
        return "redirect:/products/catalog";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") Long id,
                                @RequestParam("productImage") String image,
                                HttpServletRequest request) {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        productService.deleteProduct(id, token);
        Path uploadPath = Paths.get("uploads/images");
        try {
            Files.deleteIfExists(Path.of(uploadPath + "/" + image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/products/catalog";
    }

    @PostMapping("/change-price")
    public String changeProductPrice(@RequestParam("id") Long id, @RequestParam("newPrice") String newPrice, HttpServletRequest request) {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        productService.changeProductPrice(id, newPrice, token);
        return "redirect:/products/catalog";
    }

    @PostMapping("/update-quantity")
    public String changeProductCountByAdmin(@RequestParam("id") Long id, @RequestParam("newQuantity") int newQuantity, HttpServletRequest request) {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        productService.changeProductQuantity(id, newQuantity, token);
        return "redirect:/products/catalog";
    }

    private String extractTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String tokenFromSession = (String) session.getAttribute("Authorization");
        if (tokenFromSession == null) {
            return null;
        }
        return tokenFromSession.substring(7);
    }

    public boolean checkAdmin(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        List<String> roles = (List<String>) claims.get("roles");
        return roles.stream().anyMatch(s -> s.equals("ROLE_ADMIN"));
    }
}
