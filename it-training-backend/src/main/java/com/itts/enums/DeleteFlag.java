package com.itts.enums;

/**
 * 删除标记常量
 * 用于逻辑删除字段的统一管理
 */
public interface DeleteFlag {

    /**
     * 未删除
     */
    int NOT_DELETED = 0;

    /**
     * 已删除
     */
    int DELETED = 1;

    /**
     * 判断是否已删除
     */
    static boolean isDeleted(Integer flag) {
        return flag != null && flag == DELETED;
    }

    /**
     * 判断是否未删除
     */
    static boolean isNotDeleted(Integer flag) {
        return flag == null || flag == NOT_DELETED;
    }
}
