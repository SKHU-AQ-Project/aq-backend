package com.example.aq.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long modelId;
    private String modelName;
    private String modelProvider;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;
    private String title;
    private String content;
    private Integer rating;
    private String useCase;
    private String inputExample;
    private String outputExample;
    private List<String> tags;
    private String screenshotUrl;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLiked;
    private Boolean isBookmarked;
}
