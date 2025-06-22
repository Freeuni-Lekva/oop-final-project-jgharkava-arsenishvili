let answerCount = 1;

function showQuestionForm() {
    const selectElement = document.getElementById("questionType");
    const selectedType = selectElement.value;

    const allForms = document.querySelectorAll(".questionForm");

    allForms.forEach(form => {
        const isSelected = form.id === `${selectedType}-form`;
        form.style.display = isSelected ? "block" : "none";
        const inputs = form.querySelectorAll("input, textarea, select, button");
        inputs.forEach(input => input.disabled = !isSelected);
    });

    if (selectedType) {
        selectElement.disabled = true;
        answerCount = 1;
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // Hook up the first button
    const firstMarkButton = document.querySelector("#multiple-choice-answer-container .mark-button");
    if (firstMarkButton) {
        firstMarkButton.addEventListener("click", () =>
        handleMarkButtonClick(firstMarkButton, "multiple-choice-answer-container")
    );
}
});

function addAnswerOption(containerId) {
    answerCount++;

    const container = document.getElementById(containerId);
    const isMultipleChoice = containerId === "multiple-choice-answer-container";
    const labelText = isMultipleChoice ? `Option ${answerCount}:` : `Answer ${answerCount}:`;
    const placeholderText = isMultipleChoice ? "Type an option..." : "Type the correct answer...";

    const optionBlock = document.createElement("div");
    optionBlock.className = "option-block";

    const label = document.createElement("label");
    label.textContent = labelText;

    const textarea = document.createElement("textarea");
    textarea.name = "answer";
    textarea.placeholder = placeholderText;
    textarea.required = true;
    textarea.rows = 2;

    const buttonRow = document.createElement("div");
    buttonRow.className = "button-row";

    if (isMultipleChoice){
        const markButton = document.createElement("button");
        markButton.type = "button";
        markButton.className = "mark-button";
        markButton.textContent = "Mark as True";

        // Disable the new button if something is already marked
        const alreadyMarked = container.querySelector(".mark-button.marked");

        if (alreadyMarked) {
            markButton.disabled = true;
        }

        markButton.addEventListener("click", () =>
            handleMarkButtonClick(markButton, containerId)
        );

        buttonRow.appendChild(markButton);
    }

    optionBlock.appendChild(label);
    optionBlock.appendChild(textarea);
    optionBlock.appendChild(buttonRow);

    container.appendChild(optionBlock);
}

function handleMarkButtonClick(clickedButton, containerId) {
    const container = document.getElementById(containerId);
    const allButtons = container.querySelectorAll(".mark-button");

    if (clickedButton.classList.contains("marked")) {
        clickedButton.classList.remove("marked");
        clickedButton.textContent = "Mark as True";
        allButtons.forEach(btn => btn.disabled = false);
    } else {
        allButtons.forEach(btn => {
        btn.classList.remove("marked");
        btn.textContent = "Mark as True";
        btn.disabled = true;
    });
        clickedButton.classList.add("marked");
        clickedButton.textContent = "Marked as True";
        clickedButton.disabled = false;
    }
}


let blankInserted = false;
let selectedIndex = -1;

function renderQuestionWithBlanks() {
    const input = document.getElementById("rawQuestionInput").value;
    const container = document.getElementById("interactiveQuestionPreview");

    if(input === "") {
        container.innerHTML = "";
        return;
    }

    if (blankInserted) {
        blankInserted = false;
        selectedIndex = -1;
    }

    container.innerHTML = "";
    const words = input.trim().split(/\s+/);

    // not needed
    //if (!blankInserted) {
        const btn = document.createElement("button");
        btn.textContent = "+";
        btn.type = "button";
        btn.onclick = () => insertBlank(-1);
        container.appendChild(btn);
    //}

    words.forEach((word, index) => {
        const wordSpan = document.createElement("span");
        wordSpan.textContent = " " + word + " ";
        container.appendChild(wordSpan);

        if (!blankInserted && index < words.length - 1) {
            const btn = document.createElement("button");
            btn.textContent = "+";
            btn.type = "button";
            btn.onclick = () => insertBlank(index);
            container.appendChild(btn);
        }
    });

    if (!blankInserted && words.length > 0) {
        const endBtn = document.createElement("button");
        endBtn.textContent = "+";
        endBtn.type = "button";
        endBtn.onclick = () => insertBlank(words.length);
        container.appendChild(endBtn);
    }

    updateFinalQuestion();
}


function insertBlank(index) {
    if (blankInserted) {
        alert("Only one blank is allowed.");
        return;
    }
    selectedIndex = index;
    blankInserted = true;
    renderQuestionWithBlankApplied();
}

function renderQuestionWithBlankApplied() {
    const input = document.getElementById("rawQuestionInput").value;
    const container = document.getElementById("interactiveQuestionPreview");
    container.innerHTML = "";

    const words = input.trim().split(/\s+/);

    words.forEach((word, index) => {
        if (index === 0 && selectedIndex === -1) {
            const blankSpan = document.createElement("span");
            blankSpan.innerHTML = "<strong>_____</strong> ";
            container.appendChild(blankSpan);
        }

        const span = document.createElement("span");
        span.textContent = word + " ";
        container.appendChild(span);

        if (index === selectedIndex) {
            const blankSpan = document.createElement("span");
            blankSpan.innerHTML = "<strong>_____</strong> ";
            container.appendChild(blankSpan);
        }
    });

    if (selectedIndex === words.length) {
        const blankSpan = document.createElement("span");
        blankSpan.innerHTML = "<strong>_____</strong> ";
        container.appendChild(blankSpan);
    }

    updateFinalQuestion();
}

function updateFinalQuestion() {
    const input = document.getElementById("rawQuestionInput").value;
    const words = input.trim().split(/\s+/);

    if (blankInserted) {
        if (selectedIndex === -1) {
            words.unshift("_____");
        } else if (selectedIndex === words.length) {
            words.push("_____");
        } else {
            words.splice(selectedIndex + 1, 0, "_____");
        }
    }

    document.getElementById("questionText").value = words.join(" ");
}

document.getElementById("create-question-form").addEventListener("submit", function (e) {
    const selectedType = document.getElementById("questionType").value;

    if (selectedType === "fill-in-the-blank") {
        if (!blankInserted) {
            e.preventDefault();

            const input = document.getElementById("rawQuestionInput");
            input.setCustomValidity("You must insert a blank into the question.");
            input.reportValidity();

            setTimeout(() => input.setCustomValidity(""), 1000);
        }
    }

    if (selectedType === "multiple-choice") {
        const container = document.getElementById("multiple-choice-answer-container");
        const marked = container.querySelector(".mark-button.marked");

        if (!marked) {
            e.preventDefault();

            const firstTextarea = container.querySelector("textarea[name='answer']");
            firstTextarea.setCustomValidity("You must mark one option as correct.");
            firstTextarea.reportValidity();

            setTimeout(() => firstTextarea.setCustomValidity(""), 1000);
        }
    }
});
