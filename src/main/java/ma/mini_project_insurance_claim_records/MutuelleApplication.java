package ma.mini_project_insurance_claim_records;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Mutual Insurance Claim Processing System.
 * 
 * This Spring Boot application serves as the entry point for an ETL (Extract, Transform, Load)
 * batch processing system that automates insurance claim processing. It reads dossier data,
 * validates claims, calculates reimbursements based on reference drug prices, and persists
 * results to PostgreSQL.
 * 
 * The system integrates with Zipkin for distributed tracing and Prometheus for metrics collection.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class MutuelleApplication {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * @param args Command-line arguments (not currently used)
     */
    public static void main(String[] args) {
        SpringApplication.run(MutuelleApplication.class, args);
    }

}
