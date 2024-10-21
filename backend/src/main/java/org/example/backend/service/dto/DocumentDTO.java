package org.example.backend.service.dto;

public class DocumentDTO {

    private Long id;
    private String title;
    private String fileName;
    private String filePath;

    public DocumentDTO() {}

    public DocumentDTO(Long id, String title, String fileName, String filePath) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
