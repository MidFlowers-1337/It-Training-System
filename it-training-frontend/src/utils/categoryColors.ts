/**
 * 课程类别多色系统
 * 每个类别有专属的颜色方案（渐变、背景、文字、Lucide icon 名称）
 * 用于课程卡片封面、类别标签等视觉区分
 */
import {
  Code2, Globe, Database, Brain, Server,
  Smartphone, Shield, BarChart3, Terminal, Blocks,
  type LucideIcon,
} from 'lucide-vue-next'

export interface CategoryStyle {
  /** 类别名称关键词（用于模糊匹配） */
  keywords: string[]
  /** Lucide icon 组件 */
  icon: LucideIcon
  /** Tailwind gradient class（用于卡片封面） */
  gradient: string
  /** 浅色背景 class（用于标签/badge） */
  bgClass: string
  /** 文字色 class */
  textClass: string
  /** 边框色 class */
  borderClass: string
  /** 纯色 HEX（用于图表等场景） */
  hex: string
}

const CATEGORY_STYLES: CategoryStyle[] = [
  {
    keywords: ['java', 'spring', 'springboot', 'jvm'],
    icon: Terminal,
    gradient: 'from-amber-500 to-orange-600',
    bgClass: 'bg-amber-50 dark:bg-amber-500/10',
    textClass: 'text-amber-600 dark:text-amber-400',
    borderClass: 'border-amber-200 dark:border-amber-500/20',
    hex: '#F59E0B',
  },
  {
    keywords: ['python', 'django', 'flask', 'fastapi'],
    icon: Code2,
    gradient: 'from-blue-500 to-cyan-500',
    bgClass: 'bg-blue-50 dark:bg-blue-500/10',
    textClass: 'text-blue-600 dark:text-blue-400',
    borderClass: 'border-blue-200 dark:border-blue-500/20',
    hex: '#3B82F6',
  },
  {
    keywords: ['前端', 'web', 'html', 'css', 'javascript', 'vue', 'react', 'typescript', 'js', 'ts'],
    icon: Globe,
    gradient: 'from-emerald-500 to-teal-500',
    bgClass: 'bg-emerald-50 dark:bg-emerald-500/10',
    textClass: 'text-emerald-600 dark:text-emerald-400',
    borderClass: 'border-emerald-200 dark:border-emerald-500/20',
    hex: '#10B981',
  },
  {
    keywords: ['数据库', 'mysql', 'sql', 'redis', 'mongodb', 'postgresql', 'oracle'],
    icon: Database,
    gradient: 'from-purple-500 to-violet-600',
    bgClass: 'bg-purple-50 dark:bg-purple-500/10',
    textClass: 'text-purple-600 dark:text-purple-400',
    borderClass: 'border-purple-200 dark:border-purple-500/20',
    hex: '#8B5CF6',
  },
  {
    keywords: ['ai', '人工智能', '机器学习', '深度学习', 'ml', 'llm', '大模型', 'nlp'],
    icon: Brain,
    gradient: 'from-rose-500 to-pink-600',
    bgClass: 'bg-rose-50 dark:bg-rose-500/10',
    textClass: 'text-rose-600 dark:text-rose-400',
    borderClass: 'border-rose-200 dark:border-rose-500/20',
    hex: '#F43F5E',
  },
  {
    keywords: ['后端', 'backend', '服务端', 'api', '微服务'],
    icon: Server,
    gradient: 'from-indigo-500 to-blue-600',
    bgClass: 'bg-indigo-50 dark:bg-indigo-500/10',
    textClass: 'text-indigo-600 dark:text-indigo-400',
    borderClass: 'border-indigo-200 dark:border-indigo-500/20',
    hex: '#6366F1',
  },
  {
    keywords: ['移动', 'android', 'ios', 'flutter', 'app', '小程序'],
    icon: Smartphone,
    gradient: 'from-cyan-500 to-sky-600',
    bgClass: 'bg-cyan-50 dark:bg-cyan-500/10',
    textClass: 'text-cyan-600 dark:text-cyan-400',
    borderClass: 'border-cyan-200 dark:border-cyan-500/20',
    hex: '#06B6D4',
  },
  {
    keywords: ['安全', '网络安全', 'security', '渗透', '加密'],
    icon: Shield,
    gradient: 'from-red-500 to-rose-600',
    bgClass: 'bg-red-50 dark:bg-red-500/10',
    textClass: 'text-red-600 dark:text-red-400',
    borderClass: 'border-red-200 dark:border-red-500/20',
    hex: '#EF4444',
  },
  {
    keywords: ['数据', '大数据', '数据分析', '数据科学', 'hadoop', 'spark'],
    icon: BarChart3,
    gradient: 'from-teal-500 to-emerald-600',
    bgClass: 'bg-teal-50 dark:bg-teal-500/10',
    textClass: 'text-teal-600 dark:text-teal-400',
    borderClass: 'border-teal-200 dark:border-teal-500/20',
    hex: '#14B8A6',
  },
  {
    keywords: ['devops', '运维', 'docker', 'k8s', 'kubernetes', 'linux', '云计算', 'cloud'],
    icon: Blocks,
    gradient: 'from-slate-500 to-zinc-600',
    bgClass: 'bg-slate-50 dark:bg-slate-500/10',
    textClass: 'text-slate-600 dark:text-slate-400',
    borderClass: 'border-slate-200 dark:border-slate-500/20',
    hex: '#64748B',
  },
]

/** 默认兜底样式 */
const DEFAULT_STYLE: CategoryStyle = {
  keywords: [],
  icon: Code2,
  gradient: 'from-indigo-500 to-purple-600',
  bgClass: 'bg-indigo-50 dark:bg-indigo-500/10',
  textClass: 'text-indigo-600 dark:text-indigo-400',
  borderClass: 'border-indigo-200 dark:border-indigo-500/20',
  hex: '#6366F1',
}

/**
 * 根据课程类别名称获取对应的视觉样式
 * 模糊匹配 — 类别名称包含关键词即命中
 */
export function getCategoryStyle(category?: string | null): CategoryStyle {
  if (!category) return DEFAULT_STYLE

  const lower = category.toLowerCase()
  const matched = CATEGORY_STYLES.find((s) =>
    s.keywords.some((kw) => lower.includes(kw)),
  )
  return matched ?? DEFAULT_STYLE
}

/**
 * 获取所有类别样式（用于图例/调色板展示）
 */
export function getAllCategoryStyles(): CategoryStyle[] {
  return CATEGORY_STYLES
}
