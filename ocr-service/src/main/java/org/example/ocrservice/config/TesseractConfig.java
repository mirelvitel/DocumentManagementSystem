package org.example.ocrservice.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Value("${tessdata.prefix:}")
    private String tessdataPrefix;

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("eng");
        if (tessdataPrefix != null && !tessdataPrefix.isEmpty()) {
            tesseract.setDatapath(tessdataPrefix);
        }
        return tesseract;
    }
}