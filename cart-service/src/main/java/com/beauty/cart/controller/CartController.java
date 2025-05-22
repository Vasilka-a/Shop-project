package com.beauty.cart.controller;

import com.beauty.cart.dto.request.ItemRequest;
import com.beauty.cart.dto.response.ItemResponse;
import com.beauty.cart.kafka.producer.KafkaLogProducer;
import com.beauty.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final KafkaLogProducer loggerService;

    public CartController(CartService cartService, KafkaLogProducer loggerService) {
        this.cartService = cartService;
        this.loggerService = loggerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemResponse>> getAllItems(@RequestParam String email) {
        List<ItemResponse> products = cartService.getAllItems(email);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Received goods from the user's email %s cart: %s ", email, products));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestBody ItemRequest item, @RequestParam String email) {
        cartService.addToCart(item, email);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Added the item to the user's email %s cart: %s ", email, item));
        return ResponseEntity.status(HttpStatus.CREATED).body("Success added");
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteItem(@RequestParam Long id, @RequestParam int quantity) {
        cartService.deleteItem(id, quantity);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Success deleted item by id: %d from the cart", id));
        return ResponseEntity.ok("Success deleted");
    }

    @GetMapping("/buy")
    public ResponseEntity<ItemResponse> buyItem(@RequestParam Long id, @RequestParam int quantity) {
        ItemResponse item = cartService.buyItem(id, quantity);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Success bought item by id: %d", id));
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping("/buy-all")
    public ResponseEntity<List<ItemResponse>> buyAllItem(@RequestParam String email) {
        List<ItemResponse> items = cartService.buyAllItem(email);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Success bought all items by email: %s", email));
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam Long id, @RequestParam String action) {
        String response = cartService.updateItemQuantity(id, action);

        loggerService.sendLogInfo("Cart-Service",
                String.format("Success update item by id: %d", id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
