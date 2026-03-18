package org.example.backend.service.mapper;

import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "ocrStatus", source = "ocrStatus", qualifiedByName = "ocrStatusToString")
    DocumentDTO toDTO(DocumentEntity documentEntity);

    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "ocrStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DocumentEntity toEntity(DocumentDTO documentDTO);

    DocumentSearchEntity toSearchEntity(DocumentEntity documentEntity);

    @Mapping(target = "ocrStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DocumentEntity toEntity(DocumentSearchEntity searchEntity);

    @Mapping(target = "ocrStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DocumentDTO searchEntityToDTO(DocumentSearchEntity searchEntity);

    @Named("ocrStatusToString")
    default String ocrStatusToString(DocumentEntity.OcrStatus status) {
        return status == null ? null : status.name();
    }
}