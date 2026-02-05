package ma.mini_project_insurance_claim_records.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

/**
 * JPA Entity representing a reference medication product.
 * 
 * This entity holds information about medications from the reference database
 * (CNOPS reference medications), including their base price and reimbursement rate.
 * These records are used to calculate reimbursement amounts for patient treatments.
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
public class TreatmentProduct {
    /** A unique identifier for the product (product code). */
    @Id
    private Long CODE;

    /** The commercial name of the product. */
    private String NOM;

    /** The International Non-proprietary Name (INN) or generic name of the active ingredient. */
    private String DCI1;

    /** The base price of the product (reference price from CNOPS). */
    private double PRIXBR;

    /** The reimbursement rate for the product, typically expressed as a percentage (e.g., 80 for 80%). */
    private double TAUXREMBOURSEMENT;
}
