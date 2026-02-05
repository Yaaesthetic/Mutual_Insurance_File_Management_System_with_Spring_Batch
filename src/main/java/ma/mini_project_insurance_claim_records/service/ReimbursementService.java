package ma.mini_project_insurance_claim_records.service;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for calculating reimbursement amounts based on treatment products.
 * 
 * This service provides business logic for computing individual treatment reimbursements
 * and aggregating them into a total reimbursement amount. It applies reference medication
 * prices and reimbursement rates to calculate what will be covered by the insurance.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class ReimbursementService {
    /**
     * Calculates the reimbursement amount for a single treatment product.
     * 
     * The reimbursement is computed as:
     * {@code basePrice * (reimbursementRate / 100)}
     * 
     * @param treatmentProduct The treatment product containing price and reimbursement rate information
     * @return The calculated reimbursement amount for this product
     */
    public double calculateReimbursement(TreatmentProduct treatmentProduct) {
        double basePrice = treatmentProduct.getPRIXBR();
        double reimbursementRate = treatmentProduct.getTAUXREMBOURSEMENT();

        return basePrice * reimbursementRate;
    }

    /**
     * Calculates the total reimbursement amount from a list of individual treatment reimbursements.
     * 
     * Sums all reimbursement amounts for all treatments in a dossier to provide
     * the total amount the insurance will cover.
     * 
     * @param reimbursedTreatments A list of individual reimbursement amounts
     * @return The sum of all reimbursement amounts
     */
    public double totalReimbursement(List<Double> reimbursedTreatments) {
        return reimbursedTreatments.stream().mapToDouble(Double::doubleValue).sum();
    }
}
