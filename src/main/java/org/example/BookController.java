/**
 * Controller for storing and retrieving books
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import org.hibernate.boot.model.internal.CreateKeySecondPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * GET /books/{id}
     * Returns a JSON Book object based on the ID.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X GET -H "Accept: application/json" http://localhost:8080/books/1
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * {"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":10
     * -------------------------------------------
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBook(@PathVariable(value = "id") Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /books/all
     * Returns a JSON Book object based on the ID.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X GET -H "Accept: application/json" http://localhost:8080/books/all
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * [{"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":10},{"id":2,"bookTitle":"No Country For Old Men","bookISBN":"YYYYY","bookPicture":null,"bookDescription":"This is a scary and kind of sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":10}]
     * -------------------------------------------
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

        // Return all if no filter given
        return books;
    }

    /**
     * POST /books/addBook
     * Creates a new book.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X POST -d '{\"bookTitle\": \"The Road\", \"bookISBN\": \"XXXXX\", \"bookPicture\": null, \"bookDescription\": \"This is a very sad book.\", \"bookAuthor\": \"Cormac McCarthy\", \"bookPublisher\": \"Penguin\", \"numBooksAvailableForPurchase\": 10}'
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     {"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":10}
     * -------------------------------------------
     */
    @PostMapping("/books/addBook")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {

        /// TODO: Add logic to ensure that only an owner client can add books

        Book savedBook = bookRepository.save(book);
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
    @PutMapping(value="/books/{id}/editBook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> editBook(@PathVariable(value = "id") Long id,
                                         @RequestParam("bookISBN") String bookISBN,
                                         @RequestParam("bookTitle") String bookTitle,
                                         @RequestParam("bookAuthor") String bookAuthor,
                                         @RequestParam("bookPublisher") String bookPublisher,
                                         @RequestParam("bookDescription") String bookDescription,
                                         @RequestParam(value = "bookPicture", required = false) MultipartFile bookPicture) {

        /// TODO: Add logic to ensure that only an owner client can edit books

        Optional<Book> editBookOpt = bookRepository.findById(id);

        if(editBookOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Book editBook = editBookOpt.get();
        editBook.setBookTitle(bookTitle);
        editBook.setBookISBN(bookISBN);
        editBook.setBookDescription(bookDescription);
        editBook.setBookAuthor(bookAuthor);
        editBook.setBookPublisher(bookPublisher);

        if (bookPicture != null && !bookPicture.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + bookPicture.getOriginalFilename();
                Path uploadPath = Paths.get("src/main/resources/static/pictures/");
                Files.createDirectories(uploadPath);
                Files.copy(bookPicture.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                editBook.setBookPicture("/pictures/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        bookRepository.save(editBook);
        return ResponseEntity.ok(editBook);
    }

    /**
     * GET /books/{id}/purchaseBook
     * Returns a JSON Book object based on the ID.
     * Reduces the number of copies of the book available for purchase by 1 for each curl request.
     *
     * Example curl command for Windows:
     * -------------------------------------------
     * curl.exe -i -X GET -H "Accept: application/json" http://localhost:8080/books/1/purchaseBook
     * -------------------------------------------
     *
     * Expected JSON response:
     * -------------------------------------------
     * {"id":1,"bookTitle":"The Road","bookISBN":"XXXXX","bookPicture":null,"bookDescription":"This is a very sad book.","bookAuthor":"Cormac McCarthy","bookPublisher":"Penguin","numBooksAvailableForPurchase":9}
     * -------------------------------------------
     */
    @GetMapping("/books/{id}/purchaseBook")
    public ResponseEntity<Book> purchaseBook(@PathVariable(value = "id") Long id) {

        Optional<Book> purchaseBook = bookRepository.findById(id);

        if (purchaseBook.isPresent()) {
            if (purchaseBook.get().getNumBooksAvailableForPurchase() >= 1) {
                purchaseBook.get().setNumBooksAvailableForPurchase(purchaseBook.get().getNumBooksAvailableForPurchase()-1);
            }
        }
         bookRepository.save(purchaseBook.get());

        return ResponseEntity.ok(purchaseBook.get());
    }
}
