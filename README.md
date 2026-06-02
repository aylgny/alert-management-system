# ThreatLens

ThreatLens is a full-stack cybersecurity monitoring and response platform built with **React**, **Spring Boot**, and **PostgreSQL**.

It simulates a lightweight security operations dashboard where suspicious API traffic can be inspected, scored, blocked, audited, and managed through policy-based security rules.

---

## Features

- JWT-based admin authentication
- Protected API simulator
- SQL injection detection
- XSS detection
- Policy-based firewall decisions
- Manual IP blocking (mitigation)
- Audit trail logging
- Source reputation scoring
- PostgreSQL persistence
- Backend unit tests
- JaCoCo coverage reporting
- GitHub Actions CI pipeline

---

## Tech Stack

### Frontend
- React
- Vite
- Axios
- CSS

### Backend
- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Bean Validation

### Database
- PostgreSQL
- Docker Compose

### Testing & Automation
- JUnit 5
- Mockito
- JaCoCo
- GitHub Actions
- Maven Wrapper

---

## Architecture

```text
React Frontend
    ↓
Spring Boot REST API
    ↓
Service Layer / Security Logic
    ↓
PostgreSQL
```

### Main Backend Modules

- **auth** → login, JWT, security configuration
- **alert** → security event and alert management
- **protectedapi** → protected login simulation and firewall inspection
- **mitigation** → manual IP blocking and release
- **policy** → firewall policy rules and risk score management
- **audit** → audit log creation and retrieval
- **reputation** → source IP reputation calculation
- **dashboard** → summary and statistics
- **exception** → global exception handling

---

## Core Functionality

### 1. Authentication
Admin users log in through:

`POST /api/auth/login`

Successful authentication returns a JWT token.

Protected admin endpoints require:

```http
Authorization: Bearer <token>
```

---

### 2. Protected API Simulator
ThreatLens includes a simulator endpoint that mimics external traffic hitting a protected API:

`POST /api/protected/login`

Supported scenarios:
- Normal login
- SQL injection attempt
- XSS attempt
- Suspicious admin login
- Request from blocked IP

Example request body:

```json
{
  "sourceIp": "91.44.12.8",
  "username": "admin' OR '1'='1",
  "password": "123456"
}
```

Possible outcomes:
- `ALLOWED`
- `ALLOWED_WITH_ALERT`
- `BLOCKED`

---

### 3. Policy Engine
Firewall behavior is controlled by configurable policy rules.

Example policies:
- `SQL_INJECTION → BLOCK`
- `XSS_ATTEMPT → BLOCK`
- `ADMIN_LOGIN → DETECT_ONLY`

#### Policy Modes

| Mode | Behavior |
|------|----------|
| BLOCK | Creates an alert and blocks the request |
| DETECT_ONLY | Creates an alert but allows the request |
| DISABLED | Allows the request without creating an alert |

---

### 4. Risk Scores
Each event type has a configurable risk score.

Example:
- `SQL_INJECTION → 95`
- `XSS_ATTEMPT → 85`
- `ADMIN_LOGIN → 75`

Risk scores are used for:
- alert severity
- reputation scoring
- security classification

#### Risk Interpretation

| Score Range | Meaning |
|------------|---------|
| 90 - 100 | Critical / Malicious |
| 70 - 89 | High / Suspicious |
| 0 - 69 | Monitored |

---

### 5. Events and Alerts
The Events page shows security alerts created by:
- protected API inspection
- simulator traffic
- manual log ingestion

Each alert contains:
- source IP
- target IP
- event type
- severity
- status
- risk score
- raw message

---

### 6. Mitigations
Operators can manually block suspicious source IP addresses.

A mitigation includes:
- source IP
- reason
- TTL seconds
- status
- created by
- created at
- expires at

When a request comes from an actively blocked IP, the firewall denies it automatically.

---

### 7. Audit Trail
Important actions are stored in the audit log.

Examples:
- `MANUAL_IP_BLOCK`
- `RELEASE_MITIGATION`
- `SQL_INJECTION_BLOCKED`
- `PROTECTED_REQUEST_ALLOWED`
- `UPDATE_POLICY_RULE`
- `UPDATE_ALERT_STATUS`

Each audit entry includes:
- actor
- action
- target
- result
- details
- timestamp

---

### 8. Reputation Scoring
The Reputation page groups alerts by source IP.

Reputation status is determined using the source’s maximum observed risk score:

- `>= 90` → **MALICIOUS**
- `>= 70` → **SUSPICIOUS**
- `< 70` → **MONITORED**

---

## API Endpoints

### Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Admin login |

### Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/summary` | Dashboard summary |
| GET | `/api/dashboard/severity-distribution` | Severity distribution |

### Alerts

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/alerts` | List alerts |
| PATCH | `/api/alerts/{id}/status` | Update alert status |

### Logs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/logs/ingest` | Ingest security log |

### Protected API Simulator

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/protected/login` | Simulate protected login request |

### Mitigations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/mitigations` | List mitigations |
| POST | `/api/mitigations` | Create manual block |
| DELETE | `/api/mitigations/{id}` | Release mitigation |

### Audits

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/audits` | List audit logs |

### Reputation

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reputation` | List reputation data |

### Policies

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/policies` | List policy rules |
| PUT | `/api/policies/{eventType}` | Update mode / risk score |

---

## Running Locally

### 1. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

---

### 2. Run Backend

#### IntelliJ
Run:

`BackendApplication.java`

#### Terminal

**Windows PowerShell**
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**Mac / Linux**
```bash
cd backend
./mvnw spring-boot:run
```

Backend runs at:

`http://localhost:8080`

---

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

`http://localhost:5173`

---

## Demo Credentials

```text
Email: admin@threatlens.com
Password: Admin123!
```

---

## Sample Test Request

Example SQL injection simulation:

```json
{
  "sourceIp": "91.44.12.8",
  "username": "admin' OR '1'='1",
  "password": "123456"
}
```

Endpoint:

`POST /api/protected/login`

---

## Demo Flow

A simple demo flow for the project:

1. Login as admin
2. Open the Dashboard
3. Go to the Simulator page
4. Select the SQL Injection preset
5. Send the request
6. Observe the firewall decision
7. Open the Events page
8. Confirm that a new alert was created
9. Block the source IP from the event details
10. Go to Mitigations and confirm the active block
11. Go to Audits and inspect the logged actions
12. Go to Reputation and confirm updated source status
13. Change a rule from BLOCK to DETECT_ONLY in Policy
14. Re-run the same attack and observe different behavior

---

## Testing

The backend includes unit tests for the main business logic.

Covered services:
- `RequestInspectionService`
- `ReputationService`
- `PolicyService`
- `MitigationService`
- `AuditLogService`

### Run Tests

**Windows PowerShell**
```powershell
cd backend
.\mvnw.cmd test
```

**Mac / Linux**
```bash
cd backend
./mvnw test
```

---

## Test Coverage

JaCoCo is used for backend coverage reporting.

### Generate Report

**Windows PowerShell**
```powershell
cd backend
.\mvnw.cmd clean test jacoco:report
```

**Mac / Linux**
```bash
cd backend
./mvnw clean test jacoco:report
```

### Open Coverage Report

```text
backend/target/site/jacoco/index.html
```

Current tests mainly cover the core business logic:
- firewall decision logic
- policy updates
- mitigation workflow
- reputation scoring
- audit logging

---

## CI Pipeline

ThreatLens includes a GitHub Actions CI pipeline.

The pipeline runs on every push and pull request and performs:

- backend unit tests
- frontend production build

Workflow file:

```text
.github/workflows/ci.yml
```

---

## Validation and Error Handling

The backend uses Bean Validation annotations such as:
- `@NotBlank`
- `@Pattern`
- `@Min`
- `@Max`
- `@Size`
- `@Valid`

It also includes a global exception handler for consistent JSON error responses.

---

## Security Highlights

Implemented security-related features:
- JWT authentication
- Spring Security authorization
- SQL injection detection
- XSS detection
- policy-based blocking / allow logic
- manual IP mitigation
- audit logging
- risk scoring
- input validation
- global exception handling

---

## Future Improvements

Possible next steps:
- Dockerize backend and frontend
- Deploy to Render / Cloud Run
- Add Redis cache for active mitigations
- Add rate limiting
- Add WebSocket live updates
- Add integration tests with Testcontainers
- Add frontend component tests
- Add end-to-end tests
- Add API Gateway
- Add Kubernetes manifests

---

## Project Status

ThreatLens is currently complete as a portfolio-level full-stack cybersecurity application.

### Completed
- full-stack implementation
- PostgreSQL integration
- authentication and authorization
- protected API simulator
- security alert workflow
- mitigation workflow
- policy engine
- reputation scoring
- audit logging
- backend unit tests
- JaCoCo coverage setup
- GitHub Actions CI

### Remaining
Mostly optional production-readiness improvements such as deployment, caching, live updates, and advanced testing.
