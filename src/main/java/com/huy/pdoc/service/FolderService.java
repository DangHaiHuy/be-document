package com.huy.pdoc.service;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.huy.pdoc.dto.request.FolderDeleteRequest;
import com.huy.pdoc.dto.request.FolderRequest;
import com.huy.pdoc.dto.response.FolderDeleteResponse;
import com.huy.pdoc.dto.response.FolderLikedByUserResponse;
import com.huy.pdoc.dto.response.FolderSearchResponse;
import com.huy.pdoc.dto.response.ListResponse;
import com.huy.pdoc.dto.response.UserResponse;
import com.huy.pdoc.entity.Document;
import com.huy.pdoc.entity.Folder;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.exception.AppException;
import com.huy.pdoc.exception.ErrorCode;
import com.huy.pdoc.repository.DocumentRepository;
import com.huy.pdoc.repository.FolderRepository;

@Service
public class FolderService {
    private FolderRepository folderRepository;
    private UserService userService;
    private FirebaseService firebaseService;
    private DocumentRepository documentRepository;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserService userService, FirebaseService firebaseService,
            DocumentRepository documentRepository) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.firebaseService = firebaseService;
        this.documentRepository = documentRepository;
    }

    public ListResponse<FolderLikedByUserResponse> getFolderList(int page, int limit, String name) {
        String userId = userService.getMyInfo().getId();
        Pageable pageable = PageRequest.of(page, limit);
        Page<FolderLikedByUserResponse> pageFolder = folderRepository.getByParams(pageable, name, userId);
        return new ListResponse<FolderLikedByUserResponse>(pageFolder.getContent(), pageFolder.getTotalElements());
    }

    public ListResponse<FolderSearchResponse> getFolderSearchList(int page, int limit, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<FolderSearchResponse> pageFolderSearchResponse = folderRepository.getSearchByParams(pageable, name);
        return new ListResponse<FolderSearchResponse>(pageFolderSearchResponse.getContent(),
                pageFolderSearchResponse.getTotalElements());
    }

    public ListResponse<FolderLikedByUserResponse> getMyFolderList(int page, int limit) {
        String userId = userService.getMyUser().getId();
        Pageable pageable = PageRequest.of(page, limit);
        Page<FolderLikedByUserResponse> pageFolder = folderRepository.getByAuthor(pageable, userId);
        return new ListResponse<FolderLikedByUserResponse>(pageFolder.getContent(), pageFolder.getTotalElements());
    }

    public FolderLikedByUserResponse createFolder(FolderRequest folderRequest) {
        String slug = removeAccents(folderRequest.getName().trim().toLowerCase().replace(" ", "-"));
        Folder existFolder = folderRepository.findBySlug(slug).orElse(null);
        System.out.println(existFolder);
        if (existFolder == null) {
            Folder folder = Folder.builder()
                    .author(userService.getMyUser())
                    .createAt(new Date())
                    .name(folderRequest.getName())
                    .star(0)
                    .view(0)
                    .slug(slug)
                    .build();
            folderRepository.save(folder);
            return new FolderLikedByUserResponse(folder.getId(), folder.getName(), userService.getMyUser(),
                    folder.getView(), folder.getStar(), folder.getCreateAt(), folder.getSlug(), false);
        }
        throw new AppException(ErrorCode.EXISTED, "This folder already exists");
    }

    private String removeAccents(String str) {
        String normalize = Normalizer.normalize(str, Form.NFD);
        return normalize.replaceAll("\\p{M}", "");
    }

    public ListResponse<FolderLikedByUserResponse> getMyFavoriteFolderList(int page, int limit) {
        String userId = userService.getMyUser().getId();
        Pageable pageable = PageRequest.of(page, limit);
        Page<FolderLikedByUserResponse> pageFolder = folderRepository.getFavoriteByUserId(pageable, userId);
        return new ListResponse<FolderLikedByUserResponse>(pageFolder.getContent(), pageFolder.getTotalElements());
    }

    public FolderDeleteResponse deleteFolder(FolderDeleteRequest folderDeleteRequest) {
        UserResponse myInfo = userService.getMyInfo();
        Folder folder = folderRepository.findBySlug(folderDeleteRequest.getSlug())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Cannot find this folder in database"));
        if (myInfo.getId().equals(folder.getAuthor().getId())) {
            for (User user : folder.getUsersLiked()) {
                user.getFavoriteFolderList().remove(folder);
            }
            List<Document> listDocuments = documentRepository.findByFolderSlug(folderDeleteRequest.getSlug());
            for (Document document : listDocuments) {
                firebaseService.delete("pdf/" + document.getFirebaseId() + document.getName());
            }
            folderRepository.deleteBySlug(folderDeleteRequest.getSlug());
            return FolderDeleteResponse.builder().result("Deleted successfully").build();
        } else
            throw new AppException(ErrorCode.UNAUTHORIZED, "You cannot delete folder not your own");
    }
}
