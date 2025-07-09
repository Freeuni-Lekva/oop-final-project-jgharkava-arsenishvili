let lastHiddenTime;

document.addEventListener("visibilitychange", function () {
    if (document.visibilityState === "hidden") {
        lastHiddenTime = Date.now();
    } else if (document.visibilityState === "visible") {
        const now = Date.now();
        if (lastHiddenTime && now - lastHiddenTime > 10000) {
            location.reload();
        }
    }
});

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".accept-request-button, .delete-request-button").forEach(button => {
        button.addEventListener("click", (e) => {
            const userId = e.target.dataset.userId;
            const action = e.target.classList.contains("accept-request-button") ? "accept" : "delete";

            fetch("communication", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `friendId=${encodeURIComponent(userId)}&action=${encodeURIComponent(action)}`
            })
                .then(response => response.text())
                .then(result => {
                    const container = e.target.closest(".friend-request");
                    if (container) container.remove();

                    const remaining = document.querySelectorAll(".friend-request").length;
                    const countWrapper = document.getElementById("request-count-wrapper");
                    const countElement = document.getElementById("request-count");

                    if (remaining > 0) {
                        if (countElement) countElement.textContent = remaining;
                    } else {
                        const scrollablePane = document.querySelector(".friend-requests .scrollable-pane");
                        if (scrollablePane) scrollablePane.remove();

                        if (countWrapper) {
                            countWrapper.outerHTML = `<p class="text-small">No new friend requests.</p>`;
                        }
                    }
                })
                .catch(err => {
                    console.error("Error handling friend request:", err);
                    alert("Something went wrong.");
                });
        });
    });
});

document.addEventListener("DOMContentLoaded", () => {
    function autoFadeOut(id, delay = 3000) {
        const el = document.getElementById(id);
        if (el) {
            setTimeout(() => {
                el.style.transition = "opacity 0.5s ease";
                el.style.opacity = "0";
                setTimeout(() => el.remove(), 500);
            }, delay);
        }
    }

    autoFadeOut("error-message"); // Lookup
    autoFadeOut("login-error");   // Login
});

function updateChallengeCount() {
    const countSpan = document.getElementById("challenge-count");
    const pane = document.querySelector(".scrollable-pane");
    const remaining = document.querySelectorAll(".challenge-item").length;

    if (countSpan) countSpan.textContent = remaining;

    if (remaining === 0) {
        const wrapper = document.getElementById("challenge-count-wrapper");
        if (wrapper) {
            wrapper.innerHTML = '<p class="text-small">No new challenges.</p>';
        }
        if (pane) pane.remove();
    }
}


document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".accept-button, .delete-button").forEach(button => {
        button.addEventListener("click", (e) => {
            const challengeId = e.target.dataset.challengeId;
            const quizId = e.target.dataset.quizId;
            const isAccept = e.target.classList.contains("accept-button");
            const action = "delete-challenge"; // same for both

            //prevent double click
            e.target.disabled = true;

            fetch("communication", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `action=${encodeURIComponent(action)}&challengeId=${encodeURIComponent(challengeId)}`
            })
                .then(response => response.text())
                .then(() => {
                    if (isAccept) {
                        // Redirect to take quiz page
                        window.location.href = `quiz-overview.jsp?current-quiz-id=${encodeURIComponent(quizId)}`;
                    } else {
                        // Remove challenge from UI
                        const challengeItem = document.getElementById(`challenge-${challengeId}`);
                        if (challengeItem) challengeItem.remove();

                        const remaining = document.querySelectorAll(".challenge-item").length;
                        const countWrapper = document.getElementById("challenge-count-wrapper");
                        const countSpan = document.getElementById("challenge-count");

                        if (remaining > 0) {
                            if (countSpan) countSpan.textContent = remaining;
                        } else {
                            const scrollablePane = document.querySelector(".challenges .scrollable-pane");
                            if (scrollablePane) scrollablePane.remove();

                            if (countWrapper) {
                                countWrapper.outerHTML = `<p class="text-small">No new challenges.</p>`;
                            }
                        }
                    }
                })
                .catch(err => {
                    console.error("Error handling challenge:", err);
                    alert("Something went wrong.");
                });
        });
    });
});




document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("messageModal");
    const modalText = document.getElementById("modal-message-text");
    const closeButton = document.querySelector(".close-button");
    const countSpan = document.getElementById("message-count");
    const wrapper = document.getElementById("message-count-wrapper");

    document.querySelectorAll(".view-message-button").forEach(button => {
        button.addEventListener("click", () => {
        const message = button.dataset.message;
        modalText.textContent = message;
        modal.style.display = "flex";

        const messageItem = button.closest(".message-item");
            if (messageItem && !messageItem.classList.contains("viewed")) {
                messageItem.classList.add("viewed");

                // Update count
                if (countSpan) {
                    let count = parseInt(countSpan.textContent);
                    if (count > 0) {
                        count -= 1;
                        countSpan.textContent = count;
                    }
                }
            }
        });
    });

    closeButton.addEventListener("click", () => {
        modal.style.display = "none";
    });
});

function sendMessage() {
    const recipient = document.getElementById("recipient").value.trim();
    const message = document.getElementById("message").value.trim();
    const status = document.getElementById("message-status");

    if (!recipient || !message) {
        status.textContent = "Please enter a recipient and a message.";
        status.style.color = "red";
        return;
    }

    fetch("communication", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=send-message&recipient=${encodeURIComponent(recipient)}&message=${encodeURIComponent(message)}`
    })
    .then(response => {
      if (response.ok) {
        status.textContent = "Message sent!";
        status.style.color = "green";
        document.getElementById("recipient").value = "";
        document.getElementById("message").value = "";
      } else if (response.status === 404) {
        status.textContent = "Recipient not found.";
        status.style.color = "red";
      } else {
        status.textContent = "Something went wrong.";
        status.style.color = "red";
      }
})
    .catch(error => {
        console.error(error);
        status.textContent = "Network error.";
        status.style.color = "red";
    });
}


//for going back to the page
document.addEventListener('DOMContentLoaded', function() {

    // Load initial data
    refreshUserData();

    // Handle back button cache
    window.addEventListener('pageshow', function(event) {
        if (event.persisted) {
            refreshUserData();
        }
    });

    // Handle tab focus
    window.addEventListener('focus', function() {
        refreshUserData();
    });

    function refreshUserData() {
        const userId = getUserIdFromURL(); // You'll need to implement this

        fetch(`/api/users/${userId}`)
            .then(response => response.json())
            .then(data => updateUserInterface(data))
            .catch(error => console.error('Error:', error));
    }

    function updateUserInterface(userData) {
        // Update all the elements that might have changed
        const elements = {
            'user-name': userData.name,
            'user-status': userData.status,
            'user-email': userData.email,
            'last-login': userData.lastLogin
        };

        Object.keys(elements).forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = elements[id];
            }
        });
    }

    function getUserIdFromURL() {
        // Extract user ID from URL like /users/123
        const path = window.location.pathname;
        const matches = path.match(/\/users\/(\d+)/);
        return matches ? matches[1] : null;
    }
});



