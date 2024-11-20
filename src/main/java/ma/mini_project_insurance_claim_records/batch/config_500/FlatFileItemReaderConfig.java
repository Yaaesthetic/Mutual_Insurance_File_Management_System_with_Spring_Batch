//package ma.mini_project_insurance_claim_records.batch.config_500;
//
//import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//public class FlatFileItemReaderConfig {
//
//    @Value("${file.input}") String fileInput;
//
//    @Bean
//    public FlatFileItemReader<TreatmentProduct> productsFlatFileItemReader() {
//        return new FlatFileItemReaderBuilder<TreatmentProduct>()
//                .name("productsFlatFileItemReader")
//                .resource(new ClassPathResource(fileInput))
//                .saveState(Boolean.FALSE)
//                .linesToSkip(1)
//                .delimited()
//                .delimiter(",")
//                .names("CODE", "NOM", "DCI1", "PRIXBR", "TAUXREMBOURSEMENT")
//                .targetType(TreatmentProduct.class)
//                .build();
//    }
//}
