/**
 * User class
 * Created By: Fiona Cheng
 * Date Created: October 24, 2025
 */


package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Boolean isOwner;
    private String username;
    private String password;
    private ArrayList<Book> shoppingCart;
    private ArrayList<Book> purchasedBooks;

    /**
     * Create a basic User object
     */
    protected User() {}

    /**
     * Create a User object
     * @param username the user's username
     * @param password the user's password
     * @param isOwner whether the user owns the bookstore
     */
    public User(String username, String password, Boolean isOwner) {
        this.username = username;
        this.password = password;
        this.isOwner = isOwner;
        shoppingCart = new ArrayList<Book>();
        purchasedBooks = new ArrayList<Book>();
    }

    /**
     * @return the unique id of a user
     */
    public long getId() { return id; }

    /**
     * @return whether the user owns the bookstore
     */
    public Boolean getIsOwner() { return isOwner; }

    /**
     * @return the user's username
     */
    public String getUsername() { return username; }

    /**
     * @return the user's password
     */
    public String getPassword() { return password; }

    /**
     * @return the contents of the user's shopping cart
     */
    public ArrayList<Book> getShoppingCart() { return shoppingCart; }

    /**
     * @return the books that the user has purchased
     */
    public ArrayList<Book> getPurchasedBooks() { return purchasedBooks; }

    /**
     * Set a user's id manually
     * @param id the unique identifier of the user
     */
    public void setId(long id) { this.id = id; }

    /**
     * Set whether the user owns the bookstore
     * @param isOwner whether the user owns the bookstore
     */
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }

    /**
     * Set a user's username
     * @param username the user's username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Set a user's password
     * @param password the user's password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Add a book to the user's shopping cart
     * @param book the book to add
     */
    public void addToShoppingCart(Book book) { this.shoppingCart.add(book); }

    /**
     * Remove a book from the user's shopping cart
     * @param book the book to remove
     */
    public void removeFromShoppingCart(Book book) { this.shoppingCart.remove(book); }

    /**
     * Add a book to the user's purchased books
     * @param book the purchased book
     */
    public void addToPurchasedBooks(Book book) { this.purchasedBooks.add(book); }

}
