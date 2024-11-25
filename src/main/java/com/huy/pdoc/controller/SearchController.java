package com.huy.pdoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.FolderSearchResponse;
import com.huy.pdoc.dto.response.ListResponse;
import com.huy.pdoc.service.FolderService;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private FolderService folderService;

    @Autowired
    public SearchController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public ApiResponse<ListResponse<FolderSearchResponse>> searchData(@RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "4") int limit) {
        return ApiResponse.<ListResponse<FolderSearchResponse>>builder()
                .result(folderService.getFolderSearchList(page - 1, limit, name))
                .build();
    }

}
