import requests
import random
import time

# =============================
# ENDPOINTS
# =============================
BASE_URL = "http://localhost:8080/api"

API_USERS = f"{BASE_URL}/users"
API_CATEGORIES = f"{BASE_URL}/categories"
API_PRODUCTS = f"{BASE_URL}/products"

# =============================
# CONFIGURACIÓN
# =============================
TOTAL_PRODUCTS = 1000

# =============================
# DATOS BUSCABLES (FIJOS)
# =============================
QUALIFIERS = ["Premium", "Eco", "Plus", "Xtreme", "Lite", "Edition", "Series"]
ITEMS = ["Camera", "Speaker", "Smartwatch", "Printer", "Router", "SSD", "Drone"]
MAKERS = ["Sony", "Xiaomi", "Canon", "Bose", "TP-Link", "DJI", "Kingston"]
FEATURES = ["4K", "Wireless", "Bluetooth", "5G", "WiFi6", "128GB", "256GB"]

# =============================
# CREAR USUARIOS
# =============================
def create_users():
    print("Creando usuarios...")
    user_ids = []

    names = [
        "Valeria Mantilla",
        "Claudia Quevedo",
        "Estrella Mantilla",
        "Santiago Cedeño",
        "Patricia Chango"
    ]

    for name in names:
        first, last = name.split()
        email = f"{first.lower()}.{last.lower()}@demo.com"

        payload = {
            "name": name,
            "email": email,
            "password": "Demo1234"
        }

        r = requests.post(API_USERS, json=payload)

        if r.status_code in [200, 201]:
            user_ids.append(r.json()["id"])
            print(f"Usuario creado: {email}")
        else:
            print("Error usuario:", r.text)

    return user_ids


# =============================
# CREAR CATEGORÍAS (FIJAS)
# =============================
def create_categories():
    print("\n Registrando categorías...")
    category_ids = {}

    categories = [
        {"name": "Audio", "description": "Dispositivos de sonido"},
        {"name": "Fotografía", "description": "Cámaras y accesorios"},
        {"name": "Smart Home", "description": "Tecnología para el hogar"},
        {"name": "Almacenamiento", "description": "Memorias y discos"},
        {"name": "Conectividad", "description": "Redes y comunicación"},
        {"name": "Drones", "description": "Tecnología aérea"},
    ]

    for cat in categories:
        r = requests.post(API_CATEGORIES, json=cat)

        if r.status_code in [200, 201]:
            category_ids[cat["name"]] = r.json()["id"]
            print(f"Categoría creada: {cat['name']}")
        else:
            print("Error categoría:", r.text)

    return category_ids


# =============================
# GENERAR NOMBRE BUSCABLE
# =============================
def generate_product_name():
    return f"{random.choice(ITEMS)} {random.choice(QUALIFIERS)} {random.choice(MAKERS)} {random.choice(FEATURES)}"


# =============================
# CREAR PRODUCTOS (SIEMPRE 2 CATEGORÍAS)
# =============================
def create_products(user_ids, category_ids):
    print("\n Cargando productos masivos...\n")

    category_values = list(category_ids.values())
    created = 0

    while created < TOTAL_PRODUCTS:

        name = generate_product_name()
        price = round(random.uniform(10, 5000), 2)

        # Siempre 2 categorías distintas
        selected_categories = random.sample(category_values, 2)

        payload = {
            "name": f"{name} #{created}",
            "price": price,
            "description": "Producto generado automáticamente para pruebas",
            "userId": random.choice(user_ids),
            "categoryIds": selected_categories
        }

        r = requests.post(API_PRODUCTS, json=payload)

        if r.status_code in [200, 201]:
            created += 1
            if created % 100 == 0:
                print(f" {created} productos insertados")
        else:
            print(" Error producto:", r.text)

        time.sleep(0.01)

    print("\nCarga completada: 1000 productos creados.")


# =============================
# MAIN
# =============================
if __name__ == "__main__":

    print("===================================")
    print("   SCRIPT DE CARGA MASIVA DE DATOS  ")
    print("===================================")

    users = create_users()
    categories = create_categories()

    if users and categories:
        create_products(users, categories)
    else:
        print("No se pudo inicializar la carga de datos.")
