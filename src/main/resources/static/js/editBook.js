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

        const formData = new FormData();
        formData.append("bookISBN", document.getElementById("isbn").value);
        formData.append("bookTitle", document.getElementById("title").value);
        formData.append("bookAuthor", document.getElementById("author").value);
        formData.append("bookPublisher", document.getElementById("publisher").value);
        formData.append("bookPrice", document.getElementById("price").value);
        formData.append("numBooksAvailableForPurchase", document.getElementById("numBooks").value);
        formData.append("bookDescription", document.getElementById("description").value);

        const pictureFile = document.getElementById("bookPicture").files[0];
        if (pictureFile) formData.append("bookPicture", pictureFile);

        console.log("Submitting form data for book:", id);

        fetch(`/books/${id}/editBook`, {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update book");
                return response.json();
            })
            .then(() => window.location.href = "/inventory")
            .catch(err => console.error("Error updating book:", err));
    });
});