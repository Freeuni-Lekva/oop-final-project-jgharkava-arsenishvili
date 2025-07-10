document.addEventListener("DOMContentLoaded", function () {
    const quizId = document.getElementById("quizId").value;
    const titleElem = document.getElementById("quizTitle");
    const descriptionElem = document.getElementById("quizDescription");
    const limitElem = document.getElementById("quizTimeLimit");
    const categoryElem = document.getElementById("quizCategory");
    const orderSelect = document.getElementById("orderStatus");
    const placementSelect = document.getElementById("placementStatus");
    const correctionSelect = document.getElementById("correctionStatus");

    const originalTitle = titleElem.innerText.trim();
    const originalDescription = descriptionElem.innerText.trim();
    const originalLimit = limitElem.innerText.trim();
    const originalCategory = categoryElem.value;
    const originalOrder = orderSelect.value;
    const originalPlacement = placementSelect.value;
    const originalCorrection = correctionSelect.value;
    const originalRemoveTags = document.querySelectorAll('.removeTagBtn');
    const originalAddTags = document.querySelectorAll('.addTagBtn');

    let lastTitle = originalTitle;
    let lastDescription = originalDescription;
    let lastLimit = originalLimit;

    titleElem.addEventListener("blur", function () {
        const title = titleElem.innerText.trim();

        if(!title.trim()) {
            titleElem.innerText = lastTitle;
            alert("Quiz title cannot be empty");
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

        if(!description.trim()) {
            descriptionElem.innerText = lastDescription;
            alert("Quiz description cannot be empty");
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

        if(!isPositiveInteger(limit)) {
            limitElem.innerText = lastLimit;
            alert("Quiz time limit field must be a positive integer");
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

        if(newStatus === "one-page" && correctionSelect.value === "immediate-correction") {
            correctionSelect.value = "final-correction";
            correctionSelect.dispatchEvent(new Event("change"));
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

        if(newStatus === "immediate-correction" && placementSelect.value === "one-page") {
            placementSelect.value = "multiple-page";
            placementSelect.dispatchEvent(new Event("change"));
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

