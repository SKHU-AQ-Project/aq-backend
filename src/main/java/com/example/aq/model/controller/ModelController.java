package com.example.aq.model.controller;

import com.example.aq.domain.model.ModelCategory;
import com.example.aq.model.dto.ModelResponse;
import com.example.aq.model.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Tag(name = "AI 모델", description = "AI 모델 관련 API")
public class ModelController {

    private final ModelService modelService;

    @GetMapping("/{modelId}")
    @Operation(summary = "모델 상세 조회", description = "특정 AI 모델의 상세 정보를 조회합니다")
    public ResponseEntity<ModelResponse> getModel(
            @PathVariable Long modelId,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        ModelResponse response = modelService.getModel(modelId, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "모델 목록 조회", description = "전체 AI 모델 목록을 조회합니다")
    public ResponseEntity<Page<ModelResponse>> getModels(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ModelResponse> response = modelService.getModels(pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 모델 조회", description = "특정 카테고리의 AI 모델 목록을 조회합니다")
    public ResponseEntity<Page<ModelResponse>> getModelsByCategory(
            @PathVariable ModelCategory category,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ModelResponse> response = modelService.getModelsByCategory(category, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "모델 검색", description = "키워드로 AI 모델을 검색합니다")
    public ResponseEntity<Page<ModelResponse>> searchModels(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        Page<ModelResponse> response = modelService.searchModels(keyword, pageable, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/free-tier")
    @Operation(summary = "무료 체험 모델 조회", description = "무료 체험이 가능한 AI 모델 목록을 조회합니다")
    public ResponseEntity<List<ModelResponse>> getModelsWithFreeTier(Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        List<ModelResponse> response = modelService.getModelsWithFreeTier(userEmail);
        return ResponseEntity.ok(response);
    }
}
