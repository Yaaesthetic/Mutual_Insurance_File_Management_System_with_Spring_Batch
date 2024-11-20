package ma.mini_project_insurance_claim_records.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class TreatmentDTO {
    private Long codeBarre;
    private String nomMedicament;
    private String typeMedicament;
    private double prixMedicament;
    private boolean existe;
}
