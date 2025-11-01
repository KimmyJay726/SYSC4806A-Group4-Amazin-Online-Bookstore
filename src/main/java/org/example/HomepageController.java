package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String homepage() {
        return "index";  // Find the html template at src/main/resources/templates/index.html
    }

    @GetMapping("/login-register")
    public String signOn() {
        return "login-register";  // Find the html template at src/main/resources/templates/login-register.html
    }

    @GetMapping("/inventory/edit")
    public String editBookDetails() {
        return "editBook";
    }
}
