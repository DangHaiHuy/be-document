package com.huy.pdoc.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huy.pdoc.dto.request.DeleteListDocumentsRequest;
import com.huy.pdoc.dto.request.DocumentRequest;
import com.huy.pdoc.dto.response.DocumentDeleteResponse;
import com.huy.pdoc.dto.response.ListDocumentResponse;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.entity.Document;
import com.huy.pdoc.entity.Folder;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.exception.AppException;
import com.huy.pdoc.exception.ErrorCode;
import com.huy.pdoc.repository.DocumentRepository;
import com.huy.pdoc.repository.FolderRepository;

@Service
public class DocumentService {
    private DocumentRepository documentRepository;
    private FolderRepository folderRepository;
    private UserService userService;
    private FirebaseService firebaseService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, FolderRepository folderRepository,
            UserService userService, FirebaseService firebaseService) {
        this.documentRepository = documentRepository;
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.firebaseService = firebaseService;
    }

    public ListDocumentResponse getAllDocument(String slug) {
        UserResponse myInfo = userService.getMyInfo();
        Folder folder = folderRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this document in database"));
        List<Document> list = documentRepository.findByFolderSlugOrderByCreateAtDesc(slug);
        return new ListDocumentResponse(list, list.size(), myInfo.getId().equals(folder.getAuthor().getId()));
    }

    public Document addDocument(String slug, DocumentRequest documentRequest) {
        Folder folder = folderRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this document in datbase"));
        User myUser = userService.getMyUser();
        if (myUser.getId().equals(folder.getAuthor().getId())) {
            Document existDocument = documentRepository.findByFirebaseId(documentRequest.getFirebaseId()).orElse(null);
            if (existDocument == null) {
                Document document = Document.builder()
                        .createAt(new Date())
                        .folder(folder)
                        .firebaseId(documentRequest.getFirebaseId())
                        .name(documentRequest.getName())
                        .build();
                documentRepository.save(document);
                return document;
            } else
                throw new AppException(ErrorCode.EXISTED, "This file already exists in database");
        }
        throw new AppException(ErrorCode.UNAUTHORIZED, "You can't add document into the folder not your own");
    }

    public Document getDocument(String id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this file in database"));
        return document;
    }

    public DocumentDeleteResponse deleteDocument(String id) {
        UserResponse myInfo = userService.getMyInfo();
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Not found this document in database"));
        if (myInfo.getId().equals(document.getFolder().getAuthor().getId())) {
            firebaseService.delete("pdf/" + document.getFirebaseId() + document.getName());
            documentRepository.deleteById(id);
            return DocumentDeleteResponse.builder().result("Delete successfully").build();
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED, "You can't delete document not your own");
        }
    }

    public DocumentDeleteResponse deleteListDocuments(DeleteListDocumentsRequest deleteListDocumentsRequest) {
        UserResponse myInfo = userService.getMyInfo();
        List<String> documentsId = deleteListDocumentsRequest.getDocumentsId();
        if (deleteListDocumentsRequest.getDocumentsId().size() > 0) {
            for (String id : documentsId) {
                Document document = documentRepository.findById(id).orElseThrow(
                        () -> new AppException(ErrorCode.NOT_FOUND, "Not found document (" + id + ") in database"));
                if (myInfo.getId().equals(document.getFolder().getAuthor().getId())) {
                    firebaseService.delete("pdf/" + document.getFirebaseId() + document.getName());
                    documentRepository.deleteById(id);
                } else {
                    throw new AppException(ErrorCode.UNAUTHORIZED, "You can't delete document not your own");
                }
            }
        }
        return DocumentDeleteResponse.builder().result("Delete successfully").build();
    }
}
