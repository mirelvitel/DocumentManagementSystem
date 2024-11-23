package org.example.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DOCUMENT_UPLOAD_QUEUE = "document-upload-queue";
    public static final String OCR_RESULT_QUEUE = "ocr-result-queue";

    @Bean
    public Queue documentUploadQueue() {
        return new Queue(DOCUMENT_UPLOAD_QUEUE, true);
    }

    @Bean
    public Queue ocrResultQueue() {
        return new Queue(OCR_RESULT_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
