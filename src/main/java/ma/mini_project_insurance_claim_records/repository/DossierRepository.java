package ma.mini_project_insurance_claim_records.repository;

import ma.mini_project_insurance_claim_records.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for {@link Dossier} entities.
 * 
 * Provides database access and query methods for dossier records using a
 * unique affiliation number as the primary key. This repository handles all
 * CRUD (Create, Read, Update, Delete) operations for dossiers.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface DossierRepository extends JpaRepository<Dossier, String> {
}
