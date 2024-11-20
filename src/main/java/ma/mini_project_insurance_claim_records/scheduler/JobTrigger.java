package ma.mini_project_insurance_claim_records.scheduler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JobTrigger {

//    // Inject the value from application.properties
//    @Value("${file.input}")
//    private String fileInput;

    private final JobLauncher jobLauncher;
    private final Job fetchReimbursementProductJob;


    @Bean
    public CommandLineRunner runJob() {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("uniqueness",new Date())
//                    .addString("input_file", fileInput)
                    .toJobParameters();

            jobLauncher.run(fetchReimbursementProductJob, jobParameters);
        };
    }
}
