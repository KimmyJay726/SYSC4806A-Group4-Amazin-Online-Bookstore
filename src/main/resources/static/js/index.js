$(document).ready(function () {
    const $usernameDisplay = $("#usernameDisplay");
    const $loginLogoutBtn = $("#loginLogoutBtn");

    // Configure nav bar based on whether user is logged in
    $.get("/client/me", function (client) {
        if (client && client.username) {
            // Logged in: Display username and change to Logout button
            $usernameDisplay.text(client.username);
            $loginLogoutBtn.text("Logout");
            $loginLogoutBtn.off("click").on("click", logoutUser);
        } else {
            // Guest user
            resetToGuest();
        }
    }).fail(resetToGuest);

    // Logout the current user
    function logoutUser() {
        $.post("/client/logout", function () {
            resetToGuest();
        });
    }

    // Reset the nav bar to Guest defaults
    function resetToGuest() {
        $usernameDisplay.text("Guest User");
        $loginLogoutBtn.text("Login / Register");
        $loginLogoutBtn.off("click").on("click", function () {
            window.location.href = "/login-register";
        });
    }
});
