$(document).ready(function() {
    // Prefill form from sessionStorage
    var book = JSON.parse(sessionStorage.getItem("editBookData"));
    if (book) {
        $('#bookId').val(book.id);
        $('#bookTitle').val(book.bookTitle);
        $('#bookAuthor').val(book.bookAuthor);
        $('#bookISBN').val(book.bookISBN);
        $('#bookDescription').val(book.bookDescription);
        $('#bookPublisher').val(book.bookPublisher);
    }

    // Handle form submission via AJAX
    $('#editBookForm').submit(function(e) {
        e.preventDefault();

        var bookId = $('#bookId').val();
        var bookData = {
            bookTitle: $('#bookTitle').val(),
            bookAuthor: $('#bookAuthor').val(),
            bookISBN: $('#bookISBN').val(),
            bookDescription: $('#bookDescription').val(),
            bookPublisher: $('#bookPublisher').val()
        };

        $.ajax({
            url: '/books/' + bookId + '/editBook',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(bookData)
        });
    });
});