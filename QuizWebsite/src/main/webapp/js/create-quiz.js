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

/*TODO change this*/
form.addEventListener("submit", async function (e) {
    const quizTitle = quizTitleInput.value.trim();
    const duration = durationInput.value.trim();
    const otherTag = otherTagInput.value.trim();
    const quizDescription = quizDescriptionInput.value.trim();

    if (quizTitle === "") {
        e.preventDefault();

        quizTitleInput.setCustomValidity("Please fill out this field.");
        quizTitleInput.reportValidity();

        setTimeout(() => quizTitleInput.setCustomValidity(""), 2000);

        return;
    }

    if (quizTitle.length >= 64) {
        e.preventDefault();

        quizTitleInput.setCustomValidity("Quiz title is too long.");
        quizTitleInput.reportValidity();

        setTimeout(() => quizTitleInput.setCustomValidity(""), 2000);

        return;

    }

    if(quizDescription === "") {
        e.preventDefault();

        quizDescriptionInput.setCustomValidity("Please fill out this field.");
        quizDescriptionInput.reportValidity();

        setTimeout(() => quizDescriptionInput.setCustomValidity(""), 2000);

        return;
    }

    if(quizDescription.length >= 1000) {
        e.preventDefault();

        quizDescriptionInput.setCustomValidity("Quiz description is too long.");
        quizDescriptionInput.reportValidity();

        setTimeout(() => quizDescriptionInput.setCustomValidity(""), 2000);

        return;
    }

    if (!isPositiveInteger(duration) || duration.startsWith("0")) {
        e.preventDefault();

        durationInput.setCustomValidity("Time must be a positive integer");
        durationInput.reportValidity();

        setTimeout(() => durationInput.setCustomValidity(""), 2000);

        return;
    }

    if(duration.length >= 3) {
        e.preventDefault();

        durationInput.setCustomValidity("Quiz time limit is too big.");
        durationInput.reportValidity();

        setTimeout(() => durationInput.setCustomValidity(""), 2000);

        return;
    }

    if(document.getElementById("tag_other").checked && otherTag.length >= 64) {
        e.preventDefault();

        otherTagInput.setCustomValidity("Tag name is too long.");
        otherTagInput.reportValidity();

        setTimeout(() => otherTagInput.setCustomValidity(""), 2000);

        return;
    }

    e.preventDefault();

    const titleUnique = await isQuizTitleUnique(quizTitle);

    if (!titleUnique) {
        quizTitleInput.setCustomValidity("This quiz title is already taken");
        quizTitleInput.reportValidity();

        setTimeout(() => quizTitleInput.setCustomValidity(""), 2000);
        return;
    }

    if (document.getElementById("tag_other").checked && otherTag !== "") {
        const tagUnique = await isTagUnique(otherTag);

        if (!tagUnique) {
            otherTagInput.setCustomValidity("This tag name is already taken");
            otherTagInput.reportValidity();

            setTimeout(() => otherTagInput.setCustomValidity(""), 2000);

            return;
        }
    }

    form.submit();
});

function isPositiveInteger(value) {
    return /^\d+$/.test(value) && parseInt(value) > 0;
}

async function isQuizTitleUnique(title) {
    try {
        const response = await fetch(`validate-creation?action=validate-quiz-name&title=${encodeURIComponent(title)}`);
        const result = await response.text();
        return result === "true";
    } catch (error) {
        console.error("Error checking quiz title:", error);
        return false;
    }
}

async function isTagUnique(tagName) {
    try {
        const response = await fetch(`validate-creation?action=validate-tag-name&tag-name=${encodeURIComponent(tagName)}`);
        const result = await response.text();
        return result === "true";
    } catch (error) {
        console.error("Error checking tag:", error);
        return false;
    }
}

// handling placement-correction correlation
function enforceCorrectionOptions() {
    const placementRadios = document.getElementsByName("placementType");
    const correctionRadios = document.getElementsByName("correctionType");

    const selectedPlacement = getSelectedValue(placementRadios);
    const immediateRadio = Array.from(correctionRadios).find(r => r.value === "immediate-correction");
    const finalRadio = Array.from(correctionRadios).find(r => r.value === "final-correction");

    if (selectedPlacement === "one-page") {
        if (immediateRadio.checked) {
            immediateRadio.checked = false;
            finalRadio.checked = true;
        }

        immediateRadio.disabled = true;
    } else {
        immediateRadio.disabled = false;
    }
}

function getSelectedValue(radioNodeList) {
    for (let r of radioNodeList) {
        if (r.checked) return r.value;
    }
    return null;
}

document.getElementsByName("placementType").forEach(radio => {
    radio.addEventListener("change", enforceCorrectionOptions);
});
