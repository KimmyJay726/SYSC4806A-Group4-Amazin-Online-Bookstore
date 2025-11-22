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

    window.addBookToCart = function(bookId, maxBooks) {
        fetch(`/client/cart/add/${bookId}`, {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok){
                throw new Error("Error adding " + bookId + " to your cart")
            }
            return response.json();
        })
        .then(() => {
            console.log("Added " + bookId + " to your shopping cart");
            refreshButtonsForBook(bookId, maxBooks);
        })
        .catch(err => {
            alert(err.message);
        });
    };

    window.removeBookFromCart = function(bookId, maxBooks) {
        fetch(`/client/cart/remove/${bookId}`, { method: 'POST' })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error removing " + bookId + " from your cart");
                }
                return response.json();
            })
            .then(() => {
                console.log("Removed " + bookId + " from your shopping cart");
                refreshButtonsForBook(bookId, maxBooks);
            })
            .catch(err => alert(err.message));
    };

    // Helper function to show or hide the Add and Remove buttons for a book
    function refreshButtonsForBook(bookId, maxBooks) {
        fetch(`/client/me`)
            .then(response => response.json())  // parse the JSON response
            .then(client => {
                // Get the number of copies the client owns
                const clientCopies = countCopies(client.shoppingCart, bookId);

                // Show or hide Add button
                const addBtn = document.getElementById(`addToCart-${bookId}`);
                addBtn.style.visibility = (clientCopies < maxBooks) ? "visible" : "hidden";

                // Show or hide Remove button
                const removeBtn = document.getElementById(`removeFromCart-${bookId}`);
                removeBtn.style.visibility = (clientCopies > 0) ? "visible" : "hidden";

                // Reload page to ensure visibility changes are applied by the browser
                window.location.reload();
            })
            .catch(err => console.error("Error refreshing buttons:", err));
    }

    // Helper function to count the number of books with the same id
    function countCopies(cart, bookId) {
        let count = 0;
        for (let i = 0; i < cart.length; i++) {
            if (cart[i] === bookId) {
                count++;
            }
        }
        return count;
    }
});
