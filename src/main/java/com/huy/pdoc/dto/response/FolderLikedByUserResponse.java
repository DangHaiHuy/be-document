package com.huy.pdoc.dto.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.pdoc.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderLikedByUserResponse {
    private String id;
    private String name;
    private User author;
    private Integer view;
    private Integer star;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date createAt;
    private String slug;
    private Boolean liked;
    public FolderLikedByUserResponse(String id, String name, User author, Integer view, Integer star, Date createAt,
            String slug, Boolean liked) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.view = view;
        this.star = star;
        this.createAt = createAt;
        this.slug = slug;
        this.liked = liked;
    }
}
