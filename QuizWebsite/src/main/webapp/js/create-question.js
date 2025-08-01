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
        document.getElementById("discardQuestionContainer").style.display = "block";

        let hiddenInput = document.getElementById("questionTypeHidden");
        hiddenInput.value = selectedType;

        selectElement.disabled = true;

        answerCount = 1;

        if (selectedType === "matching") {
            addRightOption();
            addLeftOption();
        }
    }

    if (selectedType === "multi-answer") {
        addAnswerGroup();
        addAnswerGroup();
    }
    else if (selectedType === "multiple-choice")
        addAnswerOption('multiple-choice-answer-container');
    else if (selectedType === "multi-choice-multi-answers")
        addAnswerOption('multi-choice-multi-answer-container');
    else if(selectedType === "matching")
        addRightOption();
}

window.addEventListener("DOMContentLoaded", function (){
    const finishButton = document.getElementById("finishQuizButton");
    if (finishButton && hasQuestionFromSession){
        finishButton.disabled = false;
    }
})

document.addEventListener("DOMContentLoaded", function () {
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

    if(answerCount === 6) {
        document.getElementById("multiple-choice-add-button").disabled = true;
        document.getElementById("multi-choice-multi-answer-add-button").disabled = true;
    }

    if(answerCount === 11) {
        document.getElementById("fill-in-the-blank-add-button").disabled = true;
        document.getElementById("picture-response-add-button").disabled = true;
        document.getElementById("question-response-add-button").disabled = true;x
    }

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

    if (containerId === "multi-answer-container") {
        const addOptionButton = document.createElement("button");
        addOptionButton.type = "button";
        addOptionButton.textContent = "Add another answer...";
        addOptionButton.onclick = function() { addAnswerOption(containerId); };
        buttonRow.appendChild(addOptionButton);
    }

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

    container.innerHTML = "";

    if (input.trim() === "") {
        return;
    }

    const words = input.trim().split(/\s+/);
    blankInserted = false;
    selectedIndex = -1;

    // Add + button before first word
    const startWrapper = document.createElement("span");
    startWrapper.className = "word-button-wrapper";

    const startBtn = document.createElement("button");
    startBtn.textContent = "+";
    startBtn.type = "button";
    startBtn.onclick = () => insertBlank(-1);

    startWrapper.appendChild(startBtn);
    container.appendChild(startWrapper);

    words.forEach((word, index) => {
        // Word
        const wordWrapper = document.createElement("span");
        wordWrapper.className = "word-button-wrapper";

        const wordSpan = document.createElement("span");
        wordSpan.textContent = word;

        wordWrapper.appendChild(wordSpan);
        container.appendChild(wordWrapper);

        // + button between words
        if (index < words.length - 1) {
            const plusWrapper = document.createElement("span");
            plusWrapper.className = "word-button-wrapper";

            const plusBtn = document.createElement("button");
            plusBtn.textContent = "+";
            plusBtn.type = "button";
            plusBtn.onclick = () => insertBlank(index);

            plusWrapper.appendChild(plusBtn);
            container.appendChild(plusWrapper);
        }
    });

    // + button at end
    const endWrapper = document.createElement("span");
    endWrapper.className = "word-button-wrapper";

    const endBtn = document.createElement("button");
    endBtn.textContent = "+";
    endBtn.type = "button";
    endBtn.onclick = () => insertBlank(words.length);

    endWrapper.appendChild(endBtn);
    container.appendChild(endWrapper);

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

    if (selectedIndex === -1) {
        const blankSpan = document.createElement("span");
        blankSpan.innerHTML = "<strong>_____</strong>";
        container.appendChild(blankSpan);

        if (words.length > 0) {
            container.appendChild(document.createTextNode(" "));
        }
    }

    words.forEach((word, index) => {
        const span = document.createElement("span");
        span.textContent = word;
        container.appendChild(span);

        if (index === selectedIndex) {
            container.appendChild(document.createTextNode(" "));
            const blankSpan = document.createElement("span");
            blankSpan.innerHTML = "<strong>_____</strong>";
            container.appendChild(blankSpan);
        }

        container.appendChild(document.createTextNode(" "));
    });

    if (selectedIndex === words.length) {
        const blankSpan = document.createElement("span");
        blankSpan.innerHTML = "<strong>_____</strong>";
        container.appendChild(blankSpan);
        container.appendChild(document.createTextNode(" "));
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

            setTimeout(() => input.setCustomValidity(""), 2000);

            return;
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

            setTimeout(() => firstTextarea.setCustomValidity(""), 2000);

            return;
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

            setTimeout(() => firstTextarea.setCustomValidity(""), 2000);

            return;
        }
    }

    const container = document.getElementById(`${selectedType}-form`);

    const answers = container.querySelectorAll(`[name="answer"]`);

    answers.forEach(answerField => {
        if(answerField.value.trim() === "") {
            e.preventDefault();
            answerField.setCustomValidity("Please fill out this field.");
            answerField.reportValidity();

            setTimeout(() => answerField.setCustomValidity(""), 2000);
        }

        if(answerField.value.includes("¶")) {
            e.preventDefault();
            answerField.setCustomValidity("This field cannot include symbol - ¶.");
            answerField.reportValidity();

            setTimeout(() => answerField.setCustomValidity(""), 2000);
        }

        if(answerField.value.trim().length >= 100) {
            e.preventDefault();
            answerField.setCustomValidity("This field is too long.");
            answerField.reportValidity();

            setTimeout(() => answerField.setCustomValidity(""), 2000);
        }
    })

    if(selectedType === "picture-response") {
        const imageUrl = container.querySelector(`[name="imageUrl"]`);

        if(imageUrl.value.trim() === "") {
            e.preventDefault();

            imageUrl.setCustomValidity("Please fill out this field.");
            imageUrl.reportValidity();

            setTimeout(() => imageUrl.setCustomValidity(""), 2000);
        }

        if(imageUrl.value.trim().length >= 2040) {
            e.preventDefault();

            imageUrl.setCustomValidity("This field is too long.");
            imageUrl.reportValidity();

            setTimeout(() => imageUrl.setCustomValidity(""), 2000);

        }
    }
    else if(selectedType === "fill-in-the-blank") {
        const rawQuestionInput = document.getElementById("rawQuestionInput");

        if(rawQuestionInput.value.trim() === "") {
            e.preventDefault();

            rawQuestionInput.setCustomValidity("Please fill out this field.");
            rawQuestionInput.reportValidity();

            setTimeout(() => rawQuestionInput.setCustomValidity(""), 2000);
        }

        if(rawQuestionInput.value.includes("_")) {
            e.preventDefault();

            rawQuestionInput.setCustomValidity("This field cannot contain underscore (_).");
            rawQuestionInput.reportValidity();

            setTimeout(() => rawQuestionInput.setCustomValidity(""), 2000);
        }

        if(rawQuestionInput.value.trim().length >= 1000) {
            e.preventDefault();

            rawQuestionInput.setCustomValidity("This field is too long.");
            rawQuestionInput.reportValidity();

            setTimeout(() => rawQuestionInput.setCustomValidity(""), 2000);
        }
    }
    else {
        const questionText = container.querySelector(`[name="questionText"]`);

        if(questionText.value.trim() === "") {
            e.preventDefault();

            questionText.setCustomValidity("Please fill out this field.");
            questionText.reportValidity();

            setTimeout(() => questionText.setCustomValidity(""), 2000);
        }

        if(questionText.value.trim().length >= 1000) {
            e.preventDefault();

            questionText.setCustomValidity("This field is too long.");
            questionText.reportValidity();

            setTimeout(() => questionText.setCustomValidity(""), 2000);
        }
    }

    if(selectedType === "multi-answer") {
        const textAreas = document.querySelectorAll
        ("#multi-answer-form .answer-group textarea, #multi-answer-form .answer-group input[type='text']");

        textAreas.forEach(textarea => {
            if (textarea.value.trim() === "") {
                e.preventDefault();

                textarea.setCustomValidity("Please fill out this field.");
                textarea.reportValidity();

                setTimeout(() => textarea.setCustomValidity(""), 2000);
            }

            if(textarea.value.includes("¶")) {
                e.preventDefault();

                textarea.setCustomValidity("This field cannot include symbol - ¶");
                textarea.reportValidity();

                setTimeout(() => textarea.setCustomValidity(""), 2000);
            }

            if(textarea.value.trim().length >= 100) {
                e.preventDefault();

                textarea.setCustomValidity("This field is too long.");
                textarea.reportValidity();

                setTimeout(() => textarea.setCustomValidity(""), 2000);
            }
        });
    }

    if(selectedType === "matching") {
        const matchOptions = container.querySelectorAll('.left-group input[type="text"], .right-option-wrapper input[type="text"]');

        matchOptions.forEach(option => {
            if(option.value.trim() === "") {
                e.preventDefault();

                option.setCustomValidity("Please fill out this field.");
                option.reportValidity();

                setTimeout(() => option.setCustomValidity(""), 2000);
            }

            if(option.closest(".right-option-wrapper") && option.value.includes("not selected")) {
                e.preventDefault();

                option.setCustomValidity("This field cannot include 'not selected'.");
                option.reportValidity();

                setTimeout(() => option.setCustomValidity(""), 2000);
            }

            if(option.value.trim().length >= 100) {
                e.preventDefault();

                option.setCustomValidity("This field is too long.");
                option.reportValidity();

                setTimeout(() => option.setCustomValidity(""), 2000);
            }
        })
    }

    const multiAnswerContainer = document.getElementById("multi-answer-container");

    if (!multiAnswerContainer) return;

    const order = Array.from(multiAnswerContainer.querySelectorAll(".answer-group"))
        .map(group => group.dataset.groupId); // collect order of groupIds

    let hidden = document.createElement("input");
    hidden.type = "hidden";
    hidden.name = "answerOrder";
    hidden.id = "answerOrderInput";
    hidden.value = order.join(",");

    this.appendChild(hidden);
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

        <button id="add-option-button-${groupId}" type="button" onclick="addOption(${groupId})">Add Option</button>
        <button type="button" class="insert-below-button" onclick="insertAnswerBelow(this)">Insert Answer Below</button>
        <hr/>
    `;

    if (afterElement) {
        afterElement.insertAdjacentElement("afterend", group);
    } else {
        container.appendChild(group);
    }

    if (multiAnswerCount === 10) {
        document.getElementById("multi-answer-add-button").disabled = true;
        Array.from(document.getElementsByClassName("insert-below-button")).forEach(btn => btn.disabled = true);
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

    if(optionCount === 10) {
        document.getElementById(`add-option-button-${groupId}`).disabled = true;
    }
}

function insertAnswerBelow(button) {
    const group = button.closest(".answer-group");
    addAnswerGroup(group);
}

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

    if(rightOptionId === 10) {
        document.getElementById("add-right-option-button").disabled = true;
    }
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

    if(leftOptionId === 10) {
        document.getElementById("add-left-option-button").disabled = true;
    }
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

function confirmDiscard(){
    return confirm("Are you sure you want to discard all changes? This cannot be undone.");
}

function confirmQuestionDiscard(){
    return confirm("Are you sure you want to discard this question?");
}

