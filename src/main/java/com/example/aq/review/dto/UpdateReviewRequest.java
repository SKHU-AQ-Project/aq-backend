package com.example.aq.review.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 5000, message = "내용은 5000자 이하여야 합니다")
    private String content;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    private Integer rating;

    private String useCase;

    @Size(max = 2000, message = "입력 예시는 2000자 이하여야 합니다")
    private String inputExample;

    @Size(max = 2000, message = "출력 예시는 2000자 이하여야 합니다")
    private String outputExample;

    private List<String> tags;

    private String screenshotUrl;
}
