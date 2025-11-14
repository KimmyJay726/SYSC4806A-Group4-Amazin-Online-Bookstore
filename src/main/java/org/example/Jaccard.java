/**
 * Jaccard Component
 *
 * Used to calculate the jaccard index between the active client and the other clients,
 * and make book recommendations to the active client accordingly
 */

package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Jaccard {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Create a dictionary of all the non-active, clients and their purchased books
     * @param activeClient The client who is currently signed in to the website
     * @return The purchased books for all the other clients
     */
    public Dictionary<Long, HashSet<Long>> getOtherPurchasedBooks(Client activeClient) {

        // Get clients for calculating the jaccard indices
        // Remove the client who will be recommended books
        List<Client> clientList = (List<Client>) clientRepository.findAll();
        clientList.removeIf(c -> c.getId() == activeClient.getId());

        // Create a dictionary where the keys are client ids and the values are sets of purchased book ids
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = new Hashtable<>();
        for (Client client : clientList) {
            Long clientId = client.getId();
            HashSet<Long> purchasedBooks = new HashSet<>(client.getPurchasedBooks());
            otherPurchasedBooks.put(clientId, purchasedBooks);
        }
        return otherPurchasedBooks;
    }

    /**
     * Create a dictionary of all the non-active, clients and their jaccard index with the active client
     * The jaccard index is the product of the active client's purchased books and each other client's purchased books
     * @param booksActiveClient The purchased books of the active client
     * @param booksOtherClients The purchased books of the other clients
     * @return The jaccard index for all the other clients (when compared to the active client)
     */
    public Dictionary<Long, Double> getOtherPurchasedBooksJaccards(
            HashSet<Long> booksActiveClient,
            Dictionary<Long, HashSet<Long>> booksOtherClients) {

        // Get the ids of the other clients
        Enumeration<Long> otherClientIds = booksOtherClients.keys();

        // Create a dictionary where the keys are client ids and the values are the jaccard indices
        Dictionary<Long, Double> otherPurchasedBooksJaccard = new Hashtable<>();
        while (otherClientIds.hasMoreElements()) {
            Long id = otherClientIds.nextElement();
            HashSet<Long> booksOtherClient = booksOtherClients.get(id);

            Double clientJaccardIndex = getJaccardIndex(booksActiveClient, booksOtherClient);
            otherPurchasedBooksJaccard.put(id, clientJaccardIndex);
        }
        return otherPurchasedBooksJaccard;
    }

    /**
     * Calculate the jaccard index for the active client's purchased books and the other client's purchased books
     *
     * If the active client and the other client have all their purchased books in common, the index is 1.00
     * If the active client and the other client have none of their purchased books in common, the index is 0.00
     *
     * @param booksActiveClient The purchased books of the active client
     * @param booksOtherClient The purchased books of the other clients
     * @return The jaccard index for the active client's purchased books and the other client's purchased books
     */
    public Double getJaccardIndex(
            HashSet<Long> booksActiveClient,
            HashSet<Long> booksOtherClient) {

        int booksActiveClientSize = booksActiveClient.size();
        int booksOtherClientSize = booksOtherClient.size();

        // Create a set of purchased book ids commons to both the active client and the client
        HashSet<Long> booksShared = getPurchasedBooksIntersection(booksActiveClient, booksOtherClient);

        int booksSharedSize = booksShared.size();

        // Calculate the jaccard index to determine how similar the purchased books of active client are to the purchased books of the other client
        Double jaccardIndex =
                (double) booksSharedSize /
                        (double) (booksActiveClientSize + booksOtherClientSize - booksSharedSize);

        // Correct for any possible errors
        if (jaccardIndex.isNaN() || jaccardIndex.isInfinite()) {
            jaccardIndex = Double.valueOf(0.00);
        }
        return jaccardIndex;
    }

    /**
     * Get the intersection of the active client's purchased books and another client's purchased books
     * @param booksActiveClient The purchased books of the active client
     * @param booksOtherClient The purchased books of the other client
     * @return books purchased by both the active client and the other client
     */
    public HashSet<Long> getPurchasedBooksIntersection(
            HashSet<Long> booksActiveClient,
            HashSet<Long> booksOtherClient) {

        HashSet<Long> booksShared = new HashSet<>();

        // Get the intersection of books purchased by the active client and books purchased by the other client
        for (Long bookId : booksActiveClient) {
            if (booksOtherClient.contains(bookId)) {
                booksShared.add(bookId);
            }
        }
        return booksShared;
    }

    /**
     * Get the client with the maximum jaccard index for the active client's purchased books and another client's purchased books
     * @param otherBooksJaccard The jaccard indices for each of the other clients
     * @return The client id associated with the maximum jaccard index
     */
    public Long getClientIdMaxJaccard(Dictionary<Long, Double> otherBooksJaccard) {

        double maxJaccardIndex = 0.00;
        long clientId = 0;

        // Get the ids of the other clients
        Enumeration<Long> otherClientIds = otherBooksJaccard.keys();

        // Find the maximum jaccard index and the associated client id
        while (otherClientIds.hasMoreElements()) {
            Long id = otherClientIds.nextElement();

            // Increase the maximum identified jaccard index, and the associated client as the dictionary is traversed
            if (otherBooksJaccard.get(id) > maxJaccardIndex && otherBooksJaccard.get(id) != 1.00) {
                maxJaccardIndex = otherBooksJaccard.get(id);
                clientId = id;
            }
        }
        return Long.valueOf(clientId);
    }

    /**
     * Create a list of books to recommend to the active client
     * @param activeClient the client that will be recommended books
     * @return The list of books ids that the active client is likely interested in purchasing
     */
    public List<Long> recommendedBooks(Client activeClient) {
        System.out.println("Finding book recommendations for " + activeClient.getUsername() + "...");

        // Convert the client's list of purchased book ids to a set of purchased book ids
        System.out.println("Retrieving " + activeClient.getUsername() + "'s book purchases...");
        HashSet<Long> purchasedBooks = new HashSet<>(activeClient.getPurchasedBooks());

        // Get the sets of purchased book ids for the other clients
        System.out.println("Retrieving the book purchases of the other users...");
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = getOtherPurchasedBooks(activeClient);

        // Get the jaccard indices for the other clients' purchased book ids and the active client's purchased book ids
        System.out.println("Analyzing " + activeClient.getUsername() + "'s book purchases against the book purchases of other users...");
        Dictionary<Long, Double> otherPurchasedBooksJaccard = getOtherPurchasedBooksJaccards(purchasedBooks, otherPurchasedBooks);

        // Get the id of the other client that shares the most purchased books with the active client
        System.out.println("Recommending books for " + activeClient.getUsername() + "...");
        Long otherClientId = getClientIdMaxJaccard(otherPurchasedBooksJaccard);

        Iterable<Book> books = bookRepository.findAll();
        HashSet<Long> recommendedBooks = new HashSet<>();
        HashSet<Long> otherClientBooks = otherPurchasedBooks.get(otherClientId);

        // If there are no clients whose purchased books intersect with the active client's purchased books,
        // recommend all books that have not been purchased by the active client
        if (otherClientId == 0L) {
            for (Book book : books) {
                recommendedBooks.add(book.getId());
            }
            recommendedBooks.removeAll(purchasedBooks);
            return new ArrayList<>(recommendedBooks);
        }
        else {
            recommendedBooks = otherClientBooks;
            recommendedBooks.removeAll(purchasedBooks);
            return new ArrayList<>(recommendedBooks);
        }
    }
}
