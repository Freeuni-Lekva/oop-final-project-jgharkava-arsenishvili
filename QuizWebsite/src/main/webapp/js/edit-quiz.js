document.addEventListener("DOMContentLoaded", function () {
    const quizId = document.getElementById("quizId").value;
    const titleElem = document.getElementById("quizTitle");
    const descriptionElem = document.getElementById("quizDescription");
    const limitElem = document.getElementById("quizTimeLimit");
    const categoryElem = document.getElementById("quizCategory");
    const orderSelect = document.getElementById("orderStatus");
    const placementSelect = document.getElementById("placementStatus");
    const correctionSelect = document.getElementById("correctionStatus");

    let lastTitle = titleElem.innerText.trim();
    let lastDescription = descriptionElem.innerText.trim();
    let lastLimit = limitElem.innerText.trim();

    titleElem.addEventListener("blur", function () {
        const title = titleElem.innerText.trim();

        if(title === lastTitle) return;

        if(!title.trim()) {
            titleElem.innerText = lastTitle;
            alert("Quiz title cannot be empty");
            return;
        }

        if(title.length >= 60) {
            titleElem.innerText = lastTitle;
            alert("Quiz title is too long");
            return;
        }

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&title=${encodeURIComponent(title)}&field=title`
        }).then(res => {
            if (!res.ok) {
                if (res.status === 409) {
                    return res.text().then(msg => {
                        titleElem.innerText = lastTitle;
                        alert(msg); // "A quiz with this title already exists."
                    });
                } else {
                    console.error("Failed to update title");
                }
            } else {
                lastTitle = title;
            }
        }).catch(err => {
                alert("Error updating title");
                console.error(err);
        });
    });

    descriptionElem.addEventListener("blur", function () {
        const description = descriptionElem.innerText.trim();

        if(description === lastDescription) return;

        if(!description.trim()) {
            descriptionElem.innerText = lastDescription;
            alert("Quiz description cannot be empty");
            return;
        }

        if(description.length >= 1000) {
            descriptionElem.innerText = lastDescription;
            alert("Quiz description is too long");
            return;
        }

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&description=${encodeURIComponent(description)}&field=description`
        }).then(res => {
            if (!res.ok) console.error("Failed to update description");
            else lastDescription = description;
        }).catch(err => {
            alert("Error updating description");
            console.error(err);
        });
    });

    limitElem.addEventListener("blur", function () {
        const limit = limitElem.innerText.trim();

        if(limit === lastLimit) return;

        if(!isPositiveInteger(limit) || limit.startsWith("0")) {
            limitElem.innerText = lastLimit;
            alert("Quiz time limit field must be a positive integer");
            return;
        }

        if(limit.length >= 3) {
            limitElem.innerText = lastLimit;
            alert("Quiz time limit it too big");
            return;
        }

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&limit=${encodeURIComponent(limit)}&field=limit`
        }).then(res => {
            if (!res.ok) console.error("Failed to update description");
            else lastLimit = limit;
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

    document.querySelectorAll('.removeTagBtn').forEach(btn => handleRemoveTagBtnClick(btn, quizId));
    document.querySelectorAll('.addTagBtn').forEach(btn => handleAddTagBtnClick(btn, quizId));

    orderSelect.addEventListener('change', function () {
        const newStatus = this.value;

        fetch('edit-quiz', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&questionOrderStatus=${encodeURIComponent(newStatus)}&field=orderStatus`
        }).then(res => {
                if (res.ok) {
                    console.log("Question order status updated successfully.");
                } else {
                    alert("Failed to update order status.");
                }
            }).catch(err => {
                console.error(err);
            });
    });

    placementSelect.addEventListener('change', function () {
        const newStatus = this.value;

        if(newStatus === "one-page") {
            correctionSelect.value = "final-correction";
        }

        fetch('edit-quiz', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&questionPlacementStatus=${encodeURIComponent(newStatus)}&field=placementStatus`
        }).then(res => {
            if (res.ok) {
                console.log("Question placement status updated successfully.");
            } else {
                alert("Failed to update placement status.");
            }
        }).catch(err => {
                console.error(err);
        });
    });

    correctionSelect.addEventListener('change', function () {
        const newStatus = this.value;

        if(newStatus === "immediate-correction") {
            placementSelect.value = "multiple-page";
        }

        fetch('edit-quiz', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&questionCorrectionStatus=${encodeURIComponent(newStatus)}&field=correctionStatus`
        }).then(res => {
            if (res.ok) {
                console.log("Question correction status updated successfully.");
            } else {
                alert("Failed to update correction status.");
            }
        }).catch(err => {
            console.error(err);
        });
    });
});

function handleRemoveTagBtnClick(btn, quizId){
    btn.addEventListener('click', function () {
        const tagId = this.getAttribute('data-id');
        const tagElement = this.closest('.tag');

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&tagId=${encodeURIComponent(tagId)}&field=removeTag`
        }).then(res => {
            if (res.ok) {
                document.getElementById('availableTags').appendChild(tagElement);

                this.textContent = '+';
                this.classList.remove('removeTagBtn');
                this.classList.add('addTagBtn');

                this.removeEventListener('click', arguments.callee);

                handleAddTagBtnClick(this, quizId);
            } else {
                alert("Failed to remove tag.");
            }
        }).catch(err => {
            console.error(err);
        });
    });
}

function handleAddTagBtnClick(btn, quizId){
    btn.addEventListener('click', function () {
        const tagId = this.getAttribute('data-id');
        const tagElement = this.closest('.tag');

        fetch("edit-quiz", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `current-quiz-id=${encodeURIComponent(quizId)}&tagId=${encodeURIComponent(tagId)}&field=addTag`
        }).then(res => {
            if (res.ok) {
                document.getElementById('quizTags').appendChild(tagElement);

                this.textContent = '×';
                this.classList.remove('addTagBtn');
                this.classList.add('removeTagBtn');

                this.removeEventListener('click', arguments.callee);

                handleRemoveTagBtnClick(this, quizId);
            } else {
                alert("Failed to add tag.");
            }
        }).catch(err => {
            console.error(err);
        });
    });
}

function isPositiveInteger(value) {
    return /^\d+$/.test(value) && parseInt(value) > 0;
}

