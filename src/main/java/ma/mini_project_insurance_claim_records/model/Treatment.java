package ma.mini_project_insurance_claim_records.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity representing a single treatment or medication within a dossier.
 * 
 * This entity stores details about a medication prescribed to the patient,
 * including its identification code, name, type, price, and availability status.
 * Multiple treatments can be associated with a single dossier.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class Treatment {
    /** The barcode or product code that uniquely identifies the medication. */
    @Id
    private Long codeBarre;

    /** Indicates whether the medication exists in the reference database. */
    private boolean existe;

    /** The commercial name of the medication. */
    private String nomMedicament;

    /** The type or category of medication (e.g., analgesic, antibiotic, etc.). */
    private String typeMedicament;

    /** The price of the medication. */
    private double prixMedicament;
}
