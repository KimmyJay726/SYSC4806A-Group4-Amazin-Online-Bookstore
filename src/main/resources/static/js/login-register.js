$(document).ready(function () {
    const $loginForm = $("#loginForm");
    const $signupForm = $("#signupForm");
    const $loginMessage = $("#loginMessage");
    const $signupMessage = $("#signupMessage");

    // Show sign up form
    $("#showSignup").click(() => {
        $loginForm.addClass("hidden");
        $signupForm.removeClass("hidden");
        $loginMessage.addClass("hidden");
    });

    // Show login form
    $("#showLogin").click(() => {
        $signupForm.addClass("hidden");
        $loginForm.removeClass("hidden");
        $signupMessage.addClass("hidden");
    });

    // Handle Login
    $loginForm.submit(function (e) {
        e.preventDefault(); // Stop the browser from reloading the page
        const username = $("#loginUsername").val().trim();
        const password = $("#loginPassword").val();

        if (!username || !password) {
            showMessage($loginMessage, "Please fill out all fields.");
            return;
        }

        $.ajax({
            url: "/client/login",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ username, password }),
            success: function () {
                showMessage($loginMessage, "Login successful! Redirecting...", true);
                setTimeout(() => {
                    window.location.href = "/"; // Redirect to main page (index.html)
                }, 1000);
            },
            error: function () {
                showMessage($loginMessage, "Invalid username or password.");
            },
        });
    });

    // Handle Sign Up
    $signupForm.submit(function (e) {
        e.preventDefault(); // Stop the browser from reloading the page
        const username = $("#signupUsername").val().trim();
        const password = $("#signupPassword").val();

        if (!username || !password) {
            showMessage($signupMessage, "Please fill out all fields.");
            return;
        }

        // Check if username exists first
        $.ajax({
            url: `/client/username/${encodeURIComponent(username)}`,
            method: "GET",
            success: function () {
                showMessage($signupMessage, "Username is already taken.");
            },
            error: function () {
                // Register new user
                $.ajax({
                    url: "/client/register",
                    method: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({ username, password }),
                    success: function () {
                        showMessage($signupMessage, "Account created! You can now log in.", true);
                        setTimeout(() => $("#showLogin").click(), 1500);
                    },
                    error: function () {
                        showMessage($signupMessage, "Error creating account. Try again.");
                    },
                });
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
