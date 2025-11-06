package org.example;

import org.springframework.web.multipart.MultipartFile;

public class BookForm {
    private String bookISBN;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private double bookPrice;
    private int numBooksAvailableForPurchase;
    private String bookDescription;
    private MultipartFile bookPicture; // This is only in the form, not in the DB entity

    // Getters and setters
    public String getBookISBN() { return bookISBN; }
    public void setBookISBN(String bookISBN) { this.bookISBN = bookISBN; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public String getBookPublisher() { return bookPublisher; }
    public void setBookPublisher(String bookPublisher) { this.bookPublisher = bookPublisher; }

    public double getBookPrice() { return bookPrice; }
    public void setBookPrice(double bookPrice) { this.bookPrice = bookPrice; }

    public int getNumBooksAvailableForPurchase() { return numBooksAvailableForPurchase; }
    public void setNumBooksAvailableForPurchase(int numBooksAvailableForPurchase) { this.numBooksAvailableForPurchase = numBooksAvailableForPurchase; }

    public String getBookDescription() { return bookDescription; }
    public void setBookDescription(String bookDescription) { this.bookDescription = bookDescription; }

    public MultipartFile getBookPicture() { return bookPicture; }
    public void setBookPicture(MultipartFile bookPicture) { this.bookPicture = bookPicture; }
}
