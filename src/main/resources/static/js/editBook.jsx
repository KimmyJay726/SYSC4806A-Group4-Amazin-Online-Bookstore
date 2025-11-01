document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("editBookForm");
    form.addEventListener("submit", function (event) {
        event.preventDefault();

        // Get book ID from the input field
        const id = document.getElementById("bookId").value;
        if (!id) {
            alert("Please enter a Book ID.");
            return;
        }

        const bookData = {
            bookISBN: document.getElementById("isbn").value,
            bookTitle: document.getElementById("title").value,
            bookAuthor: document.getElementById("author").value,
            bookPublisher: document.getElementById("publisher").value,
            bookDescription: document.getElementById("description").value,
            bookPicture: document.getElementById("bookPicture").value
        };

        console.log("Updating book:", id, bookData);

        fetch(`/books/${id}/editBook`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(bookData)
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update book");
                return response.json();
            })
            .then(() => window.location.href = "/inventory")
            .catch(err => console.error("Error updating book:", err));
    });
});