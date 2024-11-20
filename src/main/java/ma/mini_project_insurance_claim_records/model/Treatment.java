package ma.mini_project_insurance_claim_records.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class Treatment {
    @Id
    private Long codeBarre;

    private boolean existe;

    private String nomMedicament;

    private String typeMedicament;

    private double prixMedicament;
}
