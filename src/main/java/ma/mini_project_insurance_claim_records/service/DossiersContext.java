package ma.mini_project_insurance_claim_records.service;

import ma.mini_project_insurance_claim_records.dto.DossierDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DossiersContext {
    private static final Logger logger = LoggerFactory.getLogger(DossiersContext.class);

    private List<DossierDTO> dossiers = new ArrayList<>();

    public void setDossiers(List<DossierDTO> dossiers) {
        logger.info("Setting dossiers in context");
        logger.info("Number of dossiers to set: {}", dossiers != null ? dossiers.size() : "NULL");

        // Clear existing dossiers
        this.dossiers.clear();

        // Add new dossiers
        if (dossiers != null) {
            this.dossiers.addAll(dossiers);
        }

        logger.info("Current dossiers in context after setting: {}", this.dossiers.size());
    }

    public List<DossierDTO> getDossiers() {
        logger.info("Retrieving dossiers from context");
        logger.info("Number of dossiers in context: {}", dossiers.size());
        return new ArrayList<>(dossiers); // Return a copy to prevent external modifications
    }
}