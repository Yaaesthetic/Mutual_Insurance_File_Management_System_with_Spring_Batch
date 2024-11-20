package ma.mini_project_insurance_claim_records.batch.reader;

import ma.mini_project_insurance_claim_records.dto.DossierDTO;
import ma.mini_project_insurance_claim_records.dto.TreatmentDTO;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.Treatment;
import ma.mini_project_insurance_claim_records.service.DossiersContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DossierJsonReader implements ItemReader<Dossier> {
    private static final Logger logger = LoggerFactory.getLogger(DossierJsonReader.class);

    private final DossiersContext dossiersContext;
    private Iterator<DossierDTO> dossierIterator;

    public DossierJsonReader(DossiersContext dossiersContext) {
        this.dossiersContext = dossiersContext;
    }

    @Override
    public Dossier read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // Initialize iterator if not already initialized
        if (dossierIterator == null) {
            List<DossierDTO> dossiers = dossiersContext.getDossiers();
            logger.info("Initializing iterator with {} dossiers", dossiers.size());

            // Log details of each dossier
            for (int i = 0; i < dossiers.size(); i++) {
                DossierDTO dossier = dossiers.get(i);
                logger.info("Dossier {}: {}", i + 1, dossierToString(dossier));
            }

            dossierIterator = dossiers.iterator();
        }

        // Read next dossier
        if (dossierIterator.hasNext()) {
            DossierDTO dossierDTO = dossierIterator.next();

            logger.info("Reading DossierDTO: {}", dossierToString(dossierDTO));

            Dossier dossier = convertToDossier(dossierDTO);

            logger.info("Converted Dossier: {}", dossier);
            return dossier;
        }

        logger.warn("No more DossierDTOs to read. Iterator is empty.");
        return null; // End of data
    }

    private Dossier convertToDossier(DossierDTO dossierDTO) {
        if (dossierDTO == null) {
            logger.error("Received null DossierDTO - cannot convert");
            throw new IllegalArgumentException("Cannot convert null DossierDTO");
        }

        Dossier dossier = new Dossier();

        // Detailed null checks and logging
        dossier.setAffiliationNumber(dossierDTO.getNumeroAffiliation());
        dossier.setInsuredName(dossierDTO.getNomAssure());
        dossier.setBeneficiaryName(dossierDTO.getNomBeneficiaire());
        dossier.setLienParente(dossierDTO.getLienParente());
        dossier.setDossierSubmissionDate(dossierDTO.getDateDepotDossier());
        dossier.setNombrePiecesJointes(dossierDTO.getNombrePiecesJointes());
        dossier.setPrixConsultation(dossierDTO.getPrixConsultation());
        dossier.setTotalCost(dossierDTO.getMontantTotalFrais());
        dossier.setTreatmentDate(LocalDate.now());
        // Convert treatments with additional logging
        List<Treatment> treatments = convertTreatments(dossierDTO.getTraitements());
        logger.info("Number of treatments converted: {}", treatments.size());
        dossier.setTreatments(treatments);

        return dossier;
    }

    private List<Treatment> convertTreatments(List<TreatmentDTO> traitementDTOs) {
        List<Treatment> treatments = new ArrayList<>();

        if (traitementDTOs == null) {
            logger.warn("Received null TreatmentDTO list");
            return treatments;
        }

        for (TreatmentDTO traitementDTO : traitementDTOs) {
            if (traitementDTO == null) {
                logger.warn("Skipping null TreatmentDTO");
                continue;
            }

            Treatment treatment = new Treatment();
            treatment.setCodeBarre(traitementDTO.getCodeBarre());
            treatment.setNomMedicament(traitementDTO.getNomMedicament());
            treatment.setTypeMedicament(traitementDTO.getTypeMedicament());
            treatment.setPrixMedicament(traitementDTO.getPrixMedicament());
            treatment.setExiste(traitementDTO.isExiste());

            treatments.add(treatment);
        }

        return treatments;
    }

    // Helper method to convert DossierDTO to a readable string for logging
    private String dossierToString(DossierDTO dossier) {
        if (dossier == null) {
            return "NULL DOSSIER";
        }
        return String.format(
                "AffiliationNumber: %s, InsuredName: %s, BeneficiaryName: %s, " +
                        "SubmissionDate: %s, Total Cost: %s,Treatments : %s",
                dossier.getNumeroAffiliation(),
                dossier.getNomAssure(),
                dossier.getNomBeneficiaire(),
                dossier.getDateDepotDossier(),
                dossier.getMontantTotalFrais(),
                dossier.getTraitements()
        );
    }
}