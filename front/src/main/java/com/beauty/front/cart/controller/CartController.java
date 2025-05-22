package com.beauty.front.cart.controller;

import com.beauty.front.cart.service.CartService;
import com.beauty.front.model.ItemRequest;
import com.beauty.front.model.ItemResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String showCartForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("UserName");
        String token = extractTokenFromSession(request);

        model.addAttribute("cartItems", cartService.getAllItems(token, email));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@ModelAttribute ItemRequest item, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("UserName");
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        cartService.addItemToCart(item, token, email);
        return "redirect:/products/catalog";
    }

    @PostMapping("/delete")
    public String deleteItemFromCart(@RequestParam("id") Long id, @RequestParam("quantity") int quantity,
                                     HttpServletRequest request) {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        cartService.deleteProduct(id, token, quantity);
        return "redirect:/cart";
    }


    @PostMapping("/buy")
    public String buyItem(@RequestParam("id") Long id, @RequestParam("quantity") int quantity, HttpServletRequest request, Model model) {
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        ItemResponse result = cartService.buyProduct(id, token, quantity);

        model.addAttribute("items", result);
        model.addAttribute("totalAmount", result.getPrice());
        return "purchase-confirmation";
    }

    @PostMapping("/update-quantity")
    public String updateQuantity(@RequestParam Long id, @RequestParam String action, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("UserName");
        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        String message = cartService.updateQuantity(id, action, token);

        if (message != null) {
            model.addAttribute("cartItems", cartService.getAllItems(token, email));
            model.addAttribute("error", message);
            return "cart";
        }

        return "redirect:/cart";
    }

    @PostMapping("/buy-all")
    public String buyAllItems(HttpServletRequest request, Model model) {

        String token = extractTokenFromSession(request);
        if (token == null) {
            return "redirect:/login";
        }
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("UserName");
        List<ItemResponse> result = cartService.buyAllProduct(email, token);
        BigDecimal totalAmount = result.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", result);
        model.addAttribute("totalAmount", totalAmount);

        return "purchase-confirmation";
    }


    private String extractTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String tokenFromSession = (String) session.getAttribute("Authorization");
        if (tokenFromSession == null) {
            return null;
        }
        return tokenFromSession.substring(7);
    }

}
