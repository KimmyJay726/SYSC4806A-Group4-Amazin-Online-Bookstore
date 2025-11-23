document.addEventListener('DOMContentLoaded', function () {
    let allBooks = []; 
    
    fetch('/books/all')
        .then(response => response.json())
        .then(data => allBooks = data);
    
    function substringMatch(search, target) {
        if (!search) return true;
        return target.toLowerCase().includes(search.toLowerCase());
    }
    
    function performSubstringSearch() {
        const isbn = document.getElementById('isbn').value;
        const title = document.getElementById('title').value;
        const author = document.getElementById('author').value;
        const publisher = document.getElementById('publisher').value;
        
        const filteredBooks = allBooks.filter(book => {
            return substringMatch(isbn, book.bookISBN || '') &&
                   substringMatch(title, book.bookTitle || '') &&
                   substringMatch(author, book.bookAuthor || '') &&
                   substringMatch(publisher, book.bookPublisher || '');
        });
        
        updateTable(filteredBooks);
    }
    
    function updateTable(data) {
        const tableBody = document.getElementById('results');
        tableBody.innerHTML = '';
        
        if (!data || data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="10" style="text-align:center;">No books found.</td></tr>`;
            return;
        }
        
        data.forEach(book => {
            const row = `
            <tr>
                <td>${book.id}</td>
                <td>${book.bookISBN}</td>
                <td>${book.bookTitle}</td>
                <td>
                    ${book.bookPicture ? `<img src="${book.bookPicture}" width="80" height="100">` : "No Image"}
                </td>
                <td>${book.bookAuthor}</td>
                <td>${book.bookPublisher}</td>
                <td>$${book.bookPrice ? book.bookPrice.toFixed(2) : '0.00'}</td>
                <td>${book.numBooksAvailableForPurchase}</td>
                <td>${book.bookDescription}</td>
                <td></td>
            </tr>`;
            tableBody.innerHTML += row;
        });
    }
    
    ['isbn', 'title', 'author', 'publisher'].forEach(id => {
        const input = document.getElementById(id);
        if (input) {
            input.addEventListener('input', performSubstringSearch);
        }
    });

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
