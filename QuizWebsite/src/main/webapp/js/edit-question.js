document.addEventListener("DOMContentLoaded", () => {

    function updateDeleteButtonState(answersContainer) {
        const answerBlocks = answersContainer.querySelectorAll(".answer-block");
        const deleteButtons = answersContainer.querySelectorAll(".delete-answer-btn");
        const shouldDisable = answerBlocks.length <= 1;

        deleteButtons.forEach(btn => btn.disabled = shouldDisable);
    }

    function createAnswerBlock(answerId = null, answerText = "") {
        const block = document.createElement("div");
        block.className = "answer-block";
        if (answerId) block.dataset.answerId = answerId;
        block.dataset.answerText = answerText;

        block.innerHTML = `
        <textarea class="answer-text">${answerText}</textarea>
        <button class="save-answer-btn">Save</button>
        <button class="delete-answer-btn">Delete</button>`;

        // Save
        block.querySelector(".save-answer-btn").addEventListener("click", () => {
            const newText = block.querySelector(".answer-text").value.trim();
            const oldText = block.dataset.answerText || "";
            const answerId = block.dataset.answerId;

            const isNew = !oldText;

            const payload = {
                action: 'updateAnswer',
                answerId: answerId,
                newText: newText,
                oldText: oldText,
                isNew: isNew
            };

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        block.dataset.answerText = newText;
                        alert("Saved!");
                    } else {
                        alert("Failed to save.");
                    }
                });
        });


        // Delete
        block.querySelector(".delete-answer-btn").addEventListener("click", () => {
            const answerId = block.dataset.answerId;
            const answerText = block.dataset.answerText;
            const container = block.closest(".answers");

            if (!confirm("Are you sure you want to delete this answer?")) return;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: 'removeAnswer',
                    answerId: answerId,
                    answerText: answerText
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        block.remove();
                        updateDeleteButtonState(container);
                    } else {
                        alert("Failed to delete.");
                    }
                });
        });

        return block;
    }

    // Add Option button logic
    document.querySelectorAll(".add-answer-btn").forEach(button => {
        button.addEventListener("click", () => {
            const container = button.closest(".answers");

            const existingAnswerId = container.querySelector(".answer-block")?.dataset.answerId || null;
            console.log(existingAnswerId);

            const newBlock = createAnswerBlock(existingAnswerId);  // pass it here

            container.insertBefore(newBlock, button);
            updateDeleteButtonState(container);
        });
    });

    // Initialize Save/Delete for existing answers
    document.querySelectorAll(".answer-block").forEach(block => {
        const container = block.closest(".answers");

        block.querySelector(".save-answer-btn").addEventListener("click", () => {
            const newText = block.querySelector(".answer-text").value.trim();
            const oldText = block.dataset.answerText;
            const answerId = block.dataset.answerId;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: 'updateAnswer',
                    answerId: answerId,
                    newText: newText,
                    oldText: oldText
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        block.dataset.answerText = newText;
                        alert("Saved!");
                    } else {
                        alert("Failed to save.");
                    }
                });
        });

        block.querySelector(".delete-answer-btn").addEventListener("click", () => {
            const answerId = block.dataset.answerId;
            const answerText = block.dataset.answerText;

            if (!confirm("Are you sure you want to delete this answer?")) return;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: 'removeAnswer',
                    answerId: answerId,
                    answerText: answerText
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        block.remove();
                        updateDeleteButtonState(container);
                    } else {
                        alert("Failed to delete.");
                    }
                });
        });
    });

    // Disable delete if only one answer per question
    document.querySelectorAll(".answers").forEach(container => {
        updateDeleteButtonState(container);
    });

    // Save question text/image logic
    document.querySelectorAll(".save-question-btn").forEach(button => {
        button.addEventListener("click", () => {
            const questionId = button.dataset.id;
            const block = document.querySelector(`.question-block[data-question-id='${questionId}']`);

            let questionText = null;

            // If it's a fill-in-the-blank question, use hidden input
            const fillInBlank = block.querySelector(".final-question-text");
            if (fillInBlank) {
                questionText = fillInBlank.value.trim();
            } else {
                questionText = block.querySelector(".question-text")?.value?.trim() || null;
            }

            const imageUrl = block.querySelector(".image-url")?.value?.trim() || null;

            const info = {
                action: 'updateQuestionText',
                questionId: questionId,
                questionText: questionText,
                imageUrl: imageUrl
            };

            fetch("edit-question", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(info)
            }).then(res => res.json())
                .then(json => {
                    console.log("Saved!");
                })
                .catch(err => {
                    console.error("Error saving question", err);
                    alert("Error saving question");
                });
        });
    });

    // Delete question
    document.querySelectorAll(".delete-question-btn").forEach(button => {
        button.addEventListener("click", () => {
            const questionId = button.dataset.id;
            if (!confirm("Are you sure you want to delete this question?")) return;

            const info = {
                action: 'delete',
                questionId: questionId
            };

            fetch("edit-question", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(info)
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        document.querySelector(`.question-block[data-question-id='${questionId}']`).remove();
                    } else {
                        alert("Failed to delete question");
                    }
                })
                .catch(err => {
                    console.error("Error deleting question", err);
                    alert("Error deleting question");
                });
        });
    });


    // Save Text
    document.body.addEventListener("click", function(event) {
        if (event.target.classList.contains("save-question-picture-btn")) {
            const questionId = event.target.dataset.id;
            const questionBlock = document.querySelector(`.question-block[data-question-id='${questionId}']`);
            const textarea = questionBlock.querySelector(".question-picture-text");
            const newText = textarea.value.trim();

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "updateQuestionText",
                    questionId: questionId,
                    questionText: newText
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        alert("Question text saved!");
                    } else {
                        alert("Failed to save question text.");
                    }
                });
        }
    });

    // Delete Text
    document.body.addEventListener("click", function(event) {
        if (event.target.classList.contains("delete-question-picture-btn")) {
            const questionId = event.target.dataset.id;
            if (!confirm("Delete question text?")) return;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "deleteQuestionText",
                    questionId: questionId
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        // Remove textarea and Save/Delete buttons, show Add Text button
                        const questionBlock = document.querySelector(`.question-block[data-question-id='${questionId}']`);
                        questionBlock.querySelector(".question-picture-text").remove();
                        questionBlock.querySelector(".save-question-picture-btn").remove();
                        questionBlock.querySelector(".delete-question-picture-btn").remove();

                        const addTextBtn = document.createElement("button");
                        addTextBtn.type = "button";
                        addTextBtn.className = "add-question-text-btn";
                        addTextBtn.dataset.id = questionId;
                        addTextBtn.textContent = "Add Text";

                        questionBlock.insertBefore(addTextBtn, questionBlock.querySelector(".answers"));
                    } else {
                        alert("Failed to delete question text.");
                    }
                });
        }
    });

    // Add Text
    document.body.addEventListener("click", function(event) {
        if (event.target.classList.contains("add-question-text-btn")) {
            const questionId = event.target.dataset.id;
            const questionBlock = document.querySelector(`.question-block[data-question-id='${questionId}']`);

            // Remove Add Text button
            event.target.remove();

            // Create textarea + Save + Delete buttons
            const label = document.createElement("label");
            label.innerHTML = `
            <textarea class="question-picture-text" placeholder="Enter question text..."></textarea>
            `;

            const saveBtn = document.createElement("button");
            saveBtn.type = "button";
            saveBtn.className = "save-question-picture-btn";
            saveBtn.dataset.id = questionId;
            saveBtn.textContent = "Save Text";

            const deleteBtn = document.createElement("button");
            deleteBtn.type = "button";
            deleteBtn.className = "delete-question-picture-btn";
            deleteBtn.dataset.id = questionId;
            deleteBtn.textContent = "Delete Text";

            questionBlock.insertBefore(label, questionBlock.querySelector(".answers"));
            questionBlock.insertBefore(saveBtn, questionBlock.querySelector(".answers"));
            questionBlock.insertBefore(deleteBtn, questionBlock.querySelector(".answers"));
        }
    });

    document.querySelectorAll(".fill-in-the-blank-block").forEach(block => {
        const questionId = block.dataset.questionId;
        const rawInput = block.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
        const hiddenInput = block.querySelector(`input.final-question-text[data-question-id='${questionId}']`);
        const preview = block.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);

        if (!rawInput || !hiddenInput || !preview) return;

        // Initialize state for this question
        if (!state[questionId]) {
            state[questionId] = { blankInserted: false, selectedIndex: -1 };
        }

        // Check if there's an existing blank in the hidden input
        const hiddenText = hiddenInput.value;
        if (hiddenText.includes("_")) {
            // Extract the raw text (without blanks)
            const rawText = hiddenText.replace(/_/g, "").replace(/\s+/g, " ").trim();
            rawInput.value = rawText;

            // Find the position of the blank in the original text
            const words = hiddenText.trim().split(/\s+/);
            const blankIndex = words.indexOf("_");
            if (blankIndex !== -1) {
                state[questionId].blankInserted = true;
                state[questionId].selectedIndex = blankIndex;
            }
        }

        // Render the initial preview with blank highlighted
        renderQuestionWithBlankApplied(questionId);

        // Listen to raw input changes to update preview and reset blank state
        rawInput.addEventListener("input", () => {
            state[questionId].blankInserted = false;
            state[questionId].selectedIndex = -1;
            renderQuestionWithBlanks(questionId);
        });
    });

});

const state = {};

// Updated renderExistingQuestionWithBlanks to accept container element
function renderExistingQuestionWithBlanks(questionText, container) {
    container.innerHTML = "";

    const words = questionText.trim().split(/\s+/);
    words.forEach(word => {
        const span = document.createElement("span");
        if (word === "_") {
            span.textContent = "_____";
            span.style.fontWeight = "bold";
        } else {
            span.textContent = word + " ";
        }
        container.appendChild(span);
    });
}

function renderQuestionWithBlanks(questionId) {
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
    const container = document.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);
    if (!inputElem || !container) return;

    const input = inputElem.value;

    if (!state[questionId]) {
        state[questionId] = { blankInserted: false, selectedIndex: -1 };
    }
    let { blankInserted, selectedIndex } = state[questionId];

    if (input.trim() === "") {
        container.innerHTML = "";
        return;
    }

    if (blankInserted) {
        state[questionId].blankInserted = false;
        state[questionId].selectedIndex = -1;
        blankInserted = false;
        selectedIndex = -1;
    }

    container.innerHTML = "";

    const words = input.trim().split(/\s+/);

    // Add "+" button before first word
    container.appendChild(createBlankButton(questionId, -1));

    words.forEach((word, index) => {
        const wordSpan = document.createElement("span");
        wordSpan.textContent = " " + word + " ";
        container.appendChild(wordSpan);

        if (!blankInserted && index < words.length - 1) {
            container.appendChild(createBlankButton(questionId, index + 1));
        }
    });

    // Add "+" button after last word
    if (!blankInserted && words.length > 0) {
        container.appendChild(createBlankButton(questionId, words.length));
    }

    updateFinalQuestion(questionId);
}

function createBlankButton(questionId, index) {
    const btn = document.createElement("button");
    btn.textContent = "+";
    btn.type = "button";
    btn.onclick = () => insertBlank(questionId, index);
    return btn;
}

function insertBlank(questionId, index) {
    if (!state[questionId]) {
        state[questionId] = { blankInserted: false, selectedIndex: -1 };
    }

    if (state[questionId].blankInserted) {
        alert("Only one blank is allowed.");
        return;
    }

    state[questionId].selectedIndex = index;
    state[questionId].blankInserted = true;

    renderQuestionWithBlankApplied(questionId);
}

function renderQuestionWithBlankApplied(questionId) {
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
    const container = document.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);
    if (!inputElem || !container) return;

    const input = inputElem.value;
    const { selectedIndex } = state[questionId];
    container.innerHTML = "";

    const words = input.trim().split(/\s+/);

    // Add words and blank
    for (let i = 0; i <= words.length; i++) {
        if (i === selectedIndex) {
            const blankSpan = document.createElement("span");
            blankSpan.innerHTML = "<strong>_____</strong>";
            blankSpan.style.fontWeight = "bold";
            container.appendChild(blankSpan);
        }

        if (i < words.length && words[i]) {
            const wordSpan = document.createElement("span");
            wordSpan.textContent = words[i] + " ";
            container.appendChild(wordSpan);
        }
    }

    updateFinalQuestion(questionId);
}

function updateFinalQuestion(questionId) {
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
    const hiddenInput = document.querySelector(`input.final-question-text[data-question-id='${questionId}']`);
    if (!inputElem || !hiddenInput) return;

    const input = inputElem.value;
    const { blankInserted, selectedIndex } = state[questionId] || {};

    let words = input.trim().split(/\s+/);

    if (blankInserted) {
        if (selectedIndex === -1) {
            words.unshift("_");
        } else if (selectedIndex === words.length) {
            words.push("_");
        } else {
            words.splice(selectedIndex + 1, 0, "_");
        }
    }

    hiddenInput.value = words.join(" ");
}