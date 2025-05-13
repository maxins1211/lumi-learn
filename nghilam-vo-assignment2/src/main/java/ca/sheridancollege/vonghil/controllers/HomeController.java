package ca.sheridancollege.vonghil.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.sheridancollege.vonghil.beans.User;
import ca.sheridancollege.vonghil.database.DatabaseAccess;
import ca.sheridancollege.vonghil.security.UserService;

@Controller
public class HomeController {

	@Autowired
	UserService userService;
    @Autowired
    private DatabaseAccess da;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @GetMapping("/")
    public String index() {
        return "index"; 
    }
   
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }
    
    @GetMapping("/signup")
    public String getRegisterPage(@ModelAttribute User user) {
        return "signup"; 
    }
    
    @PostMapping("/signup")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (da.emailExists(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists. Please use a different email or try to login.");
            return "signup";
        }
        user.setEncryptedPassword(passwordEncoder.encode(user.getEncryptedPassword()));
        Long userId = da.saveUser(user);
        System.out.println("New user ID: " + userId);
        da.addRole(userId, "ROLE_USER");
        model.addAttribute("successMessage", "Registration successful! Please login.");
        return "login";
    }

    @GetMapping("/permission-denied")
    public String permissionDenied() {
        return "permission-denied";
    }
}
