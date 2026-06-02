# ThreatLens

ThreatLens is a full-stack cybersecurity monitoring and response platform built with React, Spring Boot, and PostgreSQL.  
It simulates a security operations dashboard where suspicious API traffic can be inspected, blocked, audited, and scored based on risk.

The project focuses on backend security logic, policy-based firewall decisions, mitigation workflows, audit logging, reputation scoring, automated testing, and CI pipeline integration.

---

## Project Overview

ThreatLens allows an operator to monitor suspicious events, simulate protected API traffic, apply mitigation actions, and manage firewall policies.

The application includes:

- JWT-based admin authentication
- Protected API simulator
- SQL injection and XSS detection
- Policy-based allow/block decisions
- Manual IP mitigation
- Audit trail for operator and firewall actions
- Source IP reputation scoring
- PostgreSQL persistence
- Backend unit tests
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
- PostgreSQL

### Testing and Automation

- JUnit 5
- Mockito
- Maven Wrapper
- JaCoCo
- GitHub Actions

### Database

- PostgreSQL
- Docker for local database setup

---

## Architecture

```text
React Frontend
      ↓
Spring Boot REST API
      ↓
Service Layer
      ↓
PostgreSQL Database
