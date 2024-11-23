package org.example.ocrservice;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class OCRServiceTest {

    @Test
    public void testOCRExtraction() {
        Tesseract tesseract = new Tesseract();

        tesseract.setLanguage("eng");

        try {
            File imageFile = new File("src/test/resources/test-image.png");
            String result = tesseract.doOCR(imageFile);
            assertNotNull(result);
            assertFalse(result.isEmpty());
            System.out.println("Extracted Text: " + result);
        } catch (TesseractException e) {
            fail("OCR extraction failed: " + e.getMessage());
        }
    }
}
