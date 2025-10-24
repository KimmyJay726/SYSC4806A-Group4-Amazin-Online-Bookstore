package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class UserTest {
    private User user1;

    @BeforeEach
    public void setUp() {
        user1 = new User("Alice_T", "PerfectPassword", false);
        user1.setId(101);
    }

    @Test
    void testGetId() {
        Assertions.assertEquals(101, user1.getId());
    }

    @Test
    void testIsOwner(){
        Assertions.assertFalse(user1.getIsOwner());
    }

    @Test
    void testGetUsername(){
        Assertions.assertEquals("Alice_T", user1.getUsername());
    }

    @Test
    void testGetPassword(){
        Assertions.assertEquals("PerfectPassword", user1.getPassword());
    }

    @Test
    void testGetShoppingCart() {
        Assertions.assertNotNull(user1.getShoppingCart());
    }

    @Test
    void testGetPurchasedBooks() {
        Assertions.assertNotNull(user1.getPurchasedBooks());
    }

    @Test
    void testSetId(){
        user1.setId(102);
        Assertions.assertEquals(102, user1.getId());
    }

    @Test
    void testSetIsOwner(){
        user1.setIsOwner(true);
        Assertions.assertTrue(user1.getIsOwner());
    }

    @Test
    void testSetUsername(){
        user1.setUsername("Alice_v2");
        Assertions.assertEquals("Alice_v2", user1.getUsername());
    }

    @Test
    void testSetPassword(){
        user1.setPassword("PerfectPassword123!");
        Assertions.assertEquals("PerfectPassword123!", user1.getPassword());
    }

    @Test
    void testAddToShoppingCart(){
        // Cart should start empty
        Assertions.assertEquals(0, user1.getShoppingCart().size());

        // Create a book
        File picture1 = new File("pictures/The-road.jpg");
        Book book1 = new Book(
                "0-307-26543-9",
                picture1,
                "The Road is a 2006 post-apocalyptic novel by American writer Cormac McCarthy. The book details the grueling journey of a father and his young son over several months across a landscape blasted by an unspecified cataclysm that has destroyed industrial civilization and nearly all life.",
                "Cormac McCarthy",
                "Alfred A. Knopf"
        );

        // Add the book to the cart
        user1.addToShoppingCart(book1);
        Assertions.assertEquals(1, user1.getShoppingCart().size());
        Assertions.assertTrue(user1.getShoppingCart().contains(book1));
    }

    @Test
    void testRemoveFromShoppingCart(){
        // Create a book
        File picture1 = new File("pictures/The-road.jpg");
        Book book1 = new Book(
                "0-307-26543-9",
                picture1,
                "The Road is a 2006 post-apocalyptic novel by American writer Cormac McCarthy. The book details the grueling journey of a father and his young son over several months across a landscape blasted by an unspecified cataclysm that has destroyed industrial civilization and nearly all life.",
                "Cormac McCarthy",
                "Alfred A. Knopf"
        );

        // Add the book to the cart
        user1.addToShoppingCart(book1);
        Assertions.assertEquals(1, user1.getShoppingCart().size());

        // Remove the book from cart
        user1.removeFromShoppingCart(book1);
        Assertions.assertEquals(0, user1.getShoppingCart().size());
        Assertions.assertFalse(user1.getShoppingCart().contains(book1));
    }

    @Test
    void testAddToPurchasedBooks(){
        // Purchased books should start empty
        Assertions.assertEquals(0, user1.getPurchasedBooks().size());

        // Create a book
        File picture1 = new File("pictures/The-road.jpg");
        Book book1 = new Book(
                "0-307-26543-9",
                picture1,
                "The Road is a 2006 post-apocalyptic novel by American writer Cormac McCarthy. The book details the grueling journey of a father and his young son over several months across a landscape blasted by an unspecified cataclysm that has destroyed industrial civilization and nearly all life.",
                "Cormac McCarthy",
                "Alfred A. Knopf"
        );

        // Add the book to purchase list
        user1.addToPurchasedBooks(book1);
        Assertions.assertEquals(1, user1.getPurchasedBooks().size());
        Assertions.assertTrue(user1.getPurchasedBooks().contains(book1));
    }
}
