<template>
  <PageLayout>
    <!-- Hero Section -->
    <section class="relative rounded-2xl bg-fill-secondary/50 p-7 md:p-10 overflow-hidden">
      <div class="absolute inset-0 pointer-events-none">
        <div class="absolute -top-24 -right-24 w-72 h-72 bg-primary/10 blur-3xl rounded-full"></div>
        <div class="absolute -bottom-28 -left-28 w-72 h-72 bg-primary/5 blur-3xl rounded-full"></div>
      </div>

      <div class="relative flex flex-col md:flex-row md:items-center gap-6">
        <div class="flex items-center gap-4 min-w-0">
          <Avatar :src="userInfo.avatar" :size="64" class="ring-1 ring-border-primary/60">
            {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
          </Avatar>

          <div class="min-w-0">
            <p class="text-sm text-text-secondary">个人中心</p>
            <h1 class="mt-1 text-2xl md:text-3xl font-semibold tracking-tight text-text-primary truncate">
              {{ userInfo.realName || userInfo.username || '未登录用户' }}
            </h1>
            <div class="mt-2 flex flex-wrap items-center gap-2 text-sm text-text-secondary">
              <span class="truncate">@{{ userInfo.username }}</span>
              <span class="text-text-tertiary">·</span>
              <span class="px-2 py-0.5 rounded-md bg-fill-tertiary text-xs">{{ getRoleName(userInfo.role) }}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-3 md:ml-auto">
          <Button variant="secondary" @click="showAvatarDialog = true">更换头像</Button>
        </div>
      </div>
    </section>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 mt-6">
      <!-- Left Column -->
      <div class="lg:col-span-4 space-y-6">
        <!-- Overview Card -->
        <div class="rounded-2xl bg-fill-secondary/50 p-6">
          <h2 class="text-sm font-semibold text-text-primary mb-4">概览</h2>
          <InsetGroup>
            <InsetItem label="学习时长" :value="`${learningStats.totalStudyMinutes || 0} 分钟`" />
            <InsetItem label="完成课程" :value="String(learningStats.completedCourses || 0)" />
            <InsetItem label="获得成就" :value="String(learningStats.achievementCount || 0)" last />
          </InsetGroup>
        </div>

        <!-- Account Info Card -->
        <div class="rounded-2xl bg-fill-secondary/50 p-6">
          <h2 class="text-sm font-semibold text-text-primary mb-4">账号信息</h2>
          <InsetGroup>
            <InsetItem label="邮箱" :value="userInfo.email || '未绑定邮箱'">
              <template #icon>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                  <polyline points="22,6 12,13 2,6" />
                </svg>
              </template>
            </InsetItem>
            <InsetItem label="手机" :value="userInfo.phone || '未绑定手机'">
              <template #icon>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2" />
                  <line x1="12" y1="18" x2="12.01" y2="18" />
                </svg>
              </template>
            </InsetItem>
            <InsetItem label="注册时间" :value="formatDate(userInfo.createdAt) || '-'" last>
              <template #icon>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                  <line x1="16" y1="2" x2="16" y2="6" />
                  <line x1="8" y1="2" x2="8" y2="6" />
                  <line x1="3" y1="10" x2="21" y2="10" />
                </svg>
              </template>
            </InsetItem>
          </InsetGroup>
        </div>
      </div>

      <!-- Right Column -->
      <div class="lg:col-span-8">
        <div class="rounded-2xl bg-fill-secondary/50 p-6 md:p-8">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
            <div>
              <h2 class="text-lg font-semibold text-text-primary">账号设置</h2>
              <p class="mt-1 text-sm text-text-secondary">管理个人资料与安全信息</p>
            </div>

            <!-- Tab Switcher -->
            <div class="inline-flex p-1 rounded-xl bg-fill-tertiary">
              <button
                v-for="tab in tabs"
                :key="tab.key"
                type="button"
                class="px-4 py-1.5 text-sm font-medium rounded-lg transition-all"
                :class="activeTab === tab.key
                  ? 'bg-bg-primary text-text-primary shadow-sm'
                  : 'text-text-secondary hover:text-text-primary'"
                @click="activeTab = tab.key"
              >
                {{ tab.label }}
              </button>
            </div>
          </div>

          <!-- Profile Tab -->
          <div v-if="activeTab === 'profile'">
            <FormLayout class="max-w-xl">
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
          <div v-else-if="activeTab === 'security'" class="space-y-4">
            <InsetGroup>
              <InsetItem clickable @click="showPasswordDialog = true">
                <template #default>
                  <div class="space-y-1">
                    <div class="font-medium text-text-primary">登录密码</div>
                    <div class="text-sm text-text-secondary">定期更换密码可以保护账号安全</div>
                  </div>
                </template>
                <template #suffix>
                  <Button variant="secondary" size="sm">修改</Button>
                </template>
              </InsetItem>

              <InsetItem clickable @click="showEmailDialog = true">
                <template #default>
                  <div class="space-y-1">
                    <div class="font-medium text-text-primary">绑定邮箱</div>
                    <div class="text-sm text-text-secondary">
                      <span v-if="securityInfo.emailBound">已绑定：{{ securityInfo.email }}</span>
                      <span v-else>未绑定邮箱，绑定后可用于找回密码</span>
                    </div>
                  </div>
                </template>
                <template #suffix>
                  <Button variant="secondary" size="sm">{{ securityInfo.emailBound ? '更换' : '绑定' }}</Button>
                </template>
              </InsetItem>

              <InsetItem clickable @click="showPhoneDialog = true">
                <template #default>
                  <div class="space-y-1">
                    <div class="font-medium text-text-primary">绑定手机</div>
                    <div class="text-sm text-text-secondary">
                      <span v-if="securityInfo.phoneBound">已绑定：{{ securityInfo.phone }}</span>
                      <span v-else>未绑定手机，绑定后可用于找回密码</span>
                    </div>
                  </div>
                </template>
                <template #suffix>
                  <Button variant="secondary" size="sm">{{ securityInfo.phoneBound ? '更换' : '绑定' }}</Button>
                </template>
              </InsetItem>

              <InsetItem clickable last @click="showDeleteDialog = true">
                <template #default>
                  <div class="space-y-1">
                    <div class="font-medium text-text-primary">注销账号</div>
                    <div class="text-sm text-text-secondary">注销后账号将无法恢复，请谨慎操作</div>
                  </div>
                </template>
                <template #suffix>
                  <Button variant="danger" size="sm">注销</Button>
                </template>
              </InsetItem>
            </InsetGroup>
          </div>

          <!-- Learning Data Tab -->
          <div v-else class="space-y-6">
            <InsetGroup>
              <InsetItem label="总学习时长" :value="formatStudyTime(learningStats.totalStudyMinutes)" />
              <InsetItem label="完成课程数" :value="`${learningStats.completedCourses || 0} 门`" />
              <InsetItem label="进行中课程" :value="`${learningStats.inProgressCourses || 0} 门`" />
              <InsetItem label="成就积分" :value="`${learningStats.achievementPoints || 0} 分`" />
              <InsetItem label="学习等级" :value="learningStats.levelName || '学习新手'" last />
            </InsetGroup>

            <div>
              <h3 class="text-sm font-semibold text-text-primary mb-3">快捷入口</h3>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <button
                  v-for="shortcut in shortcuts"
                  :key="shortcut.path"
                  type="button"
                  class="rounded-xl bg-fill-tertiary/50 hover:bg-fill-tertiary p-4 text-left transition-colors"
                  @click="$router.push(shortcut.path)"
                >
                  <div
                    class="w-10 h-10 rounded-full flex items-center justify-center"
                    :class="shortcut.iconClass"
                    v-html="shortcut.icon"
                  ></div>
                  <div class="mt-3 text-sm font-semibold text-text-primary">{{ shortcut.title }}</div>
                  <div class="mt-1 text-xs text-text-secondary">{{ shortcut.subtitle }}</div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Password Dialog -->
    <Modal v-model="showPasswordDialog" title="修改密码" width="400px">
      <FormLayout>
        <FormItem label="当前密码" required :error="passwordErrors.currentPassword">
          <Input v-model="passwordForm.currentPassword" type="password" placeholder="请输入当前密码" />
        </FormItem>
        <FormItem label="新密码" required :error="passwordErrors.newPassword">
          <Input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </FormItem>
        <FormItem label="确认密码" required :error="passwordErrors.confirmPassword">
          <Input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showPasswordDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="handleChangePassword">确认修改</Button>
      </template>
    </Modal>

    <!-- Email Dialog -->
    <Modal v-model="showEmailDialog" title="绑定邮箱" width="400px">
      <FormLayout>
        <FormItem label="邮箱">
          <Input v-model="emailForm.email" placeholder="请输入邮箱" />
        </FormItem>
        <FormItem label="验证码">
          <div class="flex gap-2">
            <Input v-model="emailForm.code" placeholder="请输入验证码" class="flex-1" />
            <Button variant="secondary" :disabled="emailCountdown > 0" @click="handleSendEmailCode">
              {{ emailCountdown > 0 ? `${emailCountdown}s` : '发送验证码' }}
            </Button>
          </div>
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showEmailDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="handleBindEmail">确认绑定</Button>
      </template>
    </Modal>

    <!-- Phone Dialog -->
    <Modal v-model="showPhoneDialog" title="绑定手机" width="400px">
      <FormLayout>
        <FormItem label="手机号">
          <Input v-model="phoneForm.phone" placeholder="请输入手机号" />
        </FormItem>
        <FormItem label="验证码">
          <div class="flex gap-2">
            <Input v-model="phoneForm.code" placeholder="请输入验证码" class="flex-1" />
            <Button variant="secondary" :disabled="phoneCountdown > 0" @click="handleSendPhoneCode">
              {{ phoneCountdown > 0 ? `${phoneCountdown}s` : '发送验证码' }}
            </Button>
          </div>
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showPhoneDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="handleBindPhone">确认绑定</Button>
      </template>
    </Modal>

    <!-- Delete Account Dialog -->
    <Modal v-model="showDeleteDialog" title="注销账号" width="400px">
      <Alert type="error" title="警告">
        注销账号后，您的所有数据将被清除且无法恢复，请谨慎操作！
      </Alert>
      <FormLayout class="mt-4">
        <FormItem label="密码确认">
          <Input v-model="deleteForm.password" type="password" placeholder="请输入密码确认注销" />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showDeleteDialog = false">取消</Button>
        <Button variant="danger" :loading="saving" @click="handleDeleteAccount">确认注销</Button>
      </template>
    </Modal>

    <!-- Avatar Dialog -->
    <Modal v-model="showAvatarDialog" title="更换头像" width="400px">
      <FormLayout>
        <FormItem label="头像URL">
          <Input v-model="avatarUrl" placeholder="请输入头像图片URL" />
        </FormItem>
        <FormItem label="预览">
          <Avatar :src="avatarUrl" :size="80" />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showAvatarDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="handleUpdateAvatar">确认更换</Button>
      </template>
    </Modal>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {
  PageLayout,
  Button,
  Input,
  Modal,
  FormLayout,
  FormItem,
  InsetGroup,
  InsetItem,
  Avatar,
  Alert,
} from '@/design-system';
import { useUserStore } from '@/store/user';
import {
  getCurrentUser,
  updateProfile,
  changePassword,
  uploadAvatar,
  getSecurityInfo,
  bindEmail,
  bindPhone,
  sendEmailCode,
  sendPhoneCode,
  deleteAccount,
} from '@/api/user';
import { getUserProfile } from '@/api/learning';

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
    iconClass: 'bg-primary/10 border border-primary/20 text-primary',
  },
  {
    path: '/learning-report',
    title: '学习报告',
    subtitle: '趋势与总结',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>',
    iconClass: 'bg-info/10 border border-info/20 text-info',
  },
  {
    path: '/achievements',
    title: '我的成就',
    subtitle: '勋章与里程碑',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>',
    iconClass: 'bg-warning/10 border border-warning/20 text-warning',
  },
  {
    path: '/profile',
    title: '我的画像',
    subtitle: '偏好与能力',
    icon: '<svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>',
    iconClass: 'bg-fill-tertiary border border-border-primary text-text-secondary',
  },
];

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

const emailCountdown = ref(0);
const phoneCountdown = ref(0);

const profileForm = reactive({
  realName: '',
  email: '',
  phone: '',
});

const errors = reactive({
  email: '',
  phone: '',
});

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const passwordErrors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const emailForm = reactive({ email: '', code: '' });
const phoneForm = reactive({ phone: '', code: '' });
const deleteForm = reactive({ password: '' });
const avatarUrl = ref('');

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
    ElMessage.success('保存成功');
    fetchUserInfo();
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone,
    });
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const validatePassword = (): boolean => {
  passwordErrors.currentPassword = '';
  passwordErrors.newPassword = '';
  passwordErrors.confirmPassword = '';
  let valid = true;

  if (!passwordForm.currentPassword) {
    passwordErrors.currentPassword = '请输入当前密码';
    valid = false;
  }
  if (!passwordForm.newPassword) {
    passwordErrors.newPassword = '请输入新密码';
    valid = false;
  } else if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    passwordErrors.newPassword = '密码长度在6-20个字符之间';
    valid = false;
  }
  if (!passwordForm.confirmPassword) {
    passwordErrors.confirmPassword = '请确认新密码';
    valid = false;
  } else if (passwordForm.confirmPassword !== passwordForm.newPassword) {
    passwordErrors.confirmPassword = '两次输入的密码不一致';
    valid = false;
  }
  return valid;
};

const handleChangePassword = async () => {
  if (!validatePassword()) return;
  try {
    saving.value = true;
    await changePassword(passwordForm);
    ElMessage.success('密码修改成功，请重新登录');
    showPasswordDialog.value = false;
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    userStore.logout();
  } catch (error: any) {
    ElMessage.error(error.message || '修改失败');
  } finally {
    saving.value = false;
  }
};

const handleSendEmailCode = async () => {
  if (!emailForm.email) {
    ElMessage.warning('请输入邮箱');
    return;
  }
  try {
    await sendEmailCode(emailForm.email);
    ElMessage.success('验证码已发送');
    emailCountdown.value = 60;
    const timer = setInterval(() => {
      emailCountdown.value--;
      if (emailCountdown.value <= 0) clearInterval(timer);
    }, 1000);
  } catch (error) {
    ElMessage.error('发送失败');
  }
};

const handleBindEmail = async () => {
  if (!emailForm.email || !emailForm.code) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  try {
    saving.value = true;
    await bindEmail(emailForm.email, emailForm.code);
    ElMessage.success('绑定成功');
    showEmailDialog.value = false;
    fetchUserInfo();
    fetchSecurityInfo();
  } catch (error) {
    ElMessage.error('绑定失败');
  } finally {
    saving.value = false;
  }
};

const handleSendPhoneCode = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号');
    return;
  }
  try {
    await sendPhoneCode(phoneForm.phone);
    ElMessage.success('验证码已发送');
    phoneCountdown.value = 60;
    const timer = setInterval(() => {
      phoneCountdown.value--;
      if (phoneCountdown.value <= 0) clearInterval(timer);
    }, 1000);
  } catch (error) {
    ElMessage.error('发送失败');
  }
};

const handleBindPhone = async () => {
  if (!phoneForm.phone || !phoneForm.code) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  try {
    saving.value = true;
    await bindPhone(phoneForm.phone, phoneForm.code);
    ElMessage.success('绑定成功');
    showPhoneDialog.value = false;
    fetchUserInfo();
    fetchSecurityInfo();
  } catch (error) {
    ElMessage.error('绑定失败');
  } finally {
    saving.value = false;
  }
};

const handleUpdateAvatar = async () => {
  if (!avatarUrl.value) {
    ElMessage.warning('请输入头像URL');
    return;
  }
  try {
    saving.value = true;
    await uploadAvatar(avatarUrl.value);
    ElMessage.success('头像更新成功');
    showAvatarDialog.value = false;
    fetchUserInfo();
    userStore.setUserInfo({
      ...userStore.userInfo,
      avatar: avatarUrl.value,
    });
  } catch (error) {
    ElMessage.error('更新失败');
  } finally {
    saving.value = false;
  }
};

const handleDeleteAccount = async () => {
  if (!deleteForm.password) {
    ElMessage.warning('请输入密码');
    return;
  }
  try {
    saving.value = true;
    await deleteAccount(deleteForm.password);
    ElMessage.success('账号已注销');
    userStore.logout();
  } catch (error: any) {
    ElMessage.error(error.message || '注销失败');
  } finally {
    saving.value = false;
  }
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
