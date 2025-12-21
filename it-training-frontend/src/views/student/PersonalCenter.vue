<template>
  <div class="page">
    <section class="page-hero glass p-7 md:p-10">
      <div class="absolute inset-0 pointer-events-none">
        <div class="absolute -top-24 -right-24 w-72 h-72 bg-primary/15 blur-3xl rounded-full"></div>
        <div class="absolute -bottom-28 -left-28 w-72 h-72 bg-secondary/20 blur-3xl rounded-full"></div>
      </div>

      <div class="relative flex flex-col md:flex-row md:items-center gap-6">
        <div class="flex items-center gap-4 min-w-0">
          <el-avatar :size="64" :src="userInfo.avatar || defaultAvatar" class="ring-1 ring-border-color/60">
            {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
          </el-avatar>

          <div class="min-w-0">
            <p class="text-sm text-text-secondary">个人中心</p>
            <h1 class="mt-1 text-2xl md:text-3xl font-semibold tracking-tight text-text-primary truncate">
              {{ userInfo.realName || userInfo.username || '未登录用户' }}
            </h1>
            <div class="mt-2 flex flex-wrap items-center gap-2 text-sm text-text-secondary">
              <span class="truncate">@{{ userInfo.username }}</span>
              <span class="text-text-muted">·</span>
              <span class="badge badge-secondary">{{ getRoleName(userInfo.role) }}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-3 md:ml-auto">
          <button type="button" class="btn btn-secondary" @click="showAvatarDialog = true">更换头像</button>
        </div>
      </div>
    </section>

    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
      <div class="lg:col-span-4 space-y-6">
        <div class="card p-6">
          <h2 class="text-sm font-semibold text-text-primary mb-4">概览</h2>
          <div class="inset-group">
            <div class="inset-item">
              <div class="text-sm text-text-secondary">学习时长</div>
              <div class="font-semibold text-text-primary">{{ learningStats.totalStudyMinutes || 0 }} 分钟</div>
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div class="text-sm text-text-secondary">完成课程</div>
              <div class="font-semibold text-text-primary">{{ learningStats.completedCourses || 0 }}</div>
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div class="text-sm text-text-secondary">获得成就</div>
              <div class="font-semibold text-text-primary">{{ learningStats.achievementCount || 0 }}</div>
            </div>
          </div>
        </div>

        <div class="card p-6">
          <h2 class="text-sm font-semibold text-text-primary mb-4">账号信息</h2>
          <div class="inset-group">
            <div class="inset-item">
              <div class="flex items-center gap-2 text-sm text-text-secondary">
                <el-icon><Message /></el-icon>
                <span>邮箱</span>
              </div>
              <div class="text-sm font-medium text-text-primary truncate max-w-[60%]">
                {{ userInfo.email || '未绑定邮箱' }}
              </div>
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div class="flex items-center gap-2 text-sm text-text-secondary">
                <el-icon><Phone /></el-icon>
                <span>手机</span>
              </div>
              <div class="text-sm font-medium text-text-primary truncate max-w-[60%]">
                {{ userInfo.phone || '未绑定手机' }}
              </div>
            </div>
            <div class="inset-divider"></div>
            <div class="inset-item">
              <div class="flex items-center gap-2 text-sm text-text-secondary">
                <el-icon><Calendar /></el-icon>
                <span>注册时间</span>
              </div>
              <div class="text-sm font-medium text-text-primary">{{ formatDate(userInfo.createdAt) || '-' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-8">
        <div class="card p-6 md:p-8">
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-6">
            <div>
              <h2 class="text-lg font-semibold text-text-primary">账号设置</h2>
              <p class="mt-1 text-sm text-text-secondary">管理个人资料与安全信息</p>
            </div>

            <div class="segmented">
              <button
                type="button"
                class="segmented-item"
                :class="{ 'is-active': activeTab === 'profile' }"
                @click="activeTab = 'profile'"
              >
                资料
              </button>
              <button
                type="button"
                class="segmented-item"
                :class="{ 'is-active': activeTab === 'security' }"
                @click="activeTab = 'security'"
              >
                安全
              </button>
              <button
                type="button"
                class="segmented-item"
                :class="{ 'is-active': activeTab === 'learning' }"
                @click="activeTab = 'learning'"
              >
                数据
              </button>
            </div>
          </div>

          <div v-if="activeTab === 'profile'">
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-position="top"
              class="max-w-xl"
            >
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item>
                <button type="button" class="btn btn-primary" :disabled="saving" @click="saveProfile">
                  {{ saving ? '保存中...' : '保存修改' }}
                </button>
              </el-form-item>
            </el-form>
          </div>

          <div v-else-if="activeTab === 'security'" class="space-y-4">
            <div class="inset-group">
              <div class="inset-item">
                <div class="space-y-1">
                  <div class="font-medium text-text-primary">登录密码</div>
                  <div class="text-sm text-text-secondary">定期更换密码可以保护账号安全</div>
                </div>
                <button type="button" class="btn btn-secondary" @click="showPasswordDialog = true">修改</button>
              </div>
              <div class="inset-divider"></div>

              <div class="inset-item">
                <div class="space-y-1">
                  <div class="font-medium text-text-primary">绑定邮箱</div>
                  <div class="text-sm text-text-secondary">
                    <span v-if="securityInfo.emailBound">已绑定：{{ securityInfo.email }}</span>
                    <span v-else>未绑定邮箱，绑定后可用于找回密码</span>
                  </div>
                </div>
                <button type="button" class="btn btn-secondary" @click="showEmailDialog = true">
                  {{ securityInfo.emailBound ? '更换' : '绑定' }}
                </button>
              </div>
              <div class="inset-divider"></div>

              <div class="inset-item">
                <div class="space-y-1">
                  <div class="font-medium text-text-primary">绑定手机</div>
                  <div class="text-sm text-text-secondary">
                    <span v-if="securityInfo.phoneBound">已绑定：{{ securityInfo.phone }}</span>
                    <span v-else>未绑定手机，绑定后可用于找回密码</span>
                  </div>
                </div>
                <button type="button" class="btn btn-secondary" @click="showPhoneDialog = true">
                  {{ securityInfo.phoneBound ? '更换' : '绑定' }}
                </button>
              </div>
              <div class="inset-divider"></div>

              <div class="inset-item">
                <div class="space-y-1">
                  <div class="font-medium text-text-primary">注销账号</div>
                  <div class="text-sm text-text-secondary">注销后账号将无法恢复，请谨慎操作</div>
                </div>
                <button
                  type="button"
                  class="btn btn-secondary text-error border-error/30 hover:bg-error/10"
                  @click="showDeleteDialog = true"
                >
                  注销
                </button>
              </div>
            </div>
          </div>

          <div v-else class="space-y-6">
            <div class="inset-group">
              <div class="inset-item">
                <div class="text-sm text-text-secondary">总学习时长</div>
                <div class="font-semibold text-text-primary">{{ formatStudyTime(learningStats.totalStudyMinutes) }}</div>
              </div>
              <div class="inset-divider"></div>
              <div class="inset-item">
                <div class="text-sm text-text-secondary">完成课程数</div>
                <div class="font-semibold text-text-primary">{{ learningStats.completedCourses || 0 }} 门</div>
              </div>
              <div class="inset-divider"></div>
              <div class="inset-item">
                <div class="text-sm text-text-secondary">进行中课程</div>
                <div class="font-semibold text-text-primary">{{ learningStats.inProgressCourses || 0 }} 门</div>
              </div>
              <div class="inset-divider"></div>
              <div class="inset-item">
                <div class="text-sm text-text-secondary">成就积分</div>
                <div class="font-semibold text-text-primary">{{ learningStats.achievementPoints || 0 }} 分</div>
              </div>
              <div class="inset-divider"></div>
              <div class="inset-item">
                <div class="text-sm text-text-secondary">学习等级</div>
                <div class="font-semibold text-text-primary">{{ learningStats.levelName || '学习新手' }}</div>
              </div>
            </div>

            <div>
              <h3 class="text-sm font-semibold text-text-primary mb-3">快捷入口</h3>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <button
                  type="button"
                  class="card-hover p-4 text-left"
                  @click="$router.push('/learning')"
                >
                  <div
                    class="w-10 h-10 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center text-primary"
                  >
                    <el-icon><TrendCharts /></el-icon>
                  </div>
                  <div class="mt-3 text-sm font-semibold text-text-primary">学习中心</div>
                  <div class="mt-1 text-xs text-text-secondary">任务与进度</div>
                </button>

                <button
                  type="button"
                  class="card-hover p-4 text-left"
                  @click="$router.push('/learning-report')"
                >
                  <div
                    class="w-10 h-10 rounded-full bg-info/10 border border-info/20 flex items-center justify-center text-info"
                  >
                    <el-icon><DataAnalysis /></el-icon>
                  </div>
                  <div class="mt-3 text-sm font-semibold text-text-primary">学习报告</div>
                  <div class="mt-1 text-xs text-text-secondary">趋势与总结</div>
                </button>

                <button
                  type="button"
                  class="card-hover p-4 text-left"
                  @click="$router.push('/achievements')"
                >
                  <div
                    class="w-10 h-10 rounded-full bg-warning/10 border border-warning/20 flex items-center justify-center text-warning"
                  >
                    <el-icon><Trophy /></el-icon>
                  </div>
                  <div class="mt-3 text-sm font-semibold text-text-primary">我的成就</div>
                  <div class="mt-1 text-xs text-text-secondary">勋章与里程碑</div>
                </button>

                <button
                  type="button"
                  class="card-hover p-4 text-left"
                  @click="$router.push('/profile')"
                >
                  <div
                    class="w-10 h-10 rounded-full bg-bg-tertiary/60 border border-border-color/60 flex items-center justify-center text-text-secondary"
                  >
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="mt-3 text-sm font-semibold text-text-primary">我的画像</div>
                  <div class="mt-1 text-xs text-text-secondary">偏好与能力</div>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input v-model="passwordForm.currentPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
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

    <el-dialog v-model="showEmailDialog" title="绑定邮箱" width="400px">
      <el-form :model="emailForm" label-width="80px">
        <el-form-item label="邮箱">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱" />
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

    <el-dialog v-model="showPhoneDialog" title="绑定手机" width="400px">
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

    <el-dialog v-model="showDeleteDialog" title="注销账号" width="400px">
      <el-alert
        type="error"
        :closable="false"
        show-icon
        title="警告"
        description="注销账号后，您的所有数据将被清除且无法恢复，请谨慎操作！"
      />
      <el-form :model="deleteForm" label-width="80px" class="mt-4">
        <el-form-item label="密码确认">
          <el-input v-model="deleteForm.password" type="password" show-password placeholder="请输入密码确认注销" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDeleteAccount" :loading="saving">确认注销</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAvatarDialog" title="更换头像" width="400px">
      <el-form label-width="80px">
        <el-form-item label="头像URL">
          <el-input v-model="avatarUrl" placeholder="请输入头像图片URL" />
        </el-form-item>
        <el-form-item label="预览">
          <el-avatar :size="80" :src="avatarUrl || defaultAvatar" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAvatarDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateAvatar" :loading="saving">确认更换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Message, Phone, Calendar, TrendCharts, DataAnalysis, Trophy, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
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
} from '@/api/user'
import { getUserProfile } from '@/api/learning'

const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeTab = ref('profile')
const saving = ref(false)
const userInfo = ref({})
const learningStats = ref({})
const securityInfo = ref({})

const profileFormRef = ref(null)
const passwordFormRef = ref(null)

const showPasswordDialog = ref(false)
const showEmailDialog = ref(false)
const showPhoneDialog = ref(false)
const showDeleteDialog = ref(false)
const showAvatarDialog = ref(false)

const emailCountdown = ref(0)
const phoneCountdown = ref(0)

const profileForm = reactive({
  realName: '',
  email: '',
  phone: '',
})

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
})

const avatarUrl = ref('')

const profileRules = {
  realName: [{ max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
}

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
        if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
}

const fetchUserInfo = async () => {
  try {
    const res = await getCurrentUser()
    userInfo.value = res.data || {}
    profileForm.realName = userInfo.value.realName || ''
    profileForm.email = userInfo.value.email || ''
    profileForm.phone = userInfo.value.phone || ''
    avatarUrl.value = userInfo.value.avatar || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const fetchLearningStats = async () => {
  try {
    const res = await getUserProfile()
    learningStats.value = res.data || {}
  } catch (error) {
    console.error('获取学习统计失败:', error)
  }
}

const fetchSecurityInfo = async () => {
  try {
    const res = await getSecurityInfo()
    securityInfo.value = res.data || {}
  } catch (error) {
    console.error('获取安全信息失败:', error)
  }
}

const saveProfile = async () => {
  try {
    await profileFormRef.value?.validate()
    saving.value = true
    await updateProfile(profileForm)
    ElMessage.success('保存成功')
    fetchUserInfo()
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone,
    })
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('保存失败')
  } finally {
    saving.value = false
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
    fetchUserInfo()
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
    fetchUserInfo()
    fetchSecurityInfo()
  } catch (error) {
    ElMessage.error('绑定失败')
  } finally {
    saving.value = false
  }
}

const handleUpdateAvatar = async () => {
  if (!avatarUrl.value) {
    ElMessage.warning('请输入头像URL')
    return
  }
  try {
    saving.value = true
    await uploadAvatar(avatarUrl.value)
    ElMessage.success('头像更新成功')
    showAvatarDialog.value = false
    fetchUserInfo()
    userStore.setUserInfo({
      ...userStore.userInfo,
      avatar: avatarUrl.value,
    })
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    saving.value = false
  }
}

const handleDeleteAccount = async () => {
  if (!deleteForm.password) {
    ElMessage.warning('请输入密码')
    return
  }
  try {
    await ElMessageBox.confirm('确定要注销账号吗？此操作不可恢复！', '最后确认', { type: 'error' })
    saving.value = true
    await deleteAccount(deleteForm.password)
    ElMessage.success('账号已注销')
    userStore.logout()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(error.message || '注销失败')
  } finally {
    saving.value = false
  }
}

const getRoleName = (role) => {
  const roleMap = {
    ADMIN: '管理员',
    INSTRUCTOR: '讲师',
    STUDENT: '学员',
  }
  return roleMap[role] || '用户'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatStudyTime = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

onMounted(() => {
  fetchUserInfo()
  fetchLearningStats()
  fetchSecurityInfo()
})
</script>

