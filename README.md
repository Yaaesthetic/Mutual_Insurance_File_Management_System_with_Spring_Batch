# Mutual Insurance File Management System with Spring Batch

This project is a **Spring Batch** application for managing mutual insurance claim records. The system automates file processing, validates data, calculates reimbursement amounts based on reference drug data, and archives the results into a PostgreSQL database. It is designed to streamline insurance file management and ensure accurate reimbursement calculations.

---

## Table of Contents

1. [Project Context](#project-context)  
2. [Objective](#objective)  
3. [Features](#features)  
4. [Folder Structure](#folder-structure)  
5. [How It Works](#how-it-works)  
6. [Setup and Run](#setup-and-run)  
7. [Docker Integration](#docker-integration)

---

## Project Context

In mutual insurance systems, claims often include consultations and medical treatments. Managing these files involves validating data, calculating reimbursements, and archiving processed information. This project leverages **Spring Batch** to automate and streamline these tasks while ensuring compliance with reimbursement policies.

---

## Objective

This application automates the following processes:  

1. **Reading Files**: Extract insurance data from JSON files.  
2. **Data Validation**: Verify essential details for each claim.  
3. **Reimbursement Calculation**: Calculate total reimbursement using reference drug prices and fixed consultation percentages.  
4. **Data Archiving**: Store processed claims in a PostgreSQL database or output files.

---

## Features

### File Reading
- **Input Format**: JSON files containing:
  - **Insured**: Name, affiliation number, registration ID.
  - **Beneficiary**: Name, relationship to insured, filing date.
  - **Consultation**: Total cost, consultation price, number of attachments.
  - **Treatments**: Details for each treatment (barcode, name, type, price, availability).

### Reference Drug Data
- A database of reference drugs with:
  - Drug name.
  - Reference price.
  - Reimbursement percentage.

### Data Validation
- Insured name and affiliation number must be non-empty.
- Consultation price and total cost must be positive.
- Treatment list must not be empty.

### Reimbursement Calculation
- **Consultation**: Fixed reimbursement percentage applied to consultation cost.
- **Treatments**: Reimbursements based on reference prices and percentages.

### Processor Chaining
- Combines multiple processors for validation and calculation:
  1. **ValidationProcessor**: Ensures data integrity.
  2. **CalculationProcessor**: Calculates reimbursement amounts.

### Data Archiving
- Stores processed data with total reimbursement amounts in:
  - PostgreSQL database (primary storage).
  - CSV/Excel files for backup.

---

## Folder Structure

```
mutual-claim-management
├── batch/
│   ├── config/
│   │   ├── FlatFileItemReaderConfig.java
│   │   └── ReimbursementJobConfig.java
│   ├── processor/
│   │   ├── ValidationProcessor.java
│   │   ├── ConsultationProcessor.java
│   │   ├── TreatmentMappingProcessor.java
│   │   ├── TreatmentReimbursementProcessor.java
│   │   └── TotalReimbursementProcessor.java
│   ├── reader/
│   │   ├── JsonItemReader.java
│   │   └── CsvItemReader.java
│   └── writer/
│       ├── CsvItemWriter.java
│       └── DossierDatabaseWriter.java
├── controller/
│   └── BatchController.java
├── dto/
│   ├── DossierDTO.java
│   └── TreatmentDTO.java
├── model/
│   ├── Dossier.java
│   ├── Treatment.java
│   └── TreatmentProduct.java
├── repository/
│   ├── DossierRepository.java
│   └── MedicationReferenceRepository.java
├── scheduler/
│   └── JobTrigger.java
├── service/
│   ├── DossiersContext.java
│   ├── DossierService.java
│   ├── ReferenceMedicationService.java
│   └── ReimbursementService.java
├── resources/
│   ├── application.properties
│   ├── ref.csv
│   ├── schema-postgresql.sql
└── test/
```

---

## How It Works

1. **File Reading**:
   - JSON data is parsed using `JsonItemReader`.
   - Each claim is represented as a `Dossier` object.

2. **Validation**:
   - Ensures required fields are present and valid.

3. **Reimbursement Calculation**:
   - **ConsultationProcessor**: Applies a fixed percentage to consultation costs.
   - **TreatmentMappingProcessor**: Maps treatments to reference drugs.
   - **TreatmentReimbursementProcessor**: Calculates reimbursement for each treatment.
   - **TotalReimbursementProcessor**: Summarizes total reimbursements.

4. **Data Writing**:
   - Stores processed data into a PostgreSQL database via `DossierDatabaseWriter`.

---

## Setup and Run

### Prerequisites
- **Java 17** or later.
- **Maven** for building the project.
- **Docker** for database setup.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mutual-claim-management.git
   cd mutual-claim-management
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Start PostgreSQL with Docker Compose:
   ```bash
   docker-compose up -d
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. API Endpoints:
   - **Trigger Job**: `http://localhost:8080/api/start-batch`
Here's the modified **Setup and Run** section with the corrected endpoint and example request:

---

## Setup and Run

### Prerequisites
- **Java 17** or later
- **Maven** for building the project
- **Docker** for database setup

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mutual-claim-management.git
   cd mutual-claim-management
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Start PostgreSQL with Docker Compose:
   ```bash
   docker-compose up -d
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Trigger Batch Job

**Endpoint**: `POST http://localhost:8080/start-batch`

**Example Request**:
```bash
POST http://localhost:8080/start-batch
Content-Type: application/json

[
  {
    "numeroAffiliation": "A123456789",
    "nomAssure": "Jean Dupont",
    "immatriculation": "987654321",
    "lienParente": "Père",
    "montantTotalFrais": 25000,
    "prixConsultation": 1500,
    "nombrePiecesJointes": 3,
    "nomBeneficiaire": "Marie Dupont",
    "dateDepotDossier": "2024-04-15",
    "traitements": [
      {
        "code": "MED123456",
        "nom": "PARACETAMOL 500MG",
        "quantite": 2,
        "prixUnitaire": 450
      },
      {
        "code": "MED654321",
        "nom": "AMOXICILLINE 1G",
        "quantite": 1,
        "prixUnitaire": 800
      }
    ]
  }
]
```

**Expected Response**:
```
Batch job has been invoked. Status: COMPLETED
```
---

## Spring Batch features for enhanced metrics, logging, and observability

### Overview
The application implements **comprehensive observability** using Spring Boot 3's native observability features with Micrometer, enabling real-time monitoring of batch job performance, health tracking, and distributed tracing provided by Spring Batch **5.1.4**

### Metrics Collection

Spring Batch automatically collects and exposes metrics under the `spring.batch` prefix through the **Observation API**.

| Metric | Type | Description | Tags |
|--------|------|-------------|------|
| `spring.batch.job` | Timer | Duration of job execution | `name`, `status` |
| `spring.batch.job.active` | Gauge | Currently active jobs | `name` |
| `spring.batch.step` | Timer | Duration of step execution | `name`, `job.name`, `status` |
| `spring.batch.step.active` | Gauge | Currently active steps | `name`, `job.name` |
| `spring.batch.item.read` | Timer | Duration of item reading | `job.name`, `step.name`, `status` |
| `spring.batch.item.process` | Timer | Duration of item processing | `job.name`, `step.name`, `status` |
| `spring.batch.chunk.write` | Timer | Duration of chunk writing | `job.name`, `step.name`, `status` |


### Distributed Tracing

The system uses **Micrometer Tracing with Zipkin** to provide distributed tracing capabilities:[4][1]

- **Trace Creation**: Automatic trace generation for each job execution
- **Span Generation**: Individual spans for each step execution  
- **Request Tracking**: Complete visibility into processing flow and latency
- **Error Tracking**: Automatic error propagation in traces

The `ObservationRegistry` bean is configured with `DefaultTracingObservationHandler` to enable tracing:[5]

```java
@Bean
public ObservationRegistry observationRegistry(Tracer tracer) {
    ObservationRegistry registry = ObservationRegistry.create();
    registry.observationConfig().observationHandler(
        new DefaultTracingObservationHandler(tracer));
    return registry;
}
```


### Prometheus Integration

Metrics are exposed in **Prometheus format** for monitoring and alerting:[6][5]

- **Endpoint**: `http://localhost:8080/actuator/prometheus`
- **Format**: Compatible with Prometheus scraping
- **Registry**: Automatic `PrometheusMeterRegistry` configuration

### Actuator Endpoints

The following **Spring Boot Actuator endpoints** are available for monitoring:[7]

- `/actuator/metrics` - List all available metrics
- `/actuator/metrics/{metric.name}` - Detailed metric information
- `/actuator/prometheus` - Prometheus-formatted metrics
- `/actuator/health` - Application health status
- `/actuator/info` - Application information

### Dependencies

The observability stack requires the following dependencies:[5]

```xml
<!-- Spring Boot Actuator for metrics endpoints -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Tracing Bridge for distributed tracing -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin Reporter for trace export -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>

<!-- Prometheus Registry for metrics export -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```
