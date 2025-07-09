function toggleAchievements() {
    const section = document.getElementById("achievements-section");
    section.style.display = (section.style.display === "none") ? "block" : "none";
}

function handleFriendAction(action, friendId) {
    fetch("communication", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=${encodeURIComponent(action)}&friendId=${encodeURIComponent(friendId)}`
    })
    .then(res => res.ok ? res.text() : Promise.reject("Failed"))
    .then(() => {
        setTimeout(() => location.reload(), 500); // reload after a short delay
    })
    .catch(err => {
        console.error("Friend action failed:", err);
    });
}

function handleMessageAction(action, friendId) {
    let bodyParams = `action=${encodeURIComponent(action)}&friendId=${encodeURIComponent(friendId)}`;

    if (action === "send-challenge") {
        const quizInput = document.getElementById("quizName");
        const quizName = quizInput.value.trim();

        if (!quizName) {
            alert("Please enter a quiz name.");
            return;
        }

        bodyParams += `&quizName=${encodeURIComponent(quizName)}`;
    }

    if (action === "send-message") {
        const messageInput = document.getElementById("message");
        const message = messageInput.value.trim();

        if (!message) {
            alert("Please enter a message.");
            return;
        }

        bodyParams += `&message=${encodeURIComponent(message)}`;
    }

    fetch("communication", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: bodyParams
    })
        .then(res => res.ok ? res.text() : Promise.reject("Request failed"))
        .then(response => {
            // clear inputs after success
            if (action === "send-message") {
                document.getElementById("message").value = "";
              //  alert("Message sent successfully!");
            } else if (action === "send-challenge") {
                document.getElementById("quizName").value = "";
              //  alert("Challenge sent successfully!");
            }
        })
        .catch(err => {
            console.error("Error:", err);
            alert("Something went wrong.");
        });
}
