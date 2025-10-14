package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String homepage() {
        return "index";  // Find the html template at src/main/resources/templates/index.html
    }
}
