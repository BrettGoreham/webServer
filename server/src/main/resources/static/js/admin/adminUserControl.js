

const userSelect = document.getElementById('userSelect'); 
const roleSelect = document.getElementById('roleSelect'); 

const giveRolebutton = document.getElementById('giveRoleButton');
const takeRolebutton = document.getElementById('takeRoleButton');


function GetUserById(id) {
    return users.filter(obj => { return obj.id === parseInt(userSelect.value)})[0]
}


userSelect.addEventListener('change', function() { 
    CheckButtonDisable();
})
roleSelect.addEventListener('change', function() { CheckButtonDisable() })


function CheckButtonDisable() {
    if (userSelect.value != "" && roleSelect.value != "") {
        if (GetUserById(parseInt(userSelect.value)).roles.includes(roleSelect.value)) {
            takeRolebutton.disabled = false;
            giveRolebutton.disabled = true;
        }
        else {
            takeRolebutton.disabled = true;
            giveRolebutton.disabled = false;
        }
    }

    else {
        takeRolebutton.disabled = true;
        giveRolebutton.disabled = true;
    }
    
}


function TakeUserRole() {
    let json = {}
    json.userId = parseInt(userSelect.value)
    json.role = roleSelect.value

    SendPost('/admin/rest/takeRole', json, "Took role " + json.role + ' from user ' + GetUserById(json.userId).username,
        () => 
        {
            GetUserById(parseInt(userSelect.value)).roles = GetUserById(parseInt(userSelect.value)).roles.filter(val => val !== roleSelect.value)
            CheckButtonDisable()
        })
}

function GiveUserRole() {
    let json = {}
    json.userId = parseInt(userSelect.value)
    json.role = roleSelect.value

    SendPost('/admin/rest/giveRole', json, "Gave role " + json.role + ' from user ' + GetUserById(json.userId).username, 
        () => { 
            GetUserById(parseInt(userSelect.value)).roles.push(roleSelect.value)
            CheckButtonDisable()
        } )
}


function SendPost(url, obj, msg, lambda) {
    
    document.body.classList.add("loading");

    var xhr =  new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('POST', url);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseURL.endsWith("login")) {
                createErrorToast("You have been logged off :(")
            } else {
                createSuccessToast(msg)
                lambda()
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
    xhr.send(JSON.stringify(obj));
    
}






