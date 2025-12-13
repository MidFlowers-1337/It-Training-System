<template>
  <div class="personal-center">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar">
                {{ userInfo.realName?.charAt(0) || userInfo.username?.charAt(0) || 'U' }}
              </el-avatar>
              <el-button 
                type="primary" 
                link 
                class="change-avatar-btn"
                @click="showAvatarDialog = true"
              >
                更换头像
              </el-button>
            </div>
            <h2 class="username">{{ userInfo.realName || userInfo.username }}</h2>
            <p class="user-id">@{{ userInfo.username }}</p>
            <el-tag :type="getRoleTagType(userInfo.role)">
              {{ getRoleName(userInfo.role) }}
            </el-tag>
          </div>
          
          <el-divider />
          
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">{{ learningStats.totalStudyMinutes || 0 }}</div>
              <div class="stat-label">学习时长(分钟)</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ learningStats.completedCourses || 0 }}</div>
              <div class="stat-label">完成课程</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ learningStats.achievementCount || 0 }}</div>
              <div class="stat-label">获得成就</div>
            </div>
          </div>
          
          <el-divider />
          
          <div class="profile-info-list">
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>{{ userInfo.email || '未绑定邮箱' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>{{ userInfo.phone || '未绑定手机' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>注册于 {{ formatDate(userInfo.createdAt) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧内容区 -->
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <!-- 基本资料 -->
            <el-tab-pane label="基本资料" name="profile">
              <el-form 
                ref="profileFormRef"
                :model="profileForm" 
                :rules="profileRules"
                label-width="100px"
                class="profile-form"
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
                  <el-button type="primary" @click="saveProfile" :loading="saving">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 账号安全 -->
            <el-tab-pane label="账号安全" name="security">
              <div class="security-section">
                <div class="security-item">
                  <div class="security-info">
                    <h4>登录密码</h4>
                    <p>定期更换密码可以保护账号安全</p>
                  </div>
                  <el-button type="primary" @click="showPasswordDialog = true">
                    修改密码
                  </el-button>
                </div>
                
                <el-divider />
                
                <div class="security-item">
                  <div class="security-info">
                    <h4>绑定邮箱</h4>
                    <p v-if="securityInfo.emailBound">
                      已绑定: {{ securityInfo.email }}
                    </p>
                    <p v-else>未绑定邮箱，绑定后可用于找回密码</p>
                  </div>
                  <el-button @click="showEmailDialog = true">
                    {{ securityInfo.emailBound ? '更换邮箱' : '绑定邮箱' }}
                  </el-button>
                </div>
                
                <el-divider />
                
                <div class="security-item">
                  <div class="security-info">
                    <h4>绑定手机</h4>
                    <p v-if="securityInfo.phoneBound">
                      已绑定: {{ securityInfo.phone }}
                    </p>
                    <p v-else>未绑定手机，绑定后可用于找回密码</p>
                  </div>
                  <el-button @click="showPhoneDialog = true">
                    {{ securityInfo.phoneBound ? '更换手机' : '绑定手机' }}
                  </el-button>
                </div>
                
                <el-divider />
                
                <div class="security-item danger">
                  <div class="security-info">
                    <h4>注销账号</h4>
                    <p>注销后账号将无法恢复，请谨慎操作</p>
                  </div>
                  <el-button type="danger" @click="showDeleteDialog = true">
                    注销账号
                  </el-button>
                </div>
              </div>
            </el-tab-pane>

            <!-- 学习数据 -->
            <el-tab-pane label="学习数据" name="learning">
              <div class="learning-data">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="总学习时长">
                    {{ formatStudyTime(learningStats.totalStudyMinutes) }}
                  </el-descriptions-item>
                  <el-descriptions-item label="完成课程数">
                    {{ learningStats.completedCourses || 0 }} 门
                  </el-descriptions-item>
                  <el-descriptions-item label="进行中课程">
                    {{ learningStats.inProgressCourses || 0 }} 门
                  </el-descriptions-item>
                  <el-descriptions-item label="获得成就">
                    {{ learningStats.achievementCount || 0 }} 个
                  </el-descriptions-item>
                  <el-descriptions-item label="连续打卡">
                    {{ learningStats.currentStreak || 0 }} 天
                  </el-descriptions-item>
                  <el-descriptions-item label="最长连续打卡">
                    {{ learningStats.maxStreak || 0 }} 天
                  </el-descriptions-item>
                  <el-descriptions-item label="成就积分">
                    {{ learningStats.achievementPoints || 0 }} 分
                  </el-descriptions-item>
                  <el-descriptions-item label="学习等级">
                    {{ learningStats.levelName || '学习新手' }}
                  </el-descriptions-item>
                </el-descriptions>
                
                <div class="quick-links">
                  <h4>快捷入口</h4>
                  <el-row :gutter="20">
                    <el-col :span="6">
                      <el-card shadow="hover" class="link-card" @click="$router.push('/learning')">
                        <el-icon :size="32"><TrendCharts /></el-icon>
                        <span>学习中心</span>
                      </el-card>
                    </el-col>
                    <el-col :span="6">
                      <el-card shadow="hover" class="link-card" @click="$router.push('/learning-report')">
                        <el-icon :size="32"><DataAnalysis /></el-icon>
                        <span>学习报告</span>
                      </el-card>
                    </el-col>
                    <el-col :span="6">
                      <el-card shadow="hover" class="link-card" @click="$router.push('/achievements')">
                        <el-icon :size="32"><Trophy /></el-icon>
                        <span>我的成就</span>
                      </el-card>
                    </el-col>
                    <el-col :span="6">
                      <el-card shadow="hover" class="link-card" @click="$router.push('/profile')">
                        <el-icon :size="32"><User /></el-icon>
                        <span>我的画像</span>
                      </el-card>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

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
            placeholder="请输入新密码"
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
    <el-dialog v-model="showDeleteDialog" title="注销账号" width="400px">
      <el-alert 
        type="error" 
        :closable="false"
        show-icon
        title="警告"
        description="注销账号后，您的所有数据将被清除且无法恢复，请谨慎操作！"
      />
      <el-form :model="deleteForm" label-width="80px" class="mt-20">
        <el-form-item label="密码确认">
          <el-input 
            v-model="deleteForm.password" 
            type="password" 
            show-password
            placeholder="请输入密码确认注销"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDeleteAccount" :loading="saving">
          确认注销
        </el-button>
      </template>
    </el-dialog>

    <!-- 更换头像对话框 -->
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
        <el-button type="primary" @click="handleUpdateAvatar" :loading="saving">
          确认更换
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Message, Phone, Calendar, TrendCharts, DataAnalysis, Trophy, User 
} from '@element-plus/icons-vue'
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
  deleteAccount
} from '@/api/user'
import { getUserProfile } from '@/api/learning'

const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeTab = ref('profile')
const saving = ref(false)
const userInfo = ref({})
const learningStats = ref({})
const securityInfo = ref({})

// 表单引用
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

// 对话框状态
const showPasswordDialog = ref(false)
const showEmailDialog = ref(false)
const showPhoneDialog = ref(false)
const showDeleteDialog = ref(false)
const showAvatarDialog = ref(false)

// 倒计时
const emailCountdown = ref(0)
const phoneCountdown = ref(0)

// 表单数据
const profileForm = reactive({
  realName: '',
  email: '',
  phone: ''
})

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
  password: ''
})

const avatarUrl = ref('')

// 表单验证规则
const profileRules = {
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

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

// 获取用户信息
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

// 获取学习统计
const fetchLearningStats = async () => {
  try {
    const res = await getUserProfile()
    learningStats.value = res.data || {}
  } catch (error) {
    console.error('获取学习统计失败:', error)
  }
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

// 保存个人资料
const saveProfile = async () => {
  try {
    await profileFormRef.value?.validate()
    saving.value = true
    await updateProfile(profileForm)
    ElMessage.success('保存成功')
    fetchUserInfo()
    // 更新 store 中的用户信息
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.realName,
      email: profileForm.email,
      phone: profileForm.phone
    })
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
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
    // 清空表单
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    // 退出登录
    userStore.logout()
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
    fetchUserInfo()
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
    fetchUserInfo()
    fetchSecurityInfo()
  } catch (error) {
    ElMessage.error('绑定失败')
  } finally {
    saving.value = false
  }
}

// 更新头像
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
      avatar: avatarUrl.value
    })
  } catch (error) {
    ElMessage.error('更新失败')
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
  try {
    await ElMessageBox.confirm(
      '确定要注销账号吗？此操作不可恢复！',
      '最后确认',
      { type: 'error' }
    )
    saving.value = true
    await deleteAccount(deleteForm.password)
    ElMessage.success('账号已注销')
    userStore.logout()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '注销失败')
    }
  } finally {
    saving.value = false
  }
}

// 工具函数
const getRoleName = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'INSTRUCTOR': '讲师',
    'STUDENT': '学员'
  }
  return roleMap[role] || '用户'
}

const getRoleTagType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'INSTRUCTOR': 'warning',
    'STUDENT': 'success'
  }
  return typeMap[role] || 'info'
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

<style scoped>
.personal-center {
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.change-avatar-btn {
  display: block;
  margin-top: 10px;
}

.username {
  margin: 15px 0 5px;
  font-size: 20px;
}

.user-id {
  color: #999;
  margin-bottom: 10px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.profile-info-list {
  text-align: left;
  padding: 0 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  color: #666;
}

.profile-form {
  max-width: 500px;
  padding: 20px;
}

.security-section {
  padding: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.security-item.danger .security-info h4 {
  color: #F56C6C;
}

.security-info h4 {
  margin: 0 0 5px;
}

.security-info p {
  margin: 0;
  color: #999;
  font-size: 13px;
}

.learning-data {
  padding: 20px;
}

.quick-links {
  margin-top: 30px;
}

.quick-links h4 {
  margin-bottom: 15px;
}

.link-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.link-card:hover {
  transform: translateY(-5px);
}

.link-card .el-icon {
  color: #409EFF;
  margin-bottom: 10px;
}

.link-card span {
  display: block;
  font-size: 14px;
}

.mt-20 {
  margin-top: 20px;
}
</style>