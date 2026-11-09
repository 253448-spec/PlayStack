@echo off
title Servidor API Videojuegos - UPChiapas
echo ========================================================
echo   Iniciando API REST de Videojuegos con FastAPI
echo   Documentacion interactiva: http://127.0.0.1:8000/docs
echo   Acceso desde emulador Android: http://10.0.2.2:8000/
echo ========================================================
cd /d "%~dp0"
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
pause
