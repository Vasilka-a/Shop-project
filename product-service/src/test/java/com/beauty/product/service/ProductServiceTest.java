package com.beauty.product.service;

import com.beauty.product.dto.request.ProductRequest;
import com.beauty.product.dto.response.ProductResponse;
import com.beauty.product.entity.Product;
import com.beauty.product.exception.InvalidQuantityException;
import com.beauty.product.exception.ProductNotFoundException;
import com.beauty.product.kafka.producer.KafkaLogProducer;
import com.beauty.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaLogProducer loggerService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .productName("Test Product")
                .productCode("TEST001")
                .price(new BigDecimal("100.00"))
                .description("Test Description")
                .quantity(10)
                .productImage("test.jpg")
                .build();

        testProductRequest = ProductRequest.builder()
                .productName("Test Product")
                .productCode("TEST001")
                .price(new BigDecimal("100.00"))
                .description("Test Description")
                .quantity(10)
                .productImage("test.jpg")
                .build();
    }

    @Test
    void getAllProducts() {
        when(productRepository.getAllProducts()).thenReturn(Arrays.asList(testProduct));

        List<ProductResponse> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getProductName(), result.get(0).getProductName());
        assertEquals(testProduct.getProductCode(), result.get(0).getProductCode());
    }

    @Test
    void createNewProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.addProduct(testProductRequest);

        verify(productRepository).save(any(Product.class));//метод вызван корректно - verify
        verify(loggerService).sendLogInfo(eq("Product-Service"), contains("Created product"));
    }

    @Test
    void updateQuantityAfterBought_WithValidQuantity() {
        when(productRepository.getProductByCode("TEST001")).thenReturn(Optional.of(testProduct));
        when(productRepository.updateQuantityProduct(anyLong(), anyInt())).thenReturn(1);

        productService.updateQuantityAfterBought("TEST001", 5);

        verify(productRepository).updateQuantityProduct(eq(1L), eq(5));
        verify(loggerService).sendLogInfo(eq("Product-Service"), anyString());
    }

    @Test
    void updateQuantityAfterBought_WithInvalidQuantity() {
        when(productRepository.getProductByCode("TEST001")).thenReturn(Optional.of(testProduct));

        assertThrows(InvalidQuantityException.class, () ->
                productService.updateQuantityAfterBought("TEST001", 15)
        );
    }

    @Test
    void updateQuantityAfterBought_WithNonExistentProduct() {
        when(productRepository.getProductByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productService.updateQuantityAfterBought("NONEXISTENT", 5)
        );
    }

    @Test
    void updateProductQuantityByAdmin_WithValidId() {
        when(productRepository.updateQuantityProduct(1L, 20)).thenReturn(1);

        productService.updateProductQuantityByAdmin(1L, 20);

        verify(productRepository).updateQuantityProduct(eq(1L), eq(20));
        verify(loggerService).sendLogInfo(eq("Product-Service"), anyString());
    }

    @Test
    void updateProductQuantityByAdmin_WithInvalidId() {
        when(productRepository.updateQuantityProduct(999L, 20)).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProductQuantityByAdmin(999L, 20)
        );
    }

    @Test
    void updateProductPrice_WithValidId() {
        when(productRepository.updateProductPriceById(1L, new BigDecimal("150.00"))).thenReturn(1);

        productService.updateProductPrice(1L, new BigDecimal("150.00"));

        verify(productRepository).updateProductPriceById(eq(1L), eq(new BigDecimal("150.00")));
        verify(loggerService).sendLogInfo(eq("Product-Service"), anyString());
    }

    @Test
    void updateProductPrice_WithInvalidId() {
        when(productRepository.updateProductPriceById(999L, new BigDecimal("150.00"))).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProductPrice(999L, new BigDecimal("150.00"))
        );
    }

    @Test
    void deleteProduct_WithValidId() {
        when(productRepository.deleteProductById(1L)).thenReturn(1);

        productService.deleteProduct(1L);

        verify(productRepository).deleteProductById(eq(1L));
        verify(loggerService).sendLogInfo(eq("Product-Service"), anyString());
    }

    @Test
    void deleteProduct_WithInvalidId() {
        when(productRepository.deleteProductById(999L)).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () ->
                productService.deleteProduct(999L)
        );
    }
} 