# Projeto Docker Multi-Container com API Java e PostgreSQL

## Introdução
Este projeto tem como objetivo consolidar o conhecimento sobre Dockerfile, Docker Compose, redes e volumes. Ele configura um ambiente multi-container com uma API Java e um banco de dados PostgreSQL, utilizando variáveis de ambiente para flexibilidade e segurança.

**Infraestrutura como Código (IaC):** A infraestrutura é gerenciada via Terraform, permitindo provisionamento automático do servidor na Digital Ocean com Docker pré-instalado.

## Estrutura do Projeto
```
├── Dockerfile # Dockerfile multi-stage (build e produção)
├── docker-compose.yml # Compose com API e PostgreSQL
├── .env # Variáveis de ambiente para configuração local
├── .env.example # Exemplo de arquivo de variáveis de ambiente
├── terraform/ # Infraestrutura como Código
│   ├── main.tf # Definição dos recursos (droplet, SSH key)
│   ├── variables.tf # Variáveis do Terraform
│   ├── outputs.tf # Outputs (IP do servidor)
│   ├── user-data.sh # Script de inicialização (instala Docker)
│   └── terraform.tfvars # Variáveis locais (não versionado)
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

## Infraestrutura como Código (Terraform)

### Pré-requisitos de Infraestrutura

Para que o pipeline CI/CD funcione corretamente, você precisa configurar os seguintes **Secrets no GitHub** (Settings → Secrets and variables → Actions):

#### Secrets do Terraform:
- **`TF_TOKEN`**: Token do Terraform Cloud para autenticação no backend remoto
  - Como obter: Terraform Cloud → User Settings → Tokens → Create an API token
  - Formato: `hnk8Cxe3MoMMwA.atlasv1.xxxxx...`

- **`DO_TOKEN`**: Token da API da Digital Ocean para criar recursos
  - Como obter: Digital Ocean → API → Tokens/Keys → Generate New Token
  - Permissões: Read e Write
  - Formato: `dop_v1_xxxxx...`

#### Secrets do Deploy:
- **`SSH_KEY`**: Chave privada SSH para conexão no servidor
  - Arquivo: `terraform/keys/id_ed25519`
  - Conteúdo completo incluindo `-----BEGIN OPENSSH PRIVATE KEY-----` e `-----END OPENSSH PRIVATE KEY-----`

- **`USER`**: Usuário SSH (geralmente `root`)

#### Secrets do Docker Hub:
- **`DOCKERHUB_USERNAME`**: Seu usuário do Docker Hub
- **`DOCKERHUB_TOKEN`**: Token de acesso do Docker Hub

#### Secrets do Banco de Dados:
- **`DB_USER`**: Usuário do banco de dados PostgreSQL
- **`DB_PASS`**: Senha do banco de dados PostgreSQL
- **`DB_NAME`**: Nome do banco de dados PostgreSQL

### Provisionamento Automático do Servidor

O servidor é **provisionado automaticamente** via Terraform quando o pipeline é executado:

1. **Criação do Droplet**: O Terraform cria um droplet Ubuntu 24.04 na Digital Ocean
2. **Instalação Automática**: O script `user-data.sh` é executado automaticamente na inicialização do servidor e instala:
   - Docker Engine
   - Docker Compose
   - Dependências necessárias
3. **Configuração SSH**: A chave SSH pública é injetada automaticamente no servidor
4. **IP Dinâmico**: O IP do servidor é capturado automaticamente e usado no deploy

**Não é necessário criar ou configurar o servidor manualmente** - tudo é feito automaticamente pelo Terraform e pelo script de inicialização.

### Backend Remoto (Terraform Cloud)

O estado do Terraform é armazenado no **Terraform Cloud** para permitir que o GitHub Actions gerencie a infraestrutura:

- **Organização**: `devops_docker`
- **Workspace**: `devops_docker`
- **Tipo**: API-driven workflow

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

### Execução Local

1. Copie o arquivo `.env.example` para `.env` e preencha os valores.
```bash
cp .env.example .env
```

2. Build e inicialize os containers:
```bash
docker-compose up -d --build
```

3. Rota API em: http://localhost:8080

### Execução via CI/CD

O pipeline GitHub Actions executa automaticamente:

1. **Build**: Compila a aplicação, roda testes e faz push da imagem para o Docker Hub
2. **Provision Infrastructure**: Cria/atualiza o servidor na Digital Ocean via Terraform
3. **Deploy**: Faz deploy da aplicação no servidor provisionado usando o IP dinâmico

**Não é necessário executar manualmente** - basta fazer push para a branch `main` e o pipeline será executado automaticamente.
