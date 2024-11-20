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
   - **Trigger Job**: `http://localhost:8080/api/jobs/start`
   - **Check Status**: `http://localhost:8080/api/jobs/status`

---

## Docker Integration

The `docker-compose.yml` file includes configuration for a PostgreSQL database. Reference SQL schema is initialized from `schema-postgresql.sql`.

### Sample Configuration
```yaml
services:
  postgres:
    image: postgres
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: reimbursement_db
    volumes:
      - ./resources/schema-postgresql.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "5432:5432"
```
