package com.example.aq.app.model.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.dto.PageResponse;
import com.example.aq.app.model.domain.ModelCategory;
import com.example.aq.app.model.dto.ModelResponse;
import com.example.aq.app.model.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI 모델", description = "AI 모델 정보 관련 API")
public class ModelController {

    private final ModelService modelService;

    @GetMapping
    @Operation(summary = "AI 모델 목록 조회", description = "AI 모델 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getModels(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.getModels(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/top-rated")
    @Operation(summary = "높은 평점 모델 조회", description = "평점 기준으로 높은 평점 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getTopRatedModels(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ModelResponse> response = modelService.getTopRatedModels(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/most-reviewed")
    @Operation(summary = "리뷰가 많은 모델 조회", description = "리뷰 수 기준으로 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getMostReviewedModels(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ModelResponse> response = modelService.getMostReviewedModels(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/free-tier")
    @Operation(summary = "무료 티어 모델 조회", description = "무료 티어가 있는 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getFreeTierModels(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.getFreeTierModels(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "모델 검색", description = "키워드로 모델을 검색합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> searchModels(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.searchModels(keyword, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 모델 조회", description = "카테고리별로 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getModelsByCategory(
            @Parameter(description = "카테고리") @PathVariable ModelCategory category,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.getModelsByCategory(category, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/provider/{provider}")
    @Operation(summary = "제공업체별 모델 조회", description = "제공업체별로 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getModelsByProvider(
            @Parameter(description = "제공업체") @PathVariable String provider,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.getModelsByProvider(provider, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/capability/{capability}")
    @Operation(summary = "기능별 모델 조회", description = "특정 기능을 지원하는 모델을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelResponse>>> getModelsByCapability(
            @Parameter(description = "기능") @PathVariable String capability,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        PageResponse<ModelResponse> response = modelService.getModelsByCapability(capability, pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/providers")
    @Operation(summary = "제공업체 목록 조회", description = "모든 제공업체 목록을 조회합니다")
    public ResponseEntity<BaseResponse<List<String>>> getAllProviders() {
        List<String> providers = modelService.getAllProviders();
        return ResponseEntity.ok(BaseResponse.success(providers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "모델 상세 조회", description = "특정 모델의 상세 정보를 조회합니다")
    public ResponseEntity<BaseResponse<ModelResponse>> getModel(
            @Parameter(description = "모델 ID") @PathVariable Long id) {
        
        ModelResponse response = modelService.getModel(id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}

