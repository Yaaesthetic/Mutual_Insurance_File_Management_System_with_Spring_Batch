package ma.mini_project_insurance_claim_records.batch.processor;

import ma.mini_project_insurance_claim_records.model.Dossier;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
/**
 * Validates dossier data before processing.
 * Checks for required fields and data integrity.
 * 
 * @author Yeasthetic
 * @since 1.0
 */
public class DossierValidationProcessor implements ItemProcessor<Dossier, Dossier> {
    
    /**
     * Validates a dossier and returns it if valid.
     * 
     * @param dossier the dossier to validate
     * @return the validated dossier
     * @throws IllegalArgumentException if validation fails
     */
    //Responsibility:
    // Validates that essential information
    // (like String affiliationNumber, String beneficiaryName, String insuredName, LocalDate treatmentDate)
    // is present and adheres to basic constraints (e.g., positive amounts).
    //Justification:
    // This processor only checks the data integrity of each dossier, adhering to by separating validation from other processing.

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

        if(dossier.getTreatments() == null || dossier.getTreatments().isEmpty()){
            throw new IllegalArgumentException("Treatments must be not vide.");
        }

        if (dossier.getTreatments() == null || dossier.getTreatments().isEmpty()) {
    throw new IllegalArgumentException("Dossier must contain at least one treatment");
}
        // Return the valid dossier
        return dossier;
    }
}
