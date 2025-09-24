-- AI 모델 비교 및 프롬프트 공유 플랫폼 초기 데이터

-- 사용자 역할 데이터
INSERT INTO user_role (id, name, description) VALUES
(1, 'USER', '일반 사용자'),
(2, 'ADMIN', '관리자'),
(3, 'MODERATOR', '모더레이터')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 사용자 상태 데이터
INSERT INTO user_status (id, name, description) VALUES
(1, 'ACTIVE', '활성 사용자'),
(2, 'INACTIVE', '비활성 사용자'),
(3, 'SUSPENDED', '정지된 사용자'),
(4, 'PENDING', '승인 대기 중')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- AI 모델 카테고리 데이터
INSERT INTO model_category (id, name, description) VALUES
(1, 'TEXT_GENERATION', '텍스트 생성'),
(2, 'CODE_GENERATION', '코드 생성'),
(3, 'TRANSLATION', '번역'),
(4, 'SUMMARIZATION', '요약'),
(5, 'QUESTION_ANSWERING', '질문 답변'),
(6, 'CREATIVE_WRITING', '창작 글쓰기'),
(7, 'ANALYSIS', '분석'),
(8, 'CONVERSATION', '대화')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- AI 모델 상태 데이터
INSERT INTO model_status (id, name, description) VALUES
(1, 'ACTIVE', '활성 모델'),
(2, 'INACTIVE', '비활성 모델'),
(3, 'BETA', '베타 모델'),
(4, 'DEPRECATED', '사용 중단된 모델')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 리뷰 상태 데이터
INSERT INTO review_status (id, name, description) VALUES
(1, 'PUBLISHED', '게시됨'),
(2, 'DRAFT', '초안'),
(3, 'PENDING', '승인 대기'),
(4, 'REJECTED', '거부됨'),
(5, 'HIDDEN', '숨김')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 레시피 카테고리 데이터
INSERT INTO recipe_category (id, name, description) VALUES
(1, 'PRODUCTIVITY', '생산성'),
(2, 'CREATIVE', '창작'),
(3, 'EDUCATION', '교육'),
(4, 'BUSINESS', '비즈니스'),
(5, 'TECHNICAL', '기술'),
(6, 'ENTERTAINMENT', '엔터테인먼트'),
(7, 'RESEARCH', '연구'),
(8, 'COMMUNICATION', '커뮤니케이션')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 레시피 상태 데이터
INSERT INTO recipe_status (id, name, description) VALUES
(1, 'PUBLISHED', '게시됨'),
(2, 'DRAFT', '초안'),
(3, 'PENDING', '승인 대기'),
(4, 'REJECTED', '거부됨'),
(5, 'HIDDEN', '숨김')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 댓글 타입 데이터
INSERT INTO comment_type (id, name, description) VALUES
(1, 'REVIEW', '리뷰 댓글'),
(2, 'RECIPE', '레시피 댓글'),
(3, 'REPLY', '답글')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 댓글 상태 데이터
INSERT INTO comment_status (id, name, description) VALUES
(1, 'ACTIVE', '활성 댓글'),
(2, 'HIDDEN', '숨김 댓글'),
(3, 'DELETED', '삭제된 댓글')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 좋아요 타입 데이터
INSERT INTO like_type (id, name, description) VALUES
(1, 'REVIEW', '리뷰 좋아요'),
(2, 'RECIPE', '레시피 좋아요'),
(3, 'COMMENT', '댓글 좋아요')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 북마크 타입 데이터
INSERT INTO bookmark_type (id, name, description) VALUES
(1, 'REVIEW', '리뷰 북마크'),
(2, 'RECIPE', '레시피 북마크'),
(3, 'MODEL', '모델 북마크')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 샘플 AI 모델 데이터
INSERT INTO ai_model (id, name, description, category_id, status_id, provider, model_version, pricing_info, capabilities, created_at, updated_at) VALUES
(1, 'GPT-4', 'OpenAI의 최신 대화형 AI 모델', 1, 1, 'OpenAI', '4.0', '{"input": "$0.03/1K tokens", "output": "$0.06/1K tokens"}', '["text_generation", "code_generation", "analysis"]', NOW(), NOW()),
(2, 'Claude 3', 'Anthropic의 고급 AI 어시스턴트', 1, 1, 'Anthropic', '3.0', '{"input": "$0.015/1K tokens", "output": "$0.075/1K tokens"}', '["text_generation", "analysis", "creative_writing"]', NOW(), NOW()),
(3, 'Gemini Pro', 'Google의 멀티모달 AI 모델', 1, 1, 'Google', '1.0', '{"input": "$0.0005/1K tokens", "output": "$0.0015/1K tokens"}', '["text_generation", "multimodal", "translation"]', NOW(), NOW()),
(4, 'Codex', 'GitHub의 코드 생성 AI', 2, 1, 'OpenAI', '1.0', '{"usage": "$0.02/1K tokens"}', '["code_generation", "code_completion"]', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 샘플 사용자 데이터 (테스트용)
INSERT INTO user (id, email, password, username, display_name, bio, profile_image_url, role_id, status_id, points, level, created_at, updated_at) VALUES
(1, 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin', '관리자', '시스템 관리자입니다.', null, 2, 1, 1000, 10, NOW(), NOW()),
(2, 'user@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user', '일반사용자', 'AI 모델을 좋아하는 사용자입니다.', null, 1, 1, 100, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE email = VALUES(email);
