package ma.mini_project_insurance_claim_records.service;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import ma.mini_project_insurance_claim_records.repository.MedicationReferenceRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@AllArgsConstructor
public class ReferenceMedicationService {
    //Fetches reference medication data for the reimbursement calculation.
    //Responsibility:
    // Handles external interactions with the medication reference data source.
    private final MedicationReferenceRepository medicationReferenceRepository;

    public TreatmentProduct getReferenceMedication(Long medicationCODE, String medicationName, double medicationPrix) {
        String normalizedMedicationName = normalizeMedicationName(medicationName);
        return medicationReferenceRepository.findByCODEAndNOMContainingAndPRIXBR(medicationCODE,normalizedMedicationName,medicationPrix);
    }
    private String normalizeMedicationName(String input) {
        if (input == null) {
            return null;
        }

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // Remove accents
                .toUpperCase() // Convert to uppercase
                .replaceAll("[^A-Z0-9]", " ") // Replace special characters with space
                .replaceAll("\\s+", " ") // Replace multiple spaces with single space
                .trim();
    }
}
