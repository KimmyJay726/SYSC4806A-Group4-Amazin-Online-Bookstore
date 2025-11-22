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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
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

    @GetMapping("/checkout")
    public String viewCheckout(HttpSession session, Model model) {

        Client client = (Client) session.getAttribute("loggedInClient");

        if (client == null) {
            return "redirect:/login-register";
        }


        Optional<Client> refreshedClientOpt = clientRepository.findById((int) client.getId());
        if (refreshedClientOpt.isEmpty()) {
            return "redirect:/login-register";
        }

        Client refreshedClient = refreshedClientOpt.get();
        List<Long> cartBookIds = refreshedClient.getShoppingCart();

        List<Book> cartItems = new ArrayList<>();
        double subtotal = 0.0;

        for (Long bookId : cartBookIds) {

            Integer bookIdInt = bookId.intValue();

            Optional<Book> bookOpt = bookRepository.findById(bookIdInt); // Use the Integer ID

            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                cartItems.add(book);
                subtotal += book.getBookPrice();
            }
        }

        final double SHIPPING_COST = 5.00;
        final double TAX_RATE = 0.07;

        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax + SHIPPING_COST;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingCost", SHIPPING_COST);
        model.addAttribute("tax", tax);
        model.addAttribute("orderTotal", total);
        model.addAttribute("client", refreshedClient);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam String fullName,
            @RequestParam String cardNumber, // Captures the credit card number from the form
            // Add other form fields here: @RequestParam String expiry, @RequestParam String cvv,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Client client = (Client) session.getAttribute("loggedInClient");

        if (client == null) {
            return "redirect:/login-register";
        }

        //SIMULATE PAYMENT PROCESSING

        boolean paymentSuccessful = true;

        if (paymentSuccessful) {

            for (Long bookId : client.getShoppingCart()) {

                Integer numAvailableCopies = bookRepository.findById(bookId).getNumBooksAvailableForPurchase();
                Book purchasedBook = bookRepository.findById(bookId);

                // Add the ID of the copy of the book to the client's purchasedBookIds
                client.addToPurchasedBooks(bookId);

                // Decrement the number of copies of the book in the inventory by 1
                purchasedBook.setNumBooksAvailableForPurchase(numAvailableCopies);
                bookRepository.save(purchasedBook);
            }

            // Clear the cart on the Client object (requires the clearShoppingCart() method in Client.java)
            client.clearShoppingCart();

            // Save the updated Client object (with the empty cart) to the database
            clientRepository.save(client);

            //Update the session with the newly saved client object
            session.setAttribute("loggedInClient", client);

            // Redirect back to the GET /checkout with a 'success' parameter
            // The GET method will see this parameter and display the "Payment Complete" feedback.
            redirectAttributes.addAttribute("success", "true");

            return "redirect:/checkout";
        } else {
            // Handle failed payment (e.g., redirect with an error message)
            redirectAttributes.addAttribute("error", "Payment failed. Please try again.");
            return "redirect:/checkout";
        }
    }
}
