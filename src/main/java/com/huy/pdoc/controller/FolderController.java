package com.huy.pdoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.request.FolderDeleteRequest;
import com.huy.pdoc.dto.request.FolderRequest;
import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.FolderDeleteResponse;
import com.huy.pdoc.dto.response.FolderLikedByUserResponse;
import com.huy.pdoc.dto.response.ListResponse;
import com.huy.pdoc.service.FolderService;

@RestController
@RequestMapping("/api/v1/folder")
public class FolderController {
    private FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public ApiResponse<ListResponse<FolderLikedByUserResponse>> getFolderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int limit, @RequestParam(required = false) String name) {
        return ApiResponse.<ListResponse<FolderLikedByUserResponse>>builder()
                .result(folderService.getFolderList(page - 1, limit, name))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<FolderLikedByUserResponse> createFolder(@RequestBody FolderRequest folderRequest) {
        return ApiResponse.<FolderLikedByUserResponse>builder().result(folderService.createFolder(folderRequest))
                .build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<FolderDeleteResponse> deleteFolder(@RequestBody FolderDeleteRequest folderDeleteRequest) {
        return ApiResponse.<FolderDeleteResponse>builder().result(folderService.deleteFolder(folderDeleteRequest)).build();
    }
}
