document.addEventListener('DOMContentLoaded', function () {
    const searchBtn = document.getElementById('submit-btn');

    if (!searchBtn) return;

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
                            <td>${book.bookAuthor}</td>
                            <td>${book.bookPublisher}</td>
                            <td>${book.bookDescription}</td>
                        </tr>`;
                    tableBody.innerHTML += row;
                });
            })
    });
});