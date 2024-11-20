package ma.mini_project_insurance_claim_records.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class DossierDTO {
    private String numeroAffiliation;
    private String nomAssure;
    private String immatriculation;
    private String lienParente;
    private double montantTotalFrais;
    private double prixConsultation;
    private int nombrePiecesJointes;
    private String nomBeneficiaire;
    private LocalDate dateDepotDossier;
    private List<TreatmentDTO> traitements;
}
