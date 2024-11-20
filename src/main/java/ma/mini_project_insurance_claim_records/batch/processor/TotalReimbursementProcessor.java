package ma.mini_project_insurance_claim_records.batch.processor;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.service.ReimbursementService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TotalReimbursementProcessor implements ItemProcessor<List<Double>,Double> {
    //Responsibility:
    // Aggregates the reimbursement amount from consultation and treatments for each dossier.
    //Justification:
    // Combines the calculated values into a single total,
    ReimbursementService reimbursementService;

    @Override
    public Double process(List<Double> reimbursedTreatments) throws Exception {
        return reimbursementService.totalReimbursement(reimbursedTreatments);
    }

}
