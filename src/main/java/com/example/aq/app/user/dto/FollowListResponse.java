package com.example.aq.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowListResponse {
    private List<FollowUserResponse> users;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;

    public static FollowListResponse of(List<FollowUserResponse> users, int currentPage, 
                                       int totalPages, long totalElements, boolean hasNext) {
        return FollowListResponse.builder()
                .users(users)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .hasNext(hasNext)
                .build();
    }
}

