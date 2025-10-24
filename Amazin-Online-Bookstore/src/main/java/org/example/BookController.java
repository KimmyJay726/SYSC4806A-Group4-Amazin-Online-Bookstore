/**
 * Controller for storing and retrieving books
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/books/{id}")
    public String getBook(@PathVariable(value = "id") long id, Model model) {
        Book book = bookRepository.findById(id);
        model.addAttribute("bookISBN", book.getBookISBN());
        model.addAttribute("bookPicture", book.getBookPicture());
        model.addAttribute("bookDescription", book.getBookDescription());
        model.addAttribute("bookAuthor", book.getBookAuthor());
        model.addAttribute("bookPublisher", book.getBookPublisher());

        return "book";
    }

    /// TODO: Add methods for putting and posting
}
