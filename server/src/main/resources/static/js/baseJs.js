function greeting(){
    alert("hello, world");
}

function toggleHideButton(elementToToggle) {
    if (elementToToggle.style.display === "block") {
        elementToToggle.style.display = "none";
    } else {
        elementToToggle.style.display = "block";
    }
}