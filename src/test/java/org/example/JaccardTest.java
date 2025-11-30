package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class JaccardTest {

    @Autowired
    private Jaccard jaccard;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Optional<Client> activeClient;

    @BeforeEach
    void setUp() {
        activeClient = clientRepository.findById(2);
    }

    @Test
    void testSetUpBookRepository() {
        Assertions.assertNotNull(bookRepository.findAll());
    }

    @Test
    void testSetUpClientRepository() {
        Assertions.assertNotNull(clientRepository.findAll());
    }

    @Test
    void testSetUpActiveClient() {
        Assertions.assertNotNull(activeClient);
    }

    @Test
    void testSetUpActiveClientId() {
        Assertions.assertEquals(2, activeClient.get().getId());
    }

    @Test
    void testSetUpActiveClientIsOwner() {
        Assertions.assertEquals(false, activeClient.get().getIsOwner());
    }

    @Test
    void testSetUpActiveClientUsername() {
        Assertions.assertEquals("safi", activeClient.get().getUsername());
    }

    @Test
    void testSetUpActiveClientPassword() {
        Assertions.assertEquals("password", activeClient.get().getPassword());
    }

    @Test
    void testSetUpActiveClientShoppingCartIds() {
        Assertions.assertEquals(0, activeClient.get().getShoppingCart().size());
    }

    @Test
    void testSetUpActiveClientPurchasedBookIds() {
        Assertions.assertEquals(4, activeClient.get().getPurchasedBooks().size());
    }

    @Test
    void testSetUpOtherClients() {
        Assertions.assertEquals(3, clientRepository.findById(3).get().getPurchasedBooks().size());
        Assertions.assertEquals(4, clientRepository.findById(4).get().getPurchasedBooks().size());
        Assertions.assertEquals(0, clientRepository.findById(5).get().getPurchasedBooks().size());
        Assertions.assertEquals(0, clientRepository.findById(6).get().getPurchasedBooks().size());
    }

    @Test
    void testGetOtherPurchasedBooks() {
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = jaccard.getOtherPurchasedBooks(activeClient.get());

        Assertions.assertEquals(0, otherPurchasedBooks.get(1L).size());
        Assertions.assertNull(otherPurchasedBooks.get(2L));
        Assertions.assertEquals(3, otherPurchasedBooks.get(3L).size());
        Assertions.assertEquals(4, otherPurchasedBooks.get(4L).size());
        Assertions.assertEquals(0, otherPurchasedBooks.get(5L).size());
        Assertions.assertEquals(0, otherPurchasedBooks.get(6L).size());
    }

    @Test
    void testGetOtherPurchasedBooksJaccards() {
        HashSet<Long> booksActiveClient = new HashSet(activeClient.get().getPurchasedBooks());
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = jaccard.getOtherPurchasedBooks(activeClient.get());
        Dictionary<Long, Double> otherPurchasedBooksJaccard = jaccard.getOtherPurchasedBooksJaccards(booksActiveClient, otherPurchasedBooks);

        Assertions.assertEquals(0.0, otherPurchasedBooksJaccard.get(1L));
        Assertions.assertNull(otherPurchasedBooks.get(2L));
        Assertions.assertEquals(0.4, otherPurchasedBooksJaccard.get(3L));
        Assertions.assertEquals(0.0, otherPurchasedBooksJaccard.get(4L));
        Assertions.assertEquals(0.0, otherPurchasedBooksJaccard.get(5L));
        Assertions.assertEquals(0.0, otherPurchasedBooksJaccard.get(6L));
    }

    @Test
    void testGetJaccardIndex() {
        HashSet<Long> booksActiveClient = new HashSet(activeClient.get().getPurchasedBooks());
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = jaccard.getOtherPurchasedBooks(activeClient.get());

        Assertions.assertEquals(0, jaccard.getJaccardIndex(booksActiveClient, otherPurchasedBooks.get(1L)));
        Assertions.assertNull(otherPurchasedBooks.get(2L));
        Assertions.assertEquals(0.4, jaccard.getJaccardIndex(booksActiveClient, otherPurchasedBooks.get(3L)));
        Assertions.assertEquals(0.0, jaccard.getJaccardIndex(booksActiveClient, otherPurchasedBooks.get(4L)));
        Assertions.assertEquals(0.0, jaccard.getJaccardIndex(booksActiveClient, otherPurchasedBooks.get(5L)));
        Assertions.assertEquals(0.0, jaccard.getJaccardIndex(booksActiveClient, otherPurchasedBooks.get(6L)));
    }

    @Test
    void testGetPurchasedBooksIntersection() {
        HashSet<Long> booksActiveClient = new HashSet(activeClient.get().getPurchasedBooks());
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = jaccard.getOtherPurchasedBooks(activeClient.get());

        HashSet<Long> booksSharedWithAdmin = new HashSet<>();
        HashSet<Long> booksSharedWithJake = new HashSet<>();
        booksSharedWithJake.add(3L);
        booksSharedWithJake.add(4L);
        HashSet<Long> booksSharedWithAndrew = new HashSet<>();
        HashSet<Long> booksSharedWithFiona = new HashSet<>();
        HashSet<Long> booksSharedWithJacob = new HashSet<>();

        Assertions.assertEquals(booksSharedWithAdmin, jaccard.getPurchasedBooksIntersection(booksActiveClient, otherPurchasedBooks.get(1L)));
        Assertions.assertNull(otherPurchasedBooks.get(2L));
        Assertions.assertEquals(booksSharedWithJake, jaccard.getPurchasedBooksIntersection(booksActiveClient, otherPurchasedBooks.get(3L)));
        Assertions.assertEquals(booksSharedWithAndrew, jaccard.getPurchasedBooksIntersection(booksActiveClient, otherPurchasedBooks.get(4L)));
        Assertions.assertEquals(booksSharedWithFiona, jaccard.getPurchasedBooksIntersection(booksActiveClient, otherPurchasedBooks.get(5L)));
        Assertions.assertEquals(booksSharedWithJacob, jaccard.getPurchasedBooksIntersection(booksActiveClient, otherPurchasedBooks.get(6L)));
    }

    @Test
    void testGetClientIdMaxJaccard() {
        HashSet<Long> booksActiveClient = new HashSet(activeClient.get().getPurchasedBooks());
        Dictionary<Long, HashSet<Long>> otherPurchasedBooks = jaccard.getOtherPurchasedBooks(activeClient.get());
        Dictionary<Long, Double> otherPurchasedBooksJaccard = jaccard.getOtherPurchasedBooksJaccards(booksActiveClient, otherPurchasedBooks);
        Long clientIdMaxJaccard = jaccard.getClientIdMaxJaccard(otherPurchasedBooksJaccard);

        Assertions.assertEquals(3L, clientIdMaxJaccard);
    }

    @Test
    void testRecommendedBooks() {
        List<Long> recommendedBooks = jaccard.recommendedBooks(activeClient.get());

        Assertions.assertEquals(1, recommendedBooks.size());
        Assertions.assertEquals(2L, recommendedBooks.get(0));
    }
}
