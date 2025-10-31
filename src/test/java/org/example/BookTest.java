package org.example;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookTest {

    private File picture1;
    private File picture2;
    private Book book1;

    @BeforeEach
    public void setUp(){
        picture1 = new File("pictures/The-road.jpg");
        picture2 = new File("pictures/The-road-alternate.jpg");

        book1 = new Book(
                "The Road",
                "0-307-26543-9",
                picture1,
                "The Road is a 2006 post-apocalyptic novel by American writer Cormac McCarthy. The book details the grueling journey of a father and his young son over several months across a landscape blasted by an unspecified cataclysm that has destroyed industrial civilization and nearly all life.",
                "Cormac McCarthy",
                "Alfred A. Knopf",
                10
        );
    }

    @Test
    void testBook() {
        Assertions.assertNotNull(book1);
    }

    @Test
    void testGetBookTitle() { Assertions.assertEquals("The Road", book1.getBookTitle()); }

    @Test
    void testGetBookISBN() {
        Assertions.assertEquals("0-307-26543-9", book1.getBookISBN());
    }

    @Test
    void testGetBookPicture() {
        Assertions.assertEquals(picture1, book1.getBookPicture());
    }

    @Test
    void testGetBookDescription() {
        Assertions.assertEquals("The Road is a 2006 post-apocalyptic novel by American writer Cormac McCarthy. The book details the grueling journey of a father and his young son over several months across a landscape blasted by an unspecified cataclysm that has destroyed industrial civilization and nearly all life.", book1.getBookDescription());
    }

    @Test
    void testGetBookAuthor() {
        Assertions.assertEquals("Cormac McCarthy", book1.getBookAuthor());
    }

    @Test
    void testGetBookPublisher() {
        Assertions.assertEquals("Alfred A. Knopf", book1.getBookPublisher());
    }

    @Test
    void testGetNumBooksAvailableForPurchase() { Assertions.assertEquals(10, book1.getNumBooksAvailableForPurchase()); }

    @Test
    void testSetBookTitle() {
        book1.setBookTitle("The Long Road");
        Assertions.assertEquals("The Long Road", book1.getBookTitle());
    }

    @Test
    void testSetBookISBN() {
        book1.setBookISBN("70630525");
        Assertions.assertEquals("70630525", book1.getBookISBN());
    }

    @Test
    void testSetBookPicture() {
        book1.setBookPicture(picture2);
        Assertions.assertEquals(picture2, book1.getBookPicture());
    }

    @Test
    void testSetBookDescription() {
        book1.setBookDescription("The Road is a very dreary book about a father and son trying to survive in a dying world.");
        Assertions.assertEquals("The Road is a very dreary book about a father and son trying to survive in a dying world.", book1.getBookDescription());
    }

    @Test
    void testSetBookAuthor() {
        book1.setBookAuthor("Charles Joseph McCarthy Jr.");
        Assertions.assertEquals("Charles Joseph McCarthy Jr.", book1.getBookAuthor());
    }

    @Test
    void testSetBookPublisher() {
        book1.setBookPublisher("Penguin Random House");
        Assertions.assertEquals("Penguin Random House", book1.getBookPublisher());
    }

    @Test
    void testSetNumBooksAvailableForPurchase() {
        book1.setNumBooksAvailableForPurchase(5);
        Assertions.assertEquals(5, book1.getNumBooksAvailableForPurchase());
    }
}
