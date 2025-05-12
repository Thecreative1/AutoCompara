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
# options.add_argument("--headless=new")  # ← desligado para veres
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

driver = webdriver.Chrome(options=options)
wait = WebDriverWait(driver, TEMPO_ESPERA)

def obter_marcas():
    driver.get(LISTAGEM_URL)
    print("🔍 Página de marcas carregada")

    time.sleep(3)  # garantir que tudo carregou
    html = driver.page_source
    with open("pagina_debug.html", "w", encoding="utf-8") as f:
        f.write(html)
    print("💾 HTML da página guardado em pagina_debug.html")

    try:
        wait.until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, "a[data-testid='filters-list-item-link']")))
        elementos = driver.find_elements(By.CSS_SELECTOR, "a[data-testid='filters-list-item-link']")
        marcas = []
        for el in elementos:
            href = el.get_attribute("href")
            nome = el.text.strip()
            if "/carros/" in href and nome:
                marcas.append((nome, href))
        print(f"✅ Encontradas {len(marcas)} marcas")
        return marcas
    except Exception as e:
        print("❌ Erro ao encontrar marcas:", e)
        return []

def main():
    marcas = obter_marcas()
    print("🎯 Fim do teste de debug.")

if __name__ == "__main__":
    try:
        main()
    finally:
        driver.quit()
