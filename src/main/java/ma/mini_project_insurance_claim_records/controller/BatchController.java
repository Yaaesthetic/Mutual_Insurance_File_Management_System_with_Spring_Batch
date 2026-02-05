package ma.mini_project_insurance_claim_records.controller;

import ma.mini_project_insurance_claim_records.dto.DossierDTO;
import ma.mini_project_insurance_claim_records.service.DossiersContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for submitting and processing dossier batch jobs.
 * 
 * This controller provides endpoints for triggering batch processing jobs
 * that handle insurance claim dossiers. It accepts dossier data in JSON format,
 * stores it in a shared context, and launches the configured Spring Batch job
 * to process the claims.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping()
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fetchJSONToDossierJob;

    @Autowired
    private DossiersContext dossiersContext;

    /**
     * Processes a list of dossiers by launching a batch job.
     * 
     * This endpoint accepts JSON dossier data, stores it in a shared context,
     * creates job parameters for uniqueness, and launches the main batch job
     * for reimbursement calculation and persistence.
     * 
     * @param dossiers A list of {@link DossierDTO} objects containing claim information
     * @return A {@link ResponseEntity} with success message and job status, or error message on failure
     */
    @PostMapping("/start-batch")
    public ResponseEntity<String> processDossiers(@RequestBody List<DossierDTO> dossiers) {
        try {
            // Save the dossiers in the shared context
            dossiersContext.setDossiers(dossiers);

            // Creating Job Parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // Ensure uniqueness for each execution
                    .toJobParameters();

            // Launching the Job
            JobExecution jobExecution = jobLauncher.run(fetchJSONToDossierJob, jobParameters);

            return ResponseEntity.ok("Batch job has been invoked. Status: " + jobExecution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Batch job failed. Error: " + e.getMessage());
        }
    }
}