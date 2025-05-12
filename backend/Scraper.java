import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://www.standvirtual.com/carros/")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                .get();

            Elements anuncios = doc.select("article");

            for (Element anuncio : anuncios) {
                String titulo = anuncio.select("h2").text();
                String preco = anuncio.select("div[data-testid=ad-price]").text();
                String link = anuncio.select("a").attr("href");
                if (!link.startsWith("http")) {
                    link = "https://www.standvirtual.com" + link;
                }

                if (!titulo.isEmpty()) {
                    System.out.println("🚗 " + titulo);
                    System.out.println("💰 " + preco);
                    System.out.println("🔗 " + link);
                    System.out.println("------");
                }
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
