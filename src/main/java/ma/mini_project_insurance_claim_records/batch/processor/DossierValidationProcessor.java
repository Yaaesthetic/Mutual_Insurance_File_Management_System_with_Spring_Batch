package ma.mini_project_insurance_claim_records.batch.processor;

import ma.mini_project_insurance_claim_records.model.Dossier;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Spring Batch {@link ItemProcessor} for validating dossier data integrity.
 * 
 * This processor is the first step in the dossier processing pipeline. It validates
 * that all required fields are present and meet basic constraints before the dossier
 * is passed to downstream processors. This approach implements fail-fast behavior,
 * preventing resources from being wasted on invalid data.
 * 
 * Validations performed:
 * <ul>
 *   <li>Affiliation number is not empty</li>
 *   <li>Insured person name is not empty</li>
 *   <li>Beneficiary name is not empty</li>
 *   <li>Dossier submission date is valid (not in the future)</li>
 *   <li>Consultation price is positive</li>
 *   <li>Total cost is positive</li>
 *   <li>Treatments list is not empty</li>
 * </ul>
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Component
public class DossierValidationProcessor implements ItemProcessor<Dossier, Dossier> {
    
    /**
     * Validates a dossier's data integrity.
     * 
     * @param dossier the dossier to validate
     * @return the validated dossier if all validations pass
     * @throws IllegalArgumentException if any validation fails
     */
    @Override
    public Dossier process(Dossier dossier) throws Exception {
        // Validate Affiliation Number (should not be empty)
        if (dossier.getAffiliationNumber() == null || dossier.getAffiliationNumber().isEmpty()) {
            throw new IllegalArgumentException("Affiliation number is missing.");
        }

        // Validate Insured Name (should not be empty)
        if (dossier.getInsuredName() == null || dossier.getInsuredName().isEmpty()) {
            throw new IllegalArgumentException("Insured name is missing.");
        }

        // Validate Beneficiary Name (should not be empty)
        if (dossier.getBeneficiaryName() == null || dossier.getBeneficiaryName().isEmpty()) {
            throw new IllegalArgumentException("Beneficiary name is missing.");
        }

        // Validate Treatment Date (should be a valid date)
        if (dossier.getDossierSubmissionDate() == null || dossier.getDossierSubmissionDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid treatment date.");
        }

        // Validate Consultation Price (should be positive)
        if (dossier.getPrixConsultation() <= 0) {
            throw new IllegalArgumentException("Consultation price must be positive.");
        }

        // Validate Total Cost (should be positive)
        if (dossier.getTotalCost() <= 0) {
            throw new IllegalArgumentException("Total cost must be positive.");
        }

        // Validate Treatments (should not be empty)
        if (dossier.getTreatments() == null || dossier.getTreatments().isEmpty()) {
            throw new IllegalArgumentException("Dossier must contain at least one treatment");
        }

        // Return the valid dossier
        return dossier;
    }
}
