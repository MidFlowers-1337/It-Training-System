package com.itts.common.utils;

/**
 * 等级和难度转换工具类
 * 统一管理学习等级、课程难度的计算和转换逻辑
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

    // ==================== 课程难度相关 ====================

    /**
     * 难度名称数组
     */
    private static final String[] DIFFICULTY_NAMES = {"入门", "初级", "中级", "高级"};

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
     * 获取类别显示名称
     *
     * @param categoryCode 类别代码
     * @return 类别显示名称
     */
    public static String getCategoryName(String categoryCode) {
        if (categoryCode == null) {
            return "其他";
        }
        switch (categoryCode.toUpperCase()) {
            case "BACKEND":
                return "后端开发";
            case "FRONTEND":
                return "前端开发";
            case "DATABASE":
                return "数据库";
            case "CLOUD":
                return "云计算";
            case "AI":
                return "人工智能";
            case "DEVOPS":
                return "运维";
            case "MOBILE":
                return "移动开发";
            case "SECURITY":
                return "安全";
            default:
                return "其他";
        }
    }
}
