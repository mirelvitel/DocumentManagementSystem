package org.example.backend.persistence.repository;

import org.example.backend.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findByTitleContainingIgnoreCase(String title);
}
