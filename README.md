# Familien-Organizer-App

Eine Familien-App für 5 Personen mit Aufgabenmanagement, Gamification und Essensplanung.

**Tech-Stack:** Spring Boot 3.3 (Java 21) · Angular 17+ · PostgreSQL · Railway

---

## Lokale Entwicklung

### Voraussetzungen
- Java 21, Maven 3.8+
- Node.js 22+, Angular CLI (`npm install -g @angular/cli`)

### Backend starten
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Läuft auf http://localhost:8080
# H2-Konsole: http://localhost:8080/h2-console
```

### Frontend starten
```bash
cd frontend
npm install
ng serve
# Läuft auf http://localhost:4200
```

---

## Railway-Deployment

### 1. Railway-Projekt anlegen

1. [railway.app](https://railway.app) → **New Project** → **Empty Project**
2. Projekt umbenennen: `familien-organizer`

### 2. PostgreSQL-Datenbank hinzufügen

1. Im Projekt: **+ New** → **Database** → **PostgreSQL**
2. Die Verbindungsdaten werden automatisch als Umgebungsvariablen bereitgestellt

### 3. Backend-Service anlegen

1. **+ New** → **GitHub Repo** → Repository auswählen
2. **Root Directory:** `backend`
3. **Umgebungsvariablen setzen:**

| Variable | Wert |
|---|---|
| `SPRING_DATASOURCE_URL` | aus PostgreSQL-Service kopieren (`DATABASE_URL`) |
| `SPRING_DATASOURCE_USERNAME` | aus PostgreSQL-Service |
| `SPRING_DATASOURCE_PASSWORD` | aus PostgreSQL-Service |
| `FRONTEND_URL` | URL des Frontend-Service (nach dessen Erstellung) |

> Railway verlinkt die PostgreSQL-Variablen automatisch wenn der Service mit der DB verknüpft wird (im Service-Dashboard unter **Variables → Reference**).

### 4. Frontend-Service anlegen

1. **+ New** → **GitHub Repo** → selbes Repository
2. **Root Directory:** `frontend`
3. **Umgebungsvariablen setzen:**

| Variable | Wert |
|---|---|
| `BACKEND_URL` | Interne Railway-URL des Backends, z.B. `http://backend.railway.internal:8080` |

> Die interne URL findet man im Backend-Service unter **Settings → Networking → Internal**.

### 5. Deployment-Reihenfolge

```
PostgreSQL → Backend (wartet auf DB) → Frontend (wartet auf Backend-URL)
```

### 6. Deployment verifizieren

```bash
# Health-Check Backend
curl https://<backend-url>.railway.app/api/actuator/health

# Nutzer-Liste
curl https://<backend-url>.railway.app/api/v1/users
```

---

## Projektstruktur

```
familien-organizer-app/
├── backend/          Spring Boot (Java 21, Maven)
│   ├── src/main/java/com/familienorganizer/
│   │   ├── controller/   REST-Endpunkte
│   │   ├── service/      Business-Logik
│   │   ├── repository/   Spring Data JPA
│   │   ├── entity/       JPA-Entities
│   │   └── dto/          Java Records (API-Transfer)
│   └── src/main/resources/db/migration/   Flyway-Migrationen
└── frontend/         Angular 17+ (Standalone Components)
    └── src/app/
        ├── core/     Models, Services, Guards, Interceptors
        └── features/ Profile-Select, Dashboard, Tasks, Meal-Wishes
```

## API-Endpunkte

| Methode | Pfad | Beschreibung |
|---|---|---|
| GET | `/api/v1/users` | Alle Familienmitglieder |
| POST | `/api/v1/session` | Profil auswählen (PIN) |
| GET | `/api/v1/tasks` | Alle Aufgaben (Filter: status, assignedTo) |
| GET | `/api/v1/tasks/mine` | Aufgaben des aktiven Nutzers |
| POST | `/api/v1/tasks` | Aufgabe erstellen |
| PATCH | `/api/v1/tasks/{id}/status` | Status ändern |
| GET | `/api/v1/dashboard` | Leaderboard + Statistiken |
| GET | `/api/v1/meal-wishes` | Alle Essenswünsche |
| GET | `/api/v1/meal-wishes/weekly-plan` | Wochenplan |
| POST | `/api/v1/meal-wishes` | Wunsch einreichen |
| PATCH | `/api/v1/meal-wishes/{id}/accept` | Akzeptieren (nur Eltern) |
