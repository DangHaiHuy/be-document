package com.huy.pdoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.pdoc.dto.response.ApiResponse;
import com.huy.pdoc.dto.response.FavoriteFolderResponse;
import com.huy.pdoc.dto.response.FolderLikedByUserResponse;
import com.huy.pdoc.dto.response.ListResponse;
import com.huy.pdoc.service.FolderService;
import com.huy.pdoc.service.UserService;

@RestController
@RequestMapping("/users")
public class UserFolderController {
    private FolderService folderService;
    private UserService userService;

    @Autowired
    public UserFolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @GetMapping("/my-documents")
    public ApiResponse<ListResponse<FolderLikedByUserResponse>> getMyFolderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.<ListResponse<FolderLikedByUserResponse>>builder()
                .result(folderService.getMyFolderList(page - 1, limit))
                .build();
    }

    @PutMapping("/my-favorites/add/{id}")
    public ApiResponse<FavoriteFolderResponse> addFavoriteFolder(@PathVariable("id") String id) {
        return ApiResponse.<FavoriteFolderResponse>builder().result(userService.addFavoriteFolder(id)).build();
    }

    @DeleteMapping("/my-favorites/delete/{id}")
    public ApiResponse<FavoriteFolderResponse> deleteFavoriteFolder(@PathVariable("id") String id) {
        return ApiResponse.<FavoriteFolderResponse>builder().result(userService.deleteFavoriteFolder(id)).build();
    }

    @GetMapping("/my-favorites")
    public ApiResponse<ListResponse<FolderLikedByUserResponse>> getMyFavoriteFolderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.<ListResponse<FolderLikedByUserResponse>>builder()
                .result(folderService.getMyFavoriteFolderList(page - 1, limit))
                .build();
    }

}
