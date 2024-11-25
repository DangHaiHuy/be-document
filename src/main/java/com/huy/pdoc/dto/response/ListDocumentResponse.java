package com.huy.pdoc.dto.response;

import java.util.List;

import com.huy.pdoc.entity.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ListDocumentResponse {
    private List<Document> items;
    private long total;
    private boolean canUpdate;
}
