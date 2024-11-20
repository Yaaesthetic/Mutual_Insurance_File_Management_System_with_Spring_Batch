package ma.mini_project_insurance_claim_records.service;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReimbursementService {
    //Provides logic for calculating reimbursement percentages and rates based on dossier contents.
    //Responsibility:
    // Offers business logic for reimbursement calculations,
    // separate from file or database handling.
    /**
     * Calculates the reimbursement for a single treatment product.
     *
     * @param treatmentProduct The treatment product
     * @return The treatment product with updated reimbursement details
     */
    public double calculateReimbursement(TreatmentProduct treatmentProduct) {
        double basePrice = treatmentProduct.getPRIXBR();
        double reimbursementRate = treatmentProduct.getTAUXREMBOURSEMENT();

        return basePrice * reimbursementRate;
    }

    public double totalReimbursement(List<Double> reimbursedTreatments) {
        return reimbursedTreatments.stream().mapToDouble(Double::doubleValue).sum();

    }

}
