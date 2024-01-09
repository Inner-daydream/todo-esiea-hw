
function showPopup(event) {
    event.preventDefault();
    var link = event.target;
    var title = link.dataset.title;
    var dueDate = link.dataset.duedate;
    var description = link.dataset.description;
    var status = link.dataset.status;
    var id = link.dataset.id;
    document.querySelector('#popup input[name="title"]').value = title;
    document.querySelector('#popup input[name="dueDate"]').value = dueDate;
    document.querySelector('#popup input[name="description"]').value = description;
    document.querySelector('#popup input[name="id"]').value = id;
    document.querySelector('#popup select[name="status"]').value = status;
    document.getElementById('popup').style.display = 'block';
}

function hidePopup() {
    document.getElementById("popup").style.display = "none";
}