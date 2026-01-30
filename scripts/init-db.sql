-- Devtory AI MSA Database Initialization Script
-- 각 마이크로서비스별 독립 데이터베이스 생성

-- ==================== 사용자 생성 ====================
-- devtory_user도 지원 (하위 호환성)
CREATE USER IF NOT EXISTS 'devtory_user'@'%' IDENTIFIED BY 'd@vt0ry6868';

CREATE DATABASE IF NOT EXISTS devtory3;

-- ==================== 권한 부여 ====================
GRANT ALL PRIVILEGES ON devtory3.* TO 'devtory_user'@'%';

FLUSH PRIVILEGES;

-- 생성된 데이터베이스 확인
SHOW DATABASES;
