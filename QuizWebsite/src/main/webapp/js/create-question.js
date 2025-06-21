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

function addAnswerOption(containerId){
    answerCount++;

    const container = document.getElementById(containerId);

    const label = document.createElement("label");
    label.textContent = `Answer ${answerCount}:`;

    const textarea = document.createElement("textarea");
    textarea.name = "answer";
    textarea.placeholder = "Type the correct answer...";
    textarea.required = true;

    container.appendChild(document.createElement("br"));
    container.appendChild(label);
    container.appendChild(textarea);

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
            // Blank at start
            words.unshift("_____");
        } else if (selectedIndex === words.length) {
            // Blank at end
            words.push("_____");
        } else {
            // Blank after specific word
            words.splice(selectedIndex + 1, 0, "_____");
        }
    }

    document.getElementById("questionText").value = words.join(" ");
}

document.getElementById("create-question-form").addEventListener("submit", function (e) {
    if(document.getElementById("questionType").value !== "fill-in-the-blank") return;

    if(!blankInserted) {
        e.preventDefault();

        const input = document.getElementById("rawQuestionInput");

        input.setCustomValidity("You must insert a blank into the question.");
        input.reportValidity();

        setTimeout(() => input.setCustomValidity(""), 1000);
    }
})