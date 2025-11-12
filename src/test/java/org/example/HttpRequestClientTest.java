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
public class HttpRequestClientTest {
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
    public void testGetClientsRequest(){
        ResponseEntity<String> response;

        //Data seeder has 6 users
        for(int i = 1; i < 7; i++){
            url = "http://localhost:"+port+"/client/id/"+i;
            response = restTemplate.getForEntity(url, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        String[] users = {"admin", "fiona", "jake", "safi", "jacob", "andrew"};
        for(int i = 0; i < users.length; i++){
            url = "http://localhost:"+port+"/client/username/"+users[i];
            response = restTemplate.getForEntity(url, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        url = "http://localhost:"+port+"/client/username/invalid";
        response = restTemplate.getForEntity(url, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testLogin(){
        url = "http://localhost:"+port+"/client/me";

        //On start up client is null so check that
        ResponseEntity<String> response =  restTemplate.getForEntity(url, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        //Test logging in
        String[] user = {"admi", "admin", "admin"};
        String[] pass = {"admin", "admi", "admin"};
        url = "http://localhost:"+port+"/client/login";

        for(int i = 0; i < user.length; i++){
            Map<String, String> credentials = Map.of("username", user[i], "password", pass[i]);

            //Set up the HTTP request
            HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);

            //Post request
            response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if(user[i].equals("admin") && pass[i].equals("admin"))
                Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            else
                Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        //Test logging out
        url = "http://localhost:"+port+"/client/logout";

        HttpEntity<?> logoutRequest = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.POST, logoutRequest, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRegister(){
        url = "http://localhost:"+port+"/client/register";

        //Set up HTTP request
        HttpEntity<String> request = new HttpEntity<>(
                "{\"username\": \"Bob\", \"password\": \"myPassword\", \"isOwner\": \"false\"}",headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCart(){
        //Client should be null so check
        url = "http://localhost:"+port+"/client/cart/add/1";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        //Sign in to some seeded user
        //Set up the HTTP request
        url = "http://localhost:"+port+"/client/login";
        Map<String, String> credentials = Map.of("username", "andrew", "password", "password2");
        HttpEntity<Map<String, String>> request2 = new HttpEntity<>(credentials, headers);

        //Post request
        response = restTemplate.exchange(url, HttpMethod.POST, request2, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        //Check we've signed in
        //Set up cookies so we have an actual user
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertNotNull(cookies);

        String sessionCookie = cookies.stream()
                .filter(s -> s.startsWith("JSESSIONID"))
                .findFirst()
                .orElse(null);

        //Check we've signed in
        headers.set(HttpHeaders.COOKIE, sessionCookie);
        url = "http://localhost:"+port+"/client/me";
        HttpEntity<String> request3 = new HttpEntity<>(headers);
        response =  restTemplate.exchange(url, HttpMethod.GET, request3, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        //Test adding all 8 books to cart
        for(int i = 1; i < 9; i++){
            url = "http://localhost:"+port+"/client/cart/add/"+i;
            HttpEntity<String> request4 = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.POST, request4, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        //Test removing all 8 books from cart
        for(int i = 1; i < 9; i++){
            url = "http://localhost:"+port+"/client/cart/remove/"+i;
            HttpEntity<String> request5 = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.POST, request5, String.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
