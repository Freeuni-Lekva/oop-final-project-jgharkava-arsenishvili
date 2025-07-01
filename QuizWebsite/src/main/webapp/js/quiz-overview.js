let ascending = false;

function sortTable(){
    const sortBy = document.getElementById("sortBy").value;
    const table = document.getElementById("historyTable");
    const rows = Array.from(table.querySelectorAll("tbody tr"));

    rows.sort((a, b) => {
        const valA = a.dataset[sortBy];
        const valB = b.dataset[sortBy];

        let comparison;

        if (sortBy === "date"){
            comparison = new Date(valB) - new Date(valA);
        } else {
            comparison = parseFloat(valB) - parseFloat(valA);
        }

        return ascending ? comparison : -comparison;
        }
    );

    const tbody = table.querySelector("tbody");
    rows.forEach(row => tbody.appendChild(row));
}

function toggleSortDirection(){
    const btn = document.getElementById("sortDirectionBtn");
    btn.textContent = ascending ? "Sort Descending" : "Sort Ascending";

    ascending = !ascending;

    sortTable();
}

document.addEventListener("DOMContentLoaded", () => {
    filterByRange();

    const btn = document.getElementById("sortDirectionBtn");
    btn.textContent = ascending ? "Sort Descending" : "Sort Ascending";

    ascending = !ascending;
    sortTable();
});

function showAllPerformers() {
    document.getElementById("topPerformersContainer").style.display = "none";
    document.getElementById("allPerformersContainer").style.display = "block";
    document.getElementById("showAllBtn").style.display = "none";
    document.getElementById("showTopBtn").style.display = "inline-block";
}

function showTopPerformers() {
    document.getElementById("topPerformersContainer").style.display = "block";
    document.getElementById("allPerformersContainer").style.display = "none";
    document.getElementById("showAllBtn").style.display = "inline-block";
    document.getElementById("showTopBtn").style.display = "none";
}

function filterByRange(){
    const selected = document.getElementById("timeFilter").value;

    console.log("Selected range:", selected); // Debug log

    const sections = document.querySelectorAll(".range-table");

    console.log("Found sections:", sections.length); // Debug log

    sections.forEach(section => section.style.display = "none");

    const active = document.getElementById("range-" + selected);
    console.log("Looking for:", "range-" + selected, "Found:", active); // Debug log

    active.style.display = "block";
}

