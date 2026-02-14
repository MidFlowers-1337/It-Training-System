package com.itts.common.util;

/**
 * 时间格式化工具类
 * 统一管理学习时长的格式化逻辑，消除多处 Service 中的重复代码
 *
 * [Phase 1.2] 提取自 LearningProgressServiceImpl、StudyCheckinServiceImpl、UserLearningStatsServiceImpl
 */
public final class TimeFormatUtils {

    private TimeFormatUtils() {
        // 工具类禁止实例化
    }

    /**
     * 格式化学习时长（分钟→可读字符串）
     * <p>
     * 规则：
     * - 小于 60 分钟 → "X分钟"
     * - 60~1439 分钟 → "X小时Y分钟"（Y=0 时省略）
     * - 1440+ 分钟 → "X天Y小时"（Y=0 时省略）
     *
     * @param minutes 学习分钟数
     * @return 格式化后的时长字符串
     */
    public static String formatStudyTime(int minutes) {
        if (minutes < 0) {
            return "0分钟";
        }
        if (minutes < 60) {
            return minutes + "分钟";
        }
        if (minutes < 1440) {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + "小时";
            }
            return hours + "小时" + remainingMinutes + "分钟";
        }
        int days = minutes / 1440;
        int remainingHours = (minutes % 1440) / 60;
        if (remainingHours == 0) {
            return days + "天";
        }
        return days + "天" + remainingHours + "小时";
    }
}
