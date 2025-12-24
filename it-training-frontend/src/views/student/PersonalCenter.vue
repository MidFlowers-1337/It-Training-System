<template>
  <div class="personal-center-page">
    <!-- Hero Section -->
    <header class="user-hero">
      <div class="hero-bg">
        <div class="hero-orb hero-orb-1"></div>
        <div class="hero-orb hero-orb-2"></div>
      </div>

      <div class="hero-content">
        <div class="user-info">
          <Avatar :src="userInfo.avatar" :size="64" class="user-avatar">
            {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
          </Avatar>

          <div class="user-details">
            <span class="user-label">个人中心</span>
            <h1 class="user-name">{{ userInfo.realName || userInfo.username || '未登录用户' }}</h1>
            <div class="user-meta">
              <span class="username">@{{ userInfo.username }}</span>
              <span class="meta-dot">·</span>
              <span class="role-badge">{{ getRoleName(userInfo.role) }}</span>
            </div>
          </div>
        </div>

        <Button variant="secondary" @click="showAvatarDialog = true">更换头像</Button>
      </div>
    </header>

    <!-- Main Content -->
    <div class="page-content">
      <div class="content-grid">
        <!-- Left Column -->
        <aside class="left-column">
          <!-- Overview Card -->
          <div class="settings-group">
            <div class="group-header">概览</div>
            <div class="settings-item">
              <span class="item-label">学习时长</span>
              <span class="item-value">{{ learningStats.totalStudyMinutes || 0 }} 分钟</span>
            </div>
            <div class="settings-item">
              <span class="item-label">完成课程</span>
              <span class="item-value">{{ learningStats.completedCourses || 0 }}</span>
            </div>
            <div class="settings-item last">
              <span class="item-label">获得成就</span>
              <span class="item-value">{{ learningStats.achievementCount || 0 }}</span>
            </div>
          </div>

          <!-- Account Info Card -->
          <div class="settings-group">
            <div class="group-header">账号信息</div>
            <div class="settings-item">
              <div class="item-left">
                <span class="item-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                    <polyline points="22,6 12,13 2,6" />
                  </svg>
                </span>
                <span class="item-label">邮箱</span>
              </div>
              <span class="item-value">{{ userInfo.email || '未绑定邮箱' }}</span>
            </div>
            <div class="settings-item">
              <div class="item-left">
                <span class="item-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="5" y="2" width="14" height="20" rx="2" ry="2" />
                    <line x1="12" y1="18" x2="12.01" y2="18" />
                  </svg>
                </span>
                <span class="item-label">手机</span>
              </div>
              <span class="item-value">{{ userInfo.phone || '未绑定手机' }}</span>
            </div>
            <div class="settings-item last">
              <div class="item-left">
                <span class="item-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                    <line x1="16" y1="2" x2="16" y2="6" />
                    <line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                  </svg>
                </span>
                <span class="item-label">注册时间</span>
              </div>
              <span class="item-value">{{ formatDate(userInfo.createdAt) || '-' }}</span>
            </div>
          </div>
        </aside>

        <!-- Right Column -->
        <main class="right-column">
          <div class="settings-group main-card">
            <!-- Header with Tabs -->
            <div class="card-header">
              <div class="header-text">
                <h2 class="header-title">账号设置</h2>
                <p class="header-subtitle">管理个人资料与安全信息</p>
              </div>

              <div class="tab-switcher">
                <button
                  v-for="tab in tabs"
                  :key="tab.key"
                  type="button"
                  class="tab-btn"
                  :class="{ active: activeTab === tab.key }"
                  @click="activeTab = tab.key"
                >
                  {{ tab.label }}
                </button>
              </div>
            </div>

            <!-- Profile Tab -->
            <div v-if="activeTab === 'profile'" class="tab-content">
              <FormLayout class="profile-form">
                <FormItem label="用户名">
                  <Input :model-value="userInfo.username" disabled />
                </FormItem>
                <FormItem label="真实姓名">
                  <Input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                </FormItem>
                <FormItem label="邮箱" :error="errors.email">
                  <Input v-model="profileForm.email" placeholder="请输入邮箱" />
                </FormItem>
                <FormItem label="手机号" :error="errors.phone">
                  <Input v-model="profileForm.phone" placeholder="请输入手机号" />
                </FormItem>
                <FormItem>
                  <Button variant="primary" :loading="saving" @click="saveProfile">
                    {{ saving ? '保存中...' : '保存修改' }}
                  </Button>
                </FormItem>
              </FormLayout>
            </div>

            <!-- Security Tab -->
            <div v-else-if="activeTab === 'security'" class="tab-content">
              <div class="security-list">
                <button type="button" class="security-item" @click="showPasswordDialog = true">
                  <div class="security-info">
                    <span class="security-title">登录密码</span>
                    <span class="security-desc">定期更换密码可以保护账号安全</span>
                  </div>
                  <Button variant="secondary" size="sm">修改</Button>
                </button>

                <button type="button" class="security-item" @click="showEmailDialog = true">
                  <div class="security-info">
                    <span class="security-title">绑定邮箱</span>
                    <span class="security-desc">
                      {{ securityInfo.emailBound ? `已绑定：${securityInfo.email}` : '未绑定邮箱，绑定后可用于找回密码' }}
                    </span>
                  </div>
                  <Button variant="secondary" size="sm">{{ securityInfo.emailBound ? '更换' : '绑定' }}</Button>
                </button>

                <button type="button" class="security-item" @click="showPhoneDialog = true">
                  <div class="security-info">
                    <span class="security-title">绑定手机</span>
                    <span class="security-desc">
                      {{ securityInfo.phoneBound ? `已绑定：${securityInfo.phone}` : '未绑定手机，绑定后可用于找回密码' }}
                    </span>
                  </div>
                  <Button variant="secondary" size="sm">{{ securityInfo.phoneBound ? '更换' : '绑定' }}</Button>
                </button>

                <button type="button" class="security-item danger" @click="showDeleteDialog = true">
                  <div class="security-info">
                    <span class="security-title">注销账号</span>
                    <span class="security-desc">注销后账号将无法恢复，请谨慎操作</span>
                  </div>
                  <Button variant="danger" size="sm">注销</Button>
                </button>
              </div>
            </div>

            <!-- Learning Data Tab -->
            <div v-else class="tab-content">
              <div class="learning-stats">
                <div class="stat-row">
                  <span class="stat-label">总学习时长</span>
                  <span class="stat-value">{{ formatStudyTime(learningStats.totalStudyMinutes) }}</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">完成课程数</span>
                  <span class="stat-value">{{ learningStats.completedCourses || 0 }} 门</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">进行中课程</span>
                  <span class="stat-value">{{ learningStats.inProgressCourses || 0 }} 门</span>
                </div>
                <div class="stat-row">
                  <span class="stat-label">成就积分</span>
                  <span class="stat-value">{{ learningStats.achievementPoints || 0 }} 分</span>
                </div>
                <div class="stat-row last">
                  <span class="stat-label">学习等级</span>
                  <span class="stat-value">{{ learningStats.levelName || '学习新手' }}</span>
                </div>
              </div>

              <div class="shortcuts-section">
                <h3 class="shortcuts-title">快捷入口</h3>
                <div class="shortcuts-grid">
                  <button
                    v-for="shortcut in shortcuts"
                    :key="shortcut.path"
                    type="button"
                    class="shortcut-card"
                    @click="$router.push(shortcut.path)"
                  >
                    <div class="shortcut-icon" :class="shortcut.colorClass" v-html="shortcut.icon"></div>
                    <span class="shortcut-title">{{ shortcut.title }}</span>
                    <span class="shortcut-desc">{{ shortcut.subtitle }}</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- Dialog Components -->
    <ChangePasswordDialog
      v-model="showPasswordDialog"
      @success="handlePasswordChanged"
    />

    <BindVerificationDialog
      v-model="showEmailDialog"
      type="email"
      :send-code-fn="sendEmailCode"
      :bind-fn="bindEmail"
      @success="handleBindSuccess"
    />

    <BindVerificationDialog
      v-model="showPhoneDialog"
      type="phone"
      :send-code-fn="sendPhoneCode"
      :bind-fn="bindPhone"
      @success="handleBindSuccess"
    />

    <DeleteAccountDialog v-model="showDeleteDialog" />

    <AvatarUploadDialog
      v-model="showAvatarDialog"
      :current-avatar="userInfo.avatar"
      @success="handleAvatarUpdated"
    />

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast">
        <div v-if="toast.visible" class="toast" :class="toast.type">
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import {
  Button,
  Input,
  FormLayout,
  FormItem,
  Avatar,
} from '@/design-system';
import { useUserStore } from '@/store/user';
import {
  getCurrentUser,
  updateProfile,
  getSecurityInfo,
  bindEmail,
  bindPhone,
  sendEmailCode,
  sendPhoneCode,
} from '@/api/user';
import { getUserProfile } from '@/api/learning';
import {
  ChangePasswordDialog,
  BindVerificationDialog,
  DeleteAccountDialog,
  AvatarUploadDialog,
} from './personal-center';

const userStore = useUserStore();

const tabs = [
  { key: 'profile', label: '资料' },
  { key: 'security', label: '安全' },
  { key: 'learning', label: '数据' },
];

const shortcuts = [
  {
    path: '/learning',
    title: '学习中心',
    subtitle: '任务与进度',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>',
    colorClass: 'icon-primary',
  },
  {
    path: '/learning-report',
    title: '学习报告',
    subtitle: '趋势与总结',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>',
    colorClass: 'icon-info',
  },
  {
    path: '/achievements',
    title: '我的成就',
    subtitle: '勋章与里程碑',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>',
    colorClass: 'icon-warning',
  },
  {
    path: '/settings',
    title: '账号设置',
    subtitle: '安全与隐私',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>',
    colorClass: 'icon-muted',
  },
];

// State
const activeTab = ref('profile');
const saving = ref(false);
const userInfo = ref<Record<string, any>>({});
const learningStats = ref<Record<string, any>>({});
const securityInfo = ref<Record<string, any>>({});

const showPasswordDialog = ref(false);
const showEmailDialog = ref(false);
const showPhoneDialog = ref(false);
const showDeleteDialog = ref(false);
const showAvatarDialog = ref(false);

const profileForm = reactive({
  realName: '',
  email: '',
  phone: '',
});

const errors = reactive({
  email: '',
  phone: '',
});

// Toast
const toast = ref({ visible: false, message: '', type: 'success' as 'success' | 'error' });

const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  toast.value = { visible: true, message, type };
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// Methods
const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser();
    userInfo.value = res.data || {};
    profileForm.realName = userInfo.value.realName || '';
    profileForm.email = userInfo.value.email || '';
    profileForm.phone = userInfo.value.phone || '';
    avatarUrl.value = userInfo.value.avatar || '';
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

const fetchLearningStats = async () => {
  try {
    const res = await getUserProfile();
    learningStats.value = res.data || {};
  } catch (error) {
    console.error('获取学习统计失败:', error);
  }
};

const fetchSecurityInfo = async () => {
  try {
    const res = await getSecurityInfo();
    securityInfo.value = res.data || {};
  } catch (error) {
    console.error('获取安全信息失败:', error);
  }
};

const validateProfile = (): boolean => {
  errors.email = '';
  errors.phone = '';
  let valid = true;

  if (profileForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileForm.email)) {
    errors.email = '请输入正确的邮箱格式';
    valid = false;
  }
  if (profileForm.phone && !/^1[3-9]\d{9}$/.test(profileForm.phone)) {
    errors.phone = '请输入正确的手机号';
    valid = false;
  }
  return valid;
};

const saveProfile = async () => {
  if (!validateProfile()) return;
  try {
    saving.value = true;
    await updateProfile(profileForm);
    showToast('保存成功');
    fetchUserInfo();
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone,
    });
  } catch (error) {
    showToast('保存失败', 'error');
  } finally {
    saving.value = false;
  }
};

// Dialog callbacks
const handlePasswordChanged = () => {
  showToast('密码修改成功，请重新登录');
  userStore.logout();
};

const handleBindSuccess = () => {
  fetchUserInfo();
  fetchSecurityInfo();
};

const handleAvatarUpdated = (avatar: string) => {
  userInfo.value.avatar = avatar;
};

const getRoleName = (role: string) => {
  const roleMap: Record<string, string> = {
    ADMIN: '管理员',
    INSTRUCTOR: '讲师',
    STUDENT: '学员',
  };
  return roleMap[role] || '用户';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('zh-CN');
};

const formatStudyTime = (minutes: number) => {
  if (!minutes) return '0分钟';
  if (minutes < 60) return `${minutes}分钟`;
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`;
};

onMounted(() => {
  fetchUserInfo();
  fetchLearningStats();
  fetchSecurityInfo();
});
</script>

<style scoped>
/* ========================================
   iOS Settings 风格个人中心
   ======================================== */

.personal-center-page {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* ===== Hero Section ===== */
.user-hero {
  position: relative;
  padding: 48px var(--page-padding-x, 48px) 32px;
  background: var(--bg-card);
  border-bottom: 0.5px solid var(--border-color);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.hero-orb-1 {
  top: -80px;
  right: -80px;
  width: 280px;
  height: 280px;
  background: var(--primary-color);
  opacity: 0.08;
}

.hero-orb-2 {
  bottom: -100px;
  left: -100px;
  width: 280px;
  height: 280px;
  background: var(--primary-color);
  opacity: 0.04;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.user-avatar {
  flex-shrink: 0;
  border: 1px solid var(--border-color);
}

.user-details {
  min-width: 0;
}

.user-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.user-name {
  margin-top: 4px;
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.meta-dot {
  color: var(--text-muted);
}

.role-badge {
  padding: 2px 8px;
  background: var(--bg-tertiary);
  border-radius: 6px;
  font-size: 12px;
}

/* ===== Page Content ===== */
.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px var(--page-padding-x, 48px) 80px;
}

.content-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== Settings Group (iOS Style) ===== */
.settings-group {
  background: var(--bg-card);
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.03),
    0 2px 4px rgba(0, 0, 0, 0.03);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.group-header {
  padding: 12px 16px 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 0.5px solid var(--border-light);
}

.settings-item.last {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-icon {
  width: 20px;
  height: 20px;
  color: var(--text-muted);
}

.item-icon svg {
  width: 100%;
  height: 100%;
}

.item-label {
  font-size: 15px;
  color: var(--text-primary);
}

.item-value {
  font-size: 14px;
  color: var(--text-secondary);
}

/* ===== Main Card ===== */
.main-card {
  padding: 0;
  margin-bottom: 0;
}

.card-header {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 24px 24px 20px;
  border-bottom: 0.5px solid var(--border-light);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-subtitle {
  margin-top: 4px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* Tab Switcher */
.tab-switcher {
  display: inline-flex;
  padding: 4px;
  background: var(--bg-tertiary);
  border-radius: 10px;
}

.tab-btn {
  padding: 8px 16px;
  background: none;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  background: var(--bg-card);
  color: var(--text-primary);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Tab Content */
.tab-content {
  padding: 24px;
}

.profile-form {
  max-width: 480px;
}

/* Security List */
.security-list {
  display: flex;
  flex-direction: column;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px 0;
  background: none;
  border: none;
  border-bottom: 0.5px solid var(--border-light);
  text-align: left;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.security-item:last-child {
  border-bottom: none;
}

.security-item:hover {
  opacity: 0.8;
}

.security-info {
  flex: 1;
  min-width: 0;
}

.security-title {
  display: block;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}

.security-desc {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

.security-item.danger .security-title {
  color: var(--error);
}

/* Learning Stats */
.learning-stats {
  background: var(--bg-tertiary);
  border-radius: 12px;
  padding: 8px 0;
}

.stat-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 0.5px solid var(--border-light);
}

.stat-row.last {
  border-bottom: none;
}

.stat-label {
  font-size: 15px;
  color: var(--text-primary);
}

.stat-value {
  font-size: 14px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

/* Shortcuts */
.shortcuts-section {
  margin-top: 32px;
}

.shortcuts-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 768px) {
  .shortcuts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.shortcut-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 16px;
  background: var(--bg-tertiary);
  border: none;
  border-radius: 12px;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.shortcut-card:hover {
  background: var(--bg-hover);
}

.shortcut-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.shortcut-icon.icon-primary {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  color: var(--primary-color);
}

.shortcut-icon.icon-info {
  background: rgba(var(--info-rgb, 0, 122, 255) / 0.1);
  color: var(--info);
}

.shortcut-icon.icon-warning {
  background: rgba(var(--warning-rgb, 255, 149, 0) / 0.1);
  color: var(--warning);
}

.shortcut-icon.icon-muted {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 0.5px solid var(--border-color);
}

.shortcut-title {
  margin-top: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.shortcut-desc {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2000;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.toast.success {
  background: var(--success);
}

.toast.error {
  background: var(--error);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .user-hero {
    padding: 32px 24px 24px;
  }

  .user-name {
    font-size: 24px;
  }

  .page-content {
    padding: 24px 24px 64px;
  }

  .card-header {
    padding: 20px;
  }

  .tab-content {
    padding: 20px;
  }
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .user-hero,
[data-theme="dark"] .settings-group {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .tab-btn.active {
  background: var(--bg-tertiary);
}

[data-theme="dark"] .shortcut-card {
  background: var(--bg-tertiary);
}

[data-theme="dark"] .shortcut-card:hover {
  background: var(--bg-hover);
}

[data-theme="dark"] .learning-stats {
  background: var(--bg-tertiary);
}
</style>
