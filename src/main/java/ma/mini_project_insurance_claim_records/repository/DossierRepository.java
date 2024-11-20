package ma.mini_project_insurance_claim_records.repository;


import ma.mini_project_insurance_claim_records.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierRepository extends JpaRepository<Dossier,String> {
}
