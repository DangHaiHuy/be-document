package com.huy.pdoc.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.service.FirebaseService;

@RestController
public class FileController {
    private final FirebaseService firebaseService;

    public FileController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @DeleteMapping("/files/{fileName}")
    public ApiResponse<String> deleteFile(@PathVariable String fileName) {
        firebaseService.delete("images/" + fileName); // Đường dẫn đến file
        return ApiResponse.<String>builder().result("").build();
    }
}
