package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomepageController {

    @Autowired
    private Jaccard jaccard;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ClientRepository clientRepository;

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
            model.addAttribute("username", client.getUsername());
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

    @GetMapping("/recommendations")
    public String showRecommendations(
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
            model.addAttribute("username", client.getUsername());

            List<Long> recommendedBookIds = jaccard.recommendedBooks(client);
            List<Book> recommendedBooks = new ArrayList<>();

            for (Long bookId : recommendedBookIds) {
                recommendedBooks.add(bookRepository.findById(bookId));
            }
            model.addAttribute("books", recommendedBooks);
        }

        else {
            List<Book> recommendedBooks = (List<Book>) bookRepository.findAll();
            model.addAttribute("books", recommendedBooks);
        }

        return "recommendations";
    }

    @GetMapping("/inventory/edit")
    public String editBook(@RequestParam Integer id, Model model) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isPresent()) {
            model.addAttribute("book", bookOpt.get());
            return "editBook";
        } else {
            model.addAttribute("error", "Book not found");
            return "browse";
        }
    }

    @GetMapping("/purchase")
    public String purchase() {
        return "purchase";  // Find the html template at src/main/resources/templates/purchase.html
    }
}
