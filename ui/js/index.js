const swimUrl = `warp://${window.location.host}`;
const numSensors = 10;

let listElement = null;
let listItems = [];

function start() {
    initListItems();

    listElement = document.getElementById("sensorList");

    /* Data Subscriptions */
    for (let i = 0; i < numSensors; i++) {
        swim.downlinkMap()
            .hostUri(swimUrl)
            .nodeUri(`/unit/${i}`)
            .laneUri("history")
            .didUpdate((key, newMsg) => {
                listItems[i] = `${key.valueOf()}, ${newMsg}`;
                console.log(`${i} ${listItems[i]}`);
                renderList();
            })
            .open();
    }
}

function initListItems() {
    listItems = [];
    for (let i = 0; i < numSensors; i++) {
        listItems.push('-');
    }
}

function renderList() {
    listElement.innerHTML = ''; // clear the element by brute force

    // for each key in listItems,
    // create a <li> element with the value of the item
    // and a remove button, append <li> to listElement
    for(let i = 0; i < numSensors; i++) {

        // grab current list item
        const currItem = listItems[i];

        // create the <li> element
        const domElement = document.createElement('li');

        // create a <span> to hold the label for the list item
        const labelElement = document.createElement('span');
        labelElement.innerText = currItem;

        domElement.appendChild(labelElement);
        listElement.appendChild(domElement);
    }
}