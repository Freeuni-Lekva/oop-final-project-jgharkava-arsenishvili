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
        let hiddenInput = document.getElementById("questionTypeHidden");
        hiddenInput.value = selectedType;

        selectElement.disabled = true;

        answerCount = 1;

        if (selectedType === "matching") {
            addRightOption();
            addLeftOption();
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const selectedType = document.getElementById("questionType").value;

    if (selectedType === "multi-answer"){
        addAnswerGroup();
        return;
    }

    const firstMarkButtonMultiChoice = document.querySelector("#multiple-choice-answer-container .mark-button");
    if (firstMarkButtonMultiChoice) {
        const hiddenInput = firstMarkButtonMultiChoice.closest(".option-block").querySelector("input[name='isCorrect']");
        firstMarkButtonMultiChoice.addEventListener("click", () =>
        handleMarkButtonClick(firstMarkButtonMultiChoice, hiddenInput, "multiple-choice-answer-container")
    );
    }

    const firstMarkButtonMultiChoiceMultiAnswer = document.querySelector("#multi-choice-multi-answer-container .mark-button");
    if (firstMarkButtonMultiChoiceMultiAnswer){
        const hiddenInput = firstMarkButtonMultiChoiceMultiAnswer.closest(".option-block").querySelector("input[name='isCorrect']");
        firstMarkButtonMultiChoiceMultiAnswer.addEventListener("click", () =>
            handleMarkButtonClick(firstMarkButtonMultiChoiceMultiAnswer, hiddenInput, "multi-choice-multi-answer-container"));
    }
});

function addAnswerOption(containerId) {
    answerCount++;

    const container = document.getElementById(containerId);
    const isMultipleChoice = containerId === "multiple-choice-answer-container";
    const isMultipleChoiceMultiAnswer = containerId === "multi-choice-multi-answer-container";
    const labelText = (isMultipleChoice || isMultipleChoiceMultiAnswer) ? `Option ${answerCount}:` : `Answer ${answerCount}:`;
    const placeholderText = (isMultipleChoice || isMultipleChoiceMultiAnswer) ? "Type an option..." : "Type the correct answer...";

    const optionBlock = document.createElement("div");
    optionBlock.className = "option-block";

    const label = document.createElement("label");
    label.textContent = labelText;

    const textarea = document.createElement("textarea");
    textarea.name = "answer";
    textarea.placeholder = placeholderText;
    textarea.required = true;
    textarea.rows = 2;

    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "isCorrect";
    hiddenInput.value = "false";

    const buttonRow = document.createElement("div");
    buttonRow.className = "button-row";

    if (isMultipleChoice || isMultipleChoiceMultiAnswer) {
        const markButton = document.createElement("button");
        markButton.type = "button";
        markButton.className = "mark-button";
        markButton.textContent = "Mark as True";

        if (isMultipleChoice) {
            const alreadyMarked = container.querySelector(".mark-button.marked");
            if (alreadyMarked) {
                markButton.disabled = true;
            }
        }

        markButton.addEventListener("click", () =>
            handleMarkButtonClick(markButton, hiddenInput, containerId)
        );

        buttonRow.appendChild(markButton);
    }

    const addOptionButton = document.createElement("button");
    addOptionButton.type = "button";
    addOptionButton.textContent = (isMultipleChoice || isMultipleChoiceMultiAnswer) ?
        "Add another option..." : "Add another answer...";
    addOptionButton.onclick = function() { addAnswerOption(containerId); };
    buttonRow.appendChild(addOptionButton);

    optionBlock.appendChild(label);
    optionBlock.appendChild(textarea);
    optionBlock.appendChild(hiddenInput);
    optionBlock.appendChild(buttonRow);

    container.appendChild(optionBlock);
}

function handleMarkButtonClick(clickedButton, hiddenInput, containerId) {
    const container = document.getElementById(containerId);
    const isMultipleChoice = containerId === "multiple-choice-answer-container";

    if (isMultipleChoice) {
        const alreadyMarked = clickedButton.classList.contains("marked");

        if (alreadyMarked) {
            clickedButton.classList.remove("marked");
            clickedButton.textContent = "Mark as True";
            hiddenInput.value = "false";

            const allButtons = container.querySelectorAll(".mark-button");
            allButtons.forEach(btn => btn.disabled = false);
        } else {
            const allButtons = container.querySelectorAll(".mark-button");
            const allHiddenInputs = container.querySelectorAll("input[name='isCorrect']");

            allButtons.forEach(btn => {
                btn.classList.remove("marked");
                btn.textContent = "Mark as True";
                btn.disabled = true; // disable all first
            });

            allHiddenInputs.forEach(input => input.value = "false");

            clickedButton.classList.add("marked");
            clickedButton.textContent = "Marked as True";
            hiddenInput.value = "true";
            clickedButton.disabled = false;
        }

    } else {
        const marked = clickedButton.classList.toggle("marked");
        clickedButton.textContent = marked ? "Marked as True" : "Mark as True";
        hiddenInput.value = marked ? "true" : "false";
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


    const btn = document.createElement("button");
    btn.textContent = "+";
    btn.type = "button";
    btn.onclick = () => insertBlank(-1);
    container.appendChild(btn);

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

    if (selectedType === "multi-choice-multi-answers") {
        const container = document.getElementById("multi-choice-multi-answer-container");
        const markedButtons = container.querySelectorAll(".mark-button.marked");

        if (markedButtons.length === 0) {
            e.preventDefault();

            const firstTextarea = container.querySelector("textarea[name='answer']");
            firstTextarea.setCustomValidity("You must mark at least one option as correct.");
            firstTextarea.reportValidity();

            setTimeout(() => firstTextarea.setCustomValidity(""), 1000);
        }
    }
});

let multiAnswerCount = 0;

function addAnswerGroup(afterElement = null) {
    const container = document.getElementById("multi-answer-container");
    const groupId = multiAnswerCount++;

    const group = document.createElement("div");
    group.className = "answer-group";
    group.dataset.groupId = groupId.toString();

    group.innerHTML = `
        <label>Answer Text:</label>
        <textarea name="answerText-${groupId}" rows="2" required></textarea>

        <div class = "options-container" id = "options-${groupId}"></div>

        <button type="button" onclick="addOption(${groupId})">Add Option</button>
        <button type="button" onclick="insertAnswerBelow(this)">Insert Answer Below</button>
        <hr/>
    `;

    if (afterElement) {
        afterElement.insertAdjacentElement("afterend", group);
    } else {
        container.appendChild(group);
    }
}

function addOption(groupId) {
    const container = document.getElementById(`options-${groupId}`);
    const optionCount = container.querySelectorAll("input").length + 1;

    const label = document.createElement("label");
    label.textContent = `Option ${optionCount}:`;

    const input = document.createElement("input");
    input.type = "text";
    input.name = `option-${groupId}`;
    input.required = true;

    container.appendChild(label);
    container.appendChild(input);
}

function insertAnswerBelow(button) {
    const group = button.closest(".answer-group");
    addAnswerGroup(group);
}

document.getElementById("create-question-form").addEventListener("submit", function (e) {
    const container = document.getElementById("multi-answer-container");

    if (!container) return;

    const order = Array.from(container.querySelectorAll(".answer-group"))
        .map(group => group.dataset.groupId); // collect order of groupIds

    let hidden = document.createElement("input");
    hidden.type = "hidden";
    hidden.name = "answerOrder";
    hidden.id = "answerOrderInput";
    hidden.value = order.join(",");

    this.appendChild(hidden);
});

let rightOptionId = 0;
let leftOptionId = 0;

function addRightOption(value = "") {
    const rightContainer = document.getElementById("right-options");

    const wrapper = document.createElement("div");
    wrapper.className = "right-option-wrapper";

    const input = document.createElement("input");
    input.type = "text";
    input.name = `right-${rightOptionId}`;
    input.value = value;
    input.dataset.optionId = rightOptionId.toString();
    input.placeholder = "Right option";
    input.required = true;

    input.addEventListener("input", updateAllDropdowns);

    wrapper.appendChild(input);
    rightContainer.appendChild(wrapper);

    updateAllDropdowns();
    rightOptionId++;
}

function addLeftOption() {
    const leftContainer = document.getElementById("left-options");

    const wrapper = document.createElement("div");
    wrapper.className = "left-group";

    const input = document.createElement("input");
    input.type = "text";
    input.name = `left-${leftOptionId}`;
    input.placeholder = "Left option";
    input.required = true;

    const select = document.createElement("select");
    select.name = `match-${leftOptionId}`;
    select.required = true;
    select.className = "right-select";

    wrapper.appendChild(input);
    wrapper.appendChild(select);

    leftContainer.appendChild(wrapper);

    updateDropdown(select);
    leftOptionId++;
}

function updateAllDropdowns() {
    const dropdowns = document.querySelectorAll(".right-select");
    dropdowns.forEach(updateDropdown);
}

function updateDropdown(selectElement) {
    const rightInputs = document.querySelectorAll("#right-options input[type='text']");
    const currentValue = selectElement.value;

    selectElement.innerHTML = "";

    rightInputs.forEach(input => {
        const option = document.createElement("option");
        option.value = input.name;
        option.textContent = input.value || "(empty)";
        selectElement.appendChild(option);
    });

    if (currentValue) {
        selectElement.value = currentValue;
    }
}
