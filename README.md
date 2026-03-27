# DocScan — Document Management System

A full-stack document management web application with automatic OCR text extraction and full-text search. Upload documents, extract their content automatically, and search through everything instantly.

---

## Features

- **Upload documents** — Drag-and-drop support for PDF, Word (DOC/DOCX), and images (JPG, PNG, TIFF, BMP) up to 50 MB
- **Automatic OCR** — Uploaded files are processed asynchronously by a dedicated OCR service powered by Tesseract 5
- **Full-text search** — Search across all extracted document content via Elasticsearch
- **Document library** — Browse all documents with metadata, status indicators, and file-type icons
- **Download & delete** — Retrieve original files or remove documents at any time
- **Live status tracking** — OCR status updates in real time (Pending → Processing → Completed / Failed)

---

## Architecture

DocScan is built as a set of loosely coupled microservices, all containerized with Docker.

```
Browser (React)
    │
    ▼
Backend API (Spring Boot)  ──►  PostgreSQL   (metadata)
    │                      ──►  MinIO        (file storage)
    │                      ──►  Elasticsearch (search index)
    │
    ▼  RabbitMQ (async queue)
    │
    ▼
OCR Service (Spring Boot + Tesseract)
```

| Layer | Technology |
|---|---|
| Frontend | React 18, Tailwind CSS, Axios |
| Backend API | Java 17, Spring Boot 3, Spring Data JPA |
| OCR Service | Java 17, Spring Boot 3, Tesseract 5 (tess4j) |
| Database | PostgreSQL 14 |
| File Storage | MinIO (S3-compatible) |
| Message Queue | RabbitMQ 3 |
| Search Engine | Elasticsearch 7 |
| Serving | Nginx (production frontend) |
| Containerization | Docker, Docker Compose |

---

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose

### Run locally (development)

```bash
git clone https://github.com/your-username/DocumentManagementSystem.git
cd DocumentManagementSystem
docker compose -f docker-compose.dev.yml up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| RabbitMQ console | http://localhost:15672 |
| MinIO console | http://localhost:9001 |
| Elasticsearch | http://localhost:9200 |

### Run in production mode

```bash
docker compose -f docker-compose.prod.yml up --build
```

Frontend is served on **port 80** via Nginx.

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/upload` | Upload a new document |
| `GET` | `/api/documents` | List all documents |
| `GET` | `/api/documents/{id}` | Get document details |
| `GET` | `/api/documents/download/{id}` | Download original file |
| `DELETE` | `/api/documents/{id}` | Delete a document |
| `GET` | `/api/documents/search?q={term}` | Full-text search |

---

## Project Structure

```
DocumentManagementSystem/
├── backend/          Spring Boot REST API
├── frontend/         React single-page application
├── ocr-service/      Tesseract OCR microservice
├── minio/            MinIO object storage setup
├── docker-compose.dev.yml
└── docker-compose.prod.yml
```

---

## License

MIT