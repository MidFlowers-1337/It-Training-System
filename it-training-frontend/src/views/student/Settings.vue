<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账号设置</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" tab-position="left">
        <!-- 账号安全 -->
        <el-tab-pane label="账号安全" name="security">
          <div class="settings-section">
            <h3>账号安全</h3>
            
            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Lock /></el-icon>
                  <span>登录密码</span>
                </div>
                <p class="setting-desc">定期更换密码可以保护账号安全</p>
              </div>
              <el-button @click="showPasswordDialog = true">修改密码</el-button>
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Message /></el-icon>
                  <span>绑定邮箱</span>
                </div>
                <p class="setting-desc" v-if="securityInfo.emailBound">
                  已绑定: {{ securityInfo.email }}
                </p>
                <p class="setting-desc" v-else>未绑定邮箱，绑定后可用于找回密码</p>
              </div>
              <el-button @click="showEmailDialog = true">
                {{ securityInfo.emailBound ? '更换' : '绑定' }}
              </el-button>
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Phone /></el-icon>
                  <span>绑定手机</span>
                </div>
                <p class="setting-desc" v-if="securityInfo.phoneBound">
                  已绑定: {{ securityInfo.phone }}
                </p>
                <p class="setting-desc" v-else>未绑定手机，绑定后可用于找回密码</p>
              </div>
              <el-button @click="showPhoneDialog = true">
                {{ securityInfo.phoneBound ? '更换' : '绑定' }}
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 通知设置 -->
        <el-tab-pane label="通知设置" name="notification">
          <div class="settings-section">
            <h3>通知设置</h3>
            
            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Bell /></el-icon>
                  <span>系统通知</span>
                </div>
                <p class="setting-desc">接收系统公告和重要通知</p>
              </div>
              <el-switch v-model="notificationSettings.system" />
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Reading /></el-icon>
                  <span>课程通知</span>
                </div>
                <p class="setting-desc">接收课程更新、开课提醒等通知</p>
              </div>
              <el-switch v-model="notificationSettings.course" />
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Trophy /></el-icon>
                  <span>成就通知</span>
                </div>
                <p class="setting-desc">获得成就时接收通知</p>
              </div>
              <el-switch v-model="notificationSettings.achievement" />
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Promotion /></el-icon>
                  <span>营销通知</span>
                </div>
                <p class="setting-desc">接收优惠活动、推荐课程等信息</p>
              </div>
              <el-switch v-model="notificationSettings.marketing" />
            </div>

            <div class="save-btn-wrapper">
              <el-button type="primary" @click="saveNotificationSettings">
                保存设置
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 隐私设置 -->
        <el-tab-pane label="隐私设置" name="privacy">
          <div class="settings-section">
            <h3>隐私设置</h3>
            
            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><View /></el-icon>
                  <span>公开学习数据</span>
                </div>
                <p class="setting-desc">允许其他用户查看您的学习统计</p>
              </div>
              <el-switch v-model="privacySettings.showLearningData" />
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Medal /></el-icon>
                  <span>公开成就</span>
                </div>
                <p class="setting-desc">允许其他用户查看您获得的成就</p>
              </div>
              <el-switch v-model="privacySettings.showAchievements" />
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><User /></el-icon>
                  <span>公开个人资料</span>
                </div>
                <p class="setting-desc">允许其他用户查看您的个人资料</p>
              </div>
              <el-switch v-model="privacySettings.showProfile" />
            </div>

            <div class="save-btn-wrapper">
              <el-button type="primary" @click="savePrivacySettings">
                保存设置
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 外观设置 -->
        <el-tab-pane label="外观设置" name="appearance">
          <div class="settings-section">
            <h3>外观设置</h3>
            
            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Sunny /></el-icon>
                  <span>主题模式</span>
                </div>
                <p class="setting-desc">选择您喜欢的界面主题</p>
              </div>
              <el-radio-group v-model="appearanceSettings.theme">
                <el-radio-button label="light">浅色</el-radio-button>
                <el-radio-button label="dark">深色</el-radio-button>
                <el-radio-button label="auto">跟随系统</el-radio-button>
              </el-radio-group>
            </div>

            <el-divider />

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Grid /></el-icon>
                  <span>首页布局</span>
                </div>
                <p class="setting-desc">选择首页的展示布局</p>
              </div>
              <el-radio-group v-model="appearanceSettings.layout">
                <el-radio-button label="card">卡片式</el-radio-button>
                <el-radio-button label="list">列表式</el-radio-button>
              </el-radio-group>
            </div>

            <div class="save-btn-wrapper">
              <el-button type="primary" @click="saveAppearanceSettings">
                保存设置
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 危险操作 -->
        <el-tab-pane label="危险操作" name="danger">
          <div class="settings-section danger-section">
            <h3>危险操作</h3>
            <el-alert 
              type="warning" 
              :closable="false"
              show-icon
              title="以下操作不可逆，请谨慎操作"
            />
            
            <div class="setting-item danger">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><Delete /></el-icon>
                  <span>清除学习数据</span>
                </div>
                <p class="setting-desc">清除所有学习进度、打卡记录等数据</p>
              </div>
              <el-button type="danger" plain @click="handleClearData">
                清除数据
              </el-button>
            </div>

            <el-divider />

            <div class="setting-item danger">
              <div class="setting-info">
                <div class="setting-title">
                  <el-icon><CircleClose /></el-icon>
                  <span>注销账号</span>
                </div>
                <p class="setting-desc">永久删除账号及所有相关数据，此操作不可恢复</p>
              </div>
              <el-button type="danger" @click="showDeleteDialog = true">
                注销账号
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm" 
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码（6-20位）"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="saving">
          确认修改
        </el-button>
      </template>
    </el-dialog>

    <!-- 绑定邮箱对话框 -->
    <el-dialog v-model="showEmailDialog" title="绑定邮箱" width="400px">
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
        <el-button type="primary" @click="handleBindEmail" :loading="saving">
          确认绑定
        </el-button>
      </template>
    </el-dialog>

    <!-- 绑定手机对话框 -->
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
        <el-button type="primary" @click="handleBindPhone" :loading="saving">
          确认绑定
        </el-button>
      </template>
    </el-dialog>

    <!-- 注销账号对话框 -->
    <el-dialog v-model="showDeleteDialog" title="注销账号" width="450px">
      <el-alert 
        type="error" 
        :closable="false"
        show-icon
        class="mb-20"
      >
        <template #title>
          <strong>警告：此操作不可逆！</strong>
        </template>
        <template #default>
          <p>注销账号后，以下数据将被永久删除：</p>
          <ul>
            <li>个人资料和账号信息</li>
            <li>所有学习进度和记录</li>
            <li>获得的成就和积分</li>
            <li>报名的课程信息</li>
          </ul>
        </template>
      </el-alert>
      <el-form :model="deleteForm" label-width="100px">
        <el-form-item label="输入密码">
          <el-input 
            v-model="deleteForm.password" 
            type="password" 
            show-password
            placeholder="请输入密码确认注销"
          />
        </el-form-item>
        <el-form-item label="确认注销">
          <el-input
            v-model="deleteForm.confirm"
            placeholder="请输入 确认注销 以继续"
          />
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
  Lock, Message, Phone, Bell, Reading, Trophy, Promotion,
  View, Medal, User, Sunny, Grid, Delete, CircleClose
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
  clearLearningData
} from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('security')
const saving = ref(false)
const securityInfo = ref({})

// 对话框状态
const showPasswordDialog = ref(false)
const showEmailDialog = ref(false)
const showPhoneDialog = ref(false)
const showDeleteDialog = ref(false)

// 倒计时
const emailCountdown = ref(0)
const phoneCountdown = ref(0)

// 表单引用
const passwordFormRef = ref(null)

// 表单数据
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const phoneForm = reactive({
  phone: '',
  code: ''
})

const deleteForm = reactive({
  password: '',
  confirm: ''
})

// 设置数据
const notificationSettings = reactive({
  system: true,
  course: true,
  achievement: true,
  marketing: false
})

const privacySettings = reactive({
  showLearningData: true,
  showAchievements: true,
  showProfile: true
})

const appearanceSettings = reactive({
  theme: 'light',
  layout: 'card'
})

// 表单验证规则
const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
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
      trigger: 'blur'
    }
  ]
}

// 获取安全信息
const fetchSecurityInfo = async () => {
  try {
    const res = await getSecurityInfo()
    securityInfo.value = res.data || {}
  } catch (error) {
    console.error('获取安全信息失败:', error)
  }
}

// 修改密码
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
    if (error !== 'cancel') {
      ElMessage.error(error.message || '修改失败')
    }
  } finally {
    saving.value = false
  }
}

// 发送邮箱验证码
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
      if (emailCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

// 绑定邮箱
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

// 发送手机验证码
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
      if (phoneCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

// 绑定手机
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

// 保存通知设置
const saveNotificationSettings = () => {
  // 保存到本地存储
  localStorage.setItem('notificationSettings', JSON.stringify(notificationSettings))
  ElMessage.success('通知设置已保存')
}

// 保存隐私设置
const savePrivacySettings = () => {
  localStorage.setItem('privacySettings', JSON.stringify(privacySettings))
  ElMessage.success('隐私设置已保存')
}

// 保存外观设置
const saveAppearanceSettings = () => {
  localStorage.setItem('appearanceSettings', JSON.stringify(appearanceSettings))
  ElMessage.success('外观设置已保存')
}

// 清除学习数据
const handleClearData = async () => {
  try {
    const { value } = await ElMessageBox.prompt(
      '此操作将清除所有学习进度、打卡记录、成就等数据,不可恢复!请输入密码确认:',
      '清除学习数据',
      {
        confirmButtonText: '确认清除',
        cancelButtonText: '取消',
        inputType: 'password',
        inputPlaceholder: '请输入密码',
        type: 'warning'
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
    if (error !== 'cancel') {
      ElMessage.error(error.message || '清除失败')
    }
  } finally {
    saving.value = false
  }
}

// 注销账号
const handleDeleteAccount = async () => {
  if (!deleteForm.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (deleteForm.confirm !== '确认注销') {
    ElMessage.warning('请输入"确认注销"以继续')
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

// 加载本地设置
const loadLocalSettings = () => {
  const notification = localStorage.getItem('notificationSettings')
  if (notification) {
    Object.assign(notificationSettings, JSON.parse(notification))
  }
  
  const privacy = localStorage.getItem('privacySettings')
  if (privacy) {
    Object.assign(privacySettings, JSON.parse(privacy))
  }
  
  const appearance = localStorage.getItem('appearanceSettings')
  if (appearance) {
    Object.assign(appearanceSettings, JSON.parse(appearance))
  }
}

onMounted(() => {
  fetchSecurityInfo()
  loadLocalSettings()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.settings-section {
  padding: 20px;
}

.settings-section h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.setting-info {
  flex: 1;
}

.setting-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 5px;
}

.setting-desc {
  color: #999;
  font-size: 13px;
  margin: 0;
}

.save-btn-wrapper {
  margin-top: 30px;
  text-align: right;
}

.danger-section .el-alert {
  margin-bottom: 20px;
}

.setting-item.danger .setting-title {
  color: #F56C6C;
}

.mb-20 {
  margin-bottom: 20px;
}

:deep(.el-tabs__item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-tabs__content) {
  padding-left: 20px;
}
</style>