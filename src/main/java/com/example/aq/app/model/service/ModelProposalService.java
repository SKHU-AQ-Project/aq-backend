package com.example.aq.app.model.service;

import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.common.exception.UnauthorizedException;
import com.example.aq.app.interaction.domain.LikeType;
import com.example.aq.app.interaction.service.InteractionService;
import com.example.aq.app.model.domain.*;
import com.example.aq.app.model.dto.*;
import com.example.aq.app.model.repository.AIModelRepository;
import com.example.aq.app.model.repository.ModelProposalRepository;
import com.example.aq.app.model.repository.ModelUpdateRequestRepository;
import com.example.aq.app.user.domain.User;
import com.example.aq.app.user.domain.UserRole;
import com.example.aq.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelProposalService {

    private final ModelProposalRepository proposalRepository;
    private final AIModelRepository modelRepository;
    private final ModelUpdateRequestRepository updateRequestRepository;
    private final UserRepository userRepository;
    private final InteractionService interactionService;

    @Value("${app.model.proposal.auto-approve-threshold:10}")
    private Integer autoApproveThreshold;

    // 모델 제안 작성
    @Transactional
    public ModelProposalResponse createProposal(Long userId, ModelProposalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", userId));

        Optional<ModelProposal> existingProposal = proposalRepository.findByNameAndProvider(
                request.getName(), request.getProvider());
        if (existingProposal.isPresent() && existingProposal.get().isPending()) {
            throw new IllegalArgumentException("이미 동일한 모델 제안이 대기 중입니다");
        }

        // AIModel에 이미 존재하는지 체크 (활성화된 모델만)
        Optional<AIModel> existingModel = modelRepository.findByNameAndProvider(
                request.getName(), request.getProvider());
        if (existingModel.isPresent() && Boolean.TRUE.equals(existingModel.get().getActive())) {
            throw new IllegalArgumentException("이미 등록된 모델입니다");
        }

        ModelProposal proposal = ModelProposal.builder()
                .user(user)
                .name(request.getName())
                .provider(request.getProvider())
                .description(request.getDescription())
                .category(request.getCategory())
                .capabilities(request.getCapabilities())
                .inputPricePerToken(request.getInputPricePerToken())
                .outputPricePerToken(request.getOutputPricePerToken())
                .maxTokens(request.getMaxTokens())
                .hasFreeTier(request.getHasFreeTier())
                .apiEndpoint(request.getApiEndpoint())
                .documentationUrl(request.getDocumentationUrl())
                .build();

        proposal = proposalRepository.save(proposal);
        log.info("모델 제안이 생성되었습니다: {} by user {}", proposal.getId(), userId);

        return ModelProposalResponse.of(proposal, false);
    }

    // 모델 제안 목록 조회 (대기중인 제안들)
    @Transactional(readOnly = true)
    public PageResponse<ModelProposalResponse> getPendingProposals(Pageable pageable, Long currentUserId) {
        Page<ModelProposal> proposals = proposalRepository.findByStatusOrderByLikeCountDesc(
                ModelProposalStatus.PENDING, pageable);

        proposals.getContent().forEach(proposal -> {
            proposal.getCapabilities().size(); // 컬렉션 초기화
            proposal.getUser().getNickname(); // User 초기화
        });

        Page<ModelProposalResponse> response = proposals.map(proposal -> {
            Boolean isLiked = currentUserId != null && 
                    interactionService.isLiked(currentUserId, proposal.getId(), LikeType.PROPOSAL);
            return ModelProposalResponse.of(proposal, isLiked);
        });

        return PageResponse.of(response);
    }

    // 승인된 모델 제안 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<ModelProposalResponse> getApprovedProposals(Pageable pageable, Long currentUserId) {
        Page<ModelProposal> proposals = proposalRepository.findByStatusOrderByCreatedAtDesc(
                ModelProposalStatus.APPROVED, pageable);

        proposals.getContent().forEach(proposal -> {
            proposal.getCapabilities().size(); // 컬렉션 초기화
            proposal.getUser().getNickname(); // User 초기화
        });

        Page<ModelProposalResponse> response = proposals.map(proposal -> {
            Boolean isLiked = currentUserId != null && 
                    interactionService.isLiked(currentUserId, proposal.getId(), LikeType.PROPOSAL);
            return ModelProposalResponse.of(proposal, isLiked);
        });

        return PageResponse.of(response);
    }

    // 추천수 많은 제안 조회
    @Transactional(readOnly = true)
    public PageResponse<ModelProposalResponse> getTopProposals(Pageable pageable, Long currentUserId) {
        Page<ModelProposal> proposals = proposalRepository.findPendingWithMinLikeCount(
                ModelProposalStatus.PENDING, 0, pageable);

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposals.getContent().forEach(proposal -> {
            proposal.getCapabilities().size(); // 컬렉션 초기화
            proposal.getUser().getNickname(); // User 초기화
        });

        Page<ModelProposalResponse> response = proposals.map(proposal -> {
            Boolean isLiked = currentUserId != null && 
                    interactionService.isLiked(currentUserId, proposal.getId(), LikeType.PROPOSAL);
            return ModelProposalResponse.of(proposal, isLiked);
        });

        return PageResponse.of(response);
    }

    // 모델 제안 상세 조회
    @Transactional(readOnly = true)
    public ModelProposalResponse getProposal(Long proposalId, Long currentUserId) {
        ModelProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("모델 제안", "id", proposalId));

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposal.getCapabilities().size(); // 컬렉션 초기화
        proposal.getUser().getNickname(); // User 초기화

        Boolean isLiked = currentUserId != null && 
                interactionService.isLiked(currentUserId, proposalId, LikeType.PROPOSAL);

        return ModelProposalResponse.of(proposal, isLiked);
    }

    // 모델 제안 좋아요 토글
    @Transactional
    public ModelProposalResponse toggleLike(Long userId, Long proposalId) {
        ModelProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("모델 제안", "id", proposalId));

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposal.getCapabilities().size(); // 컬렉션 초기화
        proposal.getUser().getNickname(); // User 초기화

        boolean isLiked = interactionService.toggleLike(userId, proposalId, LikeType.PROPOSAL);

        // 좋아요 수 업데이트
        if (isLiked) {
            proposal.incrementLikeCount();
        } else {
            proposal.decrementLikeCount();
        }
        proposal = proposalRepository.save(proposal);

        // 추천수가 임계값 이상이면 자동 승인
        if (proposal.isPending() && proposal.getLikeCount() >= autoApproveThreshold) {
            approveProposal(userId, proposalId, true);
            proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new ResourceNotFoundException("모델 제안", "id", proposalId));
            // 재조회 후에도 초기화 필요
            proposal.getCapabilities().size();
            proposal.getUser().getNickname();
        }

        return ModelProposalResponse.of(proposal, isLiked);
    }

    // 관리자 승인/거절
    @Transactional
    public ModelProposalResponse approveProposal(Long adminId, Long proposalId, boolean autoApprove) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", adminId));

        if (!autoApprove && admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("관리자만 승인할 수 있습니다");
        }

        ModelProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("모델 제안", "id", proposalId));

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposal.getCapabilities().size(); // 컬렉션 초기화
        proposal.getUser().getNickname(); // User 초기화

        if (!proposal.isPending()) {
            throw new IllegalArgumentException("이미 처리된 제안입니다");
        }

        // AIModel 생성
        AIModel model = AIModel.builder()
                .name(proposal.getName())
                .provider(proposal.getProvider())
                .description(proposal.getDescription())
                .category(proposal.getCategory())
                .capabilities(proposal.getCapabilities())
                .inputPricePerToken(proposal.getInputPricePerToken())
                .outputPricePerToken(proposal.getOutputPricePerToken())
                .maxTokens(proposal.getMaxTokens())
                .hasFreeTier(proposal.getHasFreeTier())
                .apiEndpoint(proposal.getApiEndpoint())
                .documentationUrl(proposal.getDocumentationUrl())
                .build();

        model = modelRepository.save(model);

        // 제안 승인 처리
        proposal.approve(adminId);
        proposal.setModelId(model.getId());
        proposal = proposalRepository.save(proposal);

        log.info("모델 제안이 승인되었습니다: {} -> AIModel {}", proposalId, model.getId());

        Boolean isLiked = interactionService.isLiked(adminId, proposalId, LikeType.PROPOSAL);
        return ModelProposalResponse.of(proposal, isLiked);
    }

    // 관리자 거절
    @Transactional
    public ModelProposalResponse rejectProposal(Long adminId, Long proposalId, String reason) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", adminId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("관리자만 거절할 수 있습니다");
        }

        ModelProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("모델 제안", "id", proposalId));

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposal.getCapabilities().size(); // 컬렉션 초기화
        proposal.getUser().getNickname(); // User 초기화

        if (!proposal.isPending()) {
            throw new IllegalArgumentException("이미 처리된 제안입니다");
        }

        proposal.reject(reason);
        proposal = proposalRepository.save(proposal);

        log.info("모델 제안이 거절되었습니다: {} - 이유: {}", proposalId, reason);

        Boolean isLiked = interactionService.isLiked(adminId, proposalId, LikeType.PROPOSAL);
        return ModelProposalResponse.of(proposal, isLiked);
    }

    // 모델 수정 요청 생성
    @Transactional
    public ModelUpdateRequestResponse createUpdateRequest(Long userId, ModelUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", userId));

        AIModel model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("AI 모델", "id", request.getModelId()));

        if (!model.getActive()) {
            throw new IllegalArgumentException("비활성화된 모델은 수정 요청할 수 없습니다");
        }

        ModelUpdateRequest updateRequest = ModelUpdateRequest.builder()
                .user(user)
                .model(model)
                .name(request.getName())
                .provider(request.getProvider())
                .description(request.getDescription())
                .category(request.getCategory())
                .capabilities(request.getCapabilities())
                .inputPricePerToken(request.getInputPricePerToken())
                .outputPricePerToken(request.getOutputPricePerToken())
                .maxTokens(request.getMaxTokens())
                .hasFreeTier(request.getHasFreeTier())
                .apiEndpoint(request.getApiEndpoint())
                .documentationUrl(request.getDocumentationUrl())
                .reason(request.getReason())
                .build();

        updateRequest = updateRequestRepository.save(updateRequest);
        log.info("모델 수정 요청이 생성되었습니다: {} for model {} by user {}", 
                updateRequest.getId(), request.getModelId(), userId);

        return ModelUpdateRequestResponse.of(updateRequest);
    }

    // 수정 요청 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<ModelUpdateRequestResponse> getUpdateRequests(Pageable pageable) {
        Page<ModelUpdateRequest> requests = updateRequestRepository.findByStatus(
                UpdateRequestStatus.PENDING, pageable);

        return PageResponse.of(requests.map(ModelUpdateRequestResponse::of));
    }

    // 수정 요청 승인/거절
    @Transactional
    public ModelUpdateRequestResponse processUpdateRequest(Long adminId, Long requestId, boolean approve) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", adminId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("관리자만 처리할 수 있습니다");
        }

        ModelUpdateRequest updateRequest = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("수정 요청", "id", requestId));

        if (!updateRequest.isPending()) {
            throw new IllegalArgumentException("이미 처리된 요청입니다");
        }

        if (approve) {
            updateRequest.approve(adminId);
            // AIModel 업데이트
            AIModel model = updateRequest.getModel();
            model.updateModel(
                    updateRequest.getName(),
                    updateRequest.getDescription(),
                    updateRequest.getCapabilities(),
                    updateRequest.getInputPricePerToken(),
                    updateRequest.getOutputPricePerToken(),
                    updateRequest.getMaxTokens(),
                    updateRequest.getHasFreeTier(),
                    updateRequest.getApiEndpoint(),
                    updateRequest.getDocumentationUrl()
            );
            modelRepository.save(model);
            log.info("모델 수정 요청이 승인되었습니다: {} -> Model {}", requestId, model.getId());
        } else {
            updateRequest.reject(adminId);
            log.info("모델 수정 요청이 거절되었습니다: {}", requestId);
        }

        updateRequest = updateRequestRepository.save(updateRequest);
        return ModelUpdateRequestResponse.of(updateRequest);
    }

    // 검색
    @Transactional(readOnly = true)
    public PageResponse<ModelProposalResponse> searchProposals(String keyword, Pageable pageable, Long currentUserId) {
        Page<ModelProposal> proposals = proposalRepository.searchByKeyword(
                ModelProposalStatus.PENDING, keyword, pageable);

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposals.getContent().forEach(proposal -> {
            proposal.getCapabilities().size(); // 컬렉션 초기화
            proposal.getUser().getNickname(); // User 초기화
        });

        Page<ModelProposalResponse> response = proposals.map(proposal -> {
            Boolean isLiked = currentUserId != null && 
                    interactionService.isLiked(currentUserId, proposal.getId(), LikeType.PROPOSAL);
            return ModelProposalResponse.of(proposal, isLiked);
        });

        return PageResponse.of(response);
    }

    // 내 제안 조회
    @Transactional(readOnly = true)
    public PageResponse<ModelProposalResponse> getMyProposals(Long userId, Pageable pageable) {
        // 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자", "id", userId));

        Page<ModelProposal> proposals = proposalRepository.findByUserId(userId, pageable);

        // capabilities 컬렉션을 미리 초기화하여 LazyInitializationException 방지
        proposals.getContent().forEach(proposal -> {
            proposal.getCapabilities().size(); // 컬렉션 초기화
            proposal.getUser().getNickname(); // User 초기화
        });

        Page<ModelProposalResponse> response = proposals.map(proposal -> {
            Boolean isLiked = interactionService.isLiked(userId, proposal.getId(), LikeType.PROPOSAL);
            return ModelProposalResponse.of(proposal, isLiked);
        });

        return PageResponse.of(response);
    }
}

