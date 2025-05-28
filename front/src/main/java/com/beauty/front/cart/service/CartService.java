package com.beauty.front.cart.service;

import com.beauty.front.model.ItemRequest;
import com.beauty.front.model.ItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class CartService {
    private final RestTemplate restTemplate;
    private final String gatewayServiceUrl;

    public CartService(RestTemplate restTemplate,
                       @Value("${service.api-gateway.url}") String cartServiceUrl) {
        this.restTemplate = restTemplate;
        this.gatewayServiceUrl = cartServiceUrl;
    }

    public String addItemToCart(ItemRequest item, String token, String email) {
        String url = gatewayServiceUrl + "/api/cart/add?email=" + email;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = Map.of(
                "productCode", item.getProductCode(),
                "productName", item.getProductName(),
                "productImage", item.getProductImage(),
                "price", item.getPrice());

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
       ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
       return response.getBody();
    }

    public List<ItemResponse> getAllItems(String token, String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ItemResponse[] products = restTemplate.exchange(
                gatewayServiceUrl + "/api/cart/all?email=" + email,
                HttpMethod.GET,
                request,
                ItemResponse[].class
        ).getBody();
        return Arrays.asList(products != null ? products : new ItemResponse[0]);
    }

    public void deleteProduct(Long id, String token, int quantity) {
        String url = gatewayServiceUrl + "/api/cart?id=" + id + "&quantity=" + quantity;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    public ItemResponse buyProduct(Long id, String token, int quantity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                gatewayServiceUrl + "/api/cart/buy?id=" + id + "&quantity=" + quantity,
                HttpMethod.GET,
                entity,
                ItemResponse.class
        ).getBody();
    }

    public List<ItemResponse> buyAllProduct(String email, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ItemResponse[] items = restTemplate.exchange(
                gatewayServiceUrl + "/api/cart/buy-all?email=" + email,
                HttpMethod.GET,
                entity,
                ItemResponse[].class
        ).getBody();
        return Arrays.asList(items != null ? items : new ItemResponse[0]);
    }

    public String updateQuantity(Long id, String action, String token) {
        String url = gatewayServiceUrl + "/api/cart/update-quantity?id=" + id + "&action=" + action;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}


