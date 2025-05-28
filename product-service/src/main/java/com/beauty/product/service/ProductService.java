package com.beauty.product.service;

import com.beauty.product.dto.request.ProductRequest;
import com.beauty.product.dto.response.ProductResponse;
import com.beauty.product.entity.Product;
import com.beauty.product.exception.InvalidQuantityException;
import com.beauty.product.exception.ProductNotFoundException;
import com.beauty.product.kafka.producer.KafkaLogProducer;
import com.beauty.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> listProduct = productRepository.getAllProducts();
        return listProduct
                .stream()
                .map( product -> new ProductResponse(
                       product.getId(),
                        product.getProductName(),
                        product.getProductCode(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getQuantity(),
                       product.getProductImage()))
                .collect(Collectors.toList());
    }

    public void addProduct(ProductRequest product) {
        Product newProduct = Product.builder()
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .productImage(product.getProductImage())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
        productRepository.save(newProduct);
    }

    public void updateQuantityAfterBought(String code, int quantity) {
        Product product = productRepository.getProductByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product not found with code: %s", code)));

        if (product.getQuantity() < quantity) {
            throw new InvalidQuantityException(String.format("Insufficient quantity. Available: %d, Requested: %d",
                    product.getQuantity(), quantity));
        }
        int newQuantity = (product.getQuantity() - quantity);

        if(productRepository.updateQuantityProduct(product.getId(), newQuantity) == 0){
            throw new ProductNotFoundException(String.format("Product not found with id: %d", product.getId()));
        }
    }

    public void updateProductQuantityByAdmin(Long id, int newQuantity) {
        if (productRepository.updateQuantityProduct(id, newQuantity) == 0) {
            throw new ProductNotFoundException(String.format("Product not found with id: %d", id));
        }
    }

    public void updateProductPrice(Long id, BigDecimal newPrice) {
        if (productRepository.updateProductPriceById(id, newPrice) == 0) {
            throw new ProductNotFoundException(String.format("Product not found with id: %s", id));
        }
    }

    public void deleteProduct(Long id) {
        if (productRepository.deleteProductById(id) == 0) {
            throw new ProductNotFoundException(String.format("Product not found with id: %d", id));
        }
    }

    public int checkStoreForQuantity(String code) {
        return productRepository.getProductQuantityByCode(code);
    }
}
