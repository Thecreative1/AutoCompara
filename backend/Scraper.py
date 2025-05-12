import json
import time
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

BASE_URL = "https://www.standvirtual.com"
LISTAGEM_URL = f"{BASE_URL}/carros/"
MAX_CARROS_POR_MARCA = 5
TEMPO_ESPERA = 20

# Setup do Chrome
options = Options()
# options.add_argument("--headless=new")
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")
driver = webdriver.Chrome(options=options)
wait = WebDriverWait(driver, TEMPO_ESPERA)

def obter_marcas():
    driver.get(LISTAGEM_URL)
    print("🔍 Página carregada... a aguardar por conteúdo JS...")

    time.sleep(5)  # espera para garantir carregamento total

    # salvar HTML da página para inspeção
    with open("pagina_debug_final.html", "w", encoding="utf-8") as f:
        f.write(driver.page_source)
    print("💾 HTML salvo como pagina_debug_final.html")

    # encontrar todos os <a>
    elementos = driver.find_elements(By.TAG_NAME, "a")
    marcas = []
    for el in elementos:
        href = el.get_attribute("href")
        texto = el.text.strip()

        if not href or not texto:
            continue

        if "/carros/" in href and "anuncio" not in href:
            texto_sem_espacos = texto.replace(" ", "").replace("-", "")
            if texto_sem_espacos.isalpha() and len(texto) > 1:
                marcas.append((texto, href))

    print(f"✅ Encontradas {len(marcas)} marcas candidatas.")
    return marcas[:10]

def main():
    marcas = obter_marcas()
    for nome, link in marcas:
        print(f"➡️  {nome}: {link}")
    print("🎯 Fim do teste de debug.")

if __name__ == "__main__":
    try:
        main()
    finally:
        driver.quit()
