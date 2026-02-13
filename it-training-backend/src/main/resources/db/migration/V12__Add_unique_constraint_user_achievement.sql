-- 防止并发场景下重复授予成就
-- 先删除可能存在的重复数据（保留最早获得的记录）
DELETE ua1 FROM user_achievement ua1
INNER JOIN user_achievement ua2
  ON ua1.user_id = ua2.user_id
  AND ua1.achievement_id = ua2.achievement_id
  AND ua1.id > ua2.id;

-- 添加唯一约束
ALTER TABLE user_achievement ADD UNIQUE KEY uk_user_achievement (user_id, achievement_id);
