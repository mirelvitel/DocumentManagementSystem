package org.example.backend.service.mapper;

import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    // Map between DocumentEntity and DocumentDTO
    @Mapping(source = "textContent", target = "textContent")
    DocumentDTO toDTO(DocumentEntity documentEntity);

    @Mapping(source = "textContent", target = "textContent")
    DocumentEntity toEntity(DocumentDTO documentDTO);

    // Map between DocumentEntity and DocumentSearchEntity
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "filePath", target = "filePath")
    @Mapping(source = "textContent", target = "textContent")
    DocumentSearchEntity toSearchEntity(DocumentEntity documentEntity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "filePath", target = "filePath")
    @Mapping(source = "textContent", target = "textContent")
    DocumentEntity toEntity(DocumentSearchEntity searchEntity);
}
