<template>
  <div class="page">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <h1 class="text-3xl md:text-5xl font-semibold tracking-tight text-text-primary">账号设置</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">安全、通知、隐私与外观偏好。</p>
        </div>
      </div>
    </section>

    <div class="flex flex-col lg:flex-row gap-8">
      <!-- Sidebar -->
      <aside class="w-full lg:w-72 flex-shrink-0 lg:sticky lg:top-24 self-start">
        <div class="glass rounded-3xl border border-border-color/60 p-2">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            class="w-full flex items-center gap-3 px-4 py-3 rounded-2xl text-sm font-medium transition-all"
            :class="
              activeTab === tab.key
                ? 'bg-bg-secondary/70 border border-border-color/60 text-text-primary shadow-sm'
                : 'text-text-secondary hover:bg-bg-hover/60'
            "
            @click="activeTab = tab.key"
          >
            <el-icon :size="18" class="flex-shrink-0"><component :is="tab.icon" /></el-icon>
            <span class="truncate">{{ tab.label }}</span>
          </button>
        </div>
      </aside>

      <!-- Content -->
      <main class="flex-1 min-w-0 space-y-6">
        <!-- Security -->
        <section v-if="activeTab === 'security'" class="card p-6 md:p-8">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-5">账号安全</h2>

          <div class="inset-group">
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Lock /></el-icon>
                  登录密码
                </p>
                <p class="mt-1 text-sm text-text-muted">定期更换密码可以保护账号安全</p>
              </div>
              <button type="button" class="btn btn-secondary" @click="showPasswordDialog = true">修改</button>
            </div>

            <div class="inset-divider"></div>

            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Message /></el-icon>
                  绑定邮箱
                </p>
                <p class="mt-1 text-sm text-text-muted">
                  <span v-if="securityInfo.emailBound">已绑定：{{ securityInfo.email }}</span>
                  <span v-else>未绑定邮箱，绑定后可用于找回密码</span>
                </p>
              </div>
              <button type="button" class="btn btn-secondary" @click="showEmailDialog = true">
                {{ securityInfo.emailBound ? '更换' : '绑定' }}
              </button>
            </div>

            <div class="inset-divider"></div>

            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Phone /></el-icon>
                  绑定手机
                </p>
                <p class="mt-1 text-sm text-text-muted">
                  <span v-if="securityInfo.phoneBound">已绑定：{{ securityInfo.phone }}</span>
                  <span v-else>未绑定手机，绑定后可用于找回密码</span>
                </p>
              </div>
              <button type="button" class="btn btn-secondary" @click="showPhoneDialog = true">
                {{ securityInfo.phoneBound ? '更换' : '绑定' }}
              </button>
            </div>
          </div>
        </section>

        <!-- Notification -->
        <section v-else-if="activeTab === 'notification'" class="card p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">通知设置</h2>
              <p class="mt-1 text-sm text-text-muted">控制你收到的通知类型。</p>
            </div>
            <button type="button" class="btn btn-primary" @click="saveNotificationSettings">保存</button>
          </div>

          <div class="inset-group">
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Bell /></el-icon>
                  系统通知
                </p>
                <p class="mt-1 text-sm text-text-muted">接收系统公告和重要通知</p>
              </div>
              <el-switch v-model="notificationSettings.system" />
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Reading /></el-icon>
                  课程通知
                </p>
                <p class="mt-1 text-sm text-text-muted">接收课程更新、开课提醒等</p>
              </div>
              <el-switch v-model="notificationSettings.course" />
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Trophy /></el-icon>
                  成就通知
                </p>
                <p class="mt-1 text-sm text-text-muted">获得成就时接收通知</p>
              </div>
              <el-switch v-model="notificationSettings.achievement" />
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Promotion /></el-icon>
                  营销通知
                </p>
                <p class="mt-1 text-sm text-text-muted">接收活动与推荐信息</p>
              </div>
              <el-switch v-model="notificationSettings.marketing" />
            </div>
          </div>
        </section>

        <!-- Privacy -->
        <section v-else-if="activeTab === 'privacy'" class="card p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">隐私设置</h2>
              <p class="mt-1 text-sm text-text-muted">控制哪些信息对他人可见。</p>
            </div>
            <button type="button" class="btn btn-primary" @click="savePrivacySettings">保存</button>
          </div>

          <div class="inset-group">
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><View /></el-icon>
                  公开学习数据
                </p>
                <p class="mt-1 text-sm text-text-muted">允许其他用户查看你的学习统计</p>
              </div>
              <el-switch v-model="privacySettings.showLearningData" />
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Medal /></el-icon>
                  公开成就
                </p>
                <p class="mt-1 text-sm text-text-muted">允许其他用户查看你获得的成就</p>
              </div>
              <el-switch v-model="privacySettings.showAchievements" />
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><User /></el-icon>
                  公开个人资料
                </p>
                <p class="mt-1 text-sm text-text-muted">允许其他用户查看你的个人资料</p>
              </div>
              <el-switch v-model="privacySettings.showProfile" />
            </div>
          </div>
        </section>

        <!-- Appearance -->
        <section v-else-if="activeTab === 'appearance'" class="card p-6 md:p-8">
          <div class="flex items-end justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">外观设置</h2>
              <p class="mt-1 text-sm text-text-muted">个性化你的使用体验。</p>
            </div>
            <button type="button" class="btn btn-primary" @click="saveAppearanceSettings">保存</button>
          </div>

          <div class="space-y-6">
            <div class="inset-group">
              <div class="inset-item">
                <div>
                  <p class="font-medium text-text-primary flex items-center gap-2">
                    <el-icon><Sunny /></el-icon>
                    主题模式
                  </p>
                  <p class="mt-1 text-sm text-text-muted">选择界面主题模式</p>
                </div>
                <div class="segmented">
                  <button
                    type="button"
                    class="segmented-item"
                    :class="{ 'is-active': appearanceSettings.theme === 'light' }"
                    @click="appearanceSettings.theme = 'light'"
                  >
                    浅色
                  </button>
                  <button
                    type="button"
                    class="segmented-item"
                    :class="{ 'is-active': appearanceSettings.theme === 'dark' }"
                    @click="appearanceSettings.theme = 'dark'"
                  >
                    深色
                  </button>
                  <button
                    type="button"
                    class="segmented-item"
                    :class="{ 'is-active': appearanceSettings.theme === 'auto' }"
                    @click="appearanceSettings.theme = 'auto'"
                  >
                    跟随
                  </button>
                </div>
              </div>

              <div class="inset-divider"></div>

              <div class="inset-item">
                <div>
                  <p class="font-medium text-text-primary flex items-center gap-2">
                    <el-icon><Grid /></el-icon>
                    首页布局
                  </p>
                  <p class="mt-1 text-sm text-text-muted">选择首页展示布局</p>
                </div>
                <div class="segmented">
                  <button
                    type="button"
                    class="segmented-item"
                    :class="{ 'is-active': appearanceSettings.layout === 'card' }"
                    @click="appearanceSettings.layout = 'card'"
                  >
                    卡片
                  </button>
                  <button
                    type="button"
                    class="segmented-item"
                    :class="{ 'is-active': appearanceSettings.layout === 'list' }"
                    @click="appearanceSettings.layout = 'list'"
                  >
                    列表
                  </button>
                </div>
              </div>
            </div>

            <div class="text-xs text-text-muted leading-relaxed">
              提示：本项目全局主题为 default / warm / dark（由右上角主题切换控制），此处外观设置用于预留扩展。
            </div>
          </div>
        </section>

        <!-- Danger -->
        <section v-else class="card p-6 md:p-8">
          <div class="flex items-start justify-between gap-6 mb-5">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">危险操作</h2>
              <p class="mt-1 text-sm text-text-muted">以下操作不可逆，请谨慎操作。</p>
            </div>
          </div>

          <el-alert type="warning" :closable="false" show-icon title="以下操作不可逆，请谨慎操作" class="mb-5" />

          <div class="inset-group">
            <div class="inset-item">
              <div>
                <p class="font-medium text-text-primary flex items-center gap-2">
                  <el-icon><Delete /></el-icon>
                  清除学习数据
                </p>
                <p class="mt-1 text-sm text-text-muted">清除所有学习进度、打卡记录等数据</p>
              </div>
              <button type="button" class="btn btn-secondary text-error border-error/20 hover:border-error/40" @click="handleClearData">
                清除
              </button>
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div>
                <p class="font-medium text-error flex items-center gap-2">
                  <el-icon><CircleClose /></el-icon>
                  注销账号
                </p>
                <p class="mt-1 text-sm text-text-muted">永久删除账号及所有相关数据，此操作不可恢复</p>
              </div>
              <button type="button" class="btn btn-primary bg-error hover:bg-error/90 focus:ring-error" @click="showDeleteDialog = true">
                注销账号
              </button>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="420px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码（6-20位）" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="saving">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱对话框 -->
    <el-dialog v-model="showEmailDialog" title="绑定邮箱" width="420px">
      <el-form :model="emailForm" label-width="80px">
        <el-form-item label="邮箱">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="emailForm.code" placeholder="请输入验证码">
            <template #append>
              <el-button @click="handleSendEmailCode" :disabled="emailCountdown > 0">
                {{ emailCountdown > 0 ? `${emailCountdown}s` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEmailDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBindEmail" :loading="saving">确认绑定</el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机对话框 -->
    <el-dialog v-model="showPhoneDialog" title="绑定手机" width="420px">
      <el-form :model="phoneForm" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="phoneForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="phoneForm.code" placeholder="请输入验证码">
            <template #append>
              <el-button @click="handleSendPhoneCode" :disabled="phoneCountdown > 0">
                {{ phoneCountdown > 0 ? `${phoneCountdown}s` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPhoneDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBindPhone" :loading="saving">确认绑定</el-button>
      </template>
    </el-dialog>

    <!-- 注销账号对话框 -->
    <el-dialog v-model="showDeleteDialog" title="注销账号" width="480px">
      <el-alert type="error" :closable="false" show-icon class="mb-5">
        <template #title>
          <strong>警告：此操作不可逆！</strong>
        </template>
        <template #default>
          <p>注销账号后，以下数据将被永久删除：</p>
          <ul class="mt-2 list-disc pl-5 space-y-1">
            <li>个人资料和账号信息</li>
            <li>所有学习进度和记录</li>
            <li>获得的成就和积分</li>
            <li>报名的课程信息</li>
          </ul>
        </template>
      </el-alert>
      <el-form :model="deleteForm" label-width="100px">
        <el-form-item label="输入密码">
          <el-input v-model="deleteForm.password" type="password" show-password placeholder="请输入密码确认注销" />
        </el-form-item>
        <el-form-item label="确认注销">
          <el-input v-model="deleteForm.confirm" placeholder="请输入 确认注销 以继续" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button
          type="danger"
          @click="handleDeleteAccount"
          :loading="saving"
          :disabled="deleteForm.confirm !== '确认注销'"
        >
          确认注销
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  Lock,
  Message,
  Phone,
  Bell,
  Reading,
  Trophy,
  Promotion,
  View,
  Medal,
  User,
  Sunny,
  Grid,
  Delete,
  CircleClose,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import {
  changePassword,
  getSecurityInfo,
  bindEmail,
  bindPhone,
  sendEmailCode,
  sendPhoneCode,
  deleteAccount,
  clearLearningData,
} from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const tabs = [
  { key: 'security', label: '账号安全', icon: Lock },
  { key: 'notification', label: '通知设置', icon: Bell },
  { key: 'privacy', label: '隐私设置', icon: View },
  { key: 'appearance', label: '外观设置', icon: Sunny },
  { key: 'danger', label: '危险操作', icon: Delete },
]

const activeTab = ref('security')
const saving = ref(false)
const securityInfo = ref({})

const showPasswordDialog = ref(false)
const showEmailDialog = ref(false)
const showPhoneDialog = ref(false)
const showDeleteDialog = ref(false)

const emailCountdown = ref(0)
const phoneCountdown = ref(0)

const passwordFormRef = ref(null)

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const emailForm = reactive({
  email: '',
  code: '',
})

const phoneForm = reactive({
  phone: '',
  code: '',
})

const deleteForm = reactive({
  password: '',
  confirm: '',
})

const notificationSettings = reactive({
  system: true,
  course: true,
  achievement: true,
  marketing: false,
})

const privacySettings = reactive({
  showLearningData: true,
  showAchievements: true,
  showProfile: true,
})

const appearanceSettings = reactive({
  theme: 'light',
  layout: 'card',
})

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

const fetchSecurityInfo = async () => {
  try {
    const res = await getSecurityInfo()
    securityInfo.value = res.data || {}
  } catch (error) {
    console.error('获取安全信息失败:', error)
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value?.validate()
    saving.value = true
    await changePassword(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    showPasswordDialog.value = false
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    userStore.logout()
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '修改失败')
  } finally {
    saving.value = false
  }
}

const handleSendEmailCode = async () => {
  if (!emailForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  try {
    await sendEmailCode(emailForm.email)
    ElMessage.success('验证码已发送')
    emailCountdown.value = 60
    const timer = setInterval(() => {
      emailCountdown.value--
      if (emailCountdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

const handleBindEmail = async () => {
  if (!emailForm.email || !emailForm.code) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    saving.value = true
    await bindEmail(emailForm.email, emailForm.code)
    ElMessage.success('绑定成功')
    showEmailDialog.value = false
    emailForm.email = ''
    emailForm.code = ''
    fetchSecurityInfo()
  } catch (error) {
    ElMessage.error('绑定失败')
  } finally {
    saving.value = false
  }
}

const handleSendPhoneCode = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  try {
    await sendPhoneCode(phoneForm.phone)
    ElMessage.success('验证码已发送')
    phoneCountdown.value = 60
    const timer = setInterval(() => {
      phoneCountdown.value--
      if (phoneCountdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

const handleBindPhone = async () => {
  if (!phoneForm.phone || !phoneForm.code) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    saving.value = true
    await bindPhone(phoneForm.phone, phoneForm.code)
    ElMessage.success('绑定成功')
    showPhoneDialog.value = false
    phoneForm.phone = ''
    phoneForm.code = ''
    fetchSecurityInfo()
  } catch (error) {
    ElMessage.error('绑定失败')
  } finally {
    saving.value = false
  }
}

const saveNotificationSettings = () => {
  localStorage.setItem('notificationSettings', JSON.stringify(notificationSettings))
  ElMessage.success('通知设置已保存')
}

const savePrivacySettings = () => {
  localStorage.setItem('privacySettings', JSON.stringify(privacySettings))
  ElMessage.success('隐私设置已保存')
}

const saveAppearanceSettings = () => {
  localStorage.setItem('appearanceSettings', JSON.stringify(appearanceSettings))
  ElMessage.success('外观设置已保存')
}

const handleClearData = async () => {
  try {
    const { value } = await ElMessageBox.prompt(
      '此操作将清除所有学习进度、打卡记录、成就等数据，不可恢复！请输入密码确认：',
      '清除学习数据',
      {
        confirmButtonText: '确认清除',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPlaceholder: '请输入密码',
        type: 'warning',
      }
    )

    if (!value) {
      ElMessage.warning('请输入密码')
      return
    }

    saving.value = true
    await clearLearningData(value)
    ElMessage.success('学习数据已清除')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '清除失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteAccount = async () => {
  if (!deleteForm.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (deleteForm.confirm !== '确认注销') {
    ElMessage.warning('请输入“确认注销”以继续')
    return
  }
  try {
    saving.value = true
    await deleteAccount(deleteForm.password)
    ElMessage.success('账号已注销')
    userStore.logout()
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注销失败')
  } finally {
    saving.value = false
  }
}

const loadLocalSettings = () => {
  const notification = localStorage.getItem('notificationSettings')
  if (notification) Object.assign(notificationSettings, JSON.parse(notification))

  const privacy = localStorage.getItem('privacySettings')
  if (privacy) Object.assign(privacySettings, JSON.parse(privacy))

  const appearance = localStorage.getItem('appearanceSettings')
  if (appearance) Object.assign(appearanceSettings, JSON.parse(appearance))
}

onMounted(() => {
  fetchSecurityInfo()
  loadLocalSettings()
})
</script>
