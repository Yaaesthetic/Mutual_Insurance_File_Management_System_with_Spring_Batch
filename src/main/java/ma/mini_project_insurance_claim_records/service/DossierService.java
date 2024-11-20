package ma.mini_project_insurance_claim_records.service;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.repository.DossierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DossierService {

    private final DossierRepository dossierRepository;

    /**
     * Save a list of Dossier items if they do not already exist in the database.
     *
     * @param dossiers The list of dossiers to save.
     */
    public void saveDossiers(List<? extends Dossier> dossiers) {
        // Extract affiliation numbers
        List<String> ids = dossiers.stream()
                .map(Dossier::getAffiliationNumber)
                .collect(Collectors.toList());

        // Find existing IDs in the database
        List<String> existingIds = dossierRepository.findAllById(ids).stream()
                .map(Dossier::getAffiliationNumber)
                .toList();

        // Filter dossiers to only those not already existing
        List<Dossier> itemsToSave = dossiers.stream()
                .filter(item -> !existingIds.contains(item.getAffiliationNumber()))
                .collect(Collectors.toList());

        // Save new items if the list is not empty
        if (!itemsToSave.isEmpty()) {
            dossierRepository.saveAll(itemsToSave);
        }
    }
}
