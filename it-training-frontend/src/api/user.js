import request from '@/utils/request'

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/v1/users',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserById(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/v1/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/v1/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, data) {
  return request({
    url: `/v1/users/${id}/password`,
    method: 'patch',
    data
  })
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request({
    url: `/v1/users/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

// ==================== 个人中心 API ====================

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/v1/profile',
    method: 'get'
  })
}

/**
 * 更新个人资料
 * @param {Object} data 资料数据
 */
export function updateProfile(data) {
  return request({
    url: '/v1/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data 密码数据
 */
export function changePassword(data) {
  return request({
    url: '/v1/profile/password',
    method: 'post',
    data
  })
}

/**
 * 上传头像
 * @param {string} avatarUrl 头像URL
 */
export function uploadAvatar(avatarUrl) {
  return request({
    url: '/v1/profile/avatar',
    method: 'post',
    params: { avatarUrl }
  })
}

/**
 * 获取账号安全信息
 */
export function getSecurityInfo() {
  return request({
    url: '/v1/profile/security',
    method: 'get'
  })
}

/**
 * 绑定邮箱
 * @param {string} email 邮箱
 * @param {string} code 验证码
 */
export function bindEmail(email, code) {
  return request({
    url: '/v1/profile/bind-email',
    method: 'post',
    params: { email, code }
  })
}

/**
 * 绑定手机
 * @param {string} phone 手机号
 * @param {string} code 验证码
 */
export function bindPhone(phone, code) {
  return request({
    url: '/v1/profile/bind-phone',
    method: 'post',
    params: { phone, code }
  })
}

/**
 * 发送邮箱验证码
 * @param {string} email 邮箱
 */
export function sendEmailCode(email) {
  return request({
    url: '/v1/profile/send-email-code',
    method: 'post',
    params: { email }
  })
}

/**
 * 发送手机验证码
 * @param {string} phone 手机号
 */
export function sendPhoneCode(phone) {
  return request({
    url: '/v1/profile/send-phone-code',
    method: 'post',
    params: { phone }
  })
}

/**
 * 注销账号
 * @param {string} password 密码确认
 */
export function deleteAccount(password) {
  return request({
    url: '/v1/profile',
    method: 'delete',
    params: { password }
  })
}

/**
 * 清除学习数据
 * @param {string} password 密码确认
 */
export function clearLearningData(password) {
  return request({
    url: '/v1/profile/clear-data',
    method: 'post',
    params: { password }
  })
}
