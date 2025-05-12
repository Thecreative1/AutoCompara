let chart = null;

fetch("data.json")
  .then(response => response.json())
  .then(data => {
    const container = document.getElementById("car-list");
    const searchInput = document.getElementById("pesquisa");
    const canvas = document.getElementById("graficoPrecos");

    const estatisticasBox = document.createElement("div");
    estatisticasBox.id = "estatisticas";
    estatisticasBox.style.margin = "20px 0";
    container.before(estatisticasBox);

    function calcularEstatisticas(lista) {
      const precos = lista
        .map(c => parseFloat(c.preco.replace(/[^\d,]/g, '').replace(',', '.')))
        .filter(p => !isNaN(p));

      if (precos.length === 0) return "Sem preços disponíveis.";

      const soma = precos.reduce((a, b) => a + b, 0);
      const media = soma / precos.length;
      const min = Math.min(...precos);
      const max = Math.max(...precos);

      return `🔢 ${lista.length} resultados encontrados | 💰 Preço médio: ${media.toFixed(0)}€ | Mín: ${min}€ | Máx: ${max}€`;
    }

    function atualizarGrafico(precos) {
      const dados = precos
        .map(p => parseFloat(p.preco.replace(/[^\d,]/g, '').replace(',', '.')))
        .filter(p => !isNaN(p));

      const bins = {};
      dados.forEach(p => {
        const faixa = Math.floor(p / 1000) * 1000;
        bins[faixa] = (bins[faixa] || 0) + 1;
      });

      const labels = Object.keys(bins).sort((a, b) => a - b);
      const valores = labels.map(k => bins[k]);

      if (chart) chart.destroy();

      chart = new Chart(canvas, {
        type: 'bar',
        data: {
          labels: labels.map(l => `${l}€ - ${parseInt(l) + 999}€`),
          datasets: [{
            label: 'Nº de anúncios',
            data: valores,
            backgroundColor: '#7CBF8B'
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0
              }
            }
          }
        }
      });
    }

    function renderCars(filteredData) {
      container.innerHTML = "";
      estatisticasBox.innerText = calcularEstatisticas(filteredData);
      atualizarGrafico(filteredData);

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
