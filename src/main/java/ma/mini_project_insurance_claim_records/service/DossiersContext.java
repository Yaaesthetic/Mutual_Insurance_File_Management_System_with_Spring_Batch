package ma.mini_project_insurance_claim_records.service;

import ma.mini_project_insurance_claim_records.dto.DossierDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton component that maintains a shared context for dossier data during batch processing.
 * 
 * This component acts as a bridge between the REST controller and the batch reader,
 * providing thread-safe storage for dossier data that needs to be processed by the batch job.
 * It ensures isolation between job runs by clearing previous data before accepting new dossiers.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Component
public class DossiersContext {
    private static final Logger logger = LoggerFactory.getLogger(DossiersContext.class);

    /** The list of dossiers to be processed. */
    private List<DossierDTO> dossiers = new ArrayList<>();

    /**
     * Sets the list of dossiers in the context, clearing any previously stored dossiers.
     * 
     * This method ensures that each batch job run has a clean slate by clearing
     * the context before adding new dossiers. This pattern prevents data spillover
     * between multiple job executions.
     * 
     * @param dossiers The list of dossiers to process. If null, no dossiers are added.
     */
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

    /**
     * Retrieves a copy of the current dossiers from the context.
     * 
     * Returns a defensive copy to prevent external modifications of the internal list.
     * 
     * @return A copy of the list of dossiers in the context
     */
    public List<DossierDTO> getDossiers() {
        logger.info("Retrieving dossiers from context");
        logger.info("Number of dossiers in context: {}", dossiers.size());
        return new ArrayList<>(dossiers); // Return a copy to prevent external modifications
    }
}