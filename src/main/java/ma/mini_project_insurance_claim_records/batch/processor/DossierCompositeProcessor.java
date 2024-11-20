package ma.mini_project_insurance_claim_records.batch.processor;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class DossierCompositeProcessor implements ItemProcessor<Dossier, Dossier> {
    //Responsibility:
    // Chains DossierValidationProcessor,
    // DossierTreatmentMapper,
    // TreatmentReimbursementProcessor,
    // and TotalReimbursementProcessor in a composite processor.
    //Justification:
    // This composite processor brings together all individual processors,
    // maintaining a single responsibility of sequentially processing each dossier.

    // Declare the individual processors as fields
    private final DossierValidationProcessor dossierValidationProcessor;
    private final DossierTreatmentMapper dossierTreatmentMapper;
    private final TreatmentReimbursementProcessor treatmentReimbursementProcessor;
    private final TotalReimbursementProcessor totalReimbursementProcessor;


    @Override
    public Dossier process(Dossier dossier) throws Exception {
        // Sequentially call each processor
        dossier = dossierValidationProcessor.process(dossier);
        List<TreatmentProduct> treatmentProducts = dossierTreatmentMapper.process(dossier);
        List<Double> reimbursedTreatments = treatmentReimbursementProcessor.process(treatmentProducts);
        Double totalled=totalReimbursementProcessor.process(reimbursedTreatments);
        System.out.println(totalled);
        dossier.setReimbursedAmount(totalled);
        return dossier;
    }

}
