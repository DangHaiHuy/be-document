package com.huy.pdoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.huy.pdoc.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    public List<Document> findByFolderSlugOrderByCreateAtDesc(String slug);

    public List<Document> findByFolderSlug(String slug);

    public Optional<Document> findByFirebaseId(String id);
}
