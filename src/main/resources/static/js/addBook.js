$(document).ready(function () {
    const $addBookForm = $("#addBookForm");
    const $addBookMessage = $("#addBookMessage");

    // Handle Add Book
    $addBookForm.submit(function (e) {
        e.preventDefault(); // Stop the browser from reloading the page
        const formData = new FormData(this);
        const file = $('#bookPicture')[0].files[0];
        if (file) {
            formData.append('file', file);
        }

        // Check required fields
        const title = $("#bookTitle").val().trim();
        const isbn = $("#bookISBN").val().trim();
        const description = $("#bookDescription").val().trim();
        const author = $("#bookAuthor").val().trim();
        const publisher = $("#bookPublisher").val().trim();
        const price = $("#bookPrice").val().trim();
        const numBooks = $("#numBooks").val();

        if (!title || !isbn || !description || !author || !publisher || !price || !numBooks) {
            showMessage($addBookMessage, "Please fill out all fields.");
            return;
        }

        $.ajax({
            url: "/books/addBook",
            method: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function () {
                showMessage($addBookMessage, "Book added successfully!", true);
                // Clear the form after successful submission
                $addBookForm[0].reset();
                $("#bookPicture").val('');
                setTimeout(function() {
                    $addBookMessage.addClass("hidden");
                }, 5000);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    showMessage($addBookMessage, "Unauthorized: Only owners can add books.");
                } else {
                    showMessage($addBookMessage, "Error adding book. Try again.");
                }
            },
        });
    });

    // Helper function to display a message
    function showMessage($element, text, success = false) {
        $element
            .removeClass("hidden")
            .toggleClass("error", !success)
            .toggleClass("success", success)
            .text(text);
    }
});
