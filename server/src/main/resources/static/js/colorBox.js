document.addEventListener("keydown", (event) => {
    if(event.key == '|') {
        if (colorPickerBox.style.left == '-275px') {
            colorPickerBox.style.left = '50px'
        }
        else {
            colorPickerBox.style.left = '-275px'
        }
    }
});

var root = document.querySelector(':root');

var colorPickerBox = document.getElementById('__colorPickerBox__')

const displayToCSSName = new Map();
displayToCSSName.set('Background', '--main-bg-color');
displayToCSSName.set('Light', '--main-light-color');
displayToCSSName.set('Neutral', '--main-neutral-color');
displayToCSSName.set('Highlight', '--main-highlight-color');
displayToCSSName.set('Highlight Text', '--main-highlight-text-color');
displayToCSSName.set('Text', '--main-text-color');
displayToCSSName.set('Secondary', '--main-secondary-color');

var defaults = {}
startup()


var colorTypesDiv = document.getElementById('colorTypes')

for (const [key, value] of displayToCSSName) {
    var div = document.createElement('div')
    div.id = value
    var label = document.createElement('label')
    label.id = key + 'label'
    label.htmlFor = key + 'pick'
    label.textContent = key
    label.style = 'display: inline-block; vertical-align:middle; width: 150px;'
    div.appendChild(label)
    var colorPicker = document.createElement('input')
    colorPicker.id = key + 'pick'
    colorPicker.style =  'vertical-align:middle;'
    colorPicker.type = 'color'
    div.appendChild(colorPicker)
    colorPicker.value = getComputedPropertyValue(value)
    colorTypesDiv.appendChild(div)
}



function colorApply() {
    for (const [key, value] of displayToCSSName) {
        var color = document.getElementById(key + 'pick')
        if (color.value != getComputedPropertyValue(value)) {
            window.sessionStorage.removeItem(key)
            window.sessionStorage.setItem(key, color.value)
            root.style.setProperty(value, color.value);
        }
    }
}

function randomizeColors() {
    for (const [key, value] of displayToCSSName) {
        let newColor = '#'
        for(let i = 0; i < 6; i++) {
            newColor += colorstringChoices[getRandomInt(colorstringChoices.length)]
        }

        let pick = document.getElementById(key + 'pick')
        pick.value = newColor
    }
}

function reset() {
    for (const [key, value] of displayToCSSName) {
        let pick = document.getElementById(key + 'pick')
        pick.value = defaults[key]
    }

    colorApply()
}

var colorstringChoices = '0123456789abcdef'
function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}

function startup() {
    for (const [key, value] of displayToCSSName) {
        defaults[key] = getComputedPropertyValue(value)

        var item = window.sessionStorage.getItem(key)
        if (item != null) {
            root.style.setProperty(value, item);
        }
    }
}

function getComputedPropertyValue(value) {
    return getComputedStyle(document.body).getPropertyValue(value).trim()
}


