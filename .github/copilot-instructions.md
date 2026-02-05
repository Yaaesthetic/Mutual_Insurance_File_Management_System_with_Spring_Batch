# Mutual Insurance Claim Processing System - AI Copilot Instructions

## Project Overview
Spring Batch ETL system that automates insurance claim processing: reading dossier data, validating claims, calculating reimbursements based on reference drug prices, and persisting to PostgreSQL. Integrates distributed tracing (Zipkin) and metrics collection (Prometheus).

## Architecture & Data Flow

### Core Processing Pipeline
```
REST Controller → DossiersContext (shared state) → Reader → 
Composite Processor Chain {Validate → Map → Calculate} → Writer → PostgreSQL
```

**Key Pattern**: The system orchestrates TWO independent Spring Batch jobs:
1. **ETL-Job** (`ReimbursementJobConfig.fetchReimbursementProductJob`): Loads reference medications (CSV) into `TreatmentProduct` entities - runs once at startup
2. **fetchJSONToDossierJob**: Main reimbursement job, processes `List<DossierDTO>` sent via POST to `/start-batch`

### Processor Chain Explanation
[DossierCompositeProcessor](src/main/java/ma/mini_project_insurance_claim_records/batch/processor/DossierCompositeProcessor.java) sequences four specialized processors:
1. **DossierValidationProcessor**: Assert non-empty affiliation/names, positive amounts, non-empty treatments
2. **DossierTreatmentMapper**: Enriches treatments with reference drug data from in-memory `TreatmentProduct` cache
3. **TreatmentReimbursementProcessor**: Calculates individual treatment reimbursements (reference price × percentage)
4. **TotalReimbursementProcessor**: Aggregates to total reimbursement, updates `dossier.reimbursedAmount`

**Why this design**: Each processor has single responsibility; composite orchestrates the sequence. Validates before resource lookups to fail fast.

## Key Conventions & Patterns

### Chunk Configuration
All steps use 10-item chunks in main job (100-item in product load):
```java
.<Dossier, Dossier>chunk(10, transactionManager)
```
Adjust if processing bottlenecks occur - relates to database transaction boundaries.

### Context-Based Dossier Passing
`DossiersContext` (singleton `@Component`) stores the current batch input. Controller passes DTOs → context → reader pulls from context. This pattern avoids file I/O and enables in-memory processing.

**Important**: `setDossiers()` clears previous batch before adding new - ensures isolation between job runs.

### Data Model Transformations
- **Input**: `DossierDTO` (from REST client)
- **Processing**: Converted to `Dossier` entity by reader via `DossierJsonReader`
- **Storage**: Persisted via `DossierRepository.save()` with calculated `reimbursedAmount`
- **Reference**: `TreatmentProduct` entities queried by `MedicationReferenceRepository` during mapper phase

### Observability Conventions
- **Tracing**: Automatic via `ObservationRegistry` + `DefaultTracingObservationHandler`. All batch steps traced to Zipkin (endpoint: `http://zipkin:9411`)
- **Metrics**: Prometheus scrapes `/actuator/prometheus` on demand (no push)
- **Config**: `management.tracing.sampling.probability=1.0` samples ALL traces (production should lower this)

## Development Workflows

### Running Locally with Docker Compose
```bash
docker-compose up
# Application: http://localhost:8080
# Zipkin traces: http://localhost:9411
# Prometheus metrics: http://localhost:8080/actuator/prometheus
# PostgreSQL: localhost:5432 (user: postgres, pass: password)
```

### Triggering a Batch Job
```bash
curl -X POST http://localhost:8080/start-batch \
  -H "Content-Type: application/json" \
  -d '[{"numeroAffiliation":"123","nomAssure":"Dupont","immatriculation":"ID1","lienParente":"spouse","montantTotalFrais":500,"prixConsultation":50,"nombrePiecesJointes":1,"nomBeneficiaire":"Marie Dupont","dateDepotDossier":"2024-01-15","traitements":[{"productCode":"MED001","productName":"Paracétamol","productType":"analgesic","montant":100,"disponible":true}]}]'
```

### Running Tests
```bash
mvn clean test
```
Note: Integration tests require PostgreSQL running; configure datasource in test properties if needed.

### Build & Package
```bash
mvn clean package  # Creates fat JAR
docker build -t insurance-batch:latest .  # Uses Dockerfile
```

## Critical Implementation Points

### Adding New Validation Rules
Extend `DossierValidationProcessor.process()`. MUST throw `IllegalArgumentException` on failure - Spring Batch treats this as skip/failure handling.

### Adding New Reimbursement Calculations
Create new processor implementing `ItemProcessor<Dossier,Dossier>` and inject into `DossierCompositeProcessor`. **Order matters** - validator must run before calculators.

### Modifying Reference Drug Loading
Change CSV file path in `application.properties` (`file.input=ref-des-medicaments-cnops-2014.csv`) or override via environment variable `FILE_INPUT`. [FlatFileItemReaderConfig](src/main/java/ma/mini_project_insurance_claim_records/batch/config_500/FlatFileItemReaderConfig.java) parses the CSV.

### Adding DB Persistence Fields
1. Add field to [Dossier](src/main/java/ma/mini_project_insurance_claim_records/model/Dossier.java) entity with `@Column` annotation (primary key: `affiliationNumber`)
2. Spring Data JPA (`@EnableJpaRepositories`) auto-updates schema (`spring.jpa.hibernate.ddl-auto=update`)
3. Update [DossierDTO](src/main/java/ma/mini_project_insurance_claim_records/dto/DossierDTO.java) if client-facing

## Technology Stack Details

- **Spring Boot**: 3.3.5 with virtual threads enabled (`spring.threads.virtual.enabled=true`)
- **Java**: 21 (LTS, required for virtual thread support)
- **Database**: PostgreSQL with Spring Data JPA (no custom SQL; HQL only)
- **Batch Framework**: Spring Batch with `@EnableBatchProcessing` on [ReimbursementJobConfig](src/main/java/ma/mini_project_insurance_claim_records/batch/config_500/ReimbursementJobConfig.java)
- **Observability**: Micrometer (Prometheus + Zipkin via Brave bridge)

## Common Debugging

**Job not triggering**: Check `spring.batch.job.enabled=true` in properties. Batch jobs require explicit `JobLauncher.run()` call.

**Missing reference drugs**: Verify CSV at [resources/ref-des-medicaments-cnops-2014.csv](src/main/resources/ref-des-medicaments-cnops-2014.csv) is loaded. ETL-Job must complete before main job.

**Reimbursement amounts zero**: Trace through processor chain - check `DossierTreatmentMapper` is finding reference drugs by product code, `TreatmentReimbursementProcessor` applies correct percentage.

**Zipkin not receiving traces**: Ensure `management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans` and Zipkin container is healthy. Check application logs for connection errors.

## File Organization Summary
- **Batch Config**: `batch/config_500/` - Job/Step configuration
- **Processors**: `batch/processor/` - Data transformation & validation chain
- **Readers**: `batch/reader/` - Input data acquisition
- **Writers**: `batch/writer/` - Output persistence (DB or CSV)
- **Models**: `model/` - JPA entities (Dossier, Treatment, TreatmentProduct)
- **DTOs**: `dto/` - REST contracts
- **Services**: `service/` - Business logic (DossiersContext, ReferenceMedicationService)
- **Repository**: `repository/` - Spring Data JPA interfaces
