package com.huy.pdoc.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class DeleteListDocumentsRequest {
    private List<String> documentsId;
}
