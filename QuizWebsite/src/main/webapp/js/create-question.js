let answerCount = 1;

function showQuestionForm(){
    const selectElement = document.getElementById("questionType");
    const type = selectElement.value;

    const allForms = document.querySelectorAll(".questionForm");
    allForms.forEach(form => form.style.display = "none");

    if (type){
        const form = document.getElementById(type +"-form");
        if (form){
            form.style.display = "block";
        }

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