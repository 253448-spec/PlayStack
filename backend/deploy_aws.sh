#!/bin/bash
# Script de despliegue automático para AWS EC2 (Ubuntu)
set -e

echo "=== 1. Actualizando sistema ==="
sudo apt-get update -y
sudo apt-get install -y python3 python3-pip python3-venv

echo "=== 2. Creando entorno virtual ==="
cd /home/ubuntu
mkdir -p app && cd app
python3 -m venv venv
source venv/bin/activate

echo "=== 3. Instalando dependencias ==="
pip install --upgrade pip
pip install fastapi uvicorn pyjwt pydantic

echo "=== 4. Configurando servicio systemd para ejecución 24/7 ==="
sudo bash -c 'cat > /etc/systemd/system/videojuegos.service <<EOF
[Unit]
Description=API REST Videojuegos FastAPI
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/app
ExecStart=/home/ubuntu/app/venv/bin/uvicorn main:app --host 0.0.0.0 --port 8000
Restart=always

[Install]
WantedBy=multi-user.target
EOF'

echo "=== 5. Iniciando servicio ==="
sudo systemctl daemon-reload
sudo systemctl enable videojuegos.service
sudo systemctl restart videojuegos.service

echo "=========================================================="
echo " ¡LISTO! Tu API está corriendo en segundo plano."
echo " Prueba en tu navegador: http://$(curl -s http://checkip.amazonaws.com):8000/docs"
echo "=========================================================="
