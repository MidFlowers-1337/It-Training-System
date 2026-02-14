package com.itts.common.util;

import java.util.Map;

/**
 * 等级和难度转换工具类
 * 统一管理学习等级、课程难度、课程分类的计算和转换逻辑
 *
 * [Phase 1.1] 从 common.utils 迁移到 common.util，统一包路径
 * [Phase 1.3] 扩展 getCategoryName()，覆盖所有分类（PROGRAMMING, AI_ML, DEVOPS, SECURITY 等）
 */
public final class LevelDifficultyUtils {

    private LevelDifficultyUtils() {
        // 工具类禁止实例化
    }

    // ==================== 学习等级相关 ====================

    /**
     * 等级名称数组
     */
    private static final String[] LEVEL_NAMES = {
        "学习新手", "初级学员", "中级学员", "高级学员",
        "学习达人", "学习专家", "学习大师", "学习宗师"
    };

    /**
     * 等级经验值阈值 (分钟)
     */
    private static final int[] LEVEL_MINUTES_THRESHOLDS = {
        0, 60, 300, 1000, 3000, 6000, 12000, 24000
    };

    /**
     * 根据学习时长计算等级 (1-8级)
     *
     * @param totalMinutes 总学习分钟数
     * @return 等级 (1-8)
     */
    public static int calculateLevelByMinutes(Integer totalMinutes) {
        if (totalMinutes == null || totalMinutes < 0) {
            return 1;
        }
        for (int level = LEVEL_MINUTES_THRESHOLDS.length - 1; level >= 0; level--) {
            if (totalMinutes >= LEVEL_MINUTES_THRESHOLDS[level]) {
                return level + 1;
            }
        }
        return 1;
    }

    /**
     * 获取等级名称
     *
     * @param level 等级 (1-8)
     * @return 等级名称
     */
    public static String getLevelName(Integer level) {
        if (level == null || level < 1) {
            return LEVEL_NAMES[0];
        }
        if (level > LEVEL_NAMES.length) {
            return LEVEL_NAMES[LEVEL_NAMES.length - 1];
        }
        return LEVEL_NAMES[level - 1];
    }

    /**
     * 获取下一级所需学习分钟数
     *
     * @param currentLevel 当前等级 (1-8)
     * @return 下一级所需的总学习分钟数，已满级返回当前级阈值
     */
    public static int getNextLevelMinutes(int currentLevel) {
        if (currentLevel >= LEVEL_MINUTES_THRESHOLDS.length) {
            return LEVEL_MINUTES_THRESHOLDS[LEVEL_MINUTES_THRESHOLDS.length - 1];
        }
        return LEVEL_MINUTES_THRESHOLDS[currentLevel]; // currentLevel is 1-based, so index=currentLevel gives next level
    }

    // ==================== 课程难度相关 ====================

    /**
     * 难度名称数组
     */
    private static final String[] DIFFICULTY_NAMES = {"入门", "初级", "中级", "高级"};

    /**
     * 难度值→名称映射（供 Map 场景使用）
     */
    private static final Map<Integer, String> DIFFICULTY_NAME_MAP = Map.of(
        1, "入门",
        2, "初级",
        3, "中级",
        4, "高级"
    );

    /**
     * 获取难度名称
     *
     * @param difficulty 难度值 (1-4)
     * @return 难度名称
     */
    public static String getDifficultyName(Integer difficulty) {
        if (difficulty == null || difficulty < 1 || difficulty > DIFFICULTY_NAMES.length) {
            return "中等";
        }
        return DIFFICULTY_NAMES[difficulty - 1];
    }

    /**
     * 获取难度值→名称映射 Map
     *
     * @return 不可变 Map
     */
    public static Map<Integer, String> getDifficultyNameMap() {
        return DIFFICULTY_NAME_MAP;
    }

    /**
     * 根据难度名称获取难度值
     *
     * @param difficultyName 难度名称
     * @return 难度值 (1-4)
     */
    public static int getDifficultyValue(String difficultyName) {
        if (difficultyName == null) {
            return 2; // 默认初级
        }
        switch (difficultyName) {
            case "入门":
                return 1;
            case "初级":
                return 2;
            case "中级":
                return 3;
            case "高级":
                return 4;
            default:
                return 2;
        }
    }

    // ==================== 类别相关 ====================

    /**
     * 类别代码→显示名称映射（全量覆盖）
     * 包含：BACKEND, FRONTEND, DATABASE, CLOUD, AI, AI_ML, DEVOPS, MOBILE, SECURITY, PROGRAMMING, OTHER
     */
    private static final Map<String, String> CATEGORY_NAME_MAP = Map.ofEntries(
        Map.entry("BACKEND", "后端开发"),
        Map.entry("FRONTEND", "前端开发"),
        Map.entry("DATABASE", "数据库"),
        Map.entry("CLOUD", "云计算"),
        Map.entry("AI", "人工智能"),
        Map.entry("AI_ML", "人工智能"),
        Map.entry("DEVOPS", "运维部署"),
        Map.entry("MOBILE", "移动开发"),
        Map.entry("SECURITY", "网络安全"),
        Map.entry("PROGRAMMING", "编程开发"),
        Map.entry("OTHER", "其他")
    );

    /**
     * 获取类别显示名称
     *
     * @param categoryCode 类别代码
     * @return 类别显示名称
     */
    public static String getCategoryName(String categoryCode) {
        if (categoryCode == null) {
            return "其他";
        }
        return CATEGORY_NAME_MAP.getOrDefault(categoryCode.toUpperCase(), "其他");
    }

    /**
     * 获取类别代码→名称映射 Map
     *
     * @return 不可变 Map
     */
    public static Map<String, String> getCategoryNameMap() {
        return CATEGORY_NAME_MAP;
    }
}
