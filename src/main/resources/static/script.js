fetch(window.location.href)
    .then(response => response.text())
    .then(data => {
        document.body.innerHTML = `<h1>Simulation In Progress</h1><p>${data}</p>`;
    })
    .catch(error => {
        document.body.innerHTML = `<h1>Error</h1><p>${error}</p>`;
    });