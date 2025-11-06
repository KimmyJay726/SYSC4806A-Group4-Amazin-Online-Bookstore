/**
 * Database seeding configuration
 * Initializes the database with sample books, clients, and purchases
 * Created for SYSC4806A Group 4 - Amazin Online Bookstore
 */

package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Check if data already exists to avoid duplicate seeding
        if (bookRepository.count() > 0 || clientRepository.count() > 0) {
            System.out.println("Data already exists, skipping seeding...");
            return;
        }
        
        System.out.println("Seeding database with initial data...");
        
        // Seed books
        seedBooks();
        
        // Seed clients (including admin owner)
        seedClients();
        
        // Seed purchases (add books to client purchase history)
        seedPurchases();
        
        System.out.println("Database seeding completed successfully!");
    }

    private void seedBooks() {
        System.out.println("Creating sample books...");
        
        List<Book> books = Arrays.asList(
            new Book(
                "The Great Gatsby", 
                "978-0-7432-7356-5", 
                "/pictures/great-gatsby.jpg", 
                "A classic novel that I have never read but people like to mention",
                "F. Scott Fitzgerald", 
                "Scribner",
                13.99,
                25
            ),
            new Book(
                "To Kill a Mockingbird", 
                "978-0-06-112008-4", 
                "/pictures/mockingbird.jpg", 
                "A classic book about the evasive nature of mockingbirds",
                "Harper Lee", 
                "J.B. Lippincott",
                25.88,
                30
            ),
            new Book(
                "1984", 
                "978-0-452-28423-4", 
                "/pictures/1984.jpg", 
                "George Orwell's must read classic novel about a dystopian future",
                "George Orwell", 
                "Secker & Warburg",
                16.50,
                40
            ),
            new Book(
                "Pride and Prejudice", 
                "978-0-14-143951-8", 
                "/pictures/pride-prejudice.jpg", 
                "Women love this book",
                "Jane Austen", 
                "T. Egerton",
                40.00,
                35
            ),
            new Book(
                "The Catcher in the Rye", 
                "978-0-316-76948-0", 
                "/pictures/catcher-rye.jpg", 
                "J.D. Salinger's coming-of-age story following Holden Caulfield in New York City",
                "J.D. Salinger", 
                "Little, Brown and Company",
                23.10,
                20
            ),
            new Book(
                "The Lord of the Rings", 
                "978-0-547-92822-7", 
                "/pictures/lotr.jpg", 
                "Tolkien's trilogy about the quest to destroy the One Ring",
                "J.R.R. Tolkien", 
                "George Allen & Unwin",
                27.79,
                15
            ),
            new Book(
                "Harry Potter and the Philosopher's Stone", 
                "978-0-7475-3269-9", 
                "/pictures/harry-potter.jpg", 
                "The first book in the magical series about the boy who lived",
                "J.K. Rowling", 
                "Bloomsbury",
                31.99,
                50
            ),
            new Book(
                "The Road", 
                "978-0-307-38789-9", 
                "/pictures/The-road.jpg", 
                "A post-apocalyptic tale of a father and son's journey through a devastated America",
                "Cormac McCarthy", 
                "Alfred A. Knopf",
                18.99,
                18
            )
        );
        
        bookRepository.saveAll(books);
        System.out.println("Created " + books.size() + " sample books");
    }

    private void seedClients() {
        System.out.println("Creating sample clients...");
        
        List<Client> clients = Arrays.asList(
            // Admin owner account with username: admin, password: admin
            new Client("admin", "admin", true),
            
            // Regular clients
            new Client("safi", "password", false),
            new Client("jake", "password1", false),
            new Client("andrew", "password2", false),
            new Client("fiona", "password3", false),
            new Client("jacob", "password4", false)
        );
        
        clientRepository.saveAll(clients);
        System.out.println("Created " + clients.size() + " sample clients (including admin owner)");
    }

    private void seedPurchases() {
        System.out.println("Creating sample purchase history...");
        
        // Get clients and books for creating purchase history
        List<Client> clientList = (List<Client>) clientRepository.findAll(); 
        List<Book> bookList = (List<Book>) bookRepository.findAll(); 

        // Client order: [admin, safi, jake, andrew, fiona, jacob]
        // Book order: [Gatsby, Mockingbird, 1984, Pride&Prejudice, Catcher, LOTR, Harry Potter, The Road]
        
        Client safi = clientList.get(1);
        safi.addToPurchasedBooks(bookList.get(0)); // The Great Gatsby
        safi.addToPurchasedBooks(bookList.get(2)); // 1984
        safi.addToPurchasedBooks(bookList.get(6)); // Harry Potter
        clientRepository.save(safi);
        
        Client jake = clientList.get(2);
        jake.addToPurchasedBooks(bookList.get(1)); // To Kill a Mockingbird
        jake.addToPurchasedBooks(bookList.get(3)); // Pride and Prejudice
        clientRepository.save(jake);
        
        Client andrew = clientList.get(3);
        andrew.addToPurchasedBooks(bookList.get(4)); // The Catcher in the Rye
        andrew.addToPurchasedBooks(bookList.get(5)); // The Lord of the Rings
        andrew.addToPurchasedBooks(bookList.get(7)); // The Road
        clientRepository.save(andrew);
        
        Client fiona = clientList.get(4);
        fiona.addToShoppingCart(bookList.get(0)); // The Great Gatsby
        fiona.addToShoppingCart(bookList.get(4)); // The Catcher in the Rye
        clientRepository.save(fiona);
        
        Client jacob = clientList.get(5);
        jacob.addToShoppingCart(bookList.get(2)); // 1984
        clientRepository.save(jacob);
        
        System.out.println("Added purchase history and shopping cart items to sample clients");
    }
}
