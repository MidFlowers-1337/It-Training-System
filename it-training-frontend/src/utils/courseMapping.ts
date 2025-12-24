/**
 * Course mapping utilities
 * Centralized mapping functions for course-related data display
 */

// Category names mapping
export const CATEGORY_MAP: Record<string, string> = {
  BACKEND: '后端开发',
  FRONTEND: '前端开发',
  DATABASE: '数据库',
  CLOUD: '云计算',
  AI: '人工智能',
  DEVOPS: '运维部署',
  SECURITY: '网络安全',
  OTHER: '其他',
};

// Difficulty levels mapping
export const DIFFICULTY_MAP: Record<number, string> = {
  1: '入门',
  2: '初级',
  3: '中级',
  4: '高级',
};

// Learning plan status mapping
export const PLAN_STATUS_MAP: Record<string, string> = {
  active: '进行中',
  completed: '已完成',
  paused: '已暂停',
  cancelled: '已取消',
};

// Tag types for plan status
export const PLAN_STATUS_TYPE_MAP: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'default'> = {
  active: 'primary',
  completed: 'success',
  paused: 'warning',
  cancelled: 'danger',
};

/**
 * Get category display name
 */
export function getCategoryName(category?: string): string {
  return CATEGORY_MAP[category || ''] || category || '其他';
}

/**
 * Get difficulty display name
 */
export function getDifficultyName(difficulty?: number): string {
  return DIFFICULTY_MAP[difficulty || 1] || '入门';
}

/**
 * Get difficulty badge CSS class
 */
export function getDifficultyBadge(difficulty: number): string {
  const badges: Record<number, string> = {
    1: 'bg-success/10 text-success',
    2: 'bg-primary/10 text-primary',
    3: 'bg-warning/10 text-warning',
    4: 'bg-danger/10 text-danger',
  };
  return badges[difficulty] || badges[1];
}

/**
 * Get plan status display text
 */
export function getPlanStatusText(status: string): string {
  return PLAN_STATUS_MAP[status] || status;
}

/**
 * Get plan status tag type
 */
export function getPlanStatusType(status: string): 'primary' | 'success' | 'warning' | 'danger' | 'default' {
  return PLAN_STATUS_TYPE_MAP[status] || 'default';
}
