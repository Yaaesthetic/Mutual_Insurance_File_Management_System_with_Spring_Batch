package ma.mini_project_insurance_claim_records.batch.writer.csv;

import ma.mini_project_insurance_claim_records.repository.MedicationReferenceRepository;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvItemWriter implements ItemWriter<TreatmentProduct> {

    @Autowired
    private MedicationReferenceRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends TreatmentProduct> chunk) throws Exception {
        List<Long> ids = chunk.getItems().stream()
                .map(TreatmentProduct::getCODE)
                .collect(Collectors.toList());

        List<Long> existingIds = repository.findAllById(ids).stream()
                .map(TreatmentProduct::getCODE)
                .toList();

        List<TreatmentProduct> itemsToSave = chunk.getItems().stream()
                .filter(item -> !existingIds.contains(item.getCODE()))
                .collect(Collectors.toList());

        if (!itemsToSave.isEmpty()) {
            repository.saveAll(itemsToSave);
        }
    }

}
