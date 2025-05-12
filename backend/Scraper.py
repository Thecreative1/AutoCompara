import json
import time
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

BASE_URL = "https://www.standvirtual.com"
LISTAGEM_URL = f"{BASE_URL}/carros/"
MAX_CARROS_POR_MARCA = 50
TEMPO_ESPERA = 10  # segundos

# Configurar o browser
options = Options()
# options.add_argument("--headless")  # descomenta para rodar sem abrir janela
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")
driver = webdriver.Chrome(options=options)
wait = WebDriverWait(driver, TEMPO_ESPERA)

def obter_marcas():
    driver.get(LISTAGEM_URL)
    wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "ul.listing__makes li a")))
    elementos = driver.find_elements(By.CSS_SELECTOR, "ul.listing__makes li a")
    marcas = [(el.text.strip(), el.get_attribute("href")) for el in elementos if el.text.strip()]
    return marcas

def obter_links_carros(url_marca):
    links = set()
    pagina = 1
    while len(links) < MAX_CARROS_POR_MARCA:
        driver.get(f"{url_marca}?page={pagina}")
        try:
            wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, "a[data-testid='listing-ad-title']")))
            anuncios = driver.find_elements(By.CSS_SELECTOR, "a[data-testid='listing-ad-title']")
            for anuncio in anuncios:
                link = anuncio.get_attribute("href")
                if link and link.startswith(BASE_URL):
                    links.add(link)
                if len(links) >= MAX_CARROS_POR_MARCA:
                    break
        except:
            break
        pagina += 1
        time.sleep(1)
    return list(links)

def extrair_detalhes(link):
    try:
        driver.get(link)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "[class*='offer-price']")))
        titulo = driver.find_element(By.TAG_NAME, "h1").text.strip()
        preco = driver.find_element(By.CSS_SELECTOR, "[class*='offer-price']").text.strip()

        # localização pode não existir em todos, usar try/except
        try:
            localizacao = driver.find_element(By.CSS_SELECTOR, "[class*='seller-contact-location']").text.strip()
        except:
            localizacao = "Desconhecida"

        return {
            "titulo": titulo,
            "preco": preco,
            "localizacao": localizacao,
            "link": link
        }
    except Exception as e:
        print(f"❌ Erro ao extrair {link}: {e}")
        return None

def main():
    todos_os_carros = []
    marcas = obter_marcas()
    print(f"🔍 Encontradas {len(marcas)} marcas")

    for nome_marca, url_marca in marcas:
        print(f"➡️  A processar: {nome_marca}")
        links = obter_links_carros(url_marca)
        print(f"   • {len(links)} anúncios encontrados")

        carros = []
        for link in links:
            detalhes = extrair_detalhes(link)
            if detalhes:
                detalhes["marca"] = nome_marca
                carros.append(detalhes)
        todos_os_carros.extend(carros)

    # Guardar em JSON
    with open("data.json", "w", encoding="utf-8") as f:
        json.dump(todos_os_carros, f, ensure_ascii=False, indent=2)
    print("✅ Dados guardados em data.json")

if __name__ == "__main__":
    try:
        main()
    finally:
        driver.quit()
