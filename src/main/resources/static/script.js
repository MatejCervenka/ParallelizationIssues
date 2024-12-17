function startSimulation(endpoint) {
    fetch(endpoint).then(response => alert("Simulace spuštěna!")).catch(err => alert("Chyba!"));
}