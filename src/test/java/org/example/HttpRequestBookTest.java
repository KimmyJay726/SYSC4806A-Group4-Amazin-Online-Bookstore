package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestBookTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testGettingBooks() {
        url = "http://localhost:" + port + "/books/all";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        for(int i = 1; i < 9; i++){
            url =  "http://localhost:" + port + "/books/" + i;
            request = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void testUploadedImage(){
        //This is not a real image just a test
        url = "http://localhost:" + port + "/uploads/testimage.jpg";

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPurchaseReturnBook(){
        //Purchase all 9 different books.
        for(int i = 1; i < 9; i++){
            url = "http://localhost:" + port + "/books/"+i+"/purchaseBook";
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        //Purchase the first book maximum amount of times possible(25)
        //Note subtract 25 by 1 as we already purchase one.
        for(int i = 0; i < 24; i++){
            url = "http://localhost:" + port + "/books/1/purchaseBook";
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        //Test buying more books than possible
        url = "http://localhost:" + port + "/books/1/purchaseBook";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        //Test buying an invalid book
        url = "http://localhost:" + port + "/books/999/purchaseBook";
        request = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        //Test returning the books we just bought
        //Purchase all 9 different books.
        for(int i = 1; i < 9; i++){
            url = "http://localhost:" + port + "/books/"+i+"/returnBook";
            request = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        //Purchase the first book maximum amount of times possible(25)
        //Note subtract 25 by 1 as we already purchase one.
        for(int i = 0; i < 24; i++){
            url = "http://localhost:" + port + "/books/1/returnBook";
            request = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
