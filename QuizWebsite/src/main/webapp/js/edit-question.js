document.addEventListener("DOMContentLoaded", () => {

    document.querySelectorAll(".answer-save-text-btn").forEach(button => {
        button.addEventListener("click", () => {
            const answerBlock = button.closest(".answer-block");
            const answerId = answerBlock.dataset.answerId;
            const textarea = answerBlock.querySelector(".main-answer-text");
            const newText = textarea.value.trim();
            const oldText = answerBlock.dataset.mainAnswerText;

            toggleSaveButtonState(textarea ,button);

            // won't happen but good for error checking
            if (!newText) {
                alert("Main answer cannot be empty.");
                return;
            }

            fetch("edit-question", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "updateOption",
                    answerId: answerId,
                    newText: newText,
                    oldText: oldText,
                    isNew: false
                })
            })
                .then(res => res.json())
                .then(json => {
                    if (json.success) {
                        alert("Main answer saved.");
                    } else {
                        alert("Failed to save main answer.");
                    }
                })
                .catch(err => {
                    alert("Error while saving main answer.");
                    console.error(err);
                });
        });
    });

    // Add multi answer option button logic
    document.querySelectorAll(".answer-add-option-btn").forEach(button => {
        button.addEventListener("click", () => {
            const answerBlock = button.closest(".answer-block");
            const optionsContainer = answerBlock.querySelector(".answer-options");
            const answerId = answerBlock.dataset.answerId;

            const newBlock = createAnswerOptionBlock(answerId, "");

            optionsContainer.appendChild(newBlock);
        });
    });

    // Initialize Save/Delete for existing multi-answer options
    document.querySelectorAll(".answer-option-block").forEach(block => setupOptionBlockListeners(block));

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

    document.querySelectorAll(".matching-question-block").forEach(questionBlock => {
        // dropdown logic

        // Function to bind dropdown change event
        function bindDropdownChangeEvent(select) {
            select.addEventListener("change", () => {
                const newRightText = select.value;
                const group = select.closest(".left-group");
                const matchId = group.dataset.matchId;

                if (!matchId || matchId === "") {
                    // If this left option is not saved yet, user should save left option first before changing right select
                    alert("Please save the left option text first before changing the match.");
                    // Optionally, reset dropdown value or disable it before save
                    return;
                }
                console.log(matchId);

                fetch("edit-question", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        action: "updateRightMatchText",
                        matchId: matchId,
                        newRightText: newRightText,
                    })
                })
                    .then(res => res.json())
                    .then(json => {
                        if (json.success) {
                            console.log("Right match updated successfully.");
                        } else {
                            alert("Failed to update right match.");
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert("Error updating right match.");
                    });
            });
        }



        // Bind dropdown logic to existing elements
        questionBlock.querySelectorAll(".left-group").forEach(group => {
            const rightSelect = group.querySelector("select.right-select");
            if (rightSelect) {
                bindDropdownChangeEvent(rightSelect);
            }
        });
        const leftOptionsContainer = questionBlock.querySelector(".left-options");

        // if initially only one, disable delete
        if (leftOptionsContainer) {
            updateDeleteButtonStateLeftMatches(leftOptionsContainer);
        }

        const rightOptionContainer = questionBlock.querySelector(".right-options");
        if (rightOptionContainer){
            updateDeleteButtonStateRightOptions(rightOptionContainer);
        }

        // Delegate click event for Save buttons inside leftOptionsContainer
        leftOptionsContainer.addEventListener("click", event => {
            const btn = event.target;

            // Save Left Option button
            if (btn.classList.contains("save-left-option-btn")) {
                const group = btn.closest(".left-group");
                if (!group) return;

                const leftInput = group.querySelector(".left-match");
                const rightSelect = group.querySelector("select.right-select");

                const newLeftText = leftInput.value.trim();
                const matchId = group.dataset.matchId;
                const isNew = !matchId || matchId === "";
                const selectedRightText = rightSelect ? rightSelect.value : null;
                const questionId = questionBlock.dataset.questionId;

                console.log("question id", questionId);

                if (!newLeftText) {
                    alert("Left option cannot be empty.");
                    return;
                }

                fetch("edit-question", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        action: "updateLeftMatch",
                        matchId: matchId,
                        newLeftText: newLeftText,
                        isNew: isNew,
                        questionId: questionId,
                        rightText: selectedRightText
                    })
                })
                    .then(res => res.json())
                    .then(json => {
                        if (json.success) {
                            alert(isNew ? "Left option created successfully." : "Left option updated successfully.");
                            if (isNew) {
                                group.dataset.matchId = json.matchId; // Save new matchId on group
                            }
                        } else {
                            alert("Failed to save left option.");
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert("Error while saving left option.");
                        btn.disabled = false;
                    });
            }

            // Delete Left Option button
            if (btn.classList.contains("delete-left-option-btn")) {
                const group = btn.closest(".left-group");
                if (!group) return;

                if (!confirm("Are you sure you want to delete this left option?")) return;

                const matchId = group.dataset.matchId;

                if (matchId) {
                    fetch("edit-question", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            action: "deleteMatch",
                            matchId: matchId
                        })
                    }).then(res => res.json())
                      .then(json => {
                          if (!json.success) {
                              alert("Failed to delete left option from database.");
                          } else {
                              alert("Deleted left option from database");
                          }
                      }).catch(() => alert("Error deleting left option from database."));
                }

                group.remove();
                updateDeleteButtonStateLeftMatches(leftOptionsContainer);
            }
        });

        // Enable/disable Save button on input event (also on initial load)
        function bindSaveToggle(group) {
            const leftInput = group.querySelector(".left-match");
            const saveBtn = group.querySelector(".save-left-option-btn");
            if (!leftInput || !saveBtn) return;

            toggleSaveButtonState(leftInput, saveBtn);

            leftInput.addEventListener("input", () => {
                toggleSaveButtonState(leftInput, saveBtn);
            });
        }

        // Bind toggle on existing groups
        questionBlock.querySelectorAll(".left-group").forEach(bindSaveToggle);

        // Update dropdowns helper
        function updateDropdowns(oldVal = null, newVal = null) {
            const rightInputs = questionBlock.querySelectorAll(".right-match");
            const dropdowns = questionBlock.querySelectorAll(".right-select");

            dropdowns.forEach(select => {
                let currentValue = select.value;
                if (oldVal && currentValue === oldVal) currentValue = newVal;

                select.innerHTML = "";

                rightInputs.forEach(input => {
                    const option = document.createElement("option");
                    option.value = input.value;
                    option.textContent = input.value || "(empty)";
                    select.appendChild(option);
                });

                select.value = currentValue;
            });
        }

        // right options listener
        function bindRightOptionEvents(wrapper){
            const saveBtn = wrapper.querySelector(".save-right-option-btn");
            const input = wrapper.querySelector(".right-match");

            let originalValue = input.value;

            toggleSaveButtonState(input, saveBtn);

            saveBtn.addEventListener("click", () => {
                const newRightText = input.value.trim();

                if (!newRightText) {
                    alert("Right option cannot be empty.");
                    return;
                }

                // Find all left-groups with dropdown value == originalValue
                const affectedLeftGroups = Array.from(questionBlock.querySelectorAll(".left-group")).filter(leftGroup => {
                    const select = leftGroup.querySelector("select.right-select");
                    return select.value === originalValue;
                });

                const updates = affectedLeftGroups.map(leftGroup => {
                    const matchId = leftGroup.dataset.matchId;
                    return fetch("edit-question", {
                        method: "POST",
                        headers: {"Content-Type": "application/json"},
                        body: JSON.stringify({
                            action: "updateRightMatchText",
                            matchId: matchId,
                            newRightText: newRightText
                        })
                    }).then(res => res.json());
                });

                Promise.all(updates).then(results => {
                    if (results.every(r => r.success)) {
                        alert("Right option updated and linked matches updated successfully.");

                        updateDropdowns(originalValue, newRightText);
                        originalValue = newRightText;
                    } else {
                        alert("Failed to update some matches.");
                    }
                }).catch(() => {
                    alert("Error updating matches.");
                });
            });
        }

        // Right option save logic (unchanged)
        questionBlock.querySelectorAll(".right-option-wrapper").forEach(wrapper =>
            bindRightOptionEvents(wrapper)
        );

        // Add Left Option button logic
        const addLeftBtn = questionBlock.querySelector(".add-left-option-btn");
        if (addLeftBtn) {
            addLeftBtn.addEventListener("click", () => {
                const rightInputs = questionBlock.querySelectorAll(".right-match");
                const defaultRightValue = rightInputs.length > 0 ? rightInputs[0].value : "";

                const group = document.createElement("div");
                group.className = "left-group";
                group.dataset.matchId = ""; // new match has no id yet

                const leftInput = document.createElement("input");
                leftInput.type = "text";
                leftInput.className = "left-match";
                leftInput.placeholder = "Left option";
                leftInput.required = true;

                const select = document.createElement("select");
                select.className = "right-select";
                select.required = true;

                rightInputs.forEach(input => {
                    const option = document.createElement("option");
                    option.value = input.value;
                    option.textContent = input.value || "(empty)";
                    select.appendChild(option);
                });
                select.value = defaultRightValue;

                const saveBtn = document.createElement("button");
                saveBtn.className = "save-left-option-btn";
                saveBtn.textContent = "Save Text";
                saveBtn.disabled = true;

                const deleteBtn = document.createElement("button");
                deleteBtn.className = "delete-left-option-btn";
                deleteBtn.textContent = "Delete";

                group.appendChild(leftInput);
                group.appendChild(select);
                group.appendChild(saveBtn);
                group.appendChild(deleteBtn);

                leftOptionsContainer.appendChild(group);

                bindSaveToggle(group);
                bindDropdownChangeEvent(select);
                updateDeleteButtonStateLeftMatches(leftOptionsContainer);
            });
        }

        // Add Right Option button logic
        const addRightBtn = questionBlock.querySelector(".add-right-option-btn");
        if (addRightBtn) {
            addRightBtn.addEventListener("click", () => {
                // Create new input for right option
                const rightOptionsContainer = questionBlock.querySelector(".right-options");

                const wrapper = document.createElement("div");
                wrapper.className = "right-option-wrapper";

                const input = document.createElement("input");
                input.type = "text";
                input.className = "right-match";
                input.placeholder = "Right option text";

                // Save button disabled initially until user types
                const saveBtn = document.createElement("button");
                saveBtn.className = "save-right-option-btn";
                saveBtn.textContent = "Save Text";

                const deleteBtn = document.createElement("button");
                deleteBtn.className = "delete-right-option-btn";
                deleteBtn.textContent = "Delete";

                wrapper.appendChild(input);
                wrapper.appendChild(saveBtn);
                wrapper.appendChild(deleteBtn);
                rightOptionsContainer.appendChild(wrapper);

                toggleSaveButtonState(input, saveBtn);

                input.addEventListener("input", () => {
                    toggleSaveButtonState(input, saveBtn);
                });

                bindRightOptionEvents(wrapper);
                updateDeleteButtonStateRightOptions(rightOptionsContainer);
            });
        }

        const rightOptionsContainer = questionBlock.querySelector(".right-options");

        rightOptionsContainer.addEventListener("click", event => {
            const btn = event.target;

            if (btn.classList.contains("delete-right-option-btn")) {
                const wrapper = btn.closest(".right-option-wrapper");
                const input = wrapper.querySelector(".right-match");
                const rightTextToDelete = input.value.trim();
                const questionId = questionBlock.dataset.questionId;

                if (!confirm("Are you sure you want to delete this right option?")) return;

                const rightInputs = Array.from(questionBlock.querySelectorAll(".right-match"));
                const allRightValues = rightInputs.map(inp => inp.value.trim());

                // Find fallback (first right option that isn't the one being deleted)
                const fallbackRight = allRightValues.find(text => text && text !== rightTextToDelete);

                if (!fallbackRight) {
                    alert("Cannot delete the last remaining right option.");
                    return;
                }

                const leftGroups = Array.from(questionBlock.querySelectorAll(".left-group"));
                const updateRequests = [];

                leftGroups.forEach(group => {
                    const matchId = group.dataset.matchId;
                    const select = group.querySelector("select.right-select");

                    if (select.value === rightTextToDelete) {
                        // Change selection to fallback before saving
                        select.value = fallbackRight;

                        if (matchId) {
                            updateRequests.push(
                                fetch("edit-question", {
                                    method: "POST",
                                    headers: { "Content-Type": "application/json" },
                                    body: JSON.stringify({
                                        action: "updateRightMatchText",
                                        matchId: matchId,
                                        newRightText: fallbackRight,
                                        questionId: questionId
                                    })
                                }).then(res => res.json())
                            );
                        }
                    }
                });

                Promise.all(updateRequests).then(results => {
                    if (!results.every(r => r.success)) {
                        alert("Some left matches may not have updated successfully.");
                    }

                    // Remove the deleted option from all dropdowns
                    questionBlock.querySelectorAll("select.right-select").forEach(select => {
                        Array.from(select.options).forEach(option => {
                            if (option.value === rightTextToDelete) {
                                option.remove();
                            }
                        });
                    });

                    // Remove from DOM
                    wrapper.remove();

                    // Update delete button state
                    updateDeleteButtonStateRightOptions(rightOptionsContainer);
                }).catch(() => {
                    alert("Error while updating left matches before deleting.");
                });
            }


        });
    });
});

// creates new option for multi-answer's answer
function createAnswerOptionBlock(answerId, optionText) {
    const block = document.createElement("div");
    block.className = "answer-option-block";
    block.dataset.answerId = answerId;
    block.dataset.optionText = optionText;

    block.innerHTML = `
        <textarea class="answer-option-text">${optionText}</textarea>
        <button type="button" class="answer-save-option-btn">Save Option Text</button>
        <button type="button" class="answer-delete-option-btn">Delete Option</button>
    `;

    const textarea = block.querySelector(".answer-option-text");
    const saveButton = block.querySelector(".answer-save-option-btn");

    toggleSaveButtonState(textarea, saveButton);
    textarea.addEventListener("input", () => {
        toggleSaveButtonState(textarea, saveButton);
    });

    setupOptionBlockListeners(block);
    return block;
}

// setup listeners for multi-answer-choices
function setupOptionBlockListeners(block) {
    const container = block.closest(".answer-options");

    block.querySelector(".answer-save-option-btn").addEventListener("click", () => {
        const newText = block.querySelector(".answer-option-text").value.trim();
        const oldText = block.dataset.optionText || "";
        const answerId = block.dataset.answerId;
        const isNew = !oldText;

        if (!newText) return;

        fetch("edit-question", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                action: "updateOption",
                answerId: answerId,
                newText: newText,
                oldText: oldText,
                isNew: isNew
            })
        }).then(res => res.json())
            .then(json => {
                if (json.success) {
                    block.dataset.optionText = newText;
                    alert("Option saved.");
                } else {
                    alert("Failed to save.");
                }
            });
    });

    block.querySelector(".answer-delete-option-btn").addEventListener("click", () => {
        const answerId = block.dataset.answerId;
        const optionText = block.dataset.optionText;

        if (!confirm("Delete this option?")) return;

        fetch("edit-question", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                action: "removeOption",
                answerId: answerId,
                optionText: optionText
            })
        }).then(res => res.json())
            .then(json => {
                if (json.success) {
                    block.remove();
                } else {
                    alert("Failed to delete.");
                }
            });
    });
}

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

// creates option blocks for basic questions
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

function updateDeleteButtonStateRightOptions(rightOptionsContainer) {
    const wrappers = rightOptionsContainer.querySelectorAll(".right-option-wrapper");
    const deleteButtons = rightOptionsContainer.querySelectorAll(".delete-right-option-btn");
    const shouldDisable = wrappers.length <= 1;

    deleteButtons.forEach(btn => btn.disabled = shouldDisable);
}

function updateDeleteButtonStateLeftMatches(leftOptionsContainer) {
    const leftGroups = leftOptionsContainer.querySelectorAll(".left-group");
    const deleteButtons = leftOptionsContainer.querySelectorAll(".delete-left-option-btn");

    const shouldDisable = leftGroups.length <= 1;

    deleteButtons.forEach(btn => {
        btn.disabled = shouldDisable;
    });
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