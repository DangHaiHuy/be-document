package com.huy.pdoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.request.DeleteListDocumentsRequest;
import com.huy.pdoc.dto.request.DocumentRequest;
import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.DocumentDeleteResponse;
import com.huy.pdoc.dto.response.ListDocumentResponse;
import com.huy.pdoc.entity.Document;
import com.huy.pdoc.service.DocumentService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/file")
public class DocumentController {
    private DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{slug}")
    public ApiResponse<ListDocumentResponse> getAllDocument(@PathVariable String slug) {
        return ApiResponse.<ListDocumentResponse>builder().result(documentService.getAllDocument(slug)).build();
    }

    @Transactional
    @PostMapping("/add/{slug}")
    public ApiResponse<Document> addDocument(@PathVariable String slug, @RequestBody DocumentRequest documentRequest) {
        return ApiResponse.<Document>builder().result(documentService.addDocument(slug, documentRequest)).build();
    }

    @GetMapping("/document/{id}")
    public ApiResponse<Document> getDocument(@PathVariable String id) {
        return ApiResponse.<Document>builder().result(documentService.getDocument(id)).build();
    }

    @DeleteMapping("/document/{id}")
    public ApiResponse<DocumentDeleteResponse> deleteDocument(@PathVariable String id) {
        return ApiResponse.<DocumentDeleteResponse>builder().result(documentService.deleteDocument(id)).build();
    }

    @DeleteMapping("/document")
    public ApiResponse<DocumentDeleteResponse> deleteListDocuments(
            @RequestBody DeleteListDocumentsRequest deleteListDocumentsRequest) {
        return ApiResponse.<DocumentDeleteResponse>builder()
                .result(documentService.deleteListDocuments(deleteListDocumentsRequest)).build();
    }
}   
