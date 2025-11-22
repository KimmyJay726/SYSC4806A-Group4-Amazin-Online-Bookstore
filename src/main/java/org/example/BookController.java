/**
 * Controller for storing and retrieving books
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import jakarta.servlet.http.HttpSession;
import org.hibernate.boot.model.internal.CreateKeySecondPass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository bookRepository;

    /**
     * GET /books/all
     * Returns a JSON list of all books.
     */
    @GetMapping("/books/all")
    @ResponseBody
    public List<Book> getAllBooks() {

        return (List<Book>) bookRepository.findAll();
    }

    /**
     * GET /books/{id}
     * Returns a JSON Book object based on the ID.
     */
    @GetMapping("/books/{id}")
    @ResponseBody
    public Book getBook(@PathVariable Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getUpload(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: Failed to read file: {}", filename);
        }

        logger.info("Uploaded file: " + filename);
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /inventory/update
     * Returns a JSON list of books based on filters.
     */
    @GetMapping("/inventory/update")
    @ResponseBody
    public List<Book> searchBooks(@RequestParam(required = false) String isbn,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String author,
                                  @RequestParam(required = false) String publisher) {

        List<Book> books = null;

        //Go through the filters and return the books.
        //TODO make it cross-check each filter.
        if (isbn != null && !isbn.isEmpty()) {
            logger.info("Finding books by isbn: " + isbn);
            books = bookRepository.findByBookISBN(isbn);
        }
        else if (title != null && !title.isEmpty()) {
            logger.info("Finding books by title: " + title);
            books = bookRepository.findByBookTitle(title);
        }
        else if (author != null && !author.isEmpty()) {
            logger.info("Finding books by author: " + author);
            books = bookRepository.findByBookAuthor(author);
        }
        else if (publisher != null && !publisher.isEmpty()) {
            logger.info("Finding books by publisher: " + publisher);
            books = bookRepository.findByBookPublisher(publisher);
        }
        else {
            logger.info("Getting all books");
            books = (List<Book>) bookRepository.findAll();
        }

        // Return all if no filter given
        return books;
    }

    /**
     * GET /addBook
     * Returns the add book page. Only accessible to owners.
     */
    @GetMapping("/addBook")
    public String addBookPage(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("loggedInClient");
        
        // Check if user is logged in and is an owner
        if (client == null || !Boolean.TRUE.equals(client.getIsOwner())) {
            // Redirect to login page if not logged in or not an owner
            return "redirect:/login-register?error=unauthorized";
        }
        
        model.addAttribute("client", client);
        return "addBook";
    }

    /**
     * POST /books/addBook
     * Creates a new book.
     */
    @PostMapping("/books/addBook")
    public ResponseEntity<Book> addBook(@ModelAttribute Book book, @RequestParam(value = "file", required = false) MultipartFile file, HttpSession session) {
        Client client = (Client) session.getAttribute("loggedInClient");
        if (client == null || !Boolean.TRUE.equals(client.getIsOwner())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (file != null && !file.isEmpty()) {
            try {
                // Save file to uploads/
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
                book.setBookPicture("/uploads/" + fileName);
            } catch (IOException e) {
                logger.error("ERROR: Failed to read file.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        Book savedBook = bookRepository.save(book);
        logger.info("Book added: {} by user: {} ", book.getBookTitle(), client.getUsername());
        return ResponseEntity.ok(savedBook);
    }

    /**
     * PUT /books/{id}/editBook
     * Updates book attributes.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X PUT "http://localhost:8080/books/1/editBook" -H "Content-Type: application/json" -d '{\"bookTitle\": \"The Long Walk South\", \"bookISBN\": \"WWWWW\", \"bookPicture\": null, \"bookDescription\": \"This is a very, very, very sad book.\", \"bookAuthor\": \"Charlie McCarthy\", \"bookPublisher\": \"Random House\", \"numBooksAvailableForPurchase\": 10}'
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * {"id":1,"bookTitle":"The Long Walk South","bookISBN":"WWWWW","bookPicture":null,"bookDescription":"This is a very, very, very sad book.","bookAuthor":"Charlie McCarthy","bookPublisher":"Random House","numBooksAvailableForPurchase":10}
     * -------------------------------------------
     *
     * @param id the id of the book to be updated
     * @return
     *
     */
    @PostMapping(value = "/books/{id}/editBook")
    public ResponseEntity<Book> editBook(
            @PathVariable Long id,
            @ModelAttribute BookForm form,
            HttpSession session) {

        //Check if client is owner
        Client client = (Client) session.getAttribute("loggedInClient");
        if(client == null) {
            logger.error("ERROR: User is not signed in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!Boolean.TRUE.equals(client.getIsOwner())) {
            logger.error("ERROR: User {} is not the owner.", client.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //Find the book in the repository
        Optional<Book> editBookOpt = Optional.ofNullable(bookRepository.findById(id));
        if (editBookOpt.isEmpty()) {
            logger.error("ERROR: Book with id: {} not found", id);
            return ResponseEntity.notFound().build();
        }

        //Edit all the details that the user wanted.
        Book editBook = editBookOpt.get();
        editBook.setBookISBN(form.getBookISBN());
        editBook.setBookTitle(form.getBookTitle());
        editBook.setBookAuthor(form.getBookAuthor());
        editBook.setBookPublisher(form.getBookPublisher());
        editBook.setBookPrice(form.getBookPrice());
        editBook.setNumBooksAvailableForPurchase(form.getNumBooksAvailableForPurchase());
        editBook.setBookDescription(form.getBookDescription());

        //Handle image files as multipart files
        MultipartFile file = form.getBookPicture();
        if (file != null && !file.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
                editBook.setBookPicture("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        bookRepository.save(editBook);
        logger.info("Book edited: {} by user: {} ", editBook.getBookTitle(), client.getUsername());
        return ResponseEntity.ok(editBook);
    }

    /**
     * POST /books/{id}/purchaseBook
     * Returns a JSON Book object based on the ID.
     * Reduces the number of copies of the book available for purchase by 1 for each curl request.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X POST -H "Accept: application/json" http://localhost:8080/books/1/purchaseBook
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * {"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":9}
     * -------------------------------------------
     */
    @PostMapping("/books/{id}/purchaseBook")
    public ResponseEntity<Book> purchaseBook(@PathVariable(value = "id") Integer id) {

        Optional<Book> purchaseBook = bookRepository.findById(id);

        if (purchaseBook.isPresent()) {
            if (purchaseBook.get().getNumBooksAvailableForPurchase() >= 1) {
                logger.info("Book {} available for purchase. There are currently {} copies",
                        purchaseBook.get().getBookTitle(), purchaseBook.get().getNumBooksAvailableForPurchase());

                purchaseBook.get().setNumBooksAvailableForPurchase(purchaseBook.get().getNumBooksAvailableForPurchase()-1);
                bookRepository.save(purchaseBook.get());

                return ResponseEntity.ok(purchaseBook.get());
            }
            else{
                logger.error("ERROR: Book {} not available for purchase. There are currently {} copies",
                        purchaseBook.get().getBookTitle(), purchaseBook.get().getNumBooksAvailableForPurchase());

                return ResponseEntity.badRequest().build();
            }
        }
        else{
            logger.error("ERROR: Book not Found.");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /books/{id}/returnBook
     * Returns a JSON Book object based on the ID.
     * Increases the number of copies of the book available for purchase by 1 for each curl request.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X POST -H "Accept: application/json" http://localhost:8080/books/1/returnBook
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * {"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":9}
     * -------------------------------------------
     */
    @PostMapping("/books/{id}/returnBook")
    public ResponseEntity<Book> returnBook(@PathVariable(value = "id") Integer id) {
        Optional<Book> currentBook = bookRepository.findById(id);

        int numBooks = currentBook.get().getNumBooksAvailableForPurchase() + 1;
        currentBook.get().setNumBooksAvailableForPurchase(numBooks);
        bookRepository.save(currentBook.get());
        logger.info("Book {} returned. There are now {} copies available for purchase",
                currentBook.get().getBookTitle(), numBooks);

        return ResponseEntity.ok(currentBook.get());
    }
}
