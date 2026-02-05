package ma.mini_project_insurance_claim_records.batch.processor;

import ma.mini_project_insurance_claim_records.model.Dossier;
import ma.mini_project_insurance_claim_records.model.Treatment;
import ma.mini_project_insurance_claim_records.model.TreatmentProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for {@link DossierCompositeProcessor}.
 *
 * Tests verify the proper orchestration of the four-step processing pipeline:
 * validation, treatment mapping, reimbursement calculation, and total aggregation.
 *
 * @author Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class DossierCompositeProcessorTest {

    @Mock
    private DossierValidationProcessor dossierValidationProcessor;

    @Mock
    private DossierTreatmentMapper dossierTreatmentMapper;

    @Mock
    private TreatmentReimbursementProcessor treatmentReimbursementProcessor;

    @Mock
    private TotalReimbursementProcessor totalReimbursementProcessor;

    @InjectMocks
    private DossierCompositeProcessor compositeProcessor;

    private Dossier testDossier;
    private List<TreatmentProduct> mockTreatmentProducts;
    private List<Double> mockReimbursements;

    @BeforeEach
    void setUp() {
        // Create a valid test dossier
        testDossier = new Dossier();
        testDossier.setAffiliationNumber("AFF123456");
        testDossier.setBeneficiaryName("John Doe");
        testDossier.setInsuredName("Jane Doe");
        testDossier.setLienParente("Spouse");
        testDossier.setDossierSubmissionDate(LocalDate.now().minusDays(1));
        testDossier.setTreatmentDate(LocalDate.now().minusDays(2));
        testDossier.setNombrePiecesJointes(3);
        testDossier.setPrixConsultation(150.0);
        testDossier.setTotalCost(500.0);
        testDossier.setReimbursedAmount(0.0);

        // Create test treatments
        Treatment treatment1 = new Treatment();
        treatment1.setCodeBarre(1001L);
        treatment1.setNomMedicament("Paracetamol");
        treatment1.setTypeMedicament("Analgesic");
        treatment1.setPrixMedicament(50.0);
        treatment1.setExiste(true);

        Treatment treatment2 = new Treatment();
        treatment2.setCodeBarre(1002L);
        treatment2.setNomMedicament("Ibuprofen");
        treatment2.setTypeMedicament("Anti-inflammatory");
        treatment2.setPrixMedicament(75.0);
        treatment2.setExiste(true);

        testDossier.setTreatments(Arrays.asList(treatment1, treatment2));

        // Create mock treatment products
        TreatmentProduct product1 = new TreatmentProduct();
        product1.setCODE(1001L);
        product1.setNOM("Paracetamol");
        product1.setPRIXBR(50.0);
        product1.setTAUXREMBOURSEMENT(80.0);

        TreatmentProduct product2 = new TreatmentProduct();
        product2.setCODE(1002L);
        product2.setNOM("Ibuprofen");
        product2.setPRIXBR(75.0);
        product2.setTAUXREMBOURSEMENT(70.0);

        mockTreatmentProducts = Arrays.asList(product1, product2);

        // Create mock reimbursements
        mockReimbursements = Arrays.asList(40.0, 52.5); // 50*0.8=40, 75*0.7=52.5
    }

    /**
     * Test that the composite processor successfully processes a valid dossier
     * through all four stages of the pipeline.
     */
    @Test
    void testProcess_SuccessfulProcessing() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(mockReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(92.5);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(92.5, result.getReimbursedAmount(), 0.01);

        // Verify that all processors were called in the correct order
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(mockTreatmentProducts);
        verify(totalReimbursementProcessor, times(1)).process(mockReimbursements);
    }

    /**
     * Test that validation errors are properly propagated when the validation processor fails.
     */
    @Test
    void testProcess_ValidationFailure_ThrowsException() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class)))
            .thenThrow(new IllegalArgumentException("Affiliation number is missing."));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> compositeProcessor.process(testDossier)
        );

        assertEquals("Affiliation number is missing.", exception.getMessage());

        // Verify that only validation processor was called
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, never()).process(any(Dossier.class));
        verify(treatmentReimbursementProcessor, never()).process(anyList());
        verify(totalReimbursementProcessor, never()).process(anyList());
    }

    /**
     * Test that exceptions from the treatment mapper are properly propagated.
     */
    @Test
    void testProcess_TreatmentMappingFailure_ThrowsException() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class)))
            .thenThrow(new RuntimeException("Failed to map treatments"));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> compositeProcessor.process(testDossier)
        );

        assertEquals("Failed to map treatments", exception.getMessage());

        // Verify that processing stopped after treatment mapping failed
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, never()).process(anyList());
        verify(totalReimbursementProcessor, never()).process(anyList());
    }

    /**
     * Test that exceptions from the reimbursement processor are properly propagated.
     */
    @Test
    void testProcess_ReimbursementCalculationFailure_ThrowsException() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList()))
            .thenThrow(new RuntimeException("Failed to calculate reimbursement"));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> compositeProcessor.process(testDossier)
        );

        assertEquals("Failed to calculate reimbursement", exception.getMessage());

        // Verify that processing stopped after reimbursement calculation failed
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(mockTreatmentProducts);
        verify(totalReimbursementProcessor, never()).process(anyList());
    }

    /**
     * Test that exceptions from the total reimbursement processor are properly propagated.
     */
    @Test
    void testProcess_TotalReimbursementFailure_ThrowsException() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(mockReimbursements);
        when(totalReimbursementProcessor.process(anyList()))
            .thenThrow(new RuntimeException("Failed to calculate total"));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> compositeProcessor.process(testDossier)
        );

        assertEquals("Failed to calculate total", exception.getMessage());

        // Verify that all processors were called but final step failed
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(mockTreatmentProducts);
        verify(totalReimbursementProcessor, times(1)).process(mockReimbursements);
    }

    /**
     * Test processing with empty treatment products list (edge case).
     */
    @Test
    void testProcess_EmptyTreatmentProducts() throws Exception {
        // Arrange
        List<TreatmentProduct> emptyProducts = Collections.emptyList();
        List<Double> emptyReimbursements = Collections.emptyList();

        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(emptyProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(emptyReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(0.0);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getReimbursedAmount(), 0.01);

        // Verify all processors were called
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(emptyProducts);
        verify(totalReimbursementProcessor, times(1)).process(emptyReimbursements);
    }

    /**
     * Test processing with a single treatment (boundary case).
     */
    @Test
    void testProcess_SingleTreatment() throws Exception {
        // Arrange
        Treatment singleTreatment = new Treatment();
        singleTreatment.setCodeBarre(2001L);
        singleTreatment.setNomMedicament("Aspirin");
        singleTreatment.setTypeMedicament("Analgesic");
        singleTreatment.setPrixMedicament(30.0);
        singleTreatment.setExiste(true);

        testDossier.setTreatments(Collections.singletonList(singleTreatment));

        TreatmentProduct singleProduct = new TreatmentProduct();
        singleProduct.setCODE(2001L);
        singleProduct.setNOM("Aspirin");
        singleProduct.setPRIXBR(30.0);
        singleProduct.setTAUXREMBOURSEMENT(60.0);

        List<TreatmentProduct> singleProductList = Collections.singletonList(singleProduct);
        List<Double> singleReimbursement = Collections.singletonList(18.0); // 30*0.6=18

        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(singleProductList);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(singleReimbursement);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(18.0);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(18.0, result.getReimbursedAmount(), 0.01);
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(singleProductList);
        verify(totalReimbursementProcessor, times(1)).process(singleReimbursement);
    }

    /**
     * Test processing with multiple treatments (stress test with larger dataset).
     */
    @Test
    void testProcess_MultipleTreatments() throws Exception {
        // Arrange
        List<Treatment> manyTreatments = new ArrayList<>();
        List<TreatmentProduct> manyProducts = new ArrayList<>();
        List<Double> manyReimbursements = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Treatment treatment = new Treatment();
            treatment.setCodeBarre((long) (3000 + i));
            treatment.setNomMedicament("Medication" + i);
            treatment.setTypeMedicament("Type" + i);
            treatment.setPrixMedicament(100.0 + i * 10);
            treatment.setExiste(true);
            manyTreatments.add(treatment);

            TreatmentProduct product = new TreatmentProduct();
            product.setCODE((long) (3000 + i));
            product.setNOM("Medication" + i);
            product.setPRIXBR(100.0 + i * 10);
            product.setTAUXREMBOURSEMENT(70.0);
            manyProducts.add(product);

            manyReimbursements.add((100.0 + i * 10) * 0.7);
        }

        testDossier.setTreatments(manyTreatments);
        double expectedTotal = manyReimbursements.stream().mapToDouble(Double::doubleValue).sum();

        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(manyProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(manyReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(expectedTotal);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTotal, result.getReimbursedAmount(), 0.01);
        verify(dossierValidationProcessor, times(1)).process(testDossier);
        verify(dossierTreatmentMapper, times(1)).process(testDossier);
        verify(treatmentReimbursementProcessor, times(1)).process(manyProducts);
        verify(totalReimbursementProcessor, times(1)).process(manyReimbursements);
    }

    /**
     * Test that the dossier object is properly modified and returned.
     */
    @Test
    void testProcess_DossierIsModifiedAndReturned() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(mockReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(150.75);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertSame(testDossier, result, "The same dossier instance should be returned");
        assertEquals(150.75, result.getReimbursedAmount(), 0.01);
    }

    /**
     * Test processing with zero reimbursement amount (edge case).
     */
    @Test
    void testProcess_ZeroReimbursement() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(Arrays.asList(0.0, 0.0));
        when(totalReimbursementProcessor.process(anyList())).thenReturn(0.0);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getReimbursedAmount(), 0.01);
    }

    /**
     * Test processing with high reimbursement values (boundary case).
     */
    @Test
    void testProcess_HighReimbursementValues() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(Arrays.asList(5000.0, 7500.0));
        when(totalReimbursementProcessor.process(anyList())).thenReturn(12500.0);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertNotNull(result);
        assertEquals(12500.0, result.getReimbursedAmount(), 0.01);
    }

    /**
     * Test that null dossier is handled appropriately.
     * This is a regression test to ensure null handling.
     */
    @Test
    void testProcess_NullDossier_ThrowsException() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(null))
            .thenThrow(new NullPointerException("Dossier cannot be null"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> compositeProcessor.process(null));
        verify(dossierValidationProcessor, times(1)).process(null);
    }

    /**
     * Test that the composite processor maintains the processing order and doesn't skip steps.
     * This is a regression test to verify sequential processing.
     */
    @Test
    void testProcess_ProcessingOrderIsCorrect() throws Exception {
        // Arrange
        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(mockReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(100.0);

        // Act
        compositeProcessor.process(testDossier);

        // Assert - verify the order using InOrder
        var inOrder = inOrder(
            dossierValidationProcessor,
            dossierTreatmentMapper,
            treatmentReimbursementProcessor,
            totalReimbursementProcessor
        );

        inOrder.verify(dossierValidationProcessor).process(testDossier);
        inOrder.verify(dossierTreatmentMapper).process(testDossier);
        inOrder.verify(treatmentReimbursementProcessor).process(mockTreatmentProducts);
        inOrder.verify(totalReimbursementProcessor).process(mockReimbursements);
    }

    /**
     * Test that the reimbursed amount is properly set on the dossier.
     * This ensures the final step of setting the reimbursement is not forgotten.
     */
    @Test
    void testProcess_ReimbursedAmountIsSetOnDossier() throws Exception {
        // Arrange
        double expectedReimbursement = 234.56;
        testDossier.setReimbursedAmount(0.0); // Start with 0

        when(dossierValidationProcessor.process(any(Dossier.class))).thenReturn(testDossier);
        when(dossierTreatmentMapper.process(any(Dossier.class))).thenReturn(mockTreatmentProducts);
        when(treatmentReimbursementProcessor.process(anyList())).thenReturn(mockReimbursements);
        when(totalReimbursementProcessor.process(anyList())).thenReturn(expectedReimbursement);

        // Act
        Dossier result = compositeProcessor.process(testDossier);

        // Assert
        assertEquals(0.0, testDossier.getReimbursedAmount(), 0.01,
            "Original dossier should be modified by reference");
        assertEquals(expectedReimbursement, result.getReimbursedAmount(), 0.01,
            "Result should have the calculated reimbursement");
    }
}