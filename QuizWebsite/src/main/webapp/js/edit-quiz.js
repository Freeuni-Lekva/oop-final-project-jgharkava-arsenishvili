document.addEventListener("DOMContentLoaded", function () {
    const quizId = document.getElementById("quizId").value;

    const titleElem = document.getElementById("quizTitle");
    const descriptionElem = document.getElementById("quizDescription");
    const limitElem = document.getElementById("quizTimeLimit");
    const categoryElem = document.getElementById("quizCategory")

    titleElem.addEventListener("blur", function () {
        const title = titleElem.innerText.trim();

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&title=${encodeURIComponent(title)}&field=title`
        }).then(res => {
            if (!res.ok) console.error("Failed to update title");
        }).catch(err => {
            alert("Error updating title");
            console.error(err);
        });
    });

    descriptionElem.addEventListener("blur", function () {
        const description = descriptionElem.innerText.trim();

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&description=${encodeURIComponent(description)}&field=description`
        }).then(res => {
            if (!res.ok) console.error("Failed to update description");
        }).catch(err => {
            alert("Error updating description");
            console.error(err);
        });
    });

    limitElem.addEventListener("blur", function () {
        const limit = limitElem.innerText.trim();

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&limit=${encodeURIComponent(limit)}&field=limit`
        }).then(res => {
            if (!res.ok) console.error("Failed to update description");
        }).catch(err => {
            alert("Error updating description");
            console.error(err);
        });
    });

    categoryElem.addEventListener("change", function () {
        const categoryId = categoryElem.value;

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&category=${encodeURIComponent(categoryId)}&field=category`
        }).then(res => {
            if (!res.ok) {
                console.error("Failed to update category");
            } else {
                console.log("Category updated successfully");
            }
        }).catch(err => {
            alert("Error updating category");
            console.error(err);
        });
    });
});
