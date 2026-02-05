package ma.mini_project_insurance_claim_records.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing an insurance claim dossier.
 * 
 * This entity contains all information necessary for processing an insurance claim,
 * including member details, treatment information, medical costs, and calculated
 * reimbursement amounts. The dossier serves as the main container for a complete
 * claim submission.
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
public class Dossier {

    /**
     * Unique identifier and primary key for the dossier.
     * The affiliation number of the insured member.
     */
    @Id
    private String affiliationNumber;

    /** The name of the person receiving the treatment/reimbursement. */
    private String beneficiaryName;

    /** The name of the insured person (policy holder). */
    private String insuredName;

    /** The relationship between the beneficiary and the insured person (e.g., spouse, child). */
    private String lienParente;

    /** The date when the dossier was submitted to the insurance company. */
    private LocalDate dossierSubmissionDate;

    /** The date when the treatment was provided/administered. */
    private LocalDate treatmentDate;

    /** The number of supporting documents attached to the dossier. */
    private int nombrePiecesJointes;

    /**
     * One-to-many relationship with treatments.
     * A single dossier can contain multiple treatments.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dossier_id")
    private List<Treatment> treatments = new ArrayList<>();

    /** The price or cost of the consultation. */
    private double prixConsultation;

    /** The total cost of all medical expenses claimed in this dossier. */
    private double totalCost;

    /** The calculated amount to be reimbursed to the member based on the insurance policy. */
    private double reimbursedAmount;
}