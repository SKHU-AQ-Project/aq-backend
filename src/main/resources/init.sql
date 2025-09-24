-- AI 모델 비교 및 프롬프트 공유 플랫폼 초기 데이터

-- 사용자 역할과 상태는 JPA enum으로 관리됩니다

-- AI 모델 카테고리와 상태는 JPA enum으로 관리됩니다

-- 리뷰 상태, 레시피 카테고리, 레시피 상태는 JPA enum으로 관리됩니다

-- 댓글 타입, 댓글 상태, 좋아요 타입, 북마크 타입은 JPA enum으로 관리됩니다

-- 샘플 AI 모델 데이터
INSERT INTO ai_models (id, name, provider, description, category, input_price_per_token, output_price_per_token, max_tokens, has_free_tier, api_endpoint, documentation_url, status, created_at, updated_at) VALUES
(1, 'GPT-4', 'OpenAI', 'OpenAI의 최신 대화형 AI 모델', 'TEXT_GENERATION', 0.00003, 0.00006, 8192, true, 'https://api.openai.com/v1/chat/completions', 'https://platform.openai.com/docs/models/gpt-4', 'ACTIVE', NOW(), NOW()),
(2, 'Claude 3', 'Anthropic', 'Anthropic의 고급 AI 어시스턴트', 'TEXT_GENERATION', 0.000015, 0.000075, 200000, true, 'https://api.anthropic.com/v1/messages', 'https://docs.anthropic.com/claude/reference', 'ACTIVE', NOW(), NOW()),
(3, 'Gemini Pro', 'Google', 'Google의 멀티모달 AI 모델', 'MULTIMODAL', 0.0000005, 0.0000015, 32768, true, 'https://generativelanguage.googleapis.com/v1beta/models', 'https://ai.google.dev/docs', 'ACTIVE', NOW(), NOW()),
(4, 'Codex', 'OpenAI', 'GitHub의 코드 생성 AI', 'CODE_GENERATION', 0.00002, 0.00002, 4096, false, 'https://api.openai.com/v1/completions', 'https://platform.openai.com/docs/models/codex', 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- AI 모델 capabilities 데이터
INSERT INTO model_capabilities (model_id, capability) VALUES
(1, 'text_generation'),
(1, 'code_generation'),
(1, 'analysis'),
(2, 'text_generation'),
(2, 'analysis'),
(2, 'creative_writing'),
(3, 'text_generation'),
(3, 'multimodal'),
(3, 'translation'),
(4, 'code_generation'),
(4, 'code_completion')
ON DUPLICATE KEY UPDATE capability = VALUES(capability);

-- 샘플 사용자 데이터 (테스트용)
INSERT INTO users (id, email, password, nickname, bio, profile_image, role, status, points, level, created_at, updated_at) VALUES
(1, 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin', '시스템 관리자입니다.', null, 'ADMIN', 'ACTIVE', 1000, 10, NOW(), NOW()),
(2, 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user', 'AI 모델을 좋아하는 사용자입니다.', null, 'USER', 'ACTIVE', 100, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE email = VALUES(email);
