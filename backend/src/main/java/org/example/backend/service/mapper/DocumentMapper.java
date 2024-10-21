package org.example.backend.service.mapper;

import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentDTO toDTO(DocumentEntity documentEntity);

    DocumentEntity toEntity(DocumentDTO documentDTO);
}
