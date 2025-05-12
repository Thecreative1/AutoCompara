fetch("data.json")
  .then(response => response.json())
  .then(data => {
    const container = document.getElementById("car-list");

    // Input de pesquisa
    const searchInput = document.getElementById("pesquisa");
    searchInput.type = "text";
    searchInput.placeholder = "🔍 Procurar por marca ou modelo...";
    searchInput.style.padding = "10px";
    searchInput.style.marginBottom = "20px";
    searchInput.style.width = "100%";
    container.before(searchInput);

    function renderCars(filteredData) {
      container.innerHTML = "";
      filteredData.forEach(car => {
        const item = document.createElement("div");
        item.innerHTML = `
          <h3>${car.titulo}</h3>
          <p><strong>Preço:</strong> ${car.preco || "n/d"}</p>
          <p><strong>Localização:</strong> ${car.localizacao || "n/d"}</p>
          <a href="${car.link}" target="_blank">🔗 Ver Anúncio</a>
        `;
        container.appendChild(item);
      });
    }

    renderCars(data);

    searchInput.addEventListener("input", () => {
      const term = searchInput.value.toLowerCase();
      const filtered = data.filter(car => car.titulo.toLowerCase().includes(term));
      renderCars(filtered);
    });
  });
