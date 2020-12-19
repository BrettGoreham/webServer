function createSuccessToast(toastMessage) {
  createToast(true, toastMessage);
}

function createErrorToast(toastMessage) {
  createToast(false, toastMessage);
}

function createToast(isSuccess, toastMessage) {
  var toastContainer = createToastContainer(isSuccess);
  createToastHeader(toastContainer, isSuccess);
  createToastContent(toastContainer, toastMessage);
  initToast(toastContainer);
  destroyToast(toastContainer);
}

function createToastContainer(isSuccess) {
  var toastContainer = document.createElement("div")
  toastContainer.style.opacity = 0.0
  toastContainer.classList.add("toastContainer");
  toastContainer.classList.add("darkDiv")
  if (isSuccess) {
    toastContainer.classList.add("toastContainerSuccess");
  } else {
    toastContainer.classList.add("toastContainerError");
  }
  return toastContainer;
}

function createToastHeader(toastContainer, isSuccess) {
  var toastHeader = document.createElement("div");
  toastHeader.classList.add("toastHeader");
  toastHeader.innerHTML = isSuccess ? "Success" : "Error";
  toastContainer.append(toastHeader);
}

function createToastContent(toastContainer, toastMessage) {
  var toastContent = document.createElement("div");
  toastContent.classList.add("toastContent");
  toastContent.innerHTML = toastMessage;
  toastContainer.append(toastContent);
}

function initToast(toastContainer) {
    document.getElementById("toastsContainer").append(toastContainer);
    fadeIn(toastContainer)
}

function destroyToast(toastContainer) {
    setTimeout(function() {
        fadeAway(toastContainer)
    }, 3000);
}

function fadeIn(element) {
    var op = parseFloat(element.style.opacity);

    var timer = setInterval(function () {
        if(op >= 1.0)
            clearInterval(timer);

        op += 0.1;
        element.style.opacity = op;
    }, 50);
}

function fadeAway(element) {
    var op = parseFloat(element.style.opacity);

    var timer = setInterval(function () {
        if (op <= 0) {
            element.parentElement.removeChild(element);
            clearInterval(timer);
        }

        op -= 0.1;
        element.style.opacity = op;
    }, 50);
}