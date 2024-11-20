package ma.mini_project_insurance_claim_records.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the reimbursement record for a client.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class Dossier {

    /** A unique identifier for the client.
     * The affiliation number of the client. */
    @Id
    private String affiliationNumber;

    /** The name of the beneficiary. */
    private String beneficiaryName;

    /** The name of the insured person. */
    private String insuredName;

    private String lienParente;

    /** The date the dossier was submitted. */
    private LocalDate dossierSubmissionDate;

    /** The date the treatment was provided. */
    private LocalDate treatmentDate;

    private int nombrePiecesJointes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dossier_id")  // Specifies the foreign key column in Traitement table
    private List<Treatment> treatments = new ArrayList<>();

    private double prixConsultation;

    /** The total cost of the reimbursement request. */
    private double totalCost;

    /** The amount reimbursed to the client. */
    private double reimbursedAmount;


}
/*
  {
    "nomBeneficiaire": "Omar",
    "nomAssure": "Ibrahimi",
    "numeroAffiliation": "AFF123456",
    "dateDepotDossier": "2024-11-10",
    "dateTraitement": "2024-11-09",  // Date of treatment
    "montantTotalFrais": 150.0,
    "montantRembourse": 120.0  // Example: This is calculated based on reimbursement rate
  }
*/
// montantRembourse = montantTotalFrais * tauxReimbursement / 100;