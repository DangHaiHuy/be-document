package com.huy.pdoc.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.huy.pdoc.dto.response.FolderLikedByUserResponse;
import com.huy.pdoc.dto.response.FolderSearchResponse;
import com.huy.pdoc.entity.Folder;

import jakarta.transaction.Transactional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
        public Optional<Folder> findBySlug(String slug);

        @Query("SELECT new com.huy.pdoc.dto.response.FolderLikedByUserResponse("
                        + "f.id, f.name, f.author, f.view, f.star, f.createAt, f.slug, "
                        + "CASE WHEN (u.id IS NOT NULL) THEN true ELSE false END) "
                        + "FROM Folder f LEFT JOIN f.usersLiked u ON u.id = :userId "
                        + "WHERE f.author.id = :userId")
        public Page<FolderLikedByUserResponse> getByAuthor(Pageable pageable, @Param("userId") String userId);

        @Query("SELECT new com.huy.pdoc.dto.response.FolderLikedByUserResponse("
                        + "f.id, f.name, f.author, f.view, f.star, f.createAt, f.slug, "
                        + "CASE WHEN (u.id IS NOT NULL) THEN true ELSE false END) "
                        + "FROM Folder f LEFT JOIN f.usersLiked u ON u.id = :userId "
                        + "WHERE (:name IS NULL OR f.name LIKE %:name%) "
                        + "ORDER BY f.createAt DESC")
        public Page<FolderLikedByUserResponse> getByParams(Pageable pageable, @Param("name") String name,
                        @Param("userId") String userId);

        @Query("SELECT new com.huy.pdoc.dto.response.FolderSearchResponse(f.id,f.name,f.slug)" +
                        "FROM Folder f WHERE (:name IS NULL OR f.name LIKE %:name%) ORDER BY f.createAt DESC")
        public Page<FolderSearchResponse> getSearchByParams(Pageable pageable, @Param("name") String name);

        @Query("SELECT new com.huy.pdoc.dto.response.FolderLikedByUserResponse("
                        + "f.id, f.name, f.author, f.view, f.star, f.createAt, f.slug, "
                        + "true) "
                        + "FROM Folder f JOIN f.usersLiked u "
                        + "WHERE u.id = :userId "
                        + "ORDER BY f.createAt DESC")
        public Page<FolderLikedByUserResponse> getFavoriteByUserId(Pageable pageable, String userId);

        @Transactional
        public void deleteBySlug(String slug);
}
