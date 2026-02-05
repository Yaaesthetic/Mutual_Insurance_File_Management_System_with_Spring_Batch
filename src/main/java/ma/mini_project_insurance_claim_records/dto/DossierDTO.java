package ma.mini_project_insurance_claim_records.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for insurance claim dossier information.
 * 
 * This DTO is used to receive dossier data from REST clients and provides
 * a clean interface for dossier submission without exposing internal entity structure.
 * It contains all necessary information for processing an insurance claim.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
@Getter
@ToString
public class DossierDTO {
    /** The affiliation number of the insured person. */
    private String numeroAffiliation;
    
    /** The name of the insured person. */
    private String nomAssure;
    
    /** The identification or registration number. */
    private String immatriculation;
    
    /** The relationship of the beneficiary to the insured person (e.g., spouse, child). */
    private String lienParente;
    
    /** The total amount of medical expenses. */
    private double montantTotalFrais;
    
    /** The price of consultation. */
    private double prixConsultation;
    
    /** The number of attached documents. */
    private int nombrePiecesJointes;
    
    /** The name of the beneficiary. */
    private String nomBeneficiaire;
    
    /** The date when the dossier was submitted. */
    private LocalDate dateDepotDossier;
    
    /** The list of treatments associated with this dossier. */
    private List<TreatmentDTO> traitements;
}
