---

# Mutual Insurance File Management System with Spring Batch

This project is a **Spring Batch** application that automates the management of mutual insurance files. It processes insurance records, validates data, calculates reimbursements based on a database of reference drugs, and archives the processed data into a database.

---

## Table of Contents

1. [Project Context](#project-context)  
2. [Objectives](#objectives)  
3. [Features](#features)  
4. [Folder Structure](#folder-structure)  
5. [System Workflow](#system-workflow)  
6. [Configurations](#configurations)  
7. [How to Run](#how-to-run)  
8. [Docker Integration](#docker-integration)  
9. [Future Enhancements](#future-enhancements)

---

## Project Context

In mutual insurance systems, insured persons file claims that include consultations and medical treatments. To optimize and automate claim processing, this project utilizes **Spring Batch** to:

- Automate data processing.
- Validate claim information.
- Calculate reimbursement amounts for consultations and prescribed drugs using reference prices and reimbursement rates.
- Archive the processed data into a database.

---

## Objectives

The main objectives of this project are:

1. **Data Reading**: Read claim data from JSON files containing insured persons, beneficiaries, consultations, and treatments.
2. **Data Validation**: Ensure essential information is present and accurate.
3. **Reimbursement Calculation**: Compute total reimbursements using reference drug prices and consultation reimbursement rates.
4. **Data Archiving**: Save processed claim data into a PostgreSQL database for future use.

---

## Features

1. **File Reading**:
   - Read data from JSON files using `JsonItemReader`.
   - JSON structure includes:
     - Insured details: Name, affiliation number, registration.
     - Beneficiary details: Name, relationship to the insured, filing date.
     - Consultation details: Total costs, consultation price, number of attachments.
     - Treatments: Barcode, name, type, price, and availability.

2. **Drug Reference Database**:
   - Maintains a list of reference drugs with:
     - Name of the drug.
     - Reference price.
     - Applicable reimbursement percentage.

3. **Data Validation**:
   - Verify non-empty names and affiliation numbers for insured persons.
   - Ensure positive consultation prices and total costs.
   - Validate the presence of treatment lists.

4. **Reimbursement Calculation**:
   - Fixed percentage applied to consultation prices.
   - Drug reimbursements calculated based on reference prices and rates.

5. **Processor Chaining**:
   - Use `CompositeItemProcessor` to chain multiple sub-processors:
     - `ValidationProcessor`: Validates input data.
     - `CalculationProcessor`: Computes reimbursement amounts.

6. **Data Writing**:
   - Archive processed files into a PostgreSQL database or output files.

---

## Folder Structure

```
mutual-claim-management
├── batch/
│   ├── config/
│   │   ├── FlatFileItemReaderConfig.java
│   │   └── ReimbursementJobConfig.java
│   ├── processor/
│   │   ├── ConsultationProcessor.java
│   │   ├── TreatmentMappingProcessor.java
│   │   ├── TreatmentReimbursementProcessor.java
│   │   ├── TotalReimbursementProcessor.java
│   │   ├── DossierCompositeProcessor.java
│   │   ├── DossierTreatmentMapper.java
│   │   └── DossierValidationProcessor.java
│   ├── reader/
│   │   ├── CsvItemReader.java
│   │   └── DossierJsonReader.java
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
│   ├── ref-des-medicaments-cnops-2014.csv
│   ├── ref-des-medicaments-cnops-2014.xlk
│   ├── ref-des-medicaments-cnops-2014.xlsx
│   └── schema-postgresql.sql
└── test/
```

---

## System Workflow

1. **Input**: JSON files containing insurance claims.
2. **Validation**: Ensures that critical data is present and correct.
3. **Reimbursement Calculation**:
   - Consultation reimbursement.
   - Treatment reimbursement based on reference drugs.
4. **Output**: Processed data is saved in the database for archiving.

---

## Configurations

### `application.properties`
```properties
spring.application.name=Mini_project_insurance_claim_records
spring.batch.job.enabled=true

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/reimbursement_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.show-sql=true

# Input File
file.input=ref-des-medicaments-cnops-2014.json

# Virtual Threads
spring.threads.virtual.enabled=true
```

### `Docker Compose` Configuration
```yaml
services:
  postgres:
    image: postgres
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
      POSTGRES_DB: reimbursement_db
    ports:
      - "5432:5432"
    volumes:
      - ./src/main/resources/schema-postgresql.sql:/docker-entrypoint-initdb.d/schema.sql
```

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/mutual-claim-management.git
   cd mutual-claim-management
   ```

2. Start PostgreSQL using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Build and run the Spring application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Verify the application:
   - **API**: `http://localhost:8080`
   - **Database**: Check the `reimbursement_db`.

---
