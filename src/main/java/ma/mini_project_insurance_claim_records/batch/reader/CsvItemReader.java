package ma.mini_project_insurance_claim_records.batch.reader;

import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class CsvItemReader implements ItemReader<TreatmentProduct> {

    private final Iterator<TreatmentProduct> iterator;

    public CsvItemReader(@Value("${file.input}") String fileInput) throws Exception {
        // Load file from resources using ClassPathResource
        Resource resource = new ClassPathResource(fileInput);

        List<TreatmentProduct> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true; // To skip the header

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                // The split logic is correct for handling commas within quoted fields,
                // but the regex validation was incorrect for the file structure.
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Your CSV file has 12 columns. Let's ensure the row has enough data.
                // HEADER: CODE,NOM,DCI1,DOSAGE1,UNITE_DOSAGE1,FORME,PRESENTATION,PPV,PH,PRIX_BR,PRINCEPS_GENERIQUE,TAUX_REMBOURSEMENT
                if (fields.length < 12) {
                    System.err.println("Skipping invalid row (not enough columns): " + line);
                    continue;
                }

                try {
                    // We need to map to TreatmentProduct using the correct column indices.
                    // CODE is at index 0
                    // NOM is at index 1
                    // DCI1 is at index 2
                    // PRIX_BR is at index 9
                    // TAUX_REMBOURSEMENT is at index 11
                    TreatmentProduct product = new TreatmentProduct(
                            Long.parseLong(fields[0].trim()),            // CODE (index 0)
                            fields[1].replace("\"", "").trim(),          // NOM (index 1)
                            fields[2].replace("\"", "").trim(),          // DCI1 (index 2)
                            Double.parseDouble(fields[9].trim()),        // PRIX_BR (index 9)
                            Double.parseDouble(fields[11].trim())        // TAUX_REMBOURSEMENT (index 11)
                    );
                    products.add(product);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping row due to number format error: " + line);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping row due to missing columns: " + line);
                }
            }
        }

        this.iterator = products.iterator();
    }

    @Override
    public TreatmentProduct read() {
        return iterator.hasNext() ? iterator.next() : null; // Return next item or null to indicate end of data
    }
}
