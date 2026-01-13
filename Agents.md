# Agents

This repository hosts three collaborating Java modules. Each module behaves like an "agent" with its own responsibilities and technology stack, but they share DTOs and communicate over HTTP.

## Desktop Client Agent (`studsluzba_desktopclient`)
- **Purpose**: Rich-client front end built with JavaFX 21 and Spring Boot 3.1 that registrar staff use to search, enroll, and manage students locally while talking to the backend over REST.
- **Entry point**: `StudsluzbaFxClientApp` boots a Spring context and JavaFX stage, then delegates to `MainView` for loading `/fxml/main.fxml` and wiring controllers (`studsluzba_desktopclient/src/main/java/org/raflab/studsluzbadesktopclient/StudsluzbaFxClientApp.java`).
- **Navigation layer**: `MainView` + `NavigationService` (`navigation/NavigationService.java`) keep a history stack, bind keyboard/mouse gestures, and swap FXML roots. This keeps the UX responsive and allows browser-like back/forward support. `ContextFXMLLoader` injects Spring-managed controllers into each FXML scene.
- **UI controllers**:
    - `MenuBarController` triggers view switches (search, new student, event sandbox, reports).
    - `SearchStudentController` queries the backend asynchronously via `StudentService.searchStudentsAsync`, binds double-click to open a profile.
    - `StudentController` drives the `newStudent` form, preloads drop-downs via `CoderFactory` and `SifarniciService`, persists `StudentDto` records, and refreshes local cache after modal dialogs.
    - `AddSrednjaSkolaController`, `EventsController`, `ReportsController` cover auxiliary workflows (reference data, JavaFX event demos, JasperReports export for student cohorts).
- **Services & remote I/O**:
    - `ClientAppConfig` wires a `RestTemplate`, a Reactor `WebClient`, and a `baseUrl` (default `http://localhost:8081`).
    - `StudentService` mixes blocking (`RestTemplate`) and reactive (`WebClient`) calls for CRUD/search/report filters against `/student/...` endpoints and feeds Jasper reports.
    - `SifarniciService` wraps `/srednja-skola` endpoints for listing/saving high schools.
    - `StudProgramService` fetches sorted study programs for downstream combos.
- **Local data helpers**: the `coder` package lazily loads static code lists (countries, cities, exam periods) from `/src/main/resources/coders/*.txt`; utilities under `utils/` provide parsing helpers for inputs.
- **Reporting**: `ReportsController.generateReport` compiles `reports/sviStudenti.jrxml` into PDFs such as `sviStudentiPoGodiniUpisa.pdf`.
- **How to run**: `mvn -pl studsluzba_desktopclient javafx:run` boots the Spring+JavaFX app. Make sure the DTO jar is installed locally and the backend is reachable at the configured `baseUrl`.

## DTO Agent (`studsluzba_dto`)
- **Purpose**: Shared contract library containing Lombok-based DTOs and enums for both the desktop client and the Spring Boot server.
- **Contents**:
    - Enums like `Pol`, `VrstaSkole`, `StatusIndeksa`, `NacinPolaganja`, etc. under `org.raflab.studsluzba.model`.
    - DTOs covering every aggregate: `StudentDto`, `StudProgramDto`, `PredmetDto`, `IspitDto`, `PrijavaIspitaDto`, `UplataDto`, `RemainingTuitionDto`, etc. under `org.raflab.studsluzba.model.dto`.
    - Builders and getters/setters are generated via Lombok annotations, so Lombok must be on the annotation-processing classpath.
- **Build usage**: run `mvn -pl studsluzba_dto clean install` once to publish the jar to the local Maven repo. Other modules declare `org.raflab:studsluzba_dto:0.0.1-SNAPSHOT` as a dependency and reuse the same Java records when serializing/deserializing JSON.

## Server Agent (`studsluzba_server`)
- **Purpose**: Spring Boot 2.2 application exposing the registrar REST API (`server.port=8080`) backed by Spring Data JPA. Default datasource is a PostgreSQL instance (see `src/main/resources/application.properties`), with MySQL/H2 configs commented for local overrides.
- **Cross-cutting infrastructure**:
    - `SecurityConfig` currently disables auth (all requests permitted, CSRF off) for rapid iteration.
    - `EntityMapper` converts between JPA entities and DTOs to keep controllers thin.
    - `Seeder` seeds demo data for programs, subjects, students, faculty, exam terms, enrollments, and payments when the app starts.
    - `ExchangeRateClient` hits `https://kurs.resenje.org/...` for EUR exchange rates used in tuition calculations.
- **Packages & responsibilities**:
    - `controller/` hosts REST controllers under `/api/...`, each delegating to a service class. Domains include students, study programs, academic years (`SkolskaGodinaController`), exams, payments, and reference data.
    - `service/` holds transactional business logic. Highlights:
        - `StudentService` orchestrates lifecycle actions: CRUD, index lookups, pagination/search, enrollment (`/by-index/{index}/enroll`), repeating years, and reporting by high school. It auto-enrolls pending subjects and validates ESPB loads when enrolling or repeating.
        - `PredmetService`, `VrstaStudijaService`, `StudProgramService` manage curriculum data and calculate stats such as average exam grades per year range.
        - `IspitService`, `PrijavaIspitaService`, `IzlazakIspitService`, `StudentPredispitnaObavezaService`, `PolozenPredmetService` coordinate exam scheduling, registrations, attempt tracking, grade calculation, and accumulation of pre-exam points.
        - `UplataService` tracks tuition payments per academic year, converts RSD to EUR via live exchange rates, and exposes remaining balance summaries.
        - `Nastavnik*` services manage faculty records, titles, education, and course assignments.
        - `UpisGodineService` and `ObnovaGodineService` store raw enrollment/renewal records (complex validation lives in `StudentService`).
    - `repositories/` are Spring Data interfaces with custom queries (e.g., `StudentRepository.searchByImeAndPrezime`, `PolozenPredmetRepository.findProsecnaOcenaZaPredmetUGodinama`).
    - `model/` defines all JPA entities (students, indices, programs, exams, payments, etc.) plus aggregate builders.
- **API surface examples**:
    - Students: `/api/studenti` with CRUD, `/api/studenti/search?ime=&prezime=` for pagination, `/api/studenti/by-index/{index}/passed` for paginated exam history, `/api/studenti/by-high-school/{id}` for reporting.
    - Courses: `/api/predmeti`, `/api/predmeti/{id}/avg` for grade stats, `/api/vrste-studija`, `/api/stud-programi`.
    - Exams: `/api/ispiti`, `/api/prijave-ispita/{ispitId}/prijavljeni`, `/api/izlasci-na-ispit/broj-izlasaka?indeksId=&predmetId=`.
    - Academic progress: `/api/upisi-godine`, `/api/obnove-godine` plus the student-centric enrollment endpoints noted above.
    - Finance: `/api/uplate/student/{studentId}` for creating payments at the current exchange rate, `/api/uplate/student/{studentId}/balance` for outstanding tuition.
    - Reference data: `/api/srednje-skole`, `/api/vsu`, `/api/nastavnici`, etc.
- **Running/testing**: ensure PostgreSQL (or alternative DB) is available, then `mvn -pl studsluzba_server spring-boot:run`. The seeder will populate demo data with `spring.jpa.hibernate.ddl-auto=create-drop`; adjust for production.

## Collaboration & Integration Notes
- The Desktop Client Agent depends on DTOs at compile time and talks to the Server Agent at runtime via HTTP. Make sure the desktop `baseUrl` (`ClientAppConfig`) matches the server port and context path (`/api/...`). Aligning endpoint paths is essential before wiring features end-to-end.
- The Server Agent serializes/deserializes DTOs defined in `studsluzba_dto`, so schema changes must be released there first, then consumed by both server and client.
- Shared identifiers: indices (`Indeks`) follow the `program/year/index` format handled by `ParseUtils`. Keep this consistent across UI validation, DTOs, and server services.
- Reports or business rules that span agents (e.g., Jasper exports or tuition balance views) should fetch data via the server’s dedicated endpoints rather than duplicating calculations on the client.
- When adding new workflows, start by extending `studsluzba_dto`, implement the backend controller/service/repository logic, and finally expose the feature in the JavaFX UI (navigation entry + controller + FXML).

