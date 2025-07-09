function sendChallenge(button, friendId) {
    fetch("challenge-friend", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "challenged-id=" + encodeURIComponent(friendId) +
            "&current-quiz-id=" + encodeURIComponent(document.getElementById("quiz-id-hidden").value)
    });

    button.disabled = true;
}

let selectedRating = 0;

document.querySelectorAll("#rating-stars span").forEach(star => {
    star.addEventListener("mouseover", function () {
        highlightStars(this.dataset.value);
    });

    star.addEventListener("mouseout", function () {
        highlightStars(selectedRating);
    });

    star.addEventListener("click", function () {
        selectedRating = this.dataset.value;
        highlightStars(selectedRating);
    });
});

function highlightStars(rating) {
    document.querySelectorAll("#rating-stars span").forEach(star => {
        star.style.color = star.dataset.value <= rating ? "gold" : "#ccc";
    });
}

function submitReview() {
    const review = document.getElementById("quiz-review").value.trim();
    const status = document.getElementById("rating-status");

    if(selectedRating === 0) {
        status.textContent = "Please select a star rating.";
        status.style.color = "red";
        return;
    }

    fetch("review-quiz", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            "current-quiz-id": document.getElementById("quiz-id-hidden").value,
            "rating": selectedRating,
            "review": review
        })
    }).then(res => {
        if (res.ok) {
            status.textContent = "Thanks for your feedback!";
            status.style.color = "green";
        } else {
            status.textContent = "Failed to submit feedback.";
            status.style.color = "red";
        }
    }).catch(err => {
        console.error(err);
        status.textContent = "Error submitting feedback.";
        status.style.color = "red";
    });
}


