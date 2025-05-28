package com.beauty.front.auth.controller;


import com.beauty.front.auth.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpServletRequest request) {
        ResponseEntity<String> result = loginService.login(email, password);
        if(result.getStatusCode().is2xxSuccessful()) {
            HttpSession session = request.getSession();
            String authorizationHeader = result.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null) {
                session.setAttribute("Authorization", authorizationHeader);
                session.setAttribute("UserName", email);
                return "redirect:/products/catalog";
            }
        }
        model.addAttribute("error", "Неверный логин или пароль");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        loginService.logout();
        request.getSession().invalidate();
        return "redirect:/products/catalog";
    }
}

