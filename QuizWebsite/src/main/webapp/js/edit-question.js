document.addEventListener("DOMContentLoaded", () => {
    // Add Option button logic
    document.querySelectorAll(".add-option-btn").forEach(button => {
        button.addEventListener("click", () => {
            const container = button.closest(".options");

            const existingAnswerId = container.querySelector(".option-block")?.dataset.answerId || null;

            const newBlock = createOptionBlock(existingAnswerId);

            container.insertBefore(newBlock, button);
            updateDeleteButtonState(container);
        });
    });

    // Initialize Save/Delete for existing options
    document.querySelectorAll(".option-block").forEach(block => {
        const container = block.closest(".options");

        block.querySelector(".save-option-btn").addEventListener("click", () => {
            const newText = block.querySelector(".option-text").value.trim();
            const oldText = block.dataset.optionText;
            const answerId = block.dataset.answerId;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: 'updateOption',
                    answerId: answerId,
                    newText: newText,
                    oldText: oldText
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        block.dataset.optionText = newText;
                        alert("Saved!");
                    } else {
                        alert("Failed to save.");
                    }
                });
        });

        block.querySelector(".delete-option-btn").addEventListener("click", () => {
            const answerId = block.dataset.answerId;
            const optionText = block.dataset.optionText;

            if (!confirm("Are you sure you want to delete this answer?")) return;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: 'removeOption',
                    answerId: answerId,
                    optionText: optionText
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

    // Disable delete if only one option per question
    document.querySelectorAll(".options").forEach(container => {
        updateDeleteButtonState(container);
    });

    document.querySelectorAll(".choices").forEach(container => {
        updateDeleteButtonStateChoices(container);
    })

    // Save question text/image logic
    // For fill-in-the-blank uses final-question-text, for picture-response imageUrl and questionText
    // Only questionText for other types
    document.querySelectorAll(".save-question-btn").forEach(button => {
        button.addEventListener("click", () => {
            const questionId = button.dataset.id;
            const block = document.querySelector(`.question-block[data-question-id='${questionId}']`);

            let questionText;

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
                    alert("Question Text Saved!")
                })
                .catch(err => {
                    console.error("Error saving question", err);
                    alert("Error saving question");
                });
        });
    });

    // Delete question with all its information
    document.querySelectorAll(".delete-question-btn").forEach(button => {
        button.addEventListener("click", () => {
            const questionId = button.dataset.id;
            if (!confirm("Are you sure you want to delete this question?")) return;

            const info = {
                action: 'deleteQuestion',
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


    // Save Question Text for PICTURE-RESPONSE QUESTION
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

    // Delete Question Text for PICTURE-RESPONSE QUESTION
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
                        addTextBtn.textContent = "Add Question Text";

                        questionBlock.insertBefore(addTextBtn, questionBlock.querySelector(".options"));
                    } else {
                        alert("Failed to delete question text.");
                    }
                });
        }
    });

    // Add Question Text for PICTURE-RESPONSE QUESTION
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

            questionBlock.insertBefore(label, questionBlock.querySelector(".options"));
            questionBlock.insertBefore(saveBtn, questionBlock.querySelector(".options"));
            questionBlock.insertBefore(deleteBtn, questionBlock.querySelector(".options"));
        }
    });

    // Initialize the state for each fill-in-the-blank question on the page
    // Render the question preview (with the blank inserted visually)
    // Update the preview live when the user types
    document.querySelectorAll(".fill-in-the-blank-block").forEach(block => {
        const questionId = block.dataset.questionId;

        // input without blank
        const rawInput = block.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);

        // input later sent to servlet
        const hiddenInput = block.querySelector(`input.final-question-text[data-question-id='${questionId}']`);

        // what is visually visible on the page
        const preview = block.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);

        // if any block is missing, skip entirely (good for avoiding errors but won't happen)
        if (!rawInput || !hiddenInput || !preview) return;

        // Initialize state for this question
        if (!state[questionId]) {
            state[questionId] = { blankInserted: false, selectedIndex: -1 }; //tracking blank insertion
        }

        // Check if there's an existing blank in the hidden input
        const hiddenText = hiddenInput.value;
        if (hiddenText.includes("_")) {
            // Extract the raw text (without blanks)
            rawInput.value = hiddenText.replace(/_/g, "").replace(/\s+/g, " ").trim();

            // Find the position of the blank in the original text

            // removes all whitespaces, newlines, tabs
            const words = hiddenText.trim().split(/\s+/);
            // finds blank's index
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

    // Handle question-level inputs
    // enables/disables save area based on whether textarea has content
    function setupToggleSaveButton(textareaSelector, saveButtonSelector) {
        document.querySelectorAll(textareaSelector).forEach(textarea => {
            const saveBtn = textarea.closest(".question-block")?.querySelector(saveButtonSelector);
            if (saveBtn) {
                toggleSaveButtonState(textarea, saveBtn);
                textarea.addEventListener("input", () => toggleSaveButtonState(textarea, saveBtn));
            }
        });
    }

    setupToggleSaveButton(".question-text", ".save-question-btn");
    setupToggleSaveButton(".image-url", ".save-question-btn");
    setupToggleSaveButton(".question-picture-text", ".save-question-picture-btn");
    setupToggleSaveButton(".edit-raw-question-input", ".save-question-btn");

    // Handle answer-level inputs separately
    document.querySelectorAll(".option-text").forEach(textarea => {
        const block = textarea.closest(".option-block");
        const saveButton = block?.querySelector(".save-option-btn");
        if (saveButton) {
            toggleSaveButtonState(textarea, saveButton);
            textarea.addEventListener("input", () => toggleSaveButtonState(textarea, saveButton));
        }
    });

    document.querySelectorAll(".choices").forEach(container => setupChoiceContainer(container));

    document.querySelectorAll(".multiple-choices").forEach(container => setupMultipleChoiceContainer(container));

    // finish editing button can be validated only if multiple-choice and multi-choice-multi-answer have at least one choice marked as true
    document.querySelector(".finish-editing-button")?.addEventListener("click", () =>{
        let allValid = true;

        document.querySelectorAll(".question-block").forEach(qBlock => {
            const type = qBlock.dataset.questionType;

            // MULTIPLE-CHOICE
            if (type === "multiple-choice") {
                const choices = qBlock.querySelectorAll(".choice-block");
                const oneMarked = Array.from(choices).some(block => {
                    const btn = block.querySelector(".mark-as-true-btn");
                    return btn && btn.textContent.trim() === "Marked as true";
                });
                if (!oneMarked) {
                    alert("Every single-answer multiple-choice question must have one marked as true.");
                    allValid = false;
                    return;
                }
            }

            // MULTIPLE-CHOICE-MULTI-ANSWER
            if (type === "multi-choice-multi-answers") {
                const choices = qBlock.querySelectorAll(".multiple-choice-block");
                const atLeastOneMarked = Array.from(choices).some(block => {
                    const btn = block.querySelector(".multiple-mark-as-true-btn");
                    return btn && btn.textContent.trim() === "Marked as true";
                });
                if (!atLeastOneMarked) {
                    alert("Every multi-answer question must have at least one choice marked as true.");
                    allValid = false;
                    return;
                }
            }
        });

        if (allValid) {
            // Redirect to overview
            const quizId = document.querySelector(".finish-editing-button").dataset.quizId;
            window.location.href = `quiz-overview.jsp?current-quiz-id=${quizId}`;
        }
    });
});

// sets up multiple-choice-multi-answer container
function setupMultipleChoiceContainer(container){
    updateDeleteButtonStateMultipleChoices(container);

    // disables save if text is empty
    container.querySelectorAll(".multiple-choice-block").forEach(block => {
        const textarea = block.querySelector(".multiple-choice-text");
        const saveBtn  = block.querySelector(".save-multiple-choice-btn");

        toggleSaveButtonState(textarea, saveBtn);
        textarea.addEventListener("input", () => toggleSaveButtonState(textarea, saveBtn));
    });

    container.addEventListener("click", function (event) {
        const target = event.target;
        const block = target.closest(".multiple-choice-block");
        if (!block) return;

        const answerId = block.dataset.answerId;

        // Mark/unmark as correct
        if (target.classList.contains("multiple-mark-as-true-btn")) {
            const nowCorrect = target.textContent.trim() === "Mark as true";
            target.textContent = nowCorrect ? "Marked as true" : "Mark as true";

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "setChoiceValidity",
                    answerId: answerId,
                    isCorrect: nowCorrect
                })
            }).then(response => {
                if (!response.ok) {
                    alert("Failed to update correctness");
                }
            }).catch(err => {
                console.error("Error updating correctness", err);
            });
        }// Save choice text
        else if (target.classList.contains("save-multiple-choice-btn")) {
            const textarea = block.querySelector(".multiple-choice-text");
            const newTxt = textarea.value.trim();
            if (!newTxt) return;

            let payload;

            if (answerId) {
                // Existing: update text
                payload = {
                    action: "updateAnswerText",
                    answerId: answerId,
                    newText: newTxt
                };
            }

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }).then(response => {
                if (response.ok){
                    alert("Updated choice text");
                } else {
                    alert("Error updating choice text");
                }
            }).catch(err => {
                console.error("Error updating choice", err);
            })
        } // Delete choice
        else if (target.classList.contains("delete-multiple-choice-btn")) {
            if (!confirm("Delete this choice?")) return;

            block.remove();
            updateDeleteButtonStateMultipleChoices(container);

            if (answerId) {
                fetch("edit-question", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        action: "deleteAnswer",
                        answerId: answerId
                    })
                }).then(res => {
                    if (!res.ok) {
                        console.error("Failed to delete choice");
                    }
                }).catch(err => {
                    console.error("Error deleting choice", err);
                });
            }
        }
    });
}

// sets multiple-choice container
function setupChoiceContainer(container){
    updateDeleteButtonStateChoices(container);

    // if textarea is empty, disable save choice.
    document.querySelectorAll(".choice-block").forEach(block => {
        const textarea = block.querySelector(".choice-text");
        const saveBtn = block.querySelector(".save-choice-btn");

        toggleSaveButtonState(textarea, saveBtn);
        textarea.addEventListener("input", () => toggleSaveButtonState(textarea, saveBtn));
    });

    const questionId = container.closest(".question-block")?.dataset.questionId;
    const allChoiceBlocks = container.querySelectorAll(".choice-block");

    // Initialize correct state based on text content
    allChoiceBlocks.forEach(block => {
        const markBtn = block.querySelector(".mark-as-true-btn");
        const deleteBtn = block.querySelector(".delete-choice-btn");

        if (markBtn.textContent.trim() === "Marked as true") {
            markBtn.disabled = allChoiceBlocks.length === 1;
            deleteBtn.disabled = true;

            // Disable all other mark buttons
            allChoiceBlocks.forEach(otherBlock => {
                if (otherBlock !== block) {
                    const otherMarkBtn = otherBlock.querySelector(".mark-as-true-btn");
                    otherMarkBtn.disabled = true;
                }
            });
        }
    });

    // Click handler
    container.addEventListener("click", function (event) {
        if (event.target.classList.contains("mark-as-true-btn")){
            const clickedBlock = event.target.closest(".choice-block");
            const clickedMarkBtn = clickedBlock.querySelector(".mark-as-true-btn");
            const clickedDeleteBtn = clickedBlock.querySelector(".delete-choice-btn");

            const isMarked = clickedMarkBtn.textContent.trim() === "Marked as true";

            if (isMarked) {
                // unmark all
                allChoiceBlocks.forEach(block => {
                    block.querySelector(".mark-as-true-btn").textContent = "Mark as true";
                    block.querySelector(".mark-as-true-btn").disabled = false;
                    block.querySelector(".delete-choice-btn").disabled = false;
                });
            } else {
                // mark clicked, disable others
                allChoiceBlocks.forEach(block => {
                    const markBtn = block.querySelector(".mark-as-true-btn");
                    const deleteBtn = block.querySelector(".delete-choice-btn");

                    markBtn.textContent = "Mark as true";
                    markBtn.disabled = true;
                    deleteBtn.disabled = false;
                });

                clickedMarkBtn.textContent = "Marked as true";
                clickedMarkBtn.disabled = false;
                clickedDeleteBtn.disabled = true;

                // Send to server
                const answerId = clickedBlock.dataset.answerId;
                fetch("edit-question", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        action: "setCorrectChoice",
                        questionId: questionId,
                        answerId: answerId
                    })
                }).then(res => res.json())
                    .then(json => {
                        if (json.success) {
                            alert("Choice marked as true");
                        } else {
                            alert("error marking choice as true");
                        }
                    });
            }
        } else if (event.target.classList.contains("save-choice-btn")){
            const clickedBlock = event.target.closest(".choice-block");
            const textarea = clickedBlock.querySelector(".choice-text");
            const newText = textarea.value.trim();
            const answerId = clickedBlock.dataset.answerId;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "updateAnswerText",
                    answerId: answerId,
                    newText: newText
                })
            }).then(res => res.json())
                .then(json => {
                    if (json.success) {
                        alert("Choice text updated successfully.");
                    } else {
                        alert("Failed to update choice text.");
                    }
                }).catch(err => {
                alert("Error saving choice text.");
                console.error(err);
            });
        } else if (event.target.classList.contains("delete-choice-btn")){
            const clickedBlock = event.target.closest(".choice-block");
            const answerId = clickedBlock.dataset.answerId;
            const container = clickedBlock.closest(".choices");

            if (!confirm("Are you sure you want to delete this choice?")) return;

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "deleteAnswer",
                    questionId: questionId,
                    answerId: answerId
                })
            }).then(res => res.json())
                .then(json => {
                    if (json.success) {
                        clickedBlock.remove(); //remove from dom
                        updateDeleteButtonStateChoices(container);
                    } else {
                        alert("Failed to delete choice.");
                    }
                }).catch(err => {
                alert("Error deleting choice.");
                console.error(err);
            });
        }
    });
}

function createOptionBlock(answerId = null, optionText = "") {
    const block = document.createElement("div");

    block.className = "option-block";
    if (answerId) block.dataset.answerId = answerId;

    block.dataset.optionText = optionText;

    block.innerHTML = `
        <textarea class="option-text">${optionText}</textarea>
        <button class="save-option-btn" disabled>Save Option Text</button>
        <button class="delete-option-btn">Delete Option</button>`;

    const textarea = block.querySelector(".option-text");
    const saveButton = block.querySelector(".save-option-btn");

    toggleSaveButtonState(textarea, saveButton);
    textarea.addEventListener("input", () => {
        toggleSaveButtonState(textarea, saveButton);
    });

    // Save handler
    block.querySelector(".save-option-btn").addEventListener("click", () => {
        const newText = textarea.value.trim();
        const oldText = block.dataset.optionText || "";
        const answerId = block.dataset.answerId;
        const isNew = !oldText;

        const payload = {
            action: 'updateOption',
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
                    block.dataset.optionText = newText;
                    alert("Saved!");
                } else {
                    alert("Failed to save.");
                }
            });
    });

    // Delete handler
    block.querySelector(".delete-option-btn").addEventListener("click", () => {
        const answerId = block.dataset.answerId;
        const optionText = block.dataset.optionText;
        const container = block.closest(".options");

        if (!confirm("Are you sure you want to delete this answer?")) return;

        fetch("edit-question", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                action: 'removeOption',
                answerId: answerId,
                optionText: optionText
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

// if only one option is left, disable its delete button
function updateDeleteButtonState(optionsContainer) {
    const optionsBlock = optionsContainer.querySelectorAll(".option-block");
    const deleteButtons = optionsContainer.querySelectorAll(".delete-option-btn");
    const shouldDisable = optionsBlock.length <= 1;

    deleteButtons.forEach(btn => btn.disabled = shouldDisable);
}

// if only one choice is left, disable its delete button MULTI-CHOICE-MULTI-ANSWER
function updateDeleteButtonStateMultipleChoices(choicesContainer){
    const choiceBlocks = choicesContainer.querySelectorAll(".multiple-choice-block");
    const deleteButtons = choicesContainer.querySelectorAll(".delete-multiple-choice-btn");
    const shouldDisable = choiceBlocks.length <= 1;

    deleteButtons.forEach(btn => btn.disabled = shouldDisable);

    // if only one choice left, mark it as true and send info to server
    if (choiceBlocks.length === 1){
        const onlyChoice = choiceBlocks[0];
        const markBtn = onlyChoice.querySelector(".multiple-mark-as-true-btn");
        const deleteBtn = onlyChoice.querySelector(".delete-multiple-choice-btn");

        markBtn.textContent = "Marked as true";
        markBtn.disabled = true;
        deleteBtn.disabled = true;

        const questionId = choicesContainer.closest(".question-block")?.dataset.questionId;
        const answerId = onlyChoice.dataset.answerId;

        fetch("edit-question", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                action: "setCorrectChoice",
                questionId: questionId,
                answerId: answerId
            })
        }).then(res => res.json())
            .then(json => {
                if (!json.success) {
                    alert("Error marking the only remaining option as correct");
                }
            });
    }
}

// if only one choice is left, disable its delete button MULTIPLE-CHOICE
function updateDeleteButtonStateChoices(choicesContainer) {
    const choiceBlocks = choicesContainer.querySelectorAll(".choice-block");
    const deleteButtons = choicesContainer.querySelectorAll(".delete-choice-btn");
    const shouldDisable = choiceBlocks.length <= 1;

    deleteButtons.forEach(btn => btn.disabled = shouldDisable);

    // if only one choice left, mark it as true and send info to server
    if (choiceBlocks.length === 1){
        const onlyChoice = choiceBlocks[0];
        const markBtn = onlyChoice.querySelector(".mark-as-true-btn");
        const deleteBtn = onlyChoice.querySelector(".delete-choice-btn");

        markBtn.textContent = "Marked as true";
        markBtn.disabled = true;
        deleteBtn.disabled = true;

        const questionId = choicesContainer.closest(".question-block")?.dataset.questionId;
        const answerId = onlyChoice.dataset.answerId;

        fetch("edit-question", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                action: "setCorrectChoice",
                questionId: questionId,
                answerId: answerId
            })
        }).then(res => res.json())
            .then(json => {
                if (!json.success) {
                    alert("Error marking the only remaining option as correct");
                }
            });
    }
}

// if textarea is empty, disables saveButton
function toggleSaveButtonState(textarea, saveButton) {
    saveButton.disabled = textarea.value.trim() === "";
}

const state = {}; // for fill-in-the-blank question's state

function renderQuestionWithBlanks(questionId) {
    // textarea where the raw question text is typed
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);

    // shows preview
    const container = document.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);

    // good for avoiding errors
    if (!inputElem || !container) return;

    const input = inputElem.value;

    // if state has not been initialized, initializes it
    if (!state[questionId]) {
        state[questionId] = { blankInserted: false, selectedIndex: -1 };
    }

    let { blankInserted, selectedIndex } = state[questionId];

    // if text is empty, preview nothing
    if (input.trim() === "") {
        container.innerHTML = "";
        return;
    }

    // If a blank was previously inserted, reset the blank state to "no blank inserted".
    // before re-rendering the preview with updated text, clear blank state
    if (blankInserted) {
        state[questionId].blankInserted = false;
        state[questionId].selectedIndex = -1;
        blankInserted = false;
        selectedIndex = -1;
    }

    container.innerHTML = "";

    const words = input.trim().split(/\s+/);


    // inserting blank
    if (!blankInserted) {
        container.appendChild(createBlankButton(questionId, 0));
    }

    words.forEach((word, index) => {
        const wordSpan = document.createElement("span");
        wordSpan.textContent = " " + word + " ";
        container.appendChild(wordSpan);

        if (!blankInserted) {
            container.appendChild(createBlankButton(questionId, index + 1));
        }
    });


    updateFinalQuestion(questionId);
}

// creates blank "+" button
function createBlankButton(questionId, index) {
    const btn = document.createElement("button");
    btn.textContent = "+";
    btn.type = "button";
    btn.onclick = () => insertBlank(questionId, index);
    return btn;
}

// inserts blank
function insertBlank(questionId, index) {

    // if state's object does not exist, initializes it
    if (!state[questionId]) {
        state[questionId] = { blankInserted: false, selectedIndex: -1 };
    }

    // good for error checking but won't happen
    if (state[questionId].blankInserted) {
        alert("Only one blank is allowed.");
        return;
    }

    state[questionId].selectedIndex = index;
    state[questionId].blankInserted = true;

    renderQuestionWithBlankApplied(questionId);
}

// dynamically updates the preview showing the question with a blank inserted exactly where the user wants.
function renderQuestionWithBlankApplied(questionId) {
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
    const container = document.querySelector(`.interactive-edit-preview[data-question-id='${questionId}']`);

    // good for error checking
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

// updates hiddenInput which is later used for insertion into table
function updateFinalQuestion(questionId) {
    const inputElem = document.querySelector(`textarea.edit-raw-question-input[data-question-id='${questionId}']`);
    const hiddenInput = document.querySelector(`input.final-question-text[data-question-id='${questionId}']`);
    if (!inputElem || !hiddenInput) return;

    const input = inputElem.value;
    const { blankInserted, selectedIndex } = state[questionId] || {};

    let words = input.trim().split(/\s+/);

    if (blankInserted) {
        if (selectedIndex === 0) {
            words.unshift("_");
        } else if (selectedIndex === words.length) {
            words.push("_");
        } else {
            words.splice(selectedIndex, 0, "_");
        }
    }

    hiddenInput.value = words.join(" ");
}