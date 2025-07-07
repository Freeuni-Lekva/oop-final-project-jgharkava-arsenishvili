document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".accept, .delete").forEach(button => {
        button.addEventListener("click", (e) => {
            const userId = e.target.dataset.userId;
            const action = e.target.classList.contains("accept") ? "accept" : "delete";

            fetch("communication", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `friendId=${encodeURIComponent(userId)}&action=${encodeURIComponent(action)}`
            })
                .then(response => response.text())
                .then(result => {
                    // Remove this friend request from the page
                    const container = e.target.closest(".friend-request");
                    container.remove();

                    // Update the friend request count
                    const countElement = document.getElementById("request-count");
                    const remaining = document.querySelectorAll(".friend-request").length;

                    if (remaining > 0) {
                        countElement.textContent = `You have ${remaining} friend request(s).`;
                    } else {
                        countElement.textContent = "No new friend requests.";
                    }
                })
                .catch(err => {
                    console.error("Error handling friend request:", err);
                    alert("Something went wrong.");
                });
        });
    });
});