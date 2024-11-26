package vaultmaster.com.vault.controller;

import org.springframework.ui.Model;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/login")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        User user = userService.login(email, password);

        if (user != null && user.isPresent()) {
            model.addAttribute("message", "Welcome, " + user.getFullName() + "!");
            return "welcome"; // Redirect to a "welcome.html" page (you can create this file).
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login"; // Redirect back to the login page with an error.
        }
    }

    @GetMapping("/info")
    public User getUserInfo(@RequestParam String email) {
        return userService.login(email, "mockPasswordHash"); // Adjust for testing or dynamic logic
    }
}
