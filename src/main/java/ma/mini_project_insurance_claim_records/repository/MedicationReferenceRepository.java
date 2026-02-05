package ma.mini_project_insurance_claim_records.repository;

import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository interface for {@link TreatmentProduct} entities.
 * 
 * Provides database access and query methods for reference medication products.
 * This repository handles all CRUD operations and custom queries for treatment
 * product data used in reimbursement calculations.
 * 
 * @author Yeasthetic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface MedicationReferenceRepository extends JpaRepository<TreatmentProduct, Long> {
    /**
     * Finds a treatment product by code, name, and price.
     * 
     * Searches for a medication reference using multiple criteria to ensure
     * accurate matching of treatment products.
     * 
     * @param CODE The product code identifier
     * @param NOM The product name (supports partial matching with LIKE clause)
     * @param PRIXBR The base price of the product
     * @return The matching {@link TreatmentProduct} if found, null otherwise
     */
    TreatmentProduct findByCODEAndNOMContainingAndPRIXBR(Long CODE, String NOM, double PRIXBR);
}
