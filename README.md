# Event Service

- Stores normalized security events in PostgreSQL (or local H2 for quick demo)
- Provides APIs for timeline and search
- Includes a Next.js UI to browse bundles and inspect events

## Requirements

- Java 17
- Node.js 20+ and npm

## Run (fast, no Docker)

- Start backend:
  - `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"`
- Start frontend:
  - `cd frontend`
  - `npm install`
  - `npm run dev`
- Open:
  - UI: `http://localhost:3000`
  - API: `http://localhost:8080/bundles`

## Run (PostgreSQL with Docker)

- Install Docker Desktop
- Start DB:
  - `docker compose up -d`
- Start backend:
  - `.\mvnw.cmd spring-boot:run`
- Start frontend:
  - `cd frontend`
  - `npm install`
  - `npm run dev`

## Run (Standalone PostgreSQL on Windows/Mac)

If you downloaded and installed PostgreSQL directly (without Docker):

1. **Set up the Database Using pgAdmin 4 or psql**:
   - Create a Login/Group Role with the name: `eventuser` and password: `eventpass` (ensure the user has login privileges).
   - Create a new Database named: `eventdb` and set the Owner to `eventuser`.
2. **Start backend** (from the `event-service-main` root folder):
   - `.\mvnw.cmd spring-boot:run`
3. **Start frontend** (from the `frontend` folder):
   - `cd frontend`
   - `npm install`
   - `npm run dev`

## Configure

- Frontend API base:
  - Copy `frontend/.env.example` to `frontend/.env.local`
  - Set `NEXT_PUBLIC_API_BASE=http://localhost:8080`

## APIs

- `GET /bundles`
- `GET /timeline?bundleId=1`
- `GET /events`
- `GET /events/{id}`
