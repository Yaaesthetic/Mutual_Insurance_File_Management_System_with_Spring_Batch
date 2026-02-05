package ma.mini_project_insurance_claim_records.batch.processor;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Composite {@link ItemProcessor} that orchestrates the complete dossier processing pipeline.
 * 
 * This processor chains together four specialized processors in sequence:
 * <ol>
 *   <li>{@link DossierValidationProcessor} - Validates data integrity</li>
 *   <li>{@link DossierTreatmentMapper} - Maps treatments to reference medications</li>
 *   <li>{@link TreatmentReimbursementProcessor} - Calculates individual reimbursements</li>
 *   <li>{@link TotalReimbursementProcessor} - Aggregates into total amount</li>
 * </ol>
 * 
 * Each processor transforms the data for the next stage. This pattern ensures clear
 * separation of concerns with each processor handling a single responsibility.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Component
@Primary
@AllArgsConstructor
public class DossierCompositeProcessor implements ItemProcessor<Dossier, Dossier> {

    private final DossierValidationProcessor dossierValidationProcessor;
    private final DossierTreatmentMapper dossierTreatmentMapper;
    private final TreatmentReimbursementProcessor treatmentReimbursementProcessor;
    private final TotalReimbursementProcessor totalReimbursementProcessor;

    /**
     * Processes a dossier through the complete transformation pipeline.
     * 
     * @param dossier the dossier to process
     * @return the fully processed dossier with calculated reimbursement amount
     * @throws Exception if any processor in the chain fails
     */
    @Override
    public Dossier process(Dossier dossier) throws Exception {
        // Step 1: Validate the dossier
        dossier = dossierValidationProcessor.process(dossier);
        
        // Step 2: Map treatments to reference medications
        List<TreatmentProduct> treatmentProducts = dossierTreatmentMapper.process(dossier);
        
        // Step 3: Calculate reimbursement for each treatment
        List<Double> reimbursedTreatments = treatmentReimbursementProcessor.process(treatmentProducts);
        
        // Step 4: Aggregate reimbursements and set total
        Double totalReimbursement = totalReimbursementProcessor.process(reimbursedTreatments);
        dossier.setReimbursedAmount(totalReimbursement);
        
        return dossier;
    }
}
