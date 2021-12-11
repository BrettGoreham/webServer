// Get the modal
var modal = document.getElementById("myModal");
var editModal = document.getElementById("editModal");

// Get the <span> element that closes the modal
var spans = document.getElementsByClassName("close");

for (let span in spans) {
  // When the user clicks on <span> (x), close the modal
  span.onclick = function(span) {
    closeModal();
  }
}

// When the user clicks the button, open the modal
function openModal() {
  modal.style.display = "block";
}

function openEditModal() {
    editModal.style.display = "block"
}



// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (event.target == modal) {
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
  if (modal.style.display == "block") {
    let formElements = document.getElementById("addMealCollection").elements;
    formElements.mealCollectionName.value = "";

    modal.style.display = "none";
  }
}

function closeEditModal() {
  if (editModal.style.display == "block") {
    let formElements = document.getElementById("editMealCollection").elements;
    formElements.mealCollectionName.value = "";
    formElements.id.value = ""

    editModal.style.display = "none";
  }
}

function addMealCollection() {
    let formFields = document.getElementById("addMealCollection").elements;
    let newCollectionName = formFields.mealCollectionName.value

    postNewMealCollection(newCollectionName);

    closeAddModal();
}

function addNewCollectionAsDiv(jsonResp) {
    let collectionName = jsonResp.collectionName;
    let id = jsonResp.id;

    let parentElement = document.createElement("div")
    parentElement.id = id;
    parentElement.className = "pointer mealCollection"
    parentElement.onclick = () => redirectToMealsPage(parentElement)
    
    let div = document.createElement("div")
    div.innerText = collectionName;
    div.className = "collectionName";
    parentElement.appendChild(div);

    let editButton =  document.createElement("button")
    editButton.innerText = "edit";
    editButton.onclick = (e) => editMealCollection(e, editButton);
    parentElement.appendChild(editButton);

    let deleteButton =  document.createElement("button")
    deleteButton.innerText = "delete";
    deleteButton.onclick = (e) => deleteMealCollection(e, deleteButton);
    parentElement.appendChild(deleteButton);

    document.getElementById("mealCategoryHolder").appendChild(parentElement);
}

function postNewMealCollection(newCollectionName) {
    beginLoading()
    var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('POST', '/user/rest/meals');
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                addNewCollectionAsDiv(JSON.parse(xhr.responseText));
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
    xhr.send(newCollectionName)
}


function editMealCollection(e, button) {
  e.stopPropagation();

  let formElements = document.getElementById("editMealCollection").elements;
  formElements.mealCollectionName.value = button.parentElement.getElementsByClassName("collectionName")[0].innerHTML
  formElements.id.value = button.parentElement.id

  openEditModal()
}

function submitEditMealCollection() {
  let formElements = document.getElementById("editMealCollection").elements;
  let id = formElements.id.value;
  let newName = formElements.mealCollectionName.value;

  beginLoading();
  var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('Put', '/user/rest/meals/' + id);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                editDomMealCollection(id, newName);
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
    xhr.send(newName)
}

function editDomMealCollection(id, newName) {
  let mealCollectionDiv = document.getElementById(id);

  let textDiv = mealCollectionDiv.getElementsByClassName("collectionName")[0];

  textDiv.innerText = newName;

  closeEditModal();

}

function deleteMealCollection(e, button) {
  e.stopPropagation();
  
  let mealCollectionBox = button.parentElement;

  beginLoading();
  var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('Delete', '/user/rest/meals/' + mealCollectionBox.id);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
            } else {
                deleteDomMealCollection(mealCollectionBox);
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

function deleteDomMealCollection(divElement) {
  divElement.parentElement.removeChild(divElement);
}

function redirectToMealsPage(divElement) {
    id = divElement.id;

    window.location.href = '/user/meals/' + id;
}