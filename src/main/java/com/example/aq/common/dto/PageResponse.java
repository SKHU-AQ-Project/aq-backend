package com.example.aq.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int number; // 프론트엔드와 호환성을 위해 추가
    private int size; // 프론트엔드와 호환성을 위해 추가
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private boolean empty;

    @Builder
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements,
                       int totalPages, boolean first, boolean last, int numberOfElements, boolean empty) {
        this.content = content;
        this.number = pageNumber; // 프론트엔드 호환성
        this.size = pageSize; // 프론트엔드 호환성
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }
}
