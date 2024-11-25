package com.huy.pdoc.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ListResponse<T> {
    private List<T> items;
    private long total;

}
