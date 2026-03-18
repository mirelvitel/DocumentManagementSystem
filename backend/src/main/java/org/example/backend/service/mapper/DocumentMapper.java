package org.example.backend.service.mapper;

import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "filePath", ignore = true)
    DocumentDTO toDTO(DocumentEntity documentEntity);

    @Mapping(target = "filePath", ignore = true)
    DocumentEntity toEntity(DocumentDTO documentDTO);

    DocumentSearchEntity toSearchEntity(DocumentEntity documentEntity);

    DocumentEntity toEntity(DocumentSearchEntity searchEntity);

    @Mapping(target = "filePath", ignore = true)
    DocumentDTO searchEntityToDTO(DocumentSearchEntity searchEntity);
}