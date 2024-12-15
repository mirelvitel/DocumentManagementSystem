package org.example.backend.persistence.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "documents")
public class DocumentSearchEntity {

    @Id
    private Long id;

    private String title;

    private String fileName;

    private String filePath;

    private String textContent;

    public DocumentSearchEntity() {}

    public DocumentSearchEntity(Long id, String title, String fileName, String filePath, String textContent) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.textContent = textContent;
    }
}
