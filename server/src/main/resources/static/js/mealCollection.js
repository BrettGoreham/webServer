var mealCollectionId = getMealCollectionId();
// Get the modal
var createModal = document.getElementById("createModal");
var editModal = document.getElementById("editModal");

// Get the <span> element that closes the modal
var spans = document.getElementsByClassName("close");

for (let span in spans) {
  // When the user clicks on <span> (x), close the modal
  span.onclick = function(span) {
    closeModal();
    closeEditModal();
  }
}

function addMealToCollection(button) {
    let formFields = document.getElementById("addMeal").elements;
    let newMealName = formFields.mealName.value

    let jsonData = {};
    jsonData["mealName"] = newMealName;
    jsonData["isDisabled"] = formFields.createIsDisabled.checked;

    beginLoading()
    let xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('POST', '/user/rest/meals/' + mealCollectionId + "/meals");
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                addNewMealAsDiv(JSON.parse(xhr.responseText));
            }
        }
        else {
        }

        endLoading()
    };


    xhr.ontimeout = () => {
        endLoading()
    };
    xhr.onerror = xhr.ontimeout;
    console.log(JSON.stringify(jsonData))
    xhr.send(JSON.stringify(jsonData));

    closeAddModal();
}

function deleteMeal(button) {
    let mealBox = button.parentElement;

    beginLoading();
    var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('Delete', '/user/rest/meals/' +mealCollectionId + "/meals/" + mealBox.id);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                deleteDomMealCollection(mealBox);
            }
        }
        else {
        }

        endLoading()
    };


    xhr.ontimeout = () => {
        endLoading()
    };
    xhr.onerror = xhr.ontimeout
    xhr.send()
}

function submitEditMeal() {
    let formElements = document.getElementById("editMeal").elements;
    let id = formElements.id.value;
    let newName = formElements.editedMealName.value;
    let isDisabled = formElements.editIsDisabled.checked;

    let jsonData = {};
    jsonData["mealName"] = newName;
    jsonData["isDisabled"] = isDisabled;

    beginLoading();
    var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('Put', '/user/rest/meals/' + mealCollectionId + '/meals/' + id);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                editDomMealCollection(id, newName, isDisabled);
            }
        }
        else {
        }

        endLoading()
    };


    xhr.ontimeout = () => {
        endLoading()
    };
    xhr.onerror = xhr.ontimeout
    xhr.send(JSON.stringify(jsonData))
}




function toggleMealDisabled(checkbox) {
    isChecked = checkbox.checked;
    id = checkbox.parentElement.id

    beginLoading();
    var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('Put', '/user/rest/meals/' + mealCollectionId + '/meals/' + id + '/disabled/' + isChecked);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
            }
        }
        else {
        }

        endLoading()
    };


    xhr.ontimeout = () => {
        endLoading()
    };
    xhr.onerror = xhr.ontimeout
    xhr.send()
}


// When the user clicks the button, open the modal
function openModal() {
    createModal.style.display = "block";
}

function openEditModal(button) {

    let formElements = document.getElementById("editMeal").elements;
    formElements.editedMealName.value = button.parentElement.getElementsByClassName("mealName")[0].innerHTML
    formElements.editIsDisabled.checked = button.parentElement.getElementsByClassName("disable")[0].checked
    formElements.id.value = button.parentElement.id

    editModal.style.display = "block"
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == createModal) {
    closeAddModal();
  }
  if (event.target == editModal) {
      closeEditModal();
    }
}

function closeModal() {
    closeAddModal();
    closeEditModal();
}

function closeAddModal() {
  if (createModal.style.display == "block") {
    let formElements = document.getElementById("addMeal").elements;
    formElements.mealName.value = "";

    createModal.style.display = "none";
  }
}

function closeEditModal() {
  if (editModal.style.display == "block") {
    let formElements = document.getElementById("editMeal").elements;
    formElements.editedMealName.value = "";
    formElements.id.value = ""

    editModal.style.display = "none";
  }
}



function addNewMealAsDiv(jsonResp) {
    let mealName = jsonResp.mealName;
    let id = jsonResp.id;
    let isDisabled = jsonResp.isDisabled;

    let parentElement = document.createElement("div")
    parentElement.id = id;
    parentElement.className = "mealCollection"

    let div = document.createElement("div")
    div.innerText = mealName;
    div.className = "mealName"
    parentElement.appendChild(div);

    let checkbox = document.createElement("input")
    checkbox.type = 'checkbox'
    checkbox.checked = isDisabled
    checkbox.onclick = (e) => toggleMealDisabled(checkbox);
    parentElement.appendChild(checkbox)

    let editButton =  document.createElement("button")
    editButton.innerText = "edit";
    editButton.onclick = (e) => openEditModal(editButton);
    parentElement.appendChild(editButton);

    let deleteButton =  document.createElement("button")
    deleteButton.innerText = "delete";
    deleteButton.onclick = (e) => deleteMeal(deleteButton);
    parentElement.appendChild(deleteButton);

    document.getElementById("mealHolder").appendChild(parentElement);

}

function deleteDomMealCollection(divElement) {
    divElement.parentElement.removeChild(divElement);
}

function editDomMealCollection(id, newName, isDisabled) {
    

    var parent = document.getElementById(id);

    parent.getElementsByClassName("mealName")[0].innerText = newName;
    parent.getElementsByClassName("disable")[0].checked = isDisabled;

    closeEditModal();

}



function getMealCollectionId() {
    return document.getElementById("mealCollectionId").value;
}


function startSelectingShit() {
    let allMeals =  document.getElementsByClassName("mealCollection");

    let nonDisabled = [];

    for (let item of allMeals) {
        item.classList.remove("result")
        if(item.getElementsByClassName("disable")[0].checked === false) {
            nonDisabled.push(item.id)
        }
    }

    var numberOfRandomSelects = Math.floor(Math.random() * (nonDisabled.length * 3 - nonDisabled.length) + nonDisabled.length)

    timeoutFunction(numberOfRandomSelects, nonDisabled, -1);
}

function timeoutFunction(numberOfRandomLeftSelects, listOfElements, lastSelectedIndex) {

    if (lastSelectedIndex > -1) {
        document.getElementById(listOfElements[lastSelectedIndex]).classList.toggle("selected");
    }

    var selected = Math.floor(Math.random() * listOfElements.length);

    if (numberOfRandomLeftSelects === 1) {
        document.getElementById(listOfElements[selected]).classList.toggle("result")
    }
    else {
        document.getElementById(listOfElements[selected]).classList.toggle("selected");

        let timeout = 100;
        if (numberOfRandomLeftSelects <= 5) {
            timeout = 400
        }
        else if ( numberOfRandomLeftSelects <= 15) {
            timeout = 250;
        }

        setTimeout(timeoutFunction, timeout, numberOfRandomLeftSelects - 1, listOfElements, selected);
    }
}