package com.huy.pdoc.dto.response;

import lombok.Getter;

@Getter
public class FolderSearchResponse {
    private String id;
    private String name;
    private String slug;

    public FolderSearchResponse(String id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

}
