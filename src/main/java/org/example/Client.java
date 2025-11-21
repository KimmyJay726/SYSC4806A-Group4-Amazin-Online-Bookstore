/**
 * Client class
 * Created By: Fiona Cheng
 * Date Created: October 24, 2025
 */

package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Boolean isOwner = false;
    private String username;
    private String password;

    private List<Long> shoppingCartIds = new ArrayList<>();
    private List<Long> purchasedBookIds = new ArrayList<>();

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
    public List<Long> getShoppingCart() { return shoppingCartIds; }

    /**
     * @return the books that the client has purchased
     */
    public List<Long> getPurchasedBooks() { return purchasedBookIds; }

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
     * @param shoppingCartIds the books in the client's shopping cart
     */
    public void setShoppingCart(List<Long> shoppingCartIds) {this.shoppingCartIds = shoppingCartIds;}

    /**
     * Set a client's purchased books
     * @param purchasedBookIds the books purchased by the client
     */
    public void setPurchasedBooks(List<Long> purchasedBookIds) {this.purchasedBookIds = purchasedBookIds;}

    /**
     * Add a book to the client's shopping cart
     * @param bookId the ID of the book to add
     */
    public void addToShoppingCart(long bookId) { this.shoppingCartIds.add(bookId); }

    /**
     * Remove a book from the client's shopping cart
     * @param bookId the ID of the book to remove
     */
    public void removeFromShoppingCart(long bookId) { this.shoppingCartIds.remove(bookId); }

    /**
     * Add a book to the client's purchased books
     * @param bookId the ID of the purchased book
     */
    public void addToPurchasedBooks(long bookId) { this.purchasedBookIds.add(bookId); }

    /**
     * Clears the shoppingCart once payment is made
     */
    public void clearShoppingCart() {

        if (this.shoppingCartIds != null) {
            this.shoppingCartIds.clear();
        }
    }

    /**
     * Returns how many of a certain item appear in the client's cart
     */
    public int countItemInShoppingCart(long bookId) {
        int count = 0;
        for (long itemId: shoppingCartIds) {
            if (itemId == bookId) {
                count++;
            }
        }
        return count;
    }

}
