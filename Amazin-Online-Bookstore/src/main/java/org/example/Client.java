/**
 * Client class
 * Created By: Fiona Cheng
 * Date Created: October 24, 2025
 */

package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Boolean isOwner;
    private String username;
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Book> shoppingCart = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Book> purchasedBooks = new ArrayList<>();

    /**
     * Create a basic Client object
     */
    protected Client() {}

    /**
     * Create a Client object
     * @param username the client's username
     * @param password the client's password
     * @param isOwner whether the user owns the bookstore
     */
    public Client(String username, String password, Boolean isOwner) {
        this.username = username;
        this.password = password;
        this.isOwner = isOwner;
    }

    /**
     * @return the unique id of a client
     */
    public long getId() { return id; }

    /**
     * @return whether the client owns the bookstore
     */
    public Boolean getIsOwner() { return isOwner; }

    /**
     * @return the client's username
     */
    public String getUsername() { return username; }

    /**
     * @return the client's password
     */
    public String getPassword() { return password; }

    /**
     * @return the contents of the client's shopping cart
     */
    public List<Book> getShoppingCart() { return shoppingCart; }

    /**
     * @return the books that the client has purchased
     */
    public List<Book> getPurchasedBooks() { return purchasedBooks; }

    /**
     * Set a client's id manually
     * @param id the unique identifier of the client
     */
    public void setId(long id) { this.id = id; }

    /**
     * Set whether the client owns the bookstore
     * @param isOwner whether the client owns the bookstore
     */
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }

    /**
     * Set a client's username
     * @param username the client's username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Set a client's password
     * @param password the client's password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Set a client's shopping cart contents
     * @param shoppingCart the books in the client's shopping cart
     */
    public void setShoppingCart(List<Book> shoppingCart) {this.shoppingCart = shoppingCart;}

    /**
     * Set a client's purchased books
     * @param purchasedBooks the books purchased by the client
     */
    public void setPurchasedBooks(List<Book> purchasedBooks) {this.purchasedBooks = purchasedBooks;}

    /**
     * Add a book to the client's shopping cart
     * @param book the book to add
     */
    public void addToShoppingCart(Book book) { this.shoppingCart.add(book); }

    /**
     * Remove a book from the client's shopping cart
     * @param book the book to remove
     */
    public void removeFromShoppingCart(Book book) { this.shoppingCart.remove(book); }

    /**
     * Add a book to the client's purchased books
     * @param book the purchased book
     */
    public void addToPurchasedBooks(Book book) { this.purchasedBooks.add(book); }

}
