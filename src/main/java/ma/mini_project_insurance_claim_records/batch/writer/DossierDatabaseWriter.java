package ma.mini_project_insurance_claim_records.batch.writer;

import lombok.AllArgsConstructor;
import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.repository.DossierRepository;
import ma.mini_project_insurance_claim_records.service.DossierService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DossierDatabaseWriter implements ItemWriter<Dossier> {
    //Responsibility:
    // Writes processed dossiers with total reimbursement amounts into a database or a file.
    //Justification:
    // This writer is dedicated only to writing output data,
    // separated from the reading and processing of records.
    private final DossierService dossierService;
    private final DossierRepository dossierRepository;

    @Override
    public void write(Chunk<? extends Dossier> chunk) throws Exception {
        dossierService.saveDossiers(chunk.getItems());

    }
}
