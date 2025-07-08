function toggleOtherInput(checkbox) {
    const input = document.getElementById("otherTagInput");

    if (checkbox.checked) {
        input.style.display = "inline-block";
        input.focus();
    } else {
        input.style.display = "none";
        input.value = "";
        checkbox.value = ""; // Clear the checkbox value
    }

    otherTagInput.setCustomValidity("");
}

function updateOtherValue() {
    const input = document.getElementById("otherTagInput");
    const checkbox = document.getElementById("tag_other");

    checkbox.value = input.value;
}

const form = document.getElementById("quizForm");
const quizTitleInput = document.getElementById("quizTitle");
const durationInput = document.getElementById("quizDuration");
const otherTagInput = document.getElementById("otherTagInput");
const quizDescriptionInput = document.getElementById("quizDescription");

form.addEventListener("submit", function (e) {
    e.preventDefault();

    const quizTitle = quizTitleInput.value.trim();
    const duration = durationInput.value.trim();
    const otherTag = otherTagInput.value.trim();
    const quizDescription = quizDescriptionInput.value.trim();

    if (quizTitle === "") {
        quizTitleInput.setCustomValidity("Quiz title cannot be empty");
        quizTitleInput.reportValidity();
        return;
    }

    if(quizDescription === "") {
        quizDescriptionInput.setCustomValidity("Description cannot be empty");
        quizDescriptionInput.reportValidity();
        return;
    }

    if (!isPositiveInteger(duration)) {
        durationInput.setCustomValidity("Time must be a positive integer");
        durationInput.reportValidity();
        return;
    }

    isQuizTitleUnique(quizTitle, function (titleUnique) {
        if (!titleUnique) {
            quizTitleInput.setCustomValidity("This quiz title is already taken");
            quizTitleInput.reportValidity();
            return;
        }

        if (document.getElementById("tag_other").checked && otherTag !== "") {
            isTagUnique(otherTag, function (tagUnique) {
                if (!tagUnique) {
                    otherTagInput.setCustomValidity("Tag name already exists");
                    otherTagInput.reportValidity();
                    return;
                }

                form.submit();
            });

            return;
        }

        form.submit();
    });
});

function isPositiveInteger(value) {
    return /^\d+$/.test(value) && parseInt(value) > 0;
}

function isQuizTitleUnique(title, callback) {
    fetch(`validate-creation?action=validate-quiz-name&title=${encodeURIComponent(title)}`)
        .then(response => response.text())
        .then(result => {
            callback(result === "true");
        })
        .catch(error => {
            console.error("Error checking quiz title:", error);
            callback(false);
        });
}

function isTagUnique(tagName, callback) {
    fetch(`validate-creation?action=validate-tag-name&tag-name=${encodeURIComponent(tagName)}`)
        .then(response => response.text())
        .then(result => {
            callback(result === "true");
        })
        .catch(error => {
            console.error("Error checking tag:", error);
            callback(false);
        });
}

// handling placement-correction correlation
function enforceCorrectionOptions() {
    const placementRadios = document.getElementsByName("placementType");
    const correctionRadios = document.getElementsByName("correctionType");

    const selectedPlacement = getSelectedValue(placementRadios);
    const immediateRadio = Array.from(correctionRadios).find(r => r.value === "immediate-correction");
    const finalRadio = Array.from(correctionRadios).find(r => r.value === "final-correction");

    if (selectedPlacement === "one-page") {
        immediateRadio.disabled = true;

        if (immediateRadio.checked) {
            immediateRadio.checked = false;
            finalRadio.checked = true;
        }
    } else {
        immediateRadio.disabled = false;
    }
}

document.getElementsByName("placementType").forEach(radio => {
    radio.addEventListener("change", enforceCorrectionOptions);
});

function getSelectedValue(radioNodeList) {
    for (let r of radioNodeList) {
        if (r.checked) return r.value;
    }
    return null;
}

quizTitleInput.addEventListener("input", function () {
    quizTitleInput.setCustomValidity("");
});

otherTagInput.addEventListener("input", function() {
    otherTagInput.setCustomValidity("");
})
