-- 将所有"未开放"状态的班期更新为"报名中"状态
-- 这样可以让已存在的班期立即可以报名

UPDATE class_session 
SET status = 1  -- ENROLLING (报名中)
WHERE status = 0  -- NOT_OPEN (未开放)
  AND deleted = 0
  AND end_date >= CURDATE()  -- 只更新未结束的班期
  AND current_enrollment < max_capacity;  -- 只更新未满员的班期

-- 记录更新的班期数量
SELECT CONCAT('已更新 ', ROW_COUNT(), ' 个班期状态为"报名中"') AS result;
