document.addEventListener('DOMContentLoaded', function () {
    const searchBtn = document.getElementById('submit-btn');

    if (searchBtn){
        searchBtn.addEventListener('click', function () {
            const isbn = document.getElementById('isbn').value;
            const title = document.getElementById('title').value;
            const author = document.getElementById('author').value;
            const publisher = document.getElementById('publisher').value;

            // Build query string
            const params = new URLSearchParams();
            if (isbn) params.append('isbn', isbn);
            if (title) params.append('title', title);
            if (author) params.append('author', author);
            if (publisher) params.append('publisher', publisher);

            fetch(`/inventory/update?${params.toString()}`)
                .then(response => response.json())
                .then(data => {
                    const tableBody = document.getElementById('results');
                    tableBody.innerHTML = ''; // Clear previous results

                    if (!data || data.length === 0) {
                        tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">No books found.</td></tr>`;
                        return;
                    }

                    data.forEach(book => {
                        const row = `
                        <tr>
                            <td>${book.id}</td>
                            <td>${book.bookISBN}</td>
                            <td>${book.bookTitle}</td>
                            <td>
                                ${book.bookPicture
                            ? `<img src="${book.bookPicture}" width="80" height="100">`
                            : "No Image"}
                            </td>
                            <td>${book.bookAuthor}</td>
                            <td>${book.bookPublisher}</td>
                            <td>${book.bookDescription}</td>
                        </tr>`;
                        tableBody.innerHTML += row;
                    });
                })
        });
    }

    window.editBook = function(bookId) {
        fetch('/books/' + bookId)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Book not found");
                }
                return response.json();
            })
            .then(book => {
                sessionStorage.setItem('editBookData', JSON.stringify(book));
                window.location.href = '/inventory/edit?id=' + bookId;
            })
            .catch(err => {
                alert(err.message);
            }
        );
    }

    window.addBookToCart = function(bookId) {
        // Decrement stock for the book
        fetch(`/books/${bookId}/purchaseBook`, {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Book not found");
            }
            return response.json();
        })
        .then(book => {
            // Add the book to the client's shopping cart
            return fetch(`/client/cart/add/${book.id}`, { method: 'POST' });
        })
        .then(response => {
            if (!response.ok){
                throw new Error("Error adding " + bookId + " to your cart")
            }
            return response.json();
        })
        .then(updatedClient => {
            console.log("Updated shopping cart:", updatedClient.shoppingCart);
            window.location.reload();
        })
        .catch(err => {
            alert(err.message);
        });
    };

    window.removeBookFromCart = function(bookId) {
        // Increment stock for the book
        fetch(`/books/${bookId}/returnBook`, {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error increasing stock for bookId" + bookId);
            }
            return response.json();
        })
        .then(book => {
            // Remove the book from the client's shopping cart
            return fetch(`/client/cart/remove/${book.id}`, { method: 'POST' });
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error removing " + bookId + " from your cart");
            }
            return response.json();
        })
        .then(updatedClient => {
            console.log("Updated shopping cart:", updatedClient.shoppingCart);
            window.location.reload();
        })
        .catch(err => {
            alert(err.message);
        });
    };
});
