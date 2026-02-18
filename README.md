# SpringEcomAI

Full-stack e-commerce platform with AI-powered features. Built with Spring Boot and React, using Google Gemini for product description generation, image generation, and a RAG-based chatbot.

[![Live Demo](https://img.shields.io/badge/Live_Demo-ecom.ignalarcon.dev-blue?style=for-the-badge)](https://ecom.ignalarcon.dev)

## Features

- **Product Management** — CRUD operations with image upload
- **AI Description Generator** — generates product descriptions from name and category using Gemini 2.0 Flash
- **AI Image Generator** — creates product images using Gemini 2.5 Flash Image
- **RAG Chatbot** — answers questions about products and orders using semantic search over a vector store
- **Order System** — place orders with automatic stock management
- **Search** — keyword-based product search

## Architecture

```mermaid
graph TB
    subgraph Client["Frontend (React + Vite)"]
        UI[React App]
        Cart[Cart — localStorage]
    end

    subgraph Server["Backend (Spring Boot 3.5)"]
        API[REST API]
        Services[Services Layer]
        SpringAI[Spring AI 1.1]
        GenAISDK[Google GenAI SDK]
    end

    subgraph AI["Google Gemini"]
        Chat[Gemini 2.0 Flash — Chat]
        Embed[text-embedding-004 — Embeddings]
        ImageGen[Gemini 2.5 Flash Image]
    end

    subgraph DB["PostgreSQL 17"]
        Tables[(Products · Orders)]
        Vectors[(pgvector — Vector Store)]
    end

    UI -- "HTTP/REST" --> API
    API --> Services
    Services --> SpringAI
    Services --> GenAISDK
    SpringAI --> Chat
    SpringAI --> Embed
    GenAISDK --> ImageGen
    Embed --> Vectors
    Services --> Tables

    style Client fill:#e8f4f8,stroke:#2980b9
    style Server fill:#fef9e7,stroke:#f39c12
    style AI fill:#f9ebea,stroke:#e74c3c
    style DB fill:#eafaf1,stroke:#27ae60
```

## How the AI Features Work

### RAG Chatbot

```mermaid
sequenceDiagram
    participant User
    participant React
    participant Backend
    participant Embeddings
    participant VectorStore
    participant Gemini

    User->>React: "Do you have laptops in stock?"
    React->>Backend: GET /api/chat/ask?message=...
    Backend->>Embeddings: Embed user query (768 dims)
    Embeddings->>VectorStore: Similarity search (top 5)
    VectorStore-->>Backend: Matching product/order docs
    Backend->>Backend: Build prompt with context
    Backend->>Gemini: Augmented prompt
    Gemini-->>React: Grounded response
    React-->>User: Display answer
```

### AI Product Creation

```mermaid
flowchart LR
    A[User enters name + category] --> B["Generate Description — Gemini 2.0 Flash"]
    B --> C[User reviews/edits description]
    C --> D["Generate Image — Gemini 2.5 Flash Image"]
    D --> E[User previews image]
    E --> F[Submit product]
    F --> G[Save to DB + Embed in vector store]
```

## Project Structure

```
.
├── README.md                ← You are here
├── docker-compose.yml       ← Run everything locally
├── SpringEcomAI/            ← Backend (Spring Boot)
│   ├── Dockerfile
│   ├── README.md
│   └── src/
└── t-ecom/                  ← Frontend (React)
    ├── Dockerfile
    ├── nginx.conf
    ├── README.md
    └── src/
```

See [Backend README](./SpringEcomAI/README.md) and [Frontend README](./t-ecom/README.md) for details on each.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Frontend | React 18, Vite 7, Bootstrap 5 |
| Backend | Spring Boot 3.5, Java 21, Spring AI 1.1 |
| AI Chat & Descriptions | Google Gemini 2.0 Flash |
| AI Image Generation | Google GenAI SDK → Gemini 2.5 Flash Image |
| Embeddings | Google text-embedding-004 (768 dims) |
| Database | PostgreSQL 17 + pgvector |
| Containerization | Docker + Nginx |

## Quick Start

### Prerequisites

- Java 21+
- Node.js 18+
- PostgreSQL 17 with pgvector extension
- Google Cloud project with Gemini API key and Vertex AI enabled

### Run with Docker Compose

```bash
# Set your API key
export GEMINI_API_KEY=your_api_key_here

# Start all services
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend | http://localhost:8080 |
| PostgreSQL | localhost:5432 |

### Run Manually

**Backend:**
```bash
cd SpringEcomAI
mvn spring-boot:run
```

**Frontend:**
```bash
cd t-ecom
npm install
npm run dev
```

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `GEMINI_API_KEY` | Google Gemini API key | Yes |
| `DB_PASSWORD` | PostgreSQL password | Yes (Docker) |
| `VITE_BASE_URL` | Backend URL for frontend | Yes |

## API Endpoints

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/product/{id}` | Get product by ID |
| `POST` | `/api/product` | Add product (multipart) |
| `PUT` | `/api/product/{id}` | Update product |
| `DELETE` | `/api/product/{id}` | Delete product |
| `GET` | `/api/products/search?keyword=` | Search products |

### AI Features

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/product/generate-description` | AI product description |
| `POST` | `/api/product/generate-image` | AI product image |
| `GET` | `/api/chat/ask?message=` | RAG chatbot |

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders/place` | Place an order |
| `GET` | `/api/orders` | List all orders |

## Author

[Ignacio Alarcón Varela](https://ignalarcon.dev/)
