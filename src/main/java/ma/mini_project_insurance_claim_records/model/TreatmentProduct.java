package ma.mini_project_insurance_claim_records.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

/**
 * Represents a reimbursement for a product.
 */

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class TreatmentProduct {
    /** A unique identifier for the product. */
    @Id
    private Long CODE;

    /** The name of the product. */
    private String NOM;

    /** The International Non-proprietary Name (INN) or generic name of the active ingredient. */
    private String DCI1;

    /** The base price of the product. */
    private double PRIXBR;

    /** The reimbursement rate for the product, usually expressed as a percentage. */
    private double TAUXREMBOURSEMENT;
}
