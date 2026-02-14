-- V16: 打卡表添加唯一约束，防止并发重复打卡
-- 先删除可能存在的重复数据（保留最早的一条）
DELETE sc1 FROM study_checkin sc1
INNER JOIN study_checkin sc2
ON sc1.user_id = sc2.user_id
AND sc1.checkin_date = sc2.checkin_date
AND sc1.id > sc2.id;

-- 添加唯一约束
ALTER TABLE study_checkin
    ADD CONSTRAINT uk_user_checkin_date UNIQUE (user_id, checkin_date);
