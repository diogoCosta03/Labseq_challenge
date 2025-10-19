# LabSeq Sequence Calculator

A REST API service built with Quarkus (Java) that calculates values from the **labseq sequence** with intelligent caching. Includes a modern Angular frontend for easy interaction.

## 📋 Table of Contents

- [About the LabSeq Sequence](#about-the-labseq-sequence)
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start with Docker](#quick-start-with-docker)
- [REST API Documentation](#rest-api-documentation)
- [Execution Instructions](#execution-instructions)
- [API Endpoints](#api-endpoints)
- [Performance](#performance)
- [Development](#development)
- [Testing](#testing)
- [Technology Stack](#technology-stack)

---

## 🔢 About the LabSeq Sequence

The **labseq** sequence is defined as follows:

```
l(0) = 0
l(1) = 1
l(2) = 0
l(3) = 1
l(n) = l(n-4) + l(n-3)  for n > 3
```

### Example sequence values:
```
Index:  0  1  2  3  4  5  6  7  8  9  10 ...
Value:  0  1  0  1  1  1  1  2  2  2  3  ...
```

---

## ✨ Features

### Core Functionality
- ✅ Calculate labseq value at any non-negative index
- ✅ Intelligent caching mechanism for performance optimization
- ✅ Handles large numbers using `BigInteger`
- ✅ Thread-safe implementation with `ConcurrentHashMap`
- ✅ RESTful API following best practices

### Technical Features
- 🔄 **Caching**: All intermediate calculations are cached
- 📊 **Monitoring**: Cache statistics endpoint
- 🎯 **OpenAPI/Swagger**: Interactive API documentation
- 🐳 **Docker**: Fully containerized application
- 🎨 **Modern UI**: Angular frontend with responsive design

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Frontend (Angular)                 │
│                  http://localhost:80                 │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP/REST
                       ▼
┌─────────────────────────────────────────────────────┐
│              Backend (Quarkus/Java 21)              │
│                http://localhost:8080                 │
│  ┌──────────────────────────────────────────────┐  │
│  │  REST Resource Layer (JAX-RS)                │  │
│  │  ├─ GET  /labseq/{n}                         │  │
│  │  ├─ GET  /labseq/cache/stats                 │  │
│  │  └─ DELETE /labseq/cache                     │  │
│  └──────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────┐  │
│  │  Service Layer (Business Logic)              │  │
│  │  ├─ LabSeqService                            │  │
│  │  └─ ConcurrentHashMap Cache                  │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

### Design Decisions & Assumptions

1. **Caching Strategy**: 
   - Uses `ConcurrentHashMap` for thread-safe in-memory caching
   - Cache persists across requests (application-scoped)
   - All intermediate calculations are cached for performance

2. **Data Type**: 
   - `BigInteger` to handle arbitrarily large values
   - l(100000) produces a number with thousands of digits

3. **Algorithm**: 
   - Bottom-up dynamic programming (iterative, not recursive)
   - Avoids stack overflow issues
   - O(n) time complexity for first calculation, O(1) for cached values

4. **Thread Safety**: 
   - Service is `@ApplicationScoped` (singleton)
   - Concurrent requests safely access the shared cache
   - No locking on read operations

5. **API Design**:
   - RESTful principles with proper HTTP status codes
   - JSON responses with metadata (calculation time, cache size)
   - OpenAPI 3.0 documentation

---

## 📦 Prerequisites

### Option 1: Docker (Recommended)
- Docker 20.10+
- Docker Compose 2.0+
- 4GB RAM minimum
- 10GB free disk space

### Option 2: Local Development
- **Backend**: Java 21+, Maven 3.9+
- **Frontend**: Node.js 20+, npm 10+

---

## 🚀 Quick Start with Docker

### 1. Clone or Navigate to Project

```bash
cd labseq_challenge
```

### 2. Project Structure

```
labseq_challenge/
├── Backend/
│   └── labseq/
│       ├── src/
│       ├── pom.xml
│       └── Dockerfile
├── Frontend/
│   └── labseq-frontend/
│       ├── src/
│       ├── package.json
│       ├── nginx.conf
│       └── Dockerfile
└── docker-compose.yml
```

### 3. Start the Application

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### 4. Access the Application

Once running, access:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost | Modern web interface |
| **Backend API** | http://localhost:8080 | REST API base URL |
| **Swagger UI** | http://localhost:8080/swagger-ui | Interactive API documentation |
| **OpenAPI Spec** | http://localhost:8080/openapi | OpenAPI 3.0 specification (JSON) |

---

## 📚 REST API Documentation

### Accessing API Documentation

The application provides **interactive API documentation** using Swagger UI:

1. **Start the application** (see Quick Start above)
2. **Open browser** to: http://localhost:8080/swagger-ui
3. **Explore endpoints** and test directly in the browser

### OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:
- **JSON format**: http://localhost:8080/openapi
- **YAML format**: http://localhost:8080/openapi?format=yaml



---

## 🔧 Execution Instructions

### Docker Deployment (Production Ready)

#### Method 1: Docker Compose (Recommended)

```bash
# Navigate to project root
cd labseq_challenge

# Start services in detached mode
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

#### Method 2: Individual Docker Commands

```bash
# Backend
cd Backend/labseq
docker build -t labseq-backend .
docker run -d -p 8080:8080 --name backend labseq-backend

# Frontend
cd ../../Frontend/labseq-frontend
docker build -t labseq-frontend .
docker run -d -p 80:80 --name frontend labseq-frontend
```

### Local Development

#### Backend (Quarkus)

```bash
cd Backend/labseq

# Install dependencies and run in dev mode (with hot reload)
./mvnw quarkus:dev

# Or build and run
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar

# Access at: http://localhost:8080
```

#### Frontend (Angular)

```bash
cd Frontend/labseq-frontend

# Install dependencies
npm install

# Run development server
ng serve

# Or build for production
npm run build

# Access at: http://localhost:4200
```

### Docker Management Commands

```bash
# View running containers
docker-compose ps

# View logs for specific service
docker-compose logs -f backend
docker-compose logs -f frontend

# Restart services
docker-compose restart

# Rebuild after code changes
docker-compose up -d --build

# Stop services without removing containers
docker-compose stop

# Start stopped services
docker-compose start

# Remove everything (containers, networks, volumes)
docker-compose down -v

# View resource usage
docker stats
```

### Health Checks

```bash
# Backend health
curl http://localhost:8080/labseq/cache/stats

# Frontend health
curl http://localhost/

# Check if services are healthy
docker inspect --format='{{.State.Health.Status}}' labseq-backend
docker inspect --format='{{.State.Health.Status}}' labseq-frontend
```

---

## 🌐 API Endpoints

### Calculate LabSeq Value

**GET** `/labseq/{n}`

Calculate and return the labseq value at index n.

**Parameters:**
- `n` (path parameter, integer): Index in the sequence (must be ≥ 0)

**Response (200 OK):**
```json
{
  "index": 10,
  "value": "3",
  "calculationTimeMs": 5,
  "cacheSize": 11
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Invalid input",
  "message": "Index must be non-negative",
  "status": 400
}
```

**Example Usage:**
```bash
# Using curl
curl http://localhost:8080/labseq/10

# Using httpie
http GET http://localhost:8080/labseq/10

# Using Postman - Import from http://localhost:8080/openapi
```

---

### Get Cache Statistics

**GET** `/labseq/cache/stats`

Returns information about the current cache state.

**Response (200 OK):**
```json
{
  "cacheSize": 50
}
```

**Example Usage:**
```bash
curl http://localhost:8080/labseq/cache/stats
```

---

### Clear Cache

**DELETE** `/labseq/cache`

Clears all cached values except base values (l(0) to l(3)).

**Response (200 OK):**
```json
{
  "message": "Cache cleared successfully",
  "cacheSize": 4
}
```

**Example Usage:**
```bash
curl -X DELETE http://localhost:8080/labseq/cache
```

---



## 💻 Development

### Backend Structure

```
src/main/java/com/labseq/
├── resource/
│   └── LabSeqResource.java       # REST endpoints (JAX-RS)
├── service/
│   └── LabSeqService.java        # Business logic + caching
├── dto/
│   ├── LabSeqResponse.java       # Response DTOs
│   ├── ErrorResponse.java
│   ├── CacheStatsResponse.java
│   └── MessageResponse.java
└── exception/                     # Custom exceptions (optional)
```

### Frontend Structure

```
src/app/
├── app.component.ts              # Main component with UI
└── labseq.service.ts             # HTTP service for API calls
```

### Configuration

**Backend** (`src/main/resources/application.properties`):
```properties
quarkus.http.port=8080
quarkus.http.cors=true
quarkus.swagger-ui.always-include=true
```

**Frontend** (environment-specific):
```typescript
// labseq.service.ts
private baseUrl = 'http://localhost:8080/labseq';
```

---



## 🛠️ Technology Stack

### Backend
- **Framework**: Quarkus 3.15.1 (Supersonic Subatomic Java)
- **Language**: Java 21
- **Build Tool**: Maven 3.9+
- **REST**: JAX-RS (RESTEasy Reactive)
- **JSON**: JSON-B
- **Documentation**: SmallRye OpenAPI 3.0
- **Logging**: JBoss Logging

### Frontend
- **Framework**: Angular 17+ (Standalone Components)
- **Language**: TypeScript 5
- **Build Tool**: Angular CLI
- **HTTP Client**: Angular HttpClient
- **State Management**: Signals (Angular 17+)

### Infrastructure
- **Containerization**: Docker 20.10+
- **Orchestration**: Docker Compose 2.0+
- **Web Server**: Nginx (Alpine)
- **Base Images**: 
  - `eclipse-temurin:21-jre-alpine` (Backend)
  - `node:20-alpine` (Frontend build)
  - `nginx:alpine` (Frontend runtime)

---

## 📊 API Response Examples

### Successful Calculation

**Request:** `GET /labseq/10`

**Response:**
```json
{
  "index": 10,
  "value": "3",
  "calculationTimeMs": 2,
  "cacheSize": 11
}
```

### Large Number Calculation

**Request:** `GET /labseq/1000`

**Response:**
```json
{
  "index": 1000,
  "value": "27269884...very long number",
  "calculationTimeMs": 45,
  "cacheSize": 1001
}
```

### Error Response

**Request:** `GET /labseq/-5`

**Response:**
```json
{
  "error": "Invalid input",
  "message": "Index must be non-negative, got: -5",
  "status": 400
}
```

---

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Check what's using port 8080
lsof -i :8080

# Or use different port
docker-compose -f docker-compose.yml up -d
# Edit docker-compose.yml to change ports
```

### Docker Build Fails

```bash
# Clear Docker cache
docker system prune -a

# Rebuild from scratch
docker-compose build --no-cache
```

### Frontend Can't Connect to Backend

1. Check if backend is running: `curl http://localhost:8080/labseq/0`
2. Check CORS is enabled in `application.properties`
3. Verify network in docker-compose.yml

### View Container Logs

```bash
docker-compose logs -f backend
docker-compose logs -f frontend
```

---



