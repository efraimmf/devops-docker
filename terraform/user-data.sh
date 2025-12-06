#!/bin/bash

# Habilitar modo de erro - para se algum comando falhar
set -e

# Log de todas as operações
exec > >(tee /var/log/user-data.log)
exec 2>&1

echo "=== Iniciando instalação do Docker ==="
date

# Atualizar o sistema (SEM upgrade para evitar reinicializações)
echo "Atualizando o sistema..."
export DEBIAN_FRONTEND=noninteractive
apt-get update
# Removido apt-get upgrade -y para evitar reinicializações

# Instalar dependências necessárias (INCLUINDO openssh-server PRIMEIRO)
echo "Instalando dependências..."
apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    openssh-server

# Configurar SSH para não desconectar (DEPOIS de instalar)
echo "Configurando SSH..."
sed -i 's/#ClientAliveInterval 0/ClientAliveInterval 60/' /etc/ssh/sshd_config || true
sed -i 's/#ClientAliveCountMax 3/ClientAliveCountMax 3/' /etc/ssh/sshd_config || true
echo "ClientAliveInterval 60" >> /etc/ssh/sshd_config
echo "ClientAliveCountMax 3" >> /etc/ssh/sshd_config
systemctl enable ssh
systemctl restart ssh

# Adicionar a chave GPG oficial do Docker
echo "Adicionando chave GPG do Docker..."
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

# Adicionar o repositório do Docker
echo "Adicionando repositório do Docker..."
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

# Atualizar novamente e instalar o Docker
echo "Instalando Docker..."
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Adicionar o usuário root ao grupo docker
echo "Adicionando usuário ao grupo docker..."
usermod -aG docker root

# Habilitar e iniciar o serviço Docker
echo "Iniciando serviço Docker..."
systemctl enable docker
systemctl start docker

# Aguardar o serviço iniciar
sleep 5

# Verificar a instalação
echo "=== Verificando instalação ==="
docker --version
docker compose version

# Verificar se o serviço está rodando
systemctl status docker --no-pager

echo "=== Instalação do Docker concluída ==="
date