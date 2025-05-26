package com.beauty.cart.service;

import com.beauty.cart.dto.request.ItemRequest;
import com.beauty.cart.dto.response.ItemResponse;
import com.beauty.cart.entity.Item;
import com.beauty.cart.entity.User;
import com.beauty.cart.exception.ProductNotFoundException;
import com.beauty.cart.kafka.producer.KafkaOrderQuantityProducer;
import com.beauty.cart.repository.CartRepository;
import com.beauty.cart.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final KafkaOrderQuantityProducer kafkaOrderQuantityProducer;

    public CartService(CartRepository cartRepository, UserRepository userRepository, KafkaOrderQuantityProducer kafkaOrderQuantityProducer) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.kafkaOrderQuantityProducer = kafkaOrderQuantityProducer;
    }

    public List<ItemResponse> getAllItems(String email) {
        User user = checkUser(email);

        List<Item> items = cartRepository.getAllItems(user.getEmail());
        return items.stream()
                .map(item -> new ItemResponse(
                        item.getId(),
                        item.getProductCode(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getProductImage()
                ))
                .collect(Collectors.toList());
    }

    public void addToCart(ItemRequest item, String email) {
        User user = checkUser(email);

        Long id = cartRepository.getIdByCode(item.getProductCode());
        if (id != null) {
            updateItemQuantity(id, "increase");
        } else {
            Item newProduct = Item.builder()
                    .productName(item.getProductName())
                    .productCode(item.getProductCode())
                    .productImage(item.getProductImage())
                    .price(item.getPrice())
                    .quantity(1)
                    .user(user)
                    .build();
            cartRepository.save(newProduct);
        }
    }

    public void deleteItem(Long id, int quantity) {
        Item item = getItemById(id);
        int currentQuantity = item.getQuantity();

        if (currentQuantity <= quantity) {
            // Если запрошенное количество больше или равно текущему, удаляем товар полностью
            int deleted = cartRepository.deleteItemById(id);
            if (deleted == 0) {
                throw new ProductNotFoundException(
                    String.format("Failed to delete product with id: %d", id));
            }
        } else {
            // Иначе уменьшаем количество
            int newQuantity = currentQuantity - quantity;
            int updated = cartRepository.updateItemById(id, newQuantity);
            if (updated == 0) {
                throw new ProductNotFoundException(
                    String.format("Failed to update quantity for product with id: %d", id));
            }
        }
    }

    public ItemResponse buyItem(Long id, int quantity) {
        Item item = getItemById(id);

        deleteItem(id, quantity);
        kafkaOrderQuantityProducer.sendMessage(item.getProductCode(), quantity);
        return new ItemResponse(item.getId(),
                item.getProductCode(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity(),
                item.getProductImage());
    }

    public List<ItemResponse> buyAllItem(String email) {
        List<Item> cartItems = cartRepository.getAllItems(email);

        if (!cartItems.isEmpty()) {
            for (Item item : cartItems) {
                kafkaOrderQuantityProducer.sendMessage(item.getProductCode(), item.getQuantity());
            }
            cartRepository.deleteAll(cartItems);
        }
        return cartItems.stream()
                .map(item -> new ItemResponse(
                        item.getId(),
                        item.getProductCode(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getProductImage()
                ))
                .collect(Collectors.toList());
    }

    public Item getItemById(Long id) {
        return cartRepository.getItemById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("The product by id: %d was not found", id)));
    }

    public String updateItemQuantity(Long id, String action) {
        Item item = getItemById(id);

        int quantity = item.getQuantity();
        if ("increase".equals(action)) {
            int availableQuantity = checkStoreForQuantity(item.getProductCode());
            if (availableQuantity < (quantity + 1)) {
                return "Запрошенное количество товара недоступно";
            }
            item.setQuantity(quantity + 1);
        } else if ("decrease".equals(action) && quantity > 1) {
            item.setQuantity(quantity - 1);
        }
        if (cartRepository.updateItemById(item.getId(), item.getQuantity()) == 0) {
            throw new ProductNotFoundException(String.format("The product by id: %d was not update", id));
        }
        return null;
    }

    public int checkStoreForQuantity(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://product-service:8082/api/products/check-store?code=" + code;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, entity, Integer.class);
        return response.getBody();
    }

    public User checkUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email.trim())
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
