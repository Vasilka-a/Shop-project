package com.beauty.front.product.service;

import com.beauty.front.model.RequestProduct;
import com.beauty.front.model.ResponseProduct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final RestTemplate restTemplate;
    private final String gatewayServiceUrl;

    public ProductService(RestTemplate restTemplate,
                          @Value("${service.api-gateway.url}") String gatewayServiceUrl) {
        this.restTemplate = restTemplate;
        this.gatewayServiceUrl = gatewayServiceUrl;
    }

    public List<ResponseProduct> getAllProducts(String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseProduct[] products = restTemplate.exchange(
                gatewayServiceUrl + "/api/products/all",
                HttpMethod.GET,
                entity,
                ResponseProduct[].class
        ).getBody();

        return Arrays.asList(products != null ? products : new ResponseProduct[0]);
    }

    public List<ResponseProduct> getAllProducts() {
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseProduct[] products = restTemplate.exchange(
                gatewayServiceUrl + "/api/products/catalog",
                HttpMethod.GET,
                entity,
                ResponseProduct[].class
        ).getBody();

        return Arrays.asList(products != null ? products : new ResponseProduct[0]);
    }

    public void createProduct(RequestProduct requestProduct, String token) {
        String url = gatewayServiceUrl + "/api/products";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = Map.of(
                "productName", requestProduct.getProductName(),
                "productCode", requestProduct.getProductCode(),
                "description", requestProduct.getDescription(),
                "price", requestProduct.getPrice(),
                "quantity", requestProduct.getQuantity(),
                "productImage", requestProduct.getProductImage());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, requestEntity, String.class);
    }

    public void deleteProduct(Long id, String token) {
        String url = gatewayServiceUrl + "/api/products?id=" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    public void changeProductPrice(Long id, String newPrice, String token) {
        String url = gatewayServiceUrl + "/api/products/price?id=" + id + "&newPrice=" + newPrice;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    public void changeProductQuantity(Long id, int newQuantity, String token) {
        String url = gatewayServiceUrl + "/api/products/quantity?id=" + id + "&newQuantity=" + newQuantity;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

}
