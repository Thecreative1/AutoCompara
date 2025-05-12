fetch("data.json")
  .then(response => response.json())
  .then(data => {
    const container = document.getElementById("car-list");
    data.forEach(car => {
      const item = document.createElement("div");
      item.innerHTML = `
        <h3>${car.titulo}</h3>
        <p><strong>Preço:</strong> ${car.preco}</p>
        <p><strong>Localização:</strong> ${car.localizacao}</p>
        <a href="${car.link}" target="_blank">Ver Anúncio</a>
      `;
      container.appendChild(item);
    });
  });