package org.example;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class MockMVCTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void homePage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome to the Amazin Online Bookstore Nexus!")))
                .andExpect(content().string(containsString("BROWSE CATALOG")));

        //Sign in as admin
        MvcResult loginAttempt = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginAttempt.getRequest().getSession();

        //Check that the add book button exists for owner
        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ADD BOOK")));

        //Login as normal user
        loginAttempt = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"andrew\", \"password\": \"password2\"}"))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) loginAttempt.getRequest().getSession();

        //Check that the recommendation button exists users
        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("YOUR RECOMMENDATIONS")));
    }

    @Test
    public void loginPage() throws Exception {
        this.mockMvc.perform(get("/login-register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"loginUsername\"")))
                .andExpect(content().string(containsString("id=\"loginPassword\"")));
    }

    @Test
    public void browsePage() throws Exception{
        //Check that the client options are there
        this.mockMvc.perform(get("/inventory"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ID")))
                .andExpect(content().string(containsString("ISBN")))
                .andExpect(content().string(containsString("Title")))
                .andExpect(content().string(containsString("Picture")))
                .andExpect(content().string(containsString("Author")))
                .andExpect(content().string(containsString("Publisher")))
                .andExpect(content().string(containsString("Price")))
                .andExpect(content().string(containsString("Quantity")))
                .andExpect(content().string(containsString("Description")))
                .andExpect(content().string(containsString("Action")))
                .andExpect(content().string(containsString("Search")));

        //Login as admin
        MvcResult loginAttempt = mockMvc.perform(post("/client/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"admin\", \"password\": \"admin\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginAttempt.getRequest().getSession();

        //Check that the edit button exists
        mockMvc.perform(get("/inventory")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Edit</button>")));

        //Check for the add book button at the top
        mockMvc.perform(get("/inventory")
                    .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"add-book-button\"")));
    }

    @Test
    public void addBookPage() throws Exception {
        //Test Unauthorized Access
        mockMvc.perform(multipart("/books/addBook")
                        .param("bookTitle", "Test Book")
                        .param("bookISBN", "123456789")
                        .param("bookDescription", "A test book")
                        .param("bookAuthor", "Author")
                        .param("bookPublisher", "Publisher")
                        .param("bookPrice", "10.99")
                        .param("numBooks", "5"))
                .andExpect(status().isUnauthorized());

        //Login as a normal user
        MvcResult loginResult = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\", \"password\":\"password2\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(multipart("/books/addBook")
                        .session(session)
                        .param("bookTitle", "Test Book")
                        .param("bookISBN", "111111")
                        .param("bookDescription", "Test Desc")
                        .param("bookAuthor", "Test Author")
                        .param("bookPublisher", "Test Publisher")
                        .param("bookPrice", "9.99")
                        .param("numBooks", "3"))
                .andExpect(status().isUnauthorized());

        //Test Authorized Access
        //Login as admin
        loginResult = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\", \"password\":\"admin\"}"))
                .andExpect(status().isOk())
                .andReturn();

        session = (MockHttpSession) loginResult.getRequest().getSession();

        //Test no file upload
        mockMvc.perform(multipart("/books/addBook")
                        .session(session)
                        .param("bookTitle", "Test Book")
                        .param("bookISBN", "111111")
                        .param("bookDescription", "Test Desc")
                        .param("bookAuthor", "Test Author")
                        .param("bookPublisher", "Test Publisher")
                        .param("bookPrice", "9.99")
                        .param("numBooks", "3"))
                .andExpect(status().isOk());

        //Test file upload
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "cover.jpg",
                "image/jpeg",
                "fakeimage".getBytes()
        );

        mockMvc.perform(multipart("/books/addBook")
                        .file(mockFile)
                        .session(session)
                        .param("bookTitle", "The Hobbit")
                        .param("bookISBN", "9780547928227")
                        .param("bookDescription", "Adventure novel")
                        .param("bookAuthor", "J.R.R. Tolkien")
                        .param("bookPublisher", "HarperCollins")
                        .param("bookPrice", "19.99")
                        .param("numBooks", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The Hobbit")))
                .andExpect(content().string(containsString("Tolkien")));
    }

    @Test
    public void editBookPage() throws Exception {
        //Test not logged in
        mockMvc.perform(multipart("/books/1/editBook")
                        .param("bookISBN", "xyz")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isUnauthorized());

        //Test user is not owner
        //Login as normal user
        MvcResult loginResult = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\", \"password\":\"password2\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(multipart("/books/1/editBook")
                        .session(session)
                        .param("bookISBN", "test")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isUnauthorized());

        //Login as admin
        loginResult = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\", \"password\":\"admin\"}"))
                .andExpect(status().isOk())
                .andReturn();
        session = (MockHttpSession) loginResult.getRequest().getSession();

        //Test book not found
        mockMvc.perform(multipart("/books/999/editBook")
                        .session(session)
                        .param("bookISBN", "test")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isNotFound());

        //Test book editing non picture fields
        mockMvc.perform(multipart("/books/1/editBook")
                        .session(session)
                        .param("bookISBN", "129391")
                        .param("bookTitle", "The Hobbit")
                        .param("bookAuthor", "J.R.R. Tolkien")
                        .param("bookPublisher", "HarperCollins")
                        .param("bookPrice", "19.99")
                        .param("numBooks", "7")
                        .param("bookDescription", "Smth")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isOk());

        //Set up multipart file
        MockMultipartFile imageFile = new MockMultipartFile(
                "bookPicture",
                "cover.jpg",
                "image/jpeg",
                "fakeimage".getBytes()
        );

        //Test with file uploads
        mockMvc.perform(multipart("/books/1/editBook")
                        .file(imageFile)
                        .session(session)
                        .param("bookISBN", "129391")
                        .param("bookTitle", "The Hobbit")
                        .param("bookAuthor", "J.R.R. Tolkien")
                        .param("bookPublisher", "HarperCollins")
                        .param("bookPrice", "19.99")
                        .param("numBooks", "7")
                        .param("bookDescription", "Smth")
                        .with(request -> { request.setMethod("POST"); return request; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookPicture").value("/uploads/cover.jpg"));
    }

    @Test
    public void checkoutPage() throws Exception {
        //Login as normal user
        MvcResult loginResult = mockMvc.perform(post("/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"andrew\", \"password\":\"password2\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        mockMvc.perform(get("/checkout").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(content().string(containsString("Finalize Your Order")))
                .andExpect(content().string(containsString("Shipping Information")))
                .andExpect(content().string(containsString("Payment Information")));

        //Test sidebar
        mockMvc.perform(get("/checkout").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your Cart is Empty")));

        //Add a book to the cart
        mockMvc.perform(post("/client/cart/add/{bookId}", 1).session(session))
                .andExpect(status().isOk());

        //Info should show after adding book.
        mockMvc.perform(get("/checkout").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Subtotal:")))
                .andExpect(content().string(containsString("Shipping:")))
                .andExpect(content().string(containsString("Order Total:")));
    }
}
