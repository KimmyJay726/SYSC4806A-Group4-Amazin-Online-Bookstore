/**
 * Repository for storing and retrieving books
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Integer> {
    Book findById(@Param("id") Long id);
    List<Book> findByBookTitle(@Param("bookTitle") String bookTitle);
    List<Book> findByBookISBN(@Param("bookISBN") String bookISBN);
    List<Book> findByBookAuthor(@Param("bookAuthor") String bookAuthor);
    List<Book> findByBookPublisher(@Param("bookPublisher") String bookPublisher);
}
