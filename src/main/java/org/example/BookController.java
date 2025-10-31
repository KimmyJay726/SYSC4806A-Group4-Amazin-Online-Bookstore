/**
 * Controller for storing and retrieving books
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import org.hibernate.boot.model.internal.CreateKeySecondPass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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
    public ResponseEntity<Book> getBook(@PathVariable(value = "id") Integer id) {
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
    @GetMapping("/books/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = (List<Book>) bookRepository.findAll();
        return ResponseEntity.ok(books);
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
     * @param book a temporary book object whose attributes are used to update the book
     * @return
     *
     */
    @PutMapping("/books/{id}/editBook")
    public ResponseEntity<Book> editBook(@PathVariable(value = "id") Integer id, @RequestBody Book book) {

        /// TODO: Add logic to ensure that only an owner client can edit books

        Optional<Book> editBook = bookRepository.findById(id);

        editBook.ifPresent(value -> value.setBookTitle(book.getBookTitle()));
        editBook.ifPresent(value -> value.setBookISBN(book.getBookISBN()));
        editBook.ifPresent(value -> value.setBookDescription(book.getBookDescription()));
        editBook.ifPresent(value -> value.setBookAuthor(book.getBookAuthor()));
        editBook.ifPresent(value -> value.setBookPublisher(book.getBookPublisher()));

        bookRepository.save(editBook.get());

        return ResponseEntity.ok(editBook.get());
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
    public ResponseEntity<Book> purchaseBook(@PathVariable(value = "id") Integer id) {

        Optional<Book> purchaseBook = bookRepository.findById(id);

        purchaseBook.ifPresent(value -> value.setNumBooksAvailableForPurchase(value.getNumBooksAvailableForPurchase()-1));

         bookRepository.save(purchaseBook.get());

        return ResponseEntity.ok(purchaseBook.get());
    }
}
