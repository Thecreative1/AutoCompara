let carros = [];

async function carregarDados() {
  const resposta = await fetch("data.json");
  carros = await resposta.json();

  const marcas = [...new Set(carros.map(carro => carro.marca))].sort();
  const select = document.getElementById("marca");

  marcas.forEach(marca => {
    const option = document.createElement("option");
    option.value = marca;
    option.textContent = marca;
    select.appendChild(option);
  });

  select.addEventListener("change", () => mostrarCarros(select.value));
}

function mostrarCarros(marcaSelecionada) {
  const divResultados = document.getElementById("resultados");
  divResultados.innerHTML = "";

  const filtrados = carros
    .filter(carro => carro.marca === marcaSelecionada)
    .sort((a, b) => parsePreco(a.preco) - parsePreco(b.preco))
    .slice(0, 50);

  if (filtrados.length === 0) {
    divResultados.innerHTML = "<p>Nenhum carro encontrado.</p>";
    return;
  }

  filtrados.forEach(carro => {
    const card = document.createElement("div");
    card.className = "carro-card";
    card.innerHTML = `
      <h3>${carro.titulo}</h3>
      <p><strong>Preço:</strong> ${carro.preco}</p>
      <p><strong>Localização:</strong> ${carro.localizacao}</p>
      <a href="${carro.link}" target="_blank">Ver Anúncio</a>
    `;
    divResultados.appendChild(card);
  });
}

function parsePreco(precoStr) {
  return parseFloat(precoStr.replace(/[^\d,]/g, '').replace(',', '.')) || 0;
}

carregarDados();
