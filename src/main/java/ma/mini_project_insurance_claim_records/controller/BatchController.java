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

@RestController
@RequestMapping()
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fetchJSONToDossierJob;

    @Autowired
    private DossiersContext dossiersContext;

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