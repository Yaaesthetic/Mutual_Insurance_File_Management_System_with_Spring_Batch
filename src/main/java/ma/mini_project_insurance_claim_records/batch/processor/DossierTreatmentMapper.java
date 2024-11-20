package ma.mini_project_insurance_claim_records.batch.processor;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import ma.mini_project_insurance_claim_records.model.Treatment;
import ma.mini_project_insurance_claim_records.service.ReferenceMedicationService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


@Component
@AllArgsConstructor
public class DossierTreatmentMapper implements ItemProcessor<Dossier,List<TreatmentProduct>> {
    //Responsibility:
    // Maps each treatment in the dossier to a reference medication from a database to ensure
    // that the treatment can be reimbursed.
    //Justification: This class is responsible only for mapping treatments to reference medications, isolating it from the actual calculation logic.

    private final ReferenceMedicationService referenceMedicationService;

    public List<TreatmentProduct> process(Dossier dossier) {
        // List to store mapped reference medications
        List<TreatmentProduct> mappedTreatments = new ArrayList<>();

        // Iterate over all treatments in the dossier and map each to its reference medication
        List<Treatment> treatments = dossier.getTreatments();

        for (Treatment treatment : treatments) {
            // Fetch reference medication based on the medication name (nomMedicament)
            TreatmentProduct referenceProduct = referenceMedicationService.getReferenceMedication(treatment.getCodeBarre(), treatment.getNomMedicament(), treatment.getPrixMedicament());

            if (referenceProduct != null) {
                // Add the reference product to the mapped treatments list
                mappedTreatments.add(referenceProduct);
            } else {
                // Handle cases where the reference medication is not found (optional logging or exception)
                System.out.println("No reference medication found for: " + treatment.getNomMedicament());
            }
        }

        return mappedTreatments;
    }
}
