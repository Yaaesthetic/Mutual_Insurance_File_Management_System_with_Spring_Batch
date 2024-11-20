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
import java.util.regex.Pattern;

@Component
public class CsvItemReader implements ItemReader<TreatmentProduct> {

    private static final Pattern CSV_PATTERN = Pattern.compile(
            "^(\\d+),\"?([^\"]+?)\"?,\"?([^\"]+?)\"?,([\\d\\.]+),([\\d\\.]+)$"
    );

    private final Iterator<TreatmentProduct> iterator;

    public CsvItemReader(@Value("${file.input}") String fileInput) throws Exception {
        // Load file from resources using ClassPathResource
        Resource resource = new ClassPathResource(fileInput);

        List<TreatmentProduct> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }

                if (CSV_PATTERN.matcher(line).matches()) {
                    String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    if (fields.length < 5) {
                        System.err.println("Skipping invalid row (less fields): " + line);
                        continue;
                    }

                    try {
                        TreatmentProduct product = new TreatmentProduct(
                                Long.parseLong(fields[0]),            // CODE
                                fields[1].replace("\"", ""),          // NOM
                                fields[2].replace("\"", ""),          // DCI1
                                Double.parseDouble(fields[3]),        // PRIXBR
                                Double.parseDouble(fields[4])         // TAUXREMBOURSEMENT
                        );
                        products.add(product);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid row (number format): " + line);
                    }
                } else {
                    System.err.println("Skipping invalid format row: " + line);
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