from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

url = "https://www.standvirtual.com/carros/anuncio/dacia-lodgy-ver-1-5-blue-dci-stepway-7l-ID8PQBq1.html"

# Configurações do Chrome
options = Options()
# options.add_argument("--headless")  # descomenta para não abrir o browser

driver = webdriver.Chrome(options=options)
driver.get(url)

try:
    # Espera até o preço estar visível
    wait = WebDriverWait(driver, 10)
    preco_el = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "[class*='offer-price']")))
    titulo_el = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "h1")))

    preco = preco_el.text.strip()
    titulo = titulo_el.text.strip()

    print(f"✔ {titulo} → {preco}")

except Exception as e:
    print("❌ Preço não encontrado:", e)

finally:
    driver.quit()
