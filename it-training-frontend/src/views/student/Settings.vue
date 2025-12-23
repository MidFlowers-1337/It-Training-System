<template>
  <PageLayout max-width="lg">
    <!-- 页面标题 -->
    <PageHeader title="账号设置" subtitle="安全、通知、隐私与外观偏好" :show-divider="false" />

    <div class="flex flex-col lg:flex-row gap-8">
      <!-- 侧边栏 -->
      <aside class="w-full lg:w-64 flex-shrink-0">
        <div class="bg-bg-secondary rounded-2xl border border-border-color p-2 lg:sticky lg:top-24">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all"
            :class="activeTab === tab.key
              ? 'bg-bg-tertiary text-text-primary'
              : 'text-text-secondary hover:bg-bg-hover'"
            @click="activeTab = tab.key"
          >
            <component :is="tab.icon" class="w-5 h-5 flex-shrink-0" />
            <span class="truncate">{{ tab.label }}</span>
          </button>
        </div>
      </aside>

      <!-- 内容区 -->
      <main class="flex-1 min-w-0 space-y-6">
        <!-- 账号安全 -->
        <section v-if="activeTab === 'security'" class="bg-bg-secondary rounded-2xl border border-border-color p-6 md:p-8">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-5">账号安全</h2>

          <InsetGroup>
            <InsetItem label="登录密码" clickable @click="showPasswordDialog = true">
              <template #icon>
                <IconLock class="w-5 h-5 text-text-muted" />
              </template>
            </InsetItem>
            <InsetItem label="绑定邮箱" :value="securityInfo.emailBound ? securityInfo.email : '未绑定'" clickable @click="showEmailDialog = true">
              <template #icon>
                <IconMail class="w-5 h-5 text-text-muted" />
              </template>
            </InsetItem>
            <InsetItem label="绑定手机" :value="securityInfo.phoneBound ? securityInfo.phone : '未绑定'" clickable last @click="showPhoneDialog = true">
              <template #icon>
                <IconPhone class="w-5 h-5 text-text-muted" />
              </template>
            </InsetItem>
          </InsetGroup>
        </section>

        <!-- 通知设置 -->
        <section v-else-if="activeTab === 'notification'" class="bg-bg-secondary rounded-2xl border border-border-color p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">通知设置</h2>
              <p class="mt-1 text-sm text-text-muted">控制你收到的通知类型</p>
            </div>
            <Button variant="primary" size="sm" @click="saveNotificationSettings">保存</Button>
          </div>

          <InsetGroup>
            <InsetItem label="系统通知">
              <template #icon>
                <IconBell class="w-5 h-5 text-text-muted" />
              </template>
              <Switch v-model="notificationSettings.system" />
            </InsetItem>
            <InsetItem label="课程通知">
              <template #icon>
                <IconBook class="w-5 h-5 text-text-muted" />
              </template>
              <Switch v-model="notificationSettings.course" />
            </InsetItem>
            <InsetItem label="成就通知">
              <template #icon>
                <IconTrophy class="w-5 h-5 text-text-muted" />
              </template>
              <Switch v-model="notificationSettings.achievement" />
            </InsetItem>
            <InsetItem label="营销通知" last>
              <template #icon>
                <IconMegaphone class="w-5 h-5 text-text-muted" />
              </template>
              <Switch v-model="notificationSettings.marketing" />
            </InsetItem>
          </InsetGroup>
        </section>

        <!-- 隐私设置 -->
        <section v-else-if="activeTab === 'privacy'" class="bg-bg-secondary rounded-2xl border border-border-color p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">隐私设置</h2>
              <p class="mt-1 text-sm text-text-muted">控制哪些信息对他人可见</p>
            </div>
            <Button variant="primary" size="sm" @click="savePrivacySettings">保存</Button>
          </div>

          <InsetGroup>
            <InsetItem label="公开学习数据">
              <Switch v-model="privacySettings.showLearningData" />
            </InsetItem>
            <InsetItem label="公开成就">
              <Switch v-model="privacySettings.showAchievements" />
            </InsetItem>
            <InsetItem label="公开个人资料" last>
              <Switch v-model="privacySettings.showProfile" />
            </InsetItem>
          </InsetGroup>
        </section>

        <!-- 外观设置 -->
        <section v-else-if="activeTab === 'appearance'" class="bg-bg-secondary rounded-2xl border border-border-color p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">外观设置</h2>
              <p class="mt-1 text-sm text-text-muted">个性化你的使用体验</p>
            </div>
          </div>

          <InsetGroup title="主题模式">
            <InsetItem
              v-for="themeOption in themeOptions"
              :key="themeOption.value"
              :label="themeOption.label"
              clickable
              :last="themeOption.value === 'dark'"
              @click="setTheme(themeOption.value)"
            >
              <template #icon>
                <component :is="themeOption.icon" class="w-5 h-5 text-text-muted" />
              </template>
              <div
                v-if="currentTheme === themeOption.value"
                class="w-5 h-5 rounded-full bg-primary flex items-center justify-center"
              >
                <svg class="w-3 h-3 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                  <polyline points="20 6 9 17 4 12" />
                </svg>
              </div>
            </InsetItem>
          </InsetGroup>
        </section>

        <!-- 危险操作 -->
        <section v-else class="bg-bg-secondary rounded-2xl border border-border-color p-6 md:p-8">
          <div class="mb-5">
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">危险操作</h2>
            <p class="mt-1 text-sm text-text-muted">以下操作不可逆，请谨慎操作</p>
          </div>

          <div class="p-4 rounded-xl bg-warning/10 border border-warning/20 mb-5">
            <p class="text-sm text-warning font-medium">⚠️ 警告：以下操作不可逆，请谨慎操作</p>
          </div>

          <InsetGroup>
            <InsetItem label="清除学习数据" clickable @click="handleClearData">
              <template #icon>
                <IconTrash class="w-5 h-5 text-text-muted" />
              </template>
            </InsetItem>
            <InsetItem label="注销账号" clickable last @click="showDeleteDialog = true">
              <template #icon>
                <IconX class="w-5 h-5 text-error" />
              </template>
            </InsetItem>
          </InsetGroup>
        </section>
      </main>
    </div>

    <!-- 修改密码对话框 -->
    <Modal v-model="showPasswordDialog" title="修改密码">
      <FormLayout>
        <FormItem label="当前密码" required :error="passwordErrors.current">
          <Input
            v-model="passwordForm.currentPassword"
            type="password"
            placeholder="请输入当前密码"
            :error="!!passwordErrors.current"
          />
        </FormItem>
        <FormItem label="新密码" required :error="passwordErrors.new">
          <Input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="6-20 位字符"
            :error="!!passwordErrors.new"
          />
        </FormItem>
        <FormItem label="确认密码" required :error="passwordErrors.confirm">
          <Input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="再次输入新密码"
            :error="!!passwordErrors.confirm"
          />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showPasswordDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="handleChangePassword">确认修改</Button>
      </template>
    </Modal>

    <!-- 绑定邮箱对话框 -->
    <Modal v-model="showEmailDialog" title="绑定邮箱">
      <FormLayout>
        <FormItem label="邮箱" :error="emailError">
          <Input v-model="emailForm.email" placeholder="请输入邮箱地址" :error="!!emailError" />
        </FormItem>
        <FormItem label="验证码">
          <div class="flex gap-2">
            <Input v-model="emailForm.code" placeholder="请输入验证码" class="flex-1" />
            <Button
              variant="secondary"
              :disabled="emailCountdown > 0"
              @click="handleSendEmailCode"
            >
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

    <!-- 绑定手机对话框 -->
    <Modal v-model="showPhoneDialog" title="绑定手机">
      <FormLayout>
        <FormItem label="手机号" :error="phoneError">
          <Input v-model="phoneForm.phone" placeholder="请输入手机号" :error="!!phoneError" />
        </FormItem>
        <FormItem label="验证码">
          <div class="flex gap-2">
            <Input v-model="phoneForm.code" placeholder="请输入验证码" class="flex-1" />
            <Button
              variant="secondary"
              :disabled="phoneCountdown > 0"
              @click="handleSendPhoneCode"
            >
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

    <!-- 注销账号对话框 -->
    <Modal v-model="showDeleteDialog" title="注销账号">
      <div class="p-4 rounded-xl bg-error/10 border border-error/20 mb-4">
        <p class="text-sm text-error font-medium">⚠️ 警告：此操作不可逆！</p>
        <p class="mt-2 text-sm text-text-secondary">
          注销账号后，以下数据将被永久删除：
        </p>
        <ul class="mt-2 text-sm text-text-secondary list-disc pl-5 space-y-1">
          <li>个人资料和账号信息</li>
          <li>所有学习进度和记录</li>
          <li>获得的成就和积分</li>
          <li>报名的课程信息</li>
        </ul>
      </div>
      <FormLayout>
        <FormItem label="输入密码" :error="deleteError">
          <Input
            v-model="deleteForm.password"
            type="password"
            placeholder="请输入密码确认注销"
            :error="!!deleteError"
          />
        </FormItem>
        <FormItem label="确认注销">
          <Input v-model="deleteForm.confirm" placeholder="请输入「确认注销」以继续" />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showDeleteDialog = false">取消</Button>
        <Button
          variant="primary"
          class="!bg-error hover:!bg-error/90"
          :loading="saving"
          :disabled="deleteForm.confirm !== '确认注销'"
          @click="handleDeleteAccount"
        >
          确认注销
        </Button>
      </template>
    </Modal>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, type Component } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { useTheme } from '@/design-system';
import {
  PageLayout,
  PageHeader,
  InsetGroup,
  InsetItem,
  Switch,
  Modal,
  FormLayout,
  FormItem,
  Input,
  Button,
} from '@/design-system';
import {
  changePassword,
  getSecurityInfo,
  bindEmail,
  bindPhone,
  sendEmailCode,
  sendPhoneCode,
  deleteAccount,
  clearLearningData,
} from '@/api/user';

// 图标组件
const IconLock = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2" /><path d="M7 11V7a5 5 0 0 1 10 0v4" /></svg>` };
const IconMail = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" /></svg>` };
const IconPhone = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" /></svg>` };
const IconBell = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" /><path d="M13.73 21a2 2 0 0 1-3.46 0" /></svg>` };
const IconBook = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" /><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" /></svg>` };
const IconTrophy = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6" /><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18" /><path d="M4 22h16" /><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22" /><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22" /><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z" /></svg>` };
const IconMegaphone = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 11 18-5v12L3 13v-2z" /><path d="M11.6 16.8a3 3 0 1 1-5.8-1.6" /></svg>` };
const IconSun = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4" /><path d="M12 2v2" /><path d="M12 20v2" /><path d="m4.93 4.93 1.41 1.41" /><path d="m17.66 17.66 1.41 1.41" /><path d="M2 12h2" /><path d="M20 12h2" /><path d="m6.34 17.66-1.41 1.41" /><path d="m19.07 4.93-1.41 1.41" /></svg>` };
const IconMoon = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" /></svg>` };
const IconMonitor = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2" ry="2" /><path d="M8 21h8" /><path d="M12 17v4" /></svg>` };
const IconTrash = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 6h18" /><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" /><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" /></svg>` };
const IconX = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10" /><path d="m15 9-6 6" /><path d="m9 9 6 6" /></svg>` };

// 类型定义
interface Tab {
  key: string;
  label: string;
  icon: Component;
}

interface ThemeOption {
  value: 'light' | 'dark' | 'system';
  label: string;
  icon: Component;
}

const router = useRouter();
const userStore = useUserStore();
const { theme: currentTheme, setTheme } = useTheme();

// Tab 配置
const tabs: Tab[] = [
  { key: 'security', label: '账号安全', icon: IconLock },
  { key: 'notification', label: '通知设置', icon: IconBell },
  { key: 'privacy', label: '隐私设置', icon: IconMonitor },
  { key: 'appearance', label: '外观设置', icon: IconSun },
  { key: 'danger', label: '危险操作', icon: IconTrash },
];

// 主题选项
const themeOptions: ThemeOption[] = [
  { value: 'light', label: '浅色模式', icon: IconSun },
  { value: 'system', label: '跟随系统', icon: IconMonitor },
  { value: 'dark', label: '深色模式', icon: IconMoon },
];

// 状态
const activeTab = ref('security');
const saving = ref(false);
const securityInfo = ref<Record<string, any>>({});

// 对话框状态
const showPasswordDialog = ref(false);
const showEmailDialog = ref(false);
const showPhoneDialog = ref(false);
const showDeleteDialog = ref(false);

// 倒计时
const emailCountdown = ref(0);
const phoneCountdown = ref(0);

// 表单数据
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const passwordErrors = reactive({
  current: '',
  new: '',
  confirm: '',
});

const emailForm = reactive({ email: '', code: '' });
const emailError = ref('');

const phoneForm = reactive({ phone: '', code: '' });
const phoneError = ref('');

const deleteForm = reactive({ password: '', confirm: '' });
const deleteError = ref('');

const notificationSettings = reactive({
  system: true,
  course: true,
  achievement: true,
  marketing: false,
});

const privacySettings = reactive({
  showLearningData: true,
  showAchievements: true,
  showProfile: true,
});

// 方法
const fetchSecurityInfo = async () => {
  try {
    const res = await getSecurityInfo();
    securityInfo.value = res.data || {};
  } catch (error) {
    console.error('获取安全信息失败:', error);
  }
};

const handleChangePassword = async () => {
  // 重置错误
  passwordErrors.current = '';
  passwordErrors.new = '';
  passwordErrors.confirm = '';

  // 验证
  if (!passwordForm.currentPassword) {
    passwordErrors.current = '请输入当前密码';
    return;
  }
  if (!passwordForm.newPassword) {
    passwordErrors.new = '请输入新密码';
    return;
  }
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    passwordErrors.new = '密码长度应为 6-20 位';
    return;
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordErrors.confirm = '两次输入的密码不一致';
    return;
  }

  saving.value = true;
  try {
    await changePassword(passwordForm);
    showPasswordDialog.value = false;
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
    userStore.logout();
    router.push('/login');
  } catch (error: any) {
    passwordErrors.current = error?.message || '修改失败';
  } finally {
    saving.value = false;
  }
};

const handleSendEmailCode = async () => {
  if (!emailForm.email) {
    emailError.value = '请输入邮箱';
    return;
  }
  try {
    await sendEmailCode(emailForm.email);
    emailCountdown.value = 60;
    const timer = setInterval(() => {
      emailCountdown.value--;
      if (emailCountdown.value <= 0) clearInterval(timer);
    }, 1000);
  } catch (error) {
    emailError.value = '发送失败';
  }
};

const handleBindEmail = async () => {
  if (!emailForm.email || !emailForm.code) {
    emailError.value = '请填写完整信息';
    return;
  }
  saving.value = true;
  try {
    await bindEmail(emailForm.email, emailForm.code);
    showEmailDialog.value = false;
    emailForm.email = '';
    emailForm.code = '';
    fetchSecurityInfo();
  } catch (error) {
    emailError.value = '绑定失败';
  } finally {
    saving.value = false;
  }
};

const handleSendPhoneCode = async () => {
  if (!phoneForm.phone) {
    phoneError.value = '请输入手机号';
    return;
  }
  try {
    await sendPhoneCode(phoneForm.phone);
    phoneCountdown.value = 60;
    const timer = setInterval(() => {
      phoneCountdown.value--;
      if (phoneCountdown.value <= 0) clearInterval(timer);
    }, 1000);
  } catch (error) {
    phoneError.value = '发送失败';
  }
};

const handleBindPhone = async () => {
  if (!phoneForm.phone || !phoneForm.code) {
    phoneError.value = '请填写完整信息';
    return;
  }
  saving.value = true;
  try {
    await bindPhone(phoneForm.phone, phoneForm.code);
    showPhoneDialog.value = false;
    phoneForm.phone = '';
    phoneForm.code = '';
    fetchSecurityInfo();
  } catch (error) {
    phoneError.value = '绑定失败';
  } finally {
    saving.value = false;
  }
};

const saveNotificationSettings = () => {
  localStorage.setItem('notificationSettings', JSON.stringify(notificationSettings));
  console.info('通知设置已保存');
};

const savePrivacySettings = () => {
  localStorage.setItem('privacySettings', JSON.stringify(privacySettings));
  console.info('隐私设置已保存');
};

const handleClearData = async () => {
  const password = prompt('此操作将清除所有学习数据，请输入密码确认：');
  if (!password) return;

  saving.value = true;
  try {
    await clearLearningData(password);
    console.info('学习数据已清除');
  } catch (error: any) {
    console.error(error?.message || '清除失败');
  } finally {
    saving.value = false;
  }
};

const handleDeleteAccount = async () => {
  if (!deleteForm.password) {
    deleteError.value = '请输入密码';
    return;
  }
  if (deleteForm.confirm !== '确认注销') {
    deleteError.value = '请输入「确认注销」以继续';
    return;
  }

  saving.value = true;
  try {
    await deleteAccount(deleteForm.password);
    userStore.logout();
    router.push('/login');
  } catch (error: any) {
    deleteError.value = error?.message || '注销失败';
  } finally {
    saving.value = false;
  }
};

const loadLocalSettings = () => {
  const notification = localStorage.getItem('notificationSettings');
  if (notification) Object.assign(notificationSettings, JSON.parse(notification));

  const privacy = localStorage.getItem('privacySettings');
  if (privacy) Object.assign(privacySettings, JSON.parse(privacy));
};

onMounted(() => {
  fetchSecurityInfo();
  loadLocalSettings();
});
</script>
