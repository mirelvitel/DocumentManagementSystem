package org.example.backend.service.mapper;

import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    @Mapping(source = "textContent", target = "textContent")
    DocumentDTO toDTO(DocumentEntity documentEntity);

    @Mapping(source = "textContent", target = "textContent")
    DocumentEntity toEntity(DocumentDTO documentDTO);
}
