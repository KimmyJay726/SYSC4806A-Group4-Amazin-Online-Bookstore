package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ClientTest {
    private Client client;

    @BeforeEach
    public void setUp() {
        client = new Client("Alice_T", "PerfectPassword", false);
        client.setId(101);
    }

    @Test
    void testGetId() {
        Assertions.assertEquals(101, client.getId());
    }

    @Test
    void testIsOwner(){
        Assertions.assertFalse(client.getIsOwner());
    }

    @Test
    void testGetUsername(){
        Assertions.assertEquals("Alice_T", client.getUsername());
    }

    @Test
    void testGetPassword(){
        Assertions.assertEquals("PerfectPassword", client.getPassword());
    }

    @Test
    void testGetShoppingCart() {
        Assertions.assertNotNull(client.getShoppingCart());
    }

    @Test
    void testGetPurchasedBooks() {
        Assertions.assertNotNull(client.getPurchasedBooks());
    }

    @Test
    void testSetId(){
        client.setId(102);
        Assertions.assertEquals(102, client.getId());
    }

    @Test
    void testSetIsOwner(){
        client.setIsOwner(true);
        Assertions.assertTrue(client.getIsOwner());
    }

    @Test
    void testSetUsername(){
        client.setUsername("Alice_v2");
        Assertions.assertEquals("Alice_v2", client.getUsername());
    }

    @Test
    void testSetPassword(){
        client.setPassword("PerfectPassword123!");
        Assertions.assertEquals("PerfectPassword123!", client.getPassword());
    }

    @Test
    void testAddToShoppingCart(){
        // Cart should start empty
        Assertions.assertEquals(0, client.getShoppingCart().size());

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
        client.addToShoppingCart(book1);
        Assertions.assertEquals(1, client.getShoppingCart().size());
        Assertions.assertTrue(client.getShoppingCart().contains(book1));
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
        client.addToShoppingCart(book1);
        Assertions.assertEquals(1, client.getShoppingCart().size());

        // Remove the book from cart
        client.removeFromShoppingCart(book1);
        Assertions.assertEquals(0, client.getShoppingCart().size());
        Assertions.assertFalse(client.getShoppingCart().contains(book1));
    }

    @Test
    void testAddToPurchasedBooks(){
        // Purchased books should start empty
        Assertions.assertEquals(0, client.getPurchasedBooks().size());

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
        client.addToPurchasedBooks(book1);
        Assertions.assertEquals(1, client.getPurchasedBooks().size());
        Assertions.assertTrue(client.getPurchasedBooks().contains(book1));
    }
}
