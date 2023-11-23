function collectAndSendForUpdate() {
    let checkedOptions = document.querySelectorAll('input[class=toConfirm]:checked');
    let action = document.querySelector('input[name="mode"]:checked').value

    if (checkedOptions.length == 0) {
        createErrorToast("No options selected to confirm");
    }
    else {
        document.body.classList.add("loading");

        var xhr =  new XMLHttpRequest();
        xhr.timeout = 10000;
        xhr.open('POST', '/admin/rest/acceptCategory');
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function() {
            if (xhr.status === 200) {
                if (xhr.responseURL.endsWith("login")) {
                    createErrorToast("You have been logged off :(")
                } else {
                    removeOptionsAfterAcceptCategory(
                        JSON.parse(xhr.responseText),
                        action
                    );
                }
            }
            else {
                createErrorToast("failed to save");
            }

            document.body.classList.remove("loading")
        };


        xhr.ontimeout = () => {
            createErrorToast("Failed to reach server")
            document.body.classList.remove("loading")
        };
        xhr.onerror = xhr.ontimeout
        xhr.send(getJsonForAcceptCategory(checkedOptions, action));
    }
}

function getJsonForAcceptCategory(checkedOptions, action) {
    let checkedValues = []
    for(var i=0; i < checkedOptions.length; i++){
        checkedValues.push(checkedOptions[i].value);
    }

    let json = {}
    json.action = action
    json.ids = checkedValues

    return JSON.stringify(json)
}

function removeOptionsAfterAcceptCategory(listOfCategoriesChanged, change) {
    let namesOfAccepted = []

    for (let i = 0; i < listOfCategoriesChanged.length; i++) {
        let element = document.getElementById(listOfCategoriesChanged[i]);
        namesOfAccepted.push(element.children[0].name)
        element.parentNode.removeChild(element)
    }

    createSuccessToast("The following categories have been  " + change + "\n" + namesOfAccepted)
}

function sendOptionsForUpdate() {
    let checkedOptions = document.querySelectorAll('input[class=options]:checked');
    let action = document.querySelector('input[name="optionsMode"]:checked').value

    if (checkedOptions.length == 0) {
        createErrorToast("No options selected to confirm");
    }
    else {
        document.body.classList.add("loading");

        var xhr =  new XMLHttpRequest();
        xhr.timeout = 10000;
        xhr.open('POST', '/admin/rest/acceptOption');
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function() {
            if (xhr.status === 200) {
                if (xhr.responseURL.endsWith("login")) {
                    createErrorToast("You have been logged off :(")
                } else {
                    removeOptionsAfterAcceptOption(
                        JSON.parse(xhr.responseText),
                        action
                    );
                }
            }
            else {
                createErrorToast("failed to save");
            }

            document.body.classList.remove("loading")
        };


        xhr.ontimeout = () => {
            createErrorToast("Failed to reach server")
            document.body.classList.remove("loading")
        };
        xhr.onerror = xhr.ontimeout
        xhr.send(getJsonForAcceptCategory(checkedOptions, action));
    }
}

function removeOptionsAfterAcceptOption(listOfCategoriesChanged, change) {
    let namesOfAccepted = []

    for (let i = 0; i < listOfCategoriesChanged.length; i++) {
        let element = document.getElementById('option'+ listOfCategoriesChanged[i]);
        namesOfAccepted.push(element.children[0].name)
        element.parentNode.removeChild(element)
    }

    createSuccessToast("The following options have been  " + change + "\n" + namesOfAccepted)
}