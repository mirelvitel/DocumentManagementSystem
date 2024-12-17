package org.example.ocrservice.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        // Optionally, set the datapath if required
        // tesseract.setDatapath("/path/to/tessdata");
        return tesseract;
    }
}
