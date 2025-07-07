function toggleAchievements() {
    const section = document.getElementById("achievements-section");
    section.style.display = (section.style.display === "none") ? "block" : "none";
}
function sendMessage() {
    const recipient = document.getElementById("recipient").value.trim();
    const message = document.getElementById("message").value.trim();
    const status = document.getElementById("message-status");

    if (!recipient || !message) {
        status.innerText = "Please enter both recipient and message.";
        return;
    }

    fetch("communication", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `action=send-message&recipient=${encodeURIComponent(recipient)}&message=${encodeURIComponent(message)}`
    })
        .then(response => {
            if (response.ok) {
                status.innerText = "Message sent!";
                document.getElementById("recipient").value = "";
                document.getElementById("message").value = "";
            } else {
                status.innerText = "Failed to send message.";
            }
        })
        .catch(() => {
            status.innerText = "Error occurred while sending.";
        });
}
