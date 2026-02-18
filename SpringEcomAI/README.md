# SpringEcomAI

E-commerce platform with AI-powered features built with Spring Boot and Google Gemini.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.5.10, Java 21 |
| AI (Chat & Descriptions) | Spring AI 1.1.1 → Google Gemini 2.0 Flash |
| AI (Image Generation) | Google GenAI SDK → Gemini 2.5 Flash Image |
| AI (Embeddings) | Google text-embedding-004 via Vertex AI |
| Vector Store | PostgreSQL 17 + pgvector |
| ORM | Spring Data JPA / Hibernate |
| Frontend | React (separate project) |

## Architecture Overview

```mermaid
graph TB
    subgraph Frontend
        React[React App]
    end

    subgraph Backend["Spring Boot Backend"]
        Controllers[REST Controllers]
        ProductSvc[ProductService]
        ChatBotSvc[ChatBotService]
        OrderSvc[OrderService]
        ImageGenSvc[AiImageGeneratorService]
    end

    subgraph AI["Google Gemini"]
        Chat[Gemini 2.0 Flash<br/>Chat Model]
        Embed[text-embedding-004<br/>Embedding Model]
        ImageGen[Gemini 2.5 Flash Image<br/>Image Generation]
    end

    subgraph Database["PostgreSQL 17"]
        Tables[(Products, Orders)]
        VectorStore[(pgvector<br/>vector_store)]
    end

    React -- REST API --> Controllers
    Controllers --> ProductSvc
    Controllers --> ChatBotSvc
    Controllers --> OrderSvc
    ProductSvc --> Chat
    ProductSvc --> ImageGenSvc
    ImageGenSvc --> ImageGen
    ChatBotSvc --> Embed
    ChatBotSvc --> Chat
    ChatBotSvc --> VectorStore
    ProductSvc --> Tables
    ProductSvc --> VectorStore
    OrderSvc --> Tables
    OrderSvc --> VectorStore
```

## RAG Chatbot Flow

The chatbot uses Retrieval-Augmented Generation (RAG) to answer questions about products and orders using real data from the database.

```mermaid
sequenceDiagram
    participant User
    participant ChatBotController
    participant ChatBotService
    participant EmbeddingModel
    participant PgVectorStore
    participant GeminiChat

    User->>ChatBotController: GET /api/chat/ask?message=...
    ChatBotController->>ChatBotService: getBotResponse(userQuery)

    Note over ChatBotService: 1. Semantic Search
    ChatBotService->>EmbeddingModel: Embed user query
    EmbeddingModel->>PgVectorStore: Similarity search (top 5, threshold 0.7)
    PgVectorStore-->>ChatBotService: Matching documents (products, orders)

    Note over ChatBotService: 2. Build Prompt
    ChatBotService->>ChatBotService: Load prompt template<br/>Inject context + user query

    Note over ChatBotService: 3. Generate Response
    ChatBotService->>GeminiChat: Send augmented prompt
    GeminiChat-->>ChatBotService: AI response
    ChatBotService-->>User: Grounded answer
```

### How Data Gets Into the Vector Store

Products and orders are automatically embedded and stored when created or updated:

```mermaid
flowchart LR
    A[Product Created/Updated] --> B[Format as text document]
    B --> C[Generate embedding via text-embedding-004]
    C --> D[Store in pgvector]

    E[Order Placed] --> F[Format order summary]
    F --> G[Generate embedding]
    G --> D

    E --> H[Update product stock]
    H --> I[Re-embed updated product]
    I --> D
```

## AI Image Generation Flow

Image generation uses the Google GenAI SDK directly (not Spring AI) to call the Gemini 2.5 Flash Image model.

```mermaid
sequenceDiagram
    participant User
    participant ProductController
    participant ProductService
    participant AiImageGeneratorService
    participant GeminiImageAPI

    User->>ProductController: POST /api/product/generate-image
    Note right of User: params: name, category, description
    ProductController->>ProductService: generateImage(name, category, description)
    ProductService->>ProductService: Build detailed image prompt<br/>(studio lighting, white bg, no logos...)
    ProductService->>AiImageGeneratorService: generateImage(prompt)
    AiImageGeneratorService->>GeminiImageAPI: generateContent("gemini-2.5-flash-image", prompt)
    Note over GeminiImageAPI: responseModalities: TEXT, IMAGE
    GeminiImageAPI-->>AiImageGeneratorService: Response with inline image data
    AiImageGeneratorService->>AiImageGeneratorService: Extract byte[] from inlineData
    AiImageGeneratorService-->>User: Raw image bytes
```

## API Endpoints

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/product/{id}` | Get product by ID |
| `GET` | `/api/product/{id}/image` | Get product image |
| `POST` | `/api/product` | Add product (multipart: product + image) |
| `PUT` | `/api/product/{id}` | Update product |
| `DELETE` | `/api/product/{id}` | Delete product |
| `GET` | `/api/products/search?keyword=` | Search products |

### AI Features

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/product/generate-description?name=&category=` | Generate AI product description |
| `POST` | `/api/product/generate-image?name=&category=&description=` | Generate AI product image |
| `GET` | `/api/chat/ask?message=` | RAG chatbot |

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders/place` | Place an order |
| `GET` | `/api/orders` | List all orders |

## Setup

### Prerequisites

- Java 21+
- PostgreSQL 17 with pgvector extension
- Google Cloud project with:
    - Gemini API key ([AI Studio](https://aistudio.google.com/apikey))
    - Vertex AI API enabled
    - Billing account linked

### Configuration

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/local
spring.datasource.username=your_username
spring.datasource.password=your_password

# Google Gemini AI
spring.ai.google.genai.api-key=YOUR_API_KEY
spring.ai.google.genai.project-id=YOUR_GCP_PROJECT_ID
spring.ai.google.genai.embedding.project-id=YOUR_GCP_PROJECT_ID
spring.ai.google.genai.embedding.location=us-central1
spring.ai.google.genai.chat.options.model=gemini-2.0-flash
spring.ai.google.genai.embedding.options.model=text-embedding-004
```

### Run

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`.