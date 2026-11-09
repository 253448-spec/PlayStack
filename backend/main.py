import os
import json
import time
from typing import List, Optional
import jwt
from fastapi import FastAPI, HTTPException, Header, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr, Field

# Configuración JWT
SECRET_KEY = "mi_clave_secreta_super_segura_upchiapas_2026"
ALGORITHM = "HS256"

app = FastAPI(
    title="API de Videojuegos - Proyecto Unidad 3",
    description="API REST con autenticación JWT y CRUD para la app móvil en Kotlin Jetpack Compose",
    version="1.0.0"
)

# Permitir CORS para cualquier cliente (móvil, navegador, etc.)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

DB_FILE = os.path.join(os.path.dirname(__file__), "db.json")

# Esquemas Pydantic
class UserRegister(BaseModel):
    name: str = Field(..., min_length=2, example="Emmanuel")
    email: str = Field(..., example="usuario@ejemplo.com")
    password: str = Field(..., min_length=6, example="secreto123")

class UserLogin(BaseModel):
    email: str = Field(..., example="usuario@ejemplo.com")
    password: str = Field(..., example="secreto123")

class UserResponse(BaseModel):
    id: int
    name: str
    email: str

class AuthResponse(BaseModel):
    token: str
    user: UserResponse

class VideojuegoBase(BaseModel):
    titulo: str = Field(..., min_length=2, example="The Legend of Zelda: Tears of the Kingdom")
    plataforma: str = Field(..., example="Nintendo Switch")
    precio: float = Field(..., ge=0, example=69.99)
    horasJugadas: int = Field(default=0, ge=0, example=120)
    completado: bool = Field(default=False, example=True)
    genero: str = Field(default="Aventura", example="Acción / Aventura")

class VideojuegoCreate(VideojuegoBase):
    pass

class VideojuegoUpdate(BaseModel):
    titulo: Optional[str] = None
    plataforma: Optional[str] = None
    precio: Optional[float] = None
    horasJugadas: Optional[int] = None
    completado: Optional[bool] = None
    genero: Optional[str] = None

class Videojuego(VideojuegoBase):
    id: int

# Manejo de persistencia sencilla en JSON
def load_db():
    if not os.path.exists(DB_FILE):
        initial_data = {
            "users": [
                {
                    "id": 1,
                    "name": "Alumno Demo",
                    "email": "demo@upchiapas.edu.mx",
                    "password": "password123"
                }
            ],
            "videojuegos": [
                {
                    "id": 1,
                    "titulo": "The Legend of Zelda: Breath of the Wild",
                    "plataforma": "Nintendo Switch",
                    "precio": 59.99,
                    "horasJugadas": 150,
                    "completado": True,
                    "genero": "Aventura"
                },
                {
                    "id": 2,
                    "titulo": "Elden Ring",
                    "plataforma": "PC",
                    "precio": 59.99,
                    "horasJugadas": 90,
                    "completado": False,
                    "genero": "RPG"
                },
                {
                    "id": 3,
                    "titulo": "God of War Ragnarök",
                    "plataforma": "PlayStation 5",
                    "precio": 69.99,
                    "horasJugadas": 45,
                    "completado": True,
                    "genero": "Acción"
                }
            ]
        }
        with open(DB_FILE, "w", encoding="utf-8") as f:
            json.dump(initial_data, f, indent=2, ensure_ascii=False)
        return initial_data

    with open(DB_FILE, "r", encoding="utf-8") as f:
        return json.load(f)

def save_db(data):
    with open(DB_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

def create_jwt_token(user_id: int, email: str) -> str:
    payload = {
        "sub": str(user_id),
        "email": email,
        "iat": int(time.time()),
        "exp": int(time.time()) + (60 * 60 * 24 * 7) # Vence en 7 días
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

def verify_token(authorization: Optional[str] = Header(None)) -> dict:
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Falta el encabezado Authorization"
        )
    
    parts = authorization.split(" ")
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Formato de token inválido. Debe ser: Bearer <token>"
        )
    
    token = parts[1]
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="El token ha expirado"
        )
    except jwt.InvalidTokenError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token no válido"
        )

# ==================== ENDPOINTS DE AUTENTICACIÓN ====================

@app.post("/register", response_model=AuthResponse, status_code=status.HTTP_201_CREATED)
def register(user_data: UserRegister):
    db = load_db()
    for user in db["users"]:
        if user["email"].lower() == user_data.email.lower():
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="El correo ya se encuentra registrado"
            )
    
    new_id = max([u["id"] for u in db["users"]], default=0) + 1
    new_user = {
        "id": new_id,
        "name": user_data.name,
        "email": user_data.email,
        "password": user_data.password
    }
    db["users"].append(new_user)
    save_db(db)

    token = create_jwt_token(new_id, new_user["email"])
    return {
        "token": token,
        "user": {
            "id": new_user["id"],
            "name": new_user["name"],
            "email": new_user["email"]
        }
    }

@app.post("/login", response_model=AuthResponse)
def login(credentials: UserLogin):
    db = load_db()
    user = next((u for u in db["users"] if u["email"].lower() == credentials.email.lower()), None)
    
    if not user or user["password"] != credentials.password:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Credenciales incorrectas"
        )

    token = create_jwt_token(user["id"], user["email"])
    return {
        "token": token,
        "user": {
            "id": user["id"],
            "name": user["name"],
            "email": user["email"]
        }
    }

# ==================== ENDPOINTS CRUD VIDEOJUEGOS ====================

@app.get("/videojuegos", response_model=List[Videojuego])
def list_videojuegos(token_data: dict = Depends(verify_token)):
    db = load_db()
    return db["videojuegos"]

@app.get("/videojuegos/{videojuego_id}", response_model=Videojuego)
def get_videojuego(videojuego_id: int, token_data: dict = Depends(verify_token)):
    db = load_db()
    item = next((v for v in db["videojuegos"] if v["id"] == videojuego_id), None)
    if not item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Videojuego con ID {videojuego_id} no encontrado"
        )
    return item

@app.post("/videojuegos", response_model=Videojuego, status_code=status.HTTP_201_CREATED)
def create_videojuego(game: VideojuegoCreate, token_data: dict = Depends(verify_token)):
    db = load_db()
    new_id = max([v["id"] for v in db["videojuegos"]], default=0) + 1
    new_game = game.model_dump()
    new_game["id"] = new_id
    db["videojuegos"].append(new_game)
    save_db(db)
    return new_game

@app.put("/videojuegos/{videojuego_id}", response_model=Videojuego)
def update_videojuego(videojuego_id: int, game_update: VideojuegoUpdate, token_data: dict = Depends(verify_token)):
    db = load_db()
    item_index = next((i for i, v in enumerate(db["videojuegos"]) if v["id"] == videojuego_id), None)
    if item_index is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Videojuego con ID {videojuego_id} no encontrado"
        )
    
    current_game = db["videojuegos"][item_index]
    update_data = game_update.model_dump(exclude_unset=True)
    current_game.update(update_data)
    db["videojuegos"][item_index] = current_game
    save_db(db)
    return current_game

@app.delete("/videojuegos/{videojuego_id}", status_code=status.HTTP_200_OK)
def delete_videojuego(videojuego_id: int, token_data: dict = Depends(verify_token)):
    db = load_db()
    item_index = next((i for i, v in enumerate(db["videojuegos"]) if v["id"] == videojuego_id), None)
    if item_index is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Videojuego con ID {videojuego_id} no encontrado"
        )
    
    deleted_game = db["videojuegos"].pop(item_index)
    save_db(db)
    return {"message": f"Videojuego '{deleted_game['titulo']}' eliminado correctamente", "id": videojuego_id}

# Endpoint de chequeo
@app.get("/")
def root():
    return {"status": "ok", "message": "API de Videojuegos funcionando correctamente", "docs": "/docs"}
