package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomepageController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String homepage(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("loggedInClient");
        if (client != null) {
            model.addAttribute("client", client);
        }
        return "index";  // Find the html template at src/main/resources/templates/index.html
    }

    @GetMapping("/login-register")
    public String signOn() {
        return "login-register";  // Find the html template at src/main/resources/templates/login-register.html
    }

    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String publisher,
            HttpSession session,
            Model model) {

        // Add client information for navbar
        Client client = (Client) session.getAttribute("loggedInClient");
        if (client != null) {
            model.addAttribute("client", client);
        }

        List<Book> books;

        //Go through the filters and return the books.
        //TODO make it cross-check each filter.
        if (isbn != null && !isbn.isEmpty()) {
            books = bookRepository.findByBookISBN(isbn);
        }
        else if (title != null && !title.isEmpty()) {
            books = bookRepository.findByBookTitle(title);
        }
        else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByBookAuthor(author);
        }
        else if (publisher != null && !publisher.isEmpty()) {
            books = bookRepository.findByBookPublisher(publisher);
        }
        else {
            books = (List<Book>) bookRepository.findAll();
        }

        model.addAttribute("books", books);
        return "browse";
    }
}
