/**
 * checkout.js
 * Handles client-side logic for the checkout page, primarily form validation.
 * Cart rendering and calculation are handled by the Spring Controller and Thymeleaf.
 */

document.addEventListener('DOMContentLoaded', () => {

    // ----------------------------------------------------
    // 1. DOM ELEMENTS (for validation/interaction)
    // ----------------------------------------------------
    const checkoutForm = document.getElementById('checkoutForm');

    // We get the total from the button element, which was populated by Thymeleaf
    const buttonTotalSpan = document.getElementById('button-total');


    // ----------------------------------------------------
    // 2. FORM VALIDATION FUNCTION
    // ----------------------------------------------------

    /**
     * Adds basic client-side form validation for payment fields.
     */
    function setupFormValidation() {

        checkoutForm.addEventListener('submit', (event) => {
            // Retrieve values from the form inputs
            const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');
            const expiry = document.getElementById('expiry').value;
            const cvv = document.getElementById('cvv').value;
            const total = buttonTotalSpan ? buttonTotalSpan.textContent : "$0.00"; // Get the final price

            let isValid = true;

            // Validation Checks
            if (!/^\d{16}$/.test(cardNumber)) {
                alert('💳 Please enter a valid 16-digit card number.');
                isValid = false;
            } else if (!/^\d{2}\/\d{2}$/.test(expiry)) {
                alert('📅 Please enter the expiry date in MM/YY format (e.g., 12/26).');
                isValid = false;
            } else if (!/^\d{3,4}$/.test(cvv)) {
                alert('🔒 Please enter a valid 3 or 4 digit CVV.');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault(); // Stop form submission if validation fails
            } else {
                // Client-side confirmation before sending to the server
                // The server will handle actual payment processing, cart clearing, and database updates.
                const confirmation = confirm(`Ready to place order for ${total}?`);

                if (!confirmation) {
                    event.preventDefault(); // User cancelled
                }
            }
        });
    }

    // ----------------------------------------------------
    // 3. INITIALIZATION
    // ----------------------------------------------------
    setupFormValidation();
});