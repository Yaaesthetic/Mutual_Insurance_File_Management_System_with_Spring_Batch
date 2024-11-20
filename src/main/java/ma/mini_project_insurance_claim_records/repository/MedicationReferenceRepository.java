package ma.mini_project_insurance_claim_records.repository;

import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationReferenceRepository extends JpaRepository<TreatmentProduct,Long> {
    TreatmentProduct findByCODEAndNOMContainingAndPRIXBR(Long CODE, String NOM, double PRIXBR);
}
