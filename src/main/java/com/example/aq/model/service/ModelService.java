package com.example.aq.model.service;

import com.example.aq.domain.interaction.BookmarkRepository;
import com.example.aq.domain.interaction.BookmarkType;
import com.example.aq.domain.model.AIModel;
import com.example.aq.domain.model.AIModelRepository;
import com.example.aq.domain.model.ModelCategory;
import com.example.aq.domain.model.ModelStatus;
import com.example.aq.domain.review.ReviewRepository;
import com.example.aq.domain.user.User;
import com.example.aq.domain.user.UserRepository;
import com.example.aq.model.dto.ModelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelService {

    private final AIModelRepository modelRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public ModelResponse getModel(Long modelId, String userEmail) {
        AIModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("모델을 찾을 수 없습니다"));

        if (model.getStatus() != ModelStatus.ACTIVE) {
            throw new RuntimeException("비활성화된 모델입니다");
        }

        Double averageRating = reviewRepository.getAverageRatingByModel(model);
        Long reviewCount = reviewRepository.getReviewCountByModel(model);

        Boolean isBookmarked = false;
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(user, BookmarkType.MODEL, modelId);
            }
        }

        return convertToModelResponse(model, averageRating, reviewCount, isBookmarked);
    }

    public Page<ModelResponse> getModels(Pageable pageable, String userEmail) {
        List<AIModel> models = modelRepository.findByStatus(ModelStatus.ACTIVE);
        return Page.empty(); // TODO: 구현 필요
    }

    public Page<ModelResponse> getModelsByCategory(ModelCategory category, Pageable pageable, String userEmail) {
        List<AIModel> models = modelRepository.findByCategory(category);
        return Page.empty(); // TODO: 구현 필요
    }

    public Page<ModelResponse> searchModels(String keyword, Pageable pageable, String userEmail) {
        return modelRepository.searchByNameOrProvider(keyword, pageable)
                .map(model -> {
                    Double averageRating = reviewRepository.getAverageRatingByModel(model);
                    Long reviewCount = reviewRepository.getReviewCountByModel(model);
                    
                    Boolean isBookmarked = false;
                    if (userEmail != null) {
                        User user = userRepository.findByEmail(userEmail).orElse(null);
                        if (user != null) {
                            isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(user, BookmarkType.MODEL, model.getId());
                        }
                    }
                    
                    return convertToModelResponse(model, averageRating, reviewCount, isBookmarked);
                });
    }

    public List<ModelResponse> getModelsWithFreeTier(String userEmail) {
        List<AIModel> models = modelRepository.findModelsWithFreeTier();
        return models.stream()
                .map(model -> {
                    Double averageRating = reviewRepository.getAverageRatingByModel(model);
                    Long reviewCount = reviewRepository.getReviewCountByModel(model);
                    
                    Boolean isBookmarked = false;
                    if (userEmail != null) {
                        User user = userRepository.findByEmail(userEmail).orElse(null);
                        if (user != null) {
                            isBookmarked = bookmarkRepository.existsByUserAndTypeAndTargetId(user, BookmarkType.MODEL, model.getId());
                        }
                    }
                    
                    return convertToModelResponse(model, averageRating, reviewCount, isBookmarked);
                })
                .toList();
    }

    private ModelResponse convertToModelResponse(AIModel model, Double averageRating, Long reviewCount, Boolean isBookmarked) {
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
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .isBookmarked(isBookmarked)
                .build();
    }
}
