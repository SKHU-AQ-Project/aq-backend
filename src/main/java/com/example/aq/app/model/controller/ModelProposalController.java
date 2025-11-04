package com.example.aq.app.model.controller;

import com.example.aq.common.dto.BaseResponse;
import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.util.SecurityUtil;
import com.example.aq.app.model.dto.*;
import com.example.aq.app.model.service.ModelProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model-proposals")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "모델 제안", description = "AI 모델 제안 및 승인 관련 API")
public class ModelProposalController {

    private final ModelProposalService proposalService;

    @PostMapping
    @Operation(summary = "모델 제안 작성", description = "새로운 AI 모델 정보를 제안합니다")
    public ResponseEntity<BaseResponse<ModelProposalResponse>> createProposal(
            @Valid @RequestBody ModelProposalRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ModelProposalResponse response = proposalService.createProposal(userId, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/pending")
    @Operation(summary = "대기중인 제안 목록 조회", description = "승인 대기중인 모델 제안 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelProposalResponse>>> getPendingProposals(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        Long currentUserId = SecurityUtil.isAuthenticated() ? SecurityUtil.getCurrentUserId() : null;
        PageResponse<ModelProposalResponse> response = proposalService.getPendingProposals(pageable, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/approved")
    @Operation(summary = "승인된 제안 목록 조회", description = "승인되어 정식 등록된 모델 제안 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelProposalResponse>>> getApprovedProposals(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Long currentUserId = SecurityUtil.isAuthenticated() ? SecurityUtil.getCurrentUserId() : null;
        PageResponse<ModelProposalResponse> response = proposalService.getApprovedProposals(pageable, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/top")
    @Operation(summary = "인기 제안 조회", description = "추천수가 많은 모델 제안을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelProposalResponse>>> getTopProposals(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        Long currentUserId = SecurityUtil.isAuthenticated() ? SecurityUtil.getCurrentUserId() : null;
        PageResponse<ModelProposalResponse> response = proposalService.getTopProposals(pageable, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "제안 상세 조회", description = "특정 모델 제안의 상세 정보를 조회합니다")
    public ResponseEntity<BaseResponse<ModelProposalResponse>> getProposal(
            @Parameter(description = "제안 ID") @PathVariable Long id) {
        
        Long currentUserId = SecurityUtil.isAuthenticated() ? SecurityUtil.getCurrentUserId() : null;
        ModelProposalResponse response = proposalService.getProposal(id, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "제안 좋아요", description = "모델 제안에 좋아요를 누르거나 취소합니다")
    public ResponseEntity<BaseResponse<ModelProposalResponse>> toggleLike(
            @Parameter(description = "제안 ID") @PathVariable Long id) {
        
        Long userId = SecurityUtil.getCurrentUserId();
        ModelProposalResponse response = proposalService.toggleLike(userId, id);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "제안 승인 (관리자)", description = "모델 제안을 승인하여 정식 모델로 등록합니다")
    public ResponseEntity<BaseResponse<ModelProposalResponse>> approveProposal(
            @Parameter(description = "제안 ID") @PathVariable Long id) {
        
        Long adminId = SecurityUtil.getCurrentUserId();
        ModelProposalResponse response = proposalService.approveProposal(adminId, id, false);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "제안 거절 (관리자)", description = "모델 제안을 거절합니다")
    public ResponseEntity<BaseResponse<ModelProposalResponse>> rejectProposal(
            @Parameter(description = "제안 ID") @PathVariable Long id,
            @Valid @RequestBody ProposalApprovalRequest request) {
        
        Long adminId = SecurityUtil.getCurrentUserId();
        ModelProposalResponse response = proposalService.rejectProposal(adminId, id, request.getRejectionReason());
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "제안 검색", description = "키워드로 모델 제안을 검색합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelProposalResponse>>> searchProposals(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount"));
        Long currentUserId = SecurityUtil.isAuthenticated() ? SecurityUtil.getCurrentUserId() : null;
        PageResponse<ModelProposalResponse> response = proposalService.searchProposals(keyword, pageable, currentUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/update-requests")
    @Operation(summary = "모델 수정 요청", description = "등록된 모델의 정보 수정을 요청합니다")
    public ResponseEntity<BaseResponse<ModelUpdateRequestResponse>> createUpdateRequest(
            @Valid @RequestBody ModelUpdateRequestDto request) {
        
        Long userId = SecurityUtil.getCurrentUserId();
        ModelUpdateRequestResponse response = proposalService.createUpdateRequest(userId, request);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/update-requests")
    @Operation(summary = "수정 요청 목록 조회 (관리자)", description = "대기중인 모델 수정 요청 목록을 조회합니다")
    public ResponseEntity<BaseResponse<PageResponse<ModelUpdateRequestResponse>>> getUpdateRequests(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ModelUpdateRequestResponse> response = proposalService.getUpdateRequests(pageable);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/update-requests/{id}/approve")
    @Operation(summary = "수정 요청 승인 (관리자)", description = "모델 수정 요청을 승인합니다")
    public ResponseEntity<BaseResponse<ModelUpdateRequestResponse>> approveUpdateRequest(
            @Parameter(description = "요청 ID") @PathVariable Long id) {
        
        Long adminId = SecurityUtil.getCurrentUserId();
        ModelUpdateRequestResponse response = proposalService.processUpdateRequest(adminId, id, true);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @PostMapping("/update-requests/{id}/reject")
    @Operation(summary = "수정 요청 거절 (관리자)", description = "모델 수정 요청을 거절합니다")
    public ResponseEntity<BaseResponse<ModelUpdateRequestResponse>> rejectUpdateRequest(
            @Parameter(description = "요청 ID") @PathVariable Long id) {
        
        Long adminId = SecurityUtil.getCurrentUserId();
        ModelUpdateRequestResponse response = proposalService.processUpdateRequest(adminId, id, false);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}

