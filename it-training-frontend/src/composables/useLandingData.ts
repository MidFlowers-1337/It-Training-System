/**
 * Landing Page 共享数据 composable
 * 提供统计数据和精选课程获取逻辑
 */
import { ref } from 'vue'
import { publicStatsApi, type PublicCourse } from '@/api/publicStats'

export interface StatItem {
  value: number
  suffix: string
  label: string
}

export function useLandingData() {
  const stats = ref<StatItem[]>([
    { value: 50, suffix: '+', label: '精品课程' },
    { value: 1200, suffix: '+', label: '注册学员' },
    { value: 30, suffix: '+', label: '专业讲师' },
    { value: 85, suffix: '%', label: '完课率' },
  ])

  const featuredCourses = ref<PublicCourse[]>([])
  const coursesLoading = ref(false)

  const fetchStats = async () => {
    try {
      const d: any = await publicStatsApi.getStats()
      if (d) {
        stats.value = [
          { value: d.courseCount || d.totalCourses || 50, suffix: '+', label: '精品课程' },
          { value: d.studentCount || d.totalStudents || 1200, suffix: '+', label: '注册学员' },
          { value: d.instructorCount || d.totalInstructors || 30, suffix: '+', label: '专业讲师' },
          { value: d.completionRate || 85, suffix: '%', label: '完课率' },
        ]
      }
    } catch { /* silent fail, use defaults */ }
  }

  const fetchFeaturedCourses = async (limit = 8) => {
    coursesLoading.value = true
    try {
      const data = await publicStatsApi.getFeaturedCourses(limit)
      if (Array.isArray(data)) {
        featuredCourses.value = data
      }
    } catch { /* silent fail */ }
    finally {
      coursesLoading.value = false
    }
  }

  return { stats, featuredCourses, coursesLoading, fetchStats, fetchFeaturedCourses }
}
