package com.example.aq.app.model.service;

import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.model.domain.ModelCategory;
import com.example.aq.app.model.dto.ModelResponse;
import com.example.aq.app.model.repository.AIModelRepository;
import com.example.aq.app.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ModelService {

    private final AIModelRepository aiModelRepository;
    private final ReviewRepository reviewRepository;

    public PageResponse<ModelResponse> getModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findAllActive(pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getTopRatedModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findTopRatedModels(pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getMostReviewedModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findMostReviewedModels(pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getFreeTierModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findFreeTierModels(pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> searchModels(String keyword, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.searchModels(keyword, pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByCategory(ModelCategory category, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByCategory(category, pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByProvider(String provider, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByProvider(provider, pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByCapability(String capability, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByCapability(capability, pageable);
        // Lazy 컬렉션 초기화
        models.forEach(model -> model.getCapabilities().size());
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public ModelResponse getModel(Long id) {
        AIModel model = aiModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI 모델", "id", id));
        
        if (!model.getActive()) {
            throw new ResourceNotFoundException("AI 모델", "id", id);
        }
        
        // Lazy 컬렉션 초기화
        model.getCapabilities().size();
        
        // 실제 활성화된 리뷰 개수 계산
        Long actualReviewCount = reviewRepository.countByModel(model);
        
        // 실제 개수와 저장된 개수가 다르면 로그 출력
        if (!actualReviewCount.equals(model.getReviewCount().longValue())) {
            log.warn("모델 {}의 reviewCount 불일치: DB={}, 실제={}", 
                    model.getId(), model.getReviewCount(), actualReviewCount);
        }
        
        // ModelResponse 생성 시 실제 리뷰 개수 사용
        return ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .provider(model.getProvider())
                .description(model.getDescription())
                .category(model.getCategory())
                .capabilities(model.getCapabilities())
                .inputPricePerToken(model.getInputPricePerToken())
                .outputPricePerToken(model.getOutputPricePerToken())
                .maxTokens(model.getMaxTokens())
                .hasFreeTier(model.getHasFreeTier())
                .apiEndpoint(model.getApiEndpoint())
                .documentationUrl(model.getDocumentationUrl())
                .averageRating(model.getAverageRating())
                .reviewCount(actualReviewCount.intValue())  // 실제 리뷰 개수 사용
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public List<String> getAllProviders() {
        return aiModelRepository.findAllProviders();
    }

    /**
     * 모델의 reviewCount를 실제 활성화된 리뷰 개수와 동기화합니다.
     */
    @Transactional
    public void syncReviewCount(Long modelId) {
        AIModel model = aiModelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("AI 모델", "id", modelId));
        
        Long actualReviewCount = reviewRepository.countByModel(model);
        
        if (!actualReviewCount.equals(model.getReviewCount().longValue())) {
            log.info("모델 {}의 reviewCount 동기화: {} -> {}", 
                    modelId, model.getReviewCount(), actualReviewCount);
            
            model.setReviewCount(actualReviewCount.intValue());
            aiModelRepository.save(model);
        }
    }

    /**
     * 모든 모델의 reviewCount를 동기화합니다.
     */
    @Transactional
    public void syncAllReviewCounts() {
        List<AIModel> models = aiModelRepository.findAll();
        int syncedCount = 0;
        
        for (AIModel model : models) {
            Long actualReviewCount = reviewRepository.countByModel(model);
            
            if (!actualReviewCount.equals(model.getReviewCount().longValue())) {
                log.info("모델 {}의 reviewCount 동기화: {} -> {}", 
                        model.getId(), model.getReviewCount(), actualReviewCount);
                
                model.setReviewCount(actualReviewCount.intValue());
                syncedCount++;
            }
        }
        
        aiModelRepository.saveAll(models);
        log.info("총 {}개 모델의 reviewCount가 동기화되었습니다.", syncedCount);
    }
}

