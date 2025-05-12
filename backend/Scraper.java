import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;

public class Scraper {
    public static void main(String[] args) {
        ArrayList<HashMap<String, String>> carros = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://www.standvirtual.com/carros/")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(10000)
                .get();

            Elements anuncios = doc.select("article");

            for (int i = 0; i < Math.min(10, anuncios.size()); i++) { // LIMITAR A 10 PARA TESTAR
                Element anuncio = anuncios.get(i);

                String titulo = anuncio.select("h2").text();
                String local = anuncio.select("span[data-testid=location-text]").text();
                String link = anuncio.select("a").attr("href");
                if (!link.startsWith("http")) {
                    link = "https://www.standvirtual.com" + link;
                }

                String preco = "n/d";

                try {
                    Document pagina = Jsoup.connect(link)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(10000)
                        .get();

                    // Pode mudar: inspeciona o HTML se necessário
                    Element precoEl = pagina.selectFirst("div.offer-price__value p");
                    if (precoEl != null) {
                        preco = precoEl.text();
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao abrir: " + link);
                }

                if (!titulo.isEmpty()) {
                    HashMap<String, String> carro = new HashMap<>();
                    carro.put("titulo", titulo);
                    carro.put("preco", preco);
                    carro.put("localizacao", local);
                    carro.put("link", link);
                    carros.add(carro);
                    System.out.println("✔ " + titulo + " → " + preco);
                }
            }

            // Guardar no ficheiro JSON
            Gson gson = new Gson();
            FileWriter writer = new FileWriter("../data.json");
            gson.toJson(carros, writer);
            writer.close();

            System.out.println("✅ Anúncios exportados com sucesso para data.json!");

        } catch (Exception e) {
            System.out.println("Erro geral: " + e.getMessage());
        }
    }
}
