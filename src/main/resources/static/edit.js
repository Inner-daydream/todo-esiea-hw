
function showPopup(event) {
    event.preventDefault();
    var link = event.target;
    var title = link.dataset.title;
    var dueDate = link.dataset.duedate;
    var description = link.dataset.description;
    var status = link.dataset.status;
    var id = link.dataset.id;
    var priority = link.dataset.priority;
    document.querySelector('#popup input[name="title"]').value = title;
    document.querySelector('#popup input[name="dueDate"]').value = dueDate;
    document.querySelector('#popup input[name="description"]').value = description;
    document.querySelector('#popup input[name="id"]').value = id;
    document.querySelector('#popup select[name="status"]').value = status;
    document.querySelector('#popup select[name="priority"]').value = priority;
    document.getElementById('popup').style.display = 'block';
}

function hidePopup() {
    document.getElementById("popup").style.display = "none";
}

// Persisting filter and sorting settings

document.addEventListener('DOMContentLoaded', function () {
    var statusSelect = document.querySelector('select[name="status"]');
    if (statusSelect) {
        statusSelect.value = localStorage.getItem('status') || '';
        statusSelect.addEventListener('change', function () {
            localStorage.setItem('status', this.value);
        });
    }

    var sortedCheckbox = document.getElementById('sorted');
    if (sortedCheckbox) {
        sortedCheckbox.checked = localStorage.getItem('sorted') === 'true';
        sortedCheckbox.addEventListener('change', function () {
            localStorage.setItem('sorted', this.checked);
        });
    }
});

function submitForm() {
    var form = document.getElementById('filter-form');
    var statusSelect = document.getElementById('status-select');

    if (statusSelect.value === '') {
        // If "All" is selected, remove the status parameter
        statusSelect.removeAttribute('name');
    }

    // Submit the form
    form.submit();

    // Add the name attribute back to the status select so it's included in future submissions
    statusSelect.setAttribute('name', 'status');
}