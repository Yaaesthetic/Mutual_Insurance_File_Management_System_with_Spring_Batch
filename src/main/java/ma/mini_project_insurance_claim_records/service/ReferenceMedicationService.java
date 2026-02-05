package ma.mini_project_insurance_claim_records.service;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import ma.mini_project_insurance_claim_records.repository.MedicationReferenceRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

/**
 * Service for retrieving reference medication information from the database.
 * 
 * This service handles the lookup of reference medications based on various criteria
 * (code, name, price). It performs normalization on medication names to improve matching
 * accuracy by removing accents and converting to uppercase.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class ReferenceMedicationService {
    private final MedicationReferenceRepository medicationReferenceRepository;

    /**
     * Retrieves reference medication details based on medication identifiers.
     * 
     * Searches for a medication in the reference database using the code, name,
     * and price. The medication name is normalized before searching to improve
     * matching accuracy.
     * 
     * @param medicationCODE The product code of the medication
     * @param medicationName The name of the medication to search for
     * @param medicationPrix The price of the medication
     * @return The {@link TreatmentProduct} entity if found, null otherwise
     */
    public TreatmentProduct getReferenceMedication(Long medicationCODE, String medicationName, double medicationPrix) {
        String normalizedMedicationName = normalizeMedicationName(medicationName);
        return medicationReferenceRepository.findByCODEAndNOMContainingAndPRIXBR(medicationCODE, normalizedMedicationName, medicationPrix);
    }

    /**
     * Normalizes a medication name for comparison purposes.
     * 
     * The normalization process:
     * <ul>
     *   <li>Removes accented characters (NFD decomposition and combining mark removal)</li>
     *   <li>Converts to uppercase</li>
     *   <li>Replaces special characters with spaces</li>
     *   <li>Condenses multiple spaces into single spaces</li>
     *   <li>Trims whitespace</li>
     * </ul>
     * 
     * @param input The medication name to normalize
     * @return The normalized medication name, or null if input is null
     */
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
