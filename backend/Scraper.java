import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class Scraper {
    public static void main(String[] args) {
        ArrayList<HashMap<String, String>> carros = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Ativa isto se quiseres ocultar o browser
        WebDriver driver = new ChromeDriver(options);

        try {
            Document doc = Jsoup.connect("https://www.standvirtual.com/carros/")
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Elements anuncios = doc.select("article");

            for (int i = 0; i < Math.min(15, anuncios.size()); i++) {
                Element anuncio = anuncios.get(i);

                String titulo = anuncio.select("h2").text();
                String local = anuncio.select("span[data-testid=location-text]").text();

                // 🚫 Só aceitar links que contêm "/carros/anuncio/"
                String link = anuncio.select("a[href*='/carros/anuncio/']").attr("href");
                if (link.isEmpty()) continue;

                if (!link.startsWith("http")) {
                    link = "https://www.standvirtual.com" + link;
                }

                String preco = "n/d";

                try {
                    driver.get(link);
                    Thread.sleep(4000); // Espera 4 segundos

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement precoEl = wait.until(driver1 -> {
                        WebElement el = driver1.findElement(By.cssSelector("h3.offer-price__value"));
                        return el.getText().trim().isEmpty() ? null : el;
                    });

                    preco = precoEl.getText().replaceAll("[^0-9€,. ]", "").trim();

                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Files.copy(screenshot.toPath(), new File("screenshot_" + i + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e) {
                    System.out.println("❌ Preço não encontrado em: " + link);
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

            Gson gson = new Gson();
            FileWriter writer = new FileWriter("../data.json");
            gson.toJson(carros, writer);
            writer.close();

            System.out.println("✅ Exportado com sucesso para data.json!");
        } catch (Exception e) {
            System.out.println("Erro geral: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
