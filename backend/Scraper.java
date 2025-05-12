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
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .get();

            Elements anuncios = doc.select("article");

            for (Element anuncio : anuncios) {
                String titulo = anuncio.select("h2").text();
                String local = anuncio.select("span[data-testid=location-text]").text();
                String link = anuncio.select("a").attr("href");
                if (!link.startsWith("http")) {
                    link = "https://www.standvirtual.com" + link;
                }

                if (!titulo.isEmpty()) {
                    HashMap<String, String> carro = new HashMap<>();
                    carro.put("titulo", titulo);
                    carro.put("preco", preco);
                    carro.put("link", link);
                    carros.add(carro);
                }
            }

            // Guardar no ficheiro JSON
            Gson gson = new Gson();
            FileWriter writer = new FileWriter("../data.json");
            gson.toJson(carros, writer);
            writer.close();

            System.out.println("✅ Anúncios exportados com sucesso para frontend/data.json!");

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
