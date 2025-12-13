<template>
  <div class="achievements-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>🏆 我的成就</h1>
      <p class="subtitle">记录你的学习里程碑</p>
    </div>

    <!-- 成就统计 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-value">{{ earnedCount }}</span>
        <span class="stat-label">已获得</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ totalCount }}</span>
        <span class="stat-label">总成就</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ totalPoints }}</span>
        <span class="stat-label">总积分</span>
      </div>
    </div>

    <!-- 成就分类标签 -->
    <div class="category-tabs">
      <button 
        v-for="cat in categories" 
        :key="cat.value"
        class="tab-btn"
        :class="{ active: activeCategory === cat.value }"
        @click="activeCategory = cat.value"
      >
        {{ cat.icon }} {{ cat.label }}
      </button>
    </div>

    <!-- 成就列表 -->
    <div class="achievements-grid">
      <div 
        v-for="achievement in filteredAchievements" 
        :key="achievement.id"
        class="achievement-card"
        :class="{ earned: achievement.earned, locked: !achievement.earned }"
      >
        <div class="achievement-icon">
          {{ achievement.icon || getDefaultIcon(achievement.category) }}
        </div>
        <div class="achievement-content">
          <h3>{{ achievement.name }}</h3>
          <p class="description">{{ achievement.description }}</p>
          <div class="achievement-meta">
            <span class="points">+{{ achievement.points }} 积分</span>
            <span v-if="achievement.earned" class="earned-date">
              {{ formatDate(achievement.earnedAt) }} 获得
            </span>
          </div>
          <!-- 进度条（未获得时显示） -->
          <div v-if="!achievement.earned" class="progress-section">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: achievement.progressPercent + '%' }"></div>
            </div>
            <span class="progress-text">{{ achievement.currentProgress }}/{{ achievement.conditionValue }}</span>
          </div>
        </div>
        <div v-if="achievement.earned" class="earned-badge">
          ✓
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredAchievements.length === 0" class="empty-state">
      <div class="empty-icon">🎯</div>
      <p>该分类暂无成就</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { getAllAchievements, getAchievementPoints } from '@/api/learning'

export default {
  name: 'Achievements',
  setup() {
    const achievements = ref([])
    const totalPoints = ref(0)
    const activeCategory = ref('all')
    
    const categories = [
      { value: 'all', label: '全部', icon: '📋' },
      { value: 'streak', label: '连续学习', icon: '🔥' },
      { value: 'course', label: '课程完成', icon: '📚' },
      { value: 'general', label: '学习时长', icon: '⏱️' }
    ]
    
    const earnedCount = computed(() => {
      return achievements.value.filter(a => a.earned).length
    })
    
    const totalCount = computed(() => {
      return achievements.value.length
    })
    
    const filteredAchievements = computed(() => {
      if (activeCategory.value === 'all') {
        return achievements.value
      }
      return achievements.value.filter(a => a.category === activeCategory.value)
    })
    
    const getDefaultIcon = (category) => {
      const icons = {
        streak: '🔥',
        course: '📚',
        general: '⭐',
        skill: '💡'
      }
      return icons[category] || '🎖️'
    }
    
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
    
    const loadAchievements = async () => {
      try {
        const res = await getAllAchievements()
        if (res.code === 200) {
          achievements.value = res.data || []
        }
      } catch (error) {
        console.error('加载成就失败:', error)
      }
    }
    
    const loadPoints = async () => {
      try {
        const res = await getAchievementPoints()
        if (res.code === 200) {
          totalPoints.value = res.data || 0
        }
      } catch (error) {
        console.error('加载积分失败:', error)
      }
    }
    
    onMounted(() => {
      loadAchievements()
      loadPoints()
    })
    
    return {
      achievements,
      totalPoints,
      activeCategory,
      categories,
      earnedCount,
      totalCount,
      filteredAchievements,
      getDefaultIcon,
      formatDate
    }
  }
}
</script>

<style scoped>
.achievements-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.page-header .subtitle {
  color: var(--text-secondary);
  margin: 0;
}

/* 统计栏 */
.stats-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 32px;
}

.stat-item {
  text-align: center;
  color: white;
}

.stat-value {
  display: block;
  font-size: 36px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
}

/* 分类标签 */
.category-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  background: var(--bg-card);
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: var(--shadow-sm);
}

.tab-btn:hover {
  background: var(--bg-secondary);
}

.tab-btn.active {
  background: linear-gradient(135deg, var(--primary-color), #667eea);
  color: white;
}

/* 成就网格 */
.achievements-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.achievement-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  gap: 16px;
  position: relative;
  box-shadow: var(--shadow-sm);
  transition: transform 0.2s, box-shadow 0.2s;
}

.achievement-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.achievement-card.locked {
  opacity: 0.7;
}

.achievement-card.earned {
  border: 2px solid #38ef7d;
}

.achievement-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  background: var(--bg-secondary);
  flex-shrink: 0;
}

.achievement-card.earned .achievement-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.achievement-content {
  flex: 1;
  min-width: 0;
}

.achievement-content h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.achievement-content .description {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.achievement-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
}

.points {
  color: #f5a623;
  font-weight: 600;
}

.earned-date {
  color: var(--text-muted);
}

.progress-section {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-secondary);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), #667eea);
  border-radius: 3px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 12px;
  color: var(--text-secondary);
  min-width: 50px;
}

.earned-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  font-weight: 700;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .stats-bar {
    gap: 24px;
    padding: 20px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .achievements-grid {
    grid-template-columns: 1fr;
  }
}
</style>