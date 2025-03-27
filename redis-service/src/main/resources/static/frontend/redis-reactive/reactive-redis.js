function createRow(data){
    const tBody = document.getElementById("#fluxDataTableBody");
    const tr = document.createElement('tr');
    let td = document.createElement('td');
    td.appendChild(document.createTextNode(data.requestId));
    tr.appendChild(td);
    td = document.createElement('td');
    td.appendChild(document.createTextNode(data.requestType));
    tr.appendChild(td);
    tBody.appendChild(tr);   
}


const socket = new EventSource("http://localhost:8082/rs/redis/request/fetch");
socket.onmessage = (evt) => {
    createRow(JSON.parse( evt.data ));
}