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

    private String bookTitle;
    private String bookISBN;
    //Changed bookPicture to String as html takes the string source of the image
    private String bookPicture;
    //private File bookPicture;
    private String bookDescription;
    private String bookAuthor;
    private String bookPublisher;

    private Integer numBooksAvailableForPurchase;

    /**
     * Create Book object
     */
    protected Book() {}

    /**
     * Create a book object
     * @param author the author of the book
     * @param ISBN the ISBN of the book
     * @param picture the picture of the book on the website
     * @param description the description of the book on the website
     * @param author the author of the book
     * @param publisher the publisher of the book
     * @param numBooks the number of copies of the book available for purchase
     */
    public Book(String title, String ISBN, String picture, String description, String author, String publisher, Integer numBooks) {
        bookTitle = title;
        bookISBN = ISBN;
        bookPicture = picture;
        bookDescription = description;
        bookAuthor = author;
        bookPublisher = publisher;
        numBooksAvailableForPurchase = numBooks;
    }

    /**
     * @return the unique id of a book
     */
    public long getId() { return id; }

    /**
     * @return the title of a book
     */
    public String getBookTitle() { return bookTitle; }

    /**
     * @return the ISBN of a book
     */
    public String getBookISBN() { return this.bookISBN; }

    public String getBookPicture() { return this.bookPicture; }
    //public File getBookPicture() { return this.bookPicture; }
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
     * @return the number of copies of the book available for purchase
     */
    public Integer getNumBooksAvailableForPurchase() { return this.numBooksAvailableForPurchase; }

    /**
     * Set a book's id manually
     * @param id the unique identifier of a book
     */
    public void setId(long id) { this.id = id; }

    /**
     * Set a book's title
     * @param title the title of a book
     */
    public void setBookTitle(String title) { this.bookTitle = title; }

    /**
     * Set a book's ISBN
     * @param ISBN the ISBN of a book
     */
    public void setBookISBN(String ISBN) { this.bookISBN = ISBN; }

    /**
     * Set a book's picture
     * @param bookPicture the picture assigned to a book
     */
    public void setBookPicture(String bookPicture) {
        this.bookPicture = bookPicture;
    }

    /**
     * Set a book's picture
     * @param picture the picture assigned to a book
     */
    //public void setBookPicture(File picture) { this.bookPicture = picture; }

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
    public void setBookPublisher(String publisher) { this.bookPublisher = publisher; }

    /**
     * Set a book's number of copies available for purchase
     * @param numBooks the number of copies of a book
     */
    public void setNumBooksAvailableForPurchase(Integer numBooks) { this.numBooksAvailableForPurchase = numBooks; }
}

