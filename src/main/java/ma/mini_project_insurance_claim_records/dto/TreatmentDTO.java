package ma.mini_project_insurance_claim_records.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Data Transfer Object for treatment/medication information.
 * 
 * This DTO represents a single treatment or medication item within a dossier.
 * It carries the necessary information to identify and process a medication
 * for reimbursement calculation.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
@Getter
@ToString
public class TreatmentDTO {
    /** The barcode or identification code of the medication. */
    private Long codeBarre;
    
    /** The name of the medication/treatment. */
    private String nomMedicament;
    
    /** The type or category of medication (e.g., analgesic, antibiotic). */
    private String typeMedicament;
    
    /** The price of the medication. */
    private double prixMedicament;
    
    /** Indicates whether the medication exists in the reference database. */
    private boolean existe;
}
