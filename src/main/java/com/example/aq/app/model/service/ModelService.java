package com.example.aq.app.model.service;

import com.example.aq.common.dto.PageResponse;
import com.example.aq.common.exception.ResourceNotFoundException;
import com.example.aq.app.model.domain.AIModel;
import com.example.aq.app.model.domain.ModelCategory;
import com.example.aq.app.model.dto.ModelResponse;
import com.example.aq.app.model.repository.AIModelRepository;
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

    public PageResponse<ModelResponse> getModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findAllActive(pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getTopRatedModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findTopRatedModels(pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getMostReviewedModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findMostReviewedModels(pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getFreeTierModels(Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findFreeTierModels(pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> searchModels(String keyword, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.searchModels(keyword, pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByCategory(ModelCategory category, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByCategory(category, pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByProvider(String provider, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByProvider(provider, pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public PageResponse<ModelResponse> getModelsByCapability(String capability, Pageable pageable) {
        Page<AIModel> models = aiModelRepository.findByCapability(capability, pageable);
        return PageResponse.of(models.map(ModelResponse::of));
    }

    public ModelResponse getModel(Long id) {
        AIModel model = aiModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI 모델", "id", id));
        
        if (!model.getActive()) {
            throw new ResourceNotFoundException("AI 모델", "id", id);
        }
        
        return ModelResponse.of(model);
    }

    public List<String> getAllProviders() {
        return aiModelRepository.findAllProviders();
    }
}

