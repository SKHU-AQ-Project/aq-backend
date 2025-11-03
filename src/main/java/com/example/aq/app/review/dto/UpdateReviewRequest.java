package com.example.aq.app.review.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateReviewRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다")
    @Size(max = 5000, message = "내용은 5000자를 초과할 수 없습니다")
    private String content;
    
    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    private Integer rating;
    
    @Size(max = 100, message = "사용 사례는 100자를 초과할 수 없습니다")
    private String useCase;
    
    @Size(max = 2000, message = "입력 예시는 2000자를 초과할 수 없습니다")
    private String inputExample;
    
    @Size(max = 2000, message = "출력 예시는 2000자를 초과할 수 없습니다")
    private String outputExample;
    
    private List<String> tags;
    
    @Size(max = 500, message = "스크린샷 URL은 500자를 초과할 수 없습니다")
    private String screenshotUrl;

    @Builder
    public UpdateReviewRequest(String title, String content, Integer rating,
                              String useCase, String inputExample, String outputExample,
                              List<String> tags, String screenshotUrl) {
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.useCase = useCase;
        this.inputExample = inputExample;
        this.outputExample = outputExample;
        this.tags = tags;
        this.screenshotUrl = screenshotUrl;
    }
}
