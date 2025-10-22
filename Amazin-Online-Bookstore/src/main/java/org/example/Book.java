/**
 * Book class
 * Created By: Jake Siushansian
 * Date Created: October 21, 2025
 */

package org.example;

import jakarta.persistence.*;

import java.io.File;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String bookISBN;
    private File bookPicture;
    private String bookDescription;
    private String bookAuthor;
    private String bookPublisher;

    /**
     * Create Book object
     */
    protected Book() {}

    /**
     * Create a book object
     * @param ISBN the ISBN of the book
     * @param picture the picture of the book on the website
     * @param description the description of the book on the website
     * @param author the author of the book
     * @param publisher the publisher of the book
     */
    public Book(String ISBN, File picture, String description, String author, String publisher) {
        bookISBN = ISBN;
        bookPicture = picture;
        bookDescription = description;
        bookAuthor = author;
        bookPublisher = publisher;
    }

    /// TODO: Add attribute Inventory bookInventory to class
    /// this will connect instances of Book to instances of Inventory

    /**
     * @return the unique id of a book
     */
    public long getId() { return id; }

    /**
     * @return the ISBN of a book
     */
    public String getBookISBN() { return this.bookISBN; }


    public File getBookPicture() { return this.bookPicture; }
    /**
     * @return the description assigned to a book
     */
    public String getBookDescription() { return this.bookDescription; }

    /**
     * @return the author of a book
     */
    public String getBookAuthor() { return this.bookAuthor;}

    /**
     * @return the publisher of a book
     */
    public String getBookPublisher() { return this.bookPublisher; }

    /**
     * Set a book's id manually
     * @param id the unique identifier of a book
     */
    public void setId(long id) { this.id = id; }

    /**
     * Set a book's ISBN
     * @param ISBN the ISBN of a book
     */
    public void setBookISBN(String ISBN) { this.bookISBN = ISBN; }

    /**
     * Set a book's picture
     * @param picture the picture assigned to a book
     */
    public void setBookPicture(File picture) { this.bookPicture = picture; }

    /**
     * Set a book's descriptions
     * @param description the description assigned to a book
     */
    public void setBookDescription(String description) { this.bookDescription = description; }

    /**
     * Set a book's author
     * @param author the author of a book
     */
    public void setBookAuthor(String author) { this.bookAuthor = author; }

    /**
     * Set a book's publisher
     * @param publisher the publisher of a book
     */
    public  void setBookPublisher(String publisher) { this.bookPublisher = publisher; }
}
