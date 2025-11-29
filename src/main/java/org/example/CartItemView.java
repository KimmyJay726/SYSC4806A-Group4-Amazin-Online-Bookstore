package org.example;


public class CartItemView {
    private final Book book;
    private final int quantity;
    private final double lineTotal;

    public CartItemView(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.lineTotal = book.getBookPrice() * quantity;
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}