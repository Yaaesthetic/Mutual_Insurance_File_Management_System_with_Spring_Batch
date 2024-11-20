package ma.mini_project_insurance_claim_records.batch.processor;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import ma.mini_project_insurance_claim_records.service.ReimbursementService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TreatmentReimbursementProcessor implements ItemProcessor<List<TreatmentProduct>,List<Double>> {
    //Responsibility:
    // For each treatment, calculate the reimbursement amount based on the reference price
    // and reimbursement rate from the reference medication database.
    //Justification: Isolates treatment reimbursement logic,
    // allowing this class to focus only on calculating amounts for each medication.

    private final ReimbursementService reimbursementService;

    public List<Double> process(List<TreatmentProduct> treatmentProducts) {
        return treatmentProducts.stream()
                .map(reimbursementService::calculateReimbursement)
                .collect(Collectors.toList());
    }

}
