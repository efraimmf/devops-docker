# Projeto Docker Multi-Container com API Java e PostgreSQL

## Introdução
Este projeto tem como objetivo consolidar o conhecimento sobre Dockerfile, Docker Compose, redes e volumes. Ele configura um ambiente multi-container com uma API Java e um banco de dados PostgreSQL, utilizando variáveis de ambiente para flexibilidade e segurança.

## Estrutura do Projeto
```
├── Dockerfile # Dockerfile multi-stage (build e produção)
├── docker-compose.yml # Compose com API e PostgreSQL
├── .env # Variáveis de ambiente para configuração local
├── .env.example # Exemplo de arquivo de variáveis de ambiente
├── src/ # Código fonte Java
├── pom.xml # Projeto Maven
└── README.md # Documentação do projeto
```
## Dockerfile
- **Build:** Utiliza Maven e Alpine para compilar a aplicação.
- **Produção:** Utiliza Eclipse Temurin JRE Alpine para rodar a aplicação.
- Multi-stage para imagens menores e seguras.

## Docker Compose
- **Serviços:**
  - `api`: API Java que consome o banco de dados.
  - `db`: PostgreSQL com volume para persistência de dados.
- **Rede:** `app-network` para comunicação isolada entre containers.
- **Volumes:** `postgres-data` para manter os dados do banco persistentes.
- **Healthcheck:** Verifica se o banco está pronto antes de iniciar a API.

## Variáveis de Ambiente
O projeto utiliza variáveis de ambiente para configurar a conexão com o banco de dados.  
Crie um arquivo `.env` baseado no `.env.example`:
```
DB_URL=jdbc:postgresql://db:5432/YOUR_DB
DB_NAME=YOUR_DB
DB_USER=YOUR_USER
DB_PASS=YOUR_PASSWORD
```
## Instruções de Execução
1. Copie o arquivo `.env.example` para `.env` e preencha os valores.
```bash
cp .env.example .env
```
2. Build e inicialize os containers:
 ```
 docker-compose up -d --build
```
3. Rota API em: http://localhost:8080
