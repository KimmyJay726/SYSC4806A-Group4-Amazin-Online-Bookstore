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

import java.util.*;

@Controller
public class HomepageController {

    private static final Map<String, List<String>> VALID_TEST_CARDS;

    static {
        Map<String, List<String>> aMap = new HashMap<>();

        aMap.put("1111222233334444", Arrays.asList("12/26", "444"));

        aMap.put("5555666677778888", Arrays.asList("08/27", "888"));

        aMap.put("0000111100001111", Arrays.asList("06/28", "111"));

        VALID_TEST_CARDS = Collections.unmodifiableMap(aMap);
    }

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

        Client client = (Client) session.getAttribute("loggedInClient");

        // Check if user is not logged in or is not an owner
        if (client == null || Boolean.TRUE.equals(client.getIsOwner())) {
            // Redirect to login page if not logged in or not an owner
            return "redirect:/login-register?error=unauthorized";
        }

        // Add client information for navbar
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
    public String editBook(@RequestParam Integer id, Model model, HttpSession session) {
        Client client = (Client) session.getAttribute("loggedInClient");

        // Check if user is logged in and is an owner
        if (client == null || !Boolean.TRUE.equals(client.getIsOwner())) {
            // Redirect to login page if not logged in or not an owner
            return "redirect:/login-register?error=unauthorized";
        }

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

        // Use Long for findById
        Optional<Client> refreshedClientOpt = clientRepository.findById((int) client.getId());
        if (refreshedClientOpt.isEmpty()) {
            return "redirect:/login-register";
        }

        Client refreshedClient = refreshedClientOpt.get();
        List<Long> cartBookIds = refreshedClient.getShoppingCart();

        // 1. Count occurrences of each book ID
        Map<Long, Integer> bookIdCounts = new HashMap<>();
        for (Long bookId : cartBookIds) {
            bookIdCounts.put(bookId, bookIdCounts.getOrDefault(bookId, 0) + 1);
        }

        // 2. Create list of CartItemView DTOs
        List<CartItemView> cartItemsView = new ArrayList<>();
        double subtotal = 0.0;

        for (Map.Entry<Long, Integer> entry : bookIdCounts.entrySet()) {
            Long bookId = entry.getKey();
            int quantity = entry.getValue();

            // Use Long for findById
            Optional<Book> bookOpt = Optional.ofNullable(bookRepository.findById(bookId));

            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                CartItemView cartItem = new CartItemView(book, quantity);
                cartItemsView.add(cartItem);
                subtotal += cartItem.getLineTotal();
            }
        }
        // === END: Grouping Logic ===

        final double SHIPPING_COST = subtotal > 0 ? 5.00 : 0.00; // Only charge shipping if there are items
        final double TAX_RATE = 0.07;

        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax + SHIPPING_COST;

        // Pass the grouped list to the model
        model.addAttribute("cartItems", cartItemsView);
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
            @RequestParam String cardNumber,
            @RequestParam String expiry,
            @RequestParam String cvv,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Client client = (Client) session.getAttribute("loggedInClient");

        if (client == null) {
            return "redirect:/login-register";
        }

        //SIMULATE PAYMENT PROCESSING

        String cleanedCardNumber = cardNumber.replaceAll("\\s", "");
        boolean paymentSuccessful = VALID_TEST_CARDS.containsKey(cleanedCardNumber);

        if (paymentSuccessful) {
            List<String> cardDetails = VALID_TEST_CARDS.get(cleanedCardNumber);
            String validExpiry = cardDetails.get(0);
            String validCvv = cardDetails.get(1);

            // If the expiry or CVV do not match the expected test values, fail the payment.
            if (!validExpiry.equals(expiry) || !validCvv.equals(cvv)) {
                paymentSuccessful = false;
            }
        }

        if (paymentSuccessful) {

            for (Long bookId : client.getShoppingCart()) {

                Integer numAvailableCopies = bookRepository.findById(bookId).getNumBooksAvailableForPurchase();
                Book purchasedBook = bookRepository.findById(bookId);

                // Add the ID of the copy of the book to the client's purchasedBookIds
                client.addToPurchasedBooks(bookId);

                // Decrement the number of copies of the book in the inventory by 1
                purchasedBook.setNumBooksAvailableForPurchase(numAvailableCopies - 1);
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
