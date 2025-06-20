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
}

function updateOtherValue() {
    const input = document.getElementById("otherTagInput");
    const checkbox = document.getElementById("tag_other");

    checkbox.value = input.value;
}