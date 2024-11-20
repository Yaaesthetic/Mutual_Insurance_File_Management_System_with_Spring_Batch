package ma.mini_project_insurance_claim_records.batch.config_500;

import ma.mini_project_insurance_claim_records.batch.reader.CsvItemReader;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ReimbursementJobConfig {
    /*Configures the Spring Batch job, including the reader, writer, and composite processor.
    Responsibility:
     Sets up the Spring Batch components, focus on job configuration.
     */

    @Bean
    public Step fetchReimbursementProductStep(JobRepository jobRepository,
                                              PlatformTransactionManager transactionManager,
//                                              FlatFileItemReader<TreatmentProduct> productsFlatFileItemReader,
                                              ItemReader<TreatmentProduct> reader,
                                              ItemWriter<TreatmentProduct> writer) throws Exception {
//            , JobParameters jobParameters
        return new StepBuilder("ETL: Import-Reimbursement-Product-Step", jobRepository)
                .<TreatmentProduct, TreatmentProduct>chunk(100, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    // Job: Organizes steps for the reimbursement product import
    @Bean
    public Job fetchReimbursementProductJob(JobRepository jobRepository,
                                            Step fetchReimbursementProductStep) {
        return new JobBuilder("ETL-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchReimbursementProductStep)
                .build();
    }

    @Bean
    public Step fetchJSONToDossierStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       ItemReader<Dossier> reader,
                                       ItemProcessor<Dossier, Dossier> processor,
                                       ItemWriter<Dossier> writer) {
        return new StepBuilder("fetchJSONToDossierStep", jobRepository)
                .<Dossier, Dossier>chunk(10, transactionManager) // Specify input and output types for chunk
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job fetchJSONToDossierJob(JobRepository jobRepository,
                                     Step fetchJSONToDossierStep){
        return new JobBuilder("fetchJSONToDossierJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fetchJSONToDossierStep)
                .build();
    }
}
