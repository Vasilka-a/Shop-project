package com.beauty.cart.service;

import com.beauty.cart.dto.request.ItemRequest;
import com.beauty.cart.dto.response.ItemResponse;
import com.beauty.cart.entity.Item;
import com.beauty.cart.entity.User;
import com.beauty.cart.exception.ProductNotFoundException;
import com.beauty.cart.kafka.producer.KafkaOrderQuantityProducer;
import com.beauty.cart.repository.CartRepository;
import com.beauty.cart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KafkaOrderQuantityProducer kafkaProducer;


    @InjectMocks
    private CartService cartService;

    private User user;
    private Item testItem;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().email("user@example.com").build();

        testItem = Item.builder()
                .id(1L)
                .productCode("TEST_OO1")
                .productName("Product Test")
                .price(new BigDecimal("100.00"))
                .quantity(5)
                .user(user)
                .productImage("image.jpg")
                .build();

        itemRequest = new ItemRequest("TEST_OO1",
                "Product Test", new BigDecimal("100.00"),
                1, "image.jpg");
    }

    @Test
    void getAllItems() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.getAllItems(user.getEmail())).thenReturn(List.of(testItem));

        var result = cartService.getAllItems(user.getEmail());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductCode()).isEqualTo("TEST_OO1");
    }

    @Test
    void addToCartNewItem() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
        when(cartRepository.getIdByCode(itemRequest.getProductCode())).thenReturn(null);
        when(cartRepository.save(any(Item.class))).thenReturn(testItem);

        cartService.addToCart(itemRequest, user.getEmail());

        verify(cartRepository).save(any(Item.class));
        verify(cartRepository, never()).updateItemById(anyLong(), anyInt());
    }

    @Test
    void deleteItemWhenQuantityEquals() {
        when(cartRepository.getItemById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.deleteItemById(1L)).thenReturn(1);

        cartService.deleteItem(1L, 5);

        verify(cartRepository).deleteItemById(1L);
        verify(cartRepository, never()).updateItemById(anyLong(), anyInt());
    }

    @Test
    void deleteItemWhenQuantityLess() {
        when(cartRepository.getItemById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.updateItemById(1L, 3)).thenReturn(1);

        cartService.deleteItem(1L, 2);

        verify(cartRepository).updateItemById(1L, 3);
        verify(cartRepository, never()).deleteItemById(anyLong());
    }

    @Test
    void deleteItemWhenDeleteFails() {
        when(cartRepository.getItemById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.deleteItemById(1L)).thenReturn(0);

        assertThatThrownBy(() -> cartService.deleteItem(1L, 5))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Failed to delete product with id: 1");

        verify(cartRepository).deleteItemById(1L);
    }

    @Test
    void checkUser_shouldReturnExistingUser() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = cartService.checkUser(user.getEmail());

        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void checkUser_shouldCreateUserIfNotFound() {
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = cartService.checkUser(user.getEmail());

        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void checkUser_shouldThrowIfEmailInvalid() {
        assertThatThrownBy(() -> cartService.checkUser(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buyItemSuccessfully() {
        when(cartRepository.getItemById(1L)).thenReturn(Optional.of(testItem));
        when(cartRepository.deleteItemById(1L)).thenReturn(1);

        ItemResponse response = cartService.buyItem(1L, 5);

        assertThat(response).isNotNull();
        assertThat(response.getProductCode()).isEqualTo("TEST_OO1");

        verify(cartRepository).deleteItemById(1L);
        verify(kafkaProducer).sendMessage("TEST_OO1", 5);
    }
}
