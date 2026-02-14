<template>
  <div>
    <!-- Skeleton Loading -->
    <div v-if="!user" class="max-w-2xl mx-auto animate-pulse space-y-5">
      <div class="flex flex-col items-center gap-3 py-8">
        <div class="w-24 h-24 rounded-full" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
        <div class="h-5 w-32 rounded" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
        <div class="h-4 w-48 rounded" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
      </div>
      <div v-for="n in 3" :key="n" class="h-36 rounded-xl"
           :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
    </div>

    <template v-else>
    <!-- ================================================================
         ☀️ LIGHT — Apple Account：居中头像 + Inset 分组列表 + 圆角行
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="max-w-2xl mx-auto space-y-6">
        <!-- Profile Hero (Apple-style centered) -->
        <div class="text-center py-8">
          <div class="relative inline-block group cursor-pointer" @click="triggerAvatarUpload">
            <div class="w-24 h-24 rounded-full bg-gradient-to-br from-[#635BFF]/10 to-[#0A2540]/5 flex items-center justify-center text-3xl text-[#635BFF] font-bold overflow-hidden mx-auto ring-4 ring-white shadow-lg">
              <img v-if="user?.avatar" :src="user.avatar" alt="avatar" class="w-full h-full object-cover" />
              <span v-else>{{ initials }}</span>
            </div>
            <div class="absolute inset-0 rounded-full bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <Camera class="w-5 h-5 text-white" :stroke-width="1.75" />
            </div>
            <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarUpload" />
          </div>
          <h2 class="text-xl font-bold text-[#0A2540] mt-4">{{ user?.realName || user?.username || '用户' }}</h2>
          <p class="text-sm text-[#425466] mt-0.5">{{ user?.email || '未绑定邮箱' }}</p>
          <span class="inline-flex items-center gap-1 px-2.5 py-1 mt-2 rounded-full text-xs font-medium" :class="roleBadgeStripe">
            <Shield class="w-3 h-3" :stroke-width="2" /> {{ roleLabel }}
          </span>
        </div>

        <!-- Inset Group: Personal Information -->
        <div class="apple-inset-group">
          <div class="apple-inset-header">
            <UserIcon class="w-4 h-4 text-[#635BFF]" :stroke-width="1.75" />
            <span>个人信息</span>
            <button v-if="!editingProfile" class="ml-auto text-xs text-[#635BFF] font-medium cursor-pointer hover:underline" @click="startEditProfile">编辑</button>
            <div v-else class="ml-auto flex gap-2">
              <button class="text-xs text-[#8898AA] cursor-pointer hover:underline" @click="editingProfile = false">取消</button>
              <button class="text-xs text-[#635BFF] font-medium cursor-pointer hover:underline" @click="saveProfile" :disabled="savingProfile">{{ savingProfile ? '保存中...' : '保存' }}</button>
            </div>
          </div>
          <div class="apple-inset-rows">
            <div class="apple-row"><span class="apple-row-label">用户名</span><span class="apple-row-value">{{ user?.username }}</span></div>
            <div class="apple-row">
              <span class="apple-row-label">真实姓名</span>
              <input v-if="editingProfile" v-model="profileForm.realName" class="apple-row-input" placeholder="请输入真实姓名" />
              <span v-else class="apple-row-value">{{ user?.realName || '未设置' }}</span>
            </div>
            <div class="apple-row">
              <span class="apple-row-label">邮箱</span>
              <div class="flex items-center gap-2">
                <span class="apple-row-value">{{ user?.email || '未绑定' }}</span>
                <span v-if="user?.email" class="w-2 h-2 rounded-full bg-emerald-500"></span>
              </div>
            </div>
            <div class="apple-row">
              <span class="apple-row-label">手机号</span>
              <span class="apple-row-value">{{ user?.phone ? maskPhone(user.phone) : '未绑定' }}</span>
            </div>
            <div class="apple-row border-none">
              <span class="apple-row-label">注册时间</span>
              <span class="apple-row-value text-[#8898AA]">{{ fmtDate(user?.createdAt) }}</span>
            </div>
          </div>
        </div>

        <!-- Inset Group: Sign-In & Security -->
        <div class="apple-inset-group">
          <div class="apple-inset-header">
            <Lock class="w-4 h-4 text-[#635BFF]" :stroke-width="1.75" />
            <span>登录与安全</span>
          </div>
          <div class="apple-inset-rows">
            <button class="apple-row apple-row-btn" @click="showPasswordDialog = true">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-[#635BFF]/10 flex items-center justify-center"><KeyRound class="w-4 h-4 text-[#635BFF]" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">登录密码</span>
              </div>
              <ChevronRight class="w-4 h-4 text-[#CBD5E1]" :stroke-width="1.75" />
            </button>
            <button class="apple-row apple-row-btn" @click="!user?.email && (showBindEmailDialog = true)">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-emerald-500/10 flex items-center justify-center"><Mail class="w-4 h-4 text-emerald-600" :stroke-width="1.75" /></div>
                <div class="text-left"><div class="text-sm text-[#0A2540]">绑定邮箱</div><div class="text-[11px] text-[#8898AA]">{{ user?.email || '用于找回密码' }}</div></div>
              </div>
              <span v-if="user?.email" class="text-xs text-emerald-600 font-medium">已验证</span>
              <ChevronRight v-else class="w-4 h-4 text-[#CBD5E1]" :stroke-width="1.75" />
            </button>
            <button class="apple-row apple-row-btn border-none" @click="!user?.phone && (showBindPhoneDialog = true)">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-blue-500/10 flex items-center justify-center"><Smartphone class="w-4 h-4 text-blue-600" :stroke-width="1.75" /></div>
                <div class="text-left"><div class="text-sm text-[#0A2540]">绑定手机</div><div class="text-[11px] text-[#8898AA]">{{ user?.phone ? maskPhone(user.phone) : '接收通知和验证' }}</div></div>
              </div>
              <span v-if="user?.phone" class="text-xs text-emerald-600 font-medium">已绑定</span>
              <ChevronRight v-else class="w-4 h-4 text-[#CBD5E1]" :stroke-width="1.75" />
            </button>
          </div>
        </div>

        <!-- Inset Group: Danger -->
        <div class="apple-inset-group apple-danger-group">
          <div class="apple-inset-header"><AlertTriangle class="w-4 h-4 text-red-500" :stroke-width="1.75" /><span class="text-red-600">账号管理</span></div>
          <div class="apple-inset-rows">
            <button class="apple-row apple-row-btn" @click="showClearDataDialog = true">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center"><Trash2 class="w-4 h-4 text-red-500" :stroke-width="1.75" /></div>
                <div class="text-left"><div class="text-sm text-red-600">清除学习数据</div><div class="text-[11px] text-[#8898AA]">删除进度、打卡、成就，不可恢复</div></div>
              </div>
              <ChevronRight class="w-4 h-4 text-[#CBD5E1]" :stroke-width="1.75" />
            </button>
            <button class="apple-row apple-row-btn border-none" @click="showDisableDialog = true">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center"><Power class="w-4 h-4 text-red-500" :stroke-width="1.75" /></div>
                <div class="text-left"><div class="text-sm text-red-600">停用账号</div><div class="text-[11px] text-[#8898AA]">数据保留 30 天后自动删除</div></div>
              </div>
              <ChevronRight class="w-4 h-4 text-[#CBD5E1]" :stroke-width="1.75" />
            </button>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord：Banner + 悬浮头像 + Tab 分段 + 在线状态
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="max-w-3xl mx-auto space-y-5">
        <!-- Banner + Avatar Card -->
        <div class="discord-profile-card">
          <div class="h-28 rounded-t-xl bg-gradient-to-r from-[#5865F2] via-[#EB459E] to-[#FEE75C] relative">
            <div class="absolute -bottom-10 left-5">
              <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
                <div class="w-20 h-20 rounded-full bg-[#111113] p-1">
                  <div class="w-full h-full rounded-full bg-[#5865F2]/20 flex items-center justify-center text-2xl text-[#5865F2] font-bold overflow-hidden">
                    <img v-if="user?.avatar" :src="user.avatar" alt="avatar" class="w-full h-full object-cover" />
                    <span v-else>{{ initials }}</span>
                  </div>
                </div>
                <div class="absolute inset-1 rounded-full bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <Camera class="w-5 h-5 text-white" :stroke-width="1.75" />
                </div>
                <div class="absolute bottom-0.5 right-0.5 w-5 h-5 rounded-full bg-[#111113] flex items-center justify-center">
                  <div class="w-3.5 h-3.5 rounded-full bg-[#23A55A]"></div>
                </div>
                <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarUpload" />
              </div>
            </div>
          </div>
          <div class="pt-14 px-5 pb-5">
            <div class="flex items-start justify-between">
              <div>
                <h2 class="text-xl font-bold text-[#EDEDED]">{{ user?.realName || user?.username || '用户' }}</h2>
                <p class="text-sm text-[#B5BAC1] mt-0.5">{{ user?.username }}<span class="text-[#4E5058]">#{{ String(user?.id || 0).padStart(4, '0') }}</span></p>
              </div>
              <span class="px-2 py-1 rounded text-[10px] font-semibold" :class="roleBadgeDark">{{ roleLabel }}</span>
            </div>
            <div class="flex gap-6 mt-4 pt-4 border-t border-white/[0.06]">
              <div><div class="text-[10px] text-[#B5BAC1] uppercase tracking-wider font-semibold">邮箱</div><div class="text-sm text-[#EDEDED] mt-0.5">{{ user?.email || '未绑定' }}</div></div>
              <div><div class="text-[10px] text-[#B5BAC1] uppercase tracking-wider font-semibold">手机</div><div class="text-sm text-[#EDEDED] mt-0.5">{{ user?.phone ? maskPhone(user.phone) : '未绑定' }}</div></div>
              <div><div class="text-[10px] text-[#B5BAC1] uppercase tracking-wider font-semibold">注册时间</div><div class="text-sm text-[#EDEDED] mt-0.5">{{ fmtDate(user?.createdAt) }}</div></div>
            </div>
          </div>
        </div>

        <!-- Tab Bar -->
        <div class="flex gap-1 bg-white/[0.02] p-1 rounded-lg">
          <button v-for="t in discordTabs" :key="t.key" @click="discordTab = t.key"
            :class="['flex-1 py-2 rounded-md text-xs font-medium transition-all cursor-pointer',
              discordTab === t.key ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] hover:text-[#EDEDED] hover:bg-white/[0.04]']">
            {{ t.label }}
          </button>
        </div>

        <!-- Tab: My Account -->
        <div v-if="discordTab === 'account'" class="discord-card">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-sm font-semibold text-[#EDEDED]">个人资料</h3>
            <button v-if="!editingProfile" class="discord-btn-outline" @click="startEditProfile">编辑</button>
            <div v-else class="flex gap-2">
              <button class="discord-btn-outline" @click="editingProfile = false">取消</button>
              <button class="discord-btn-primary" @click="saveProfile" :disabled="savingProfile">{{ savingProfile ? '...' : '保存' }}</button>
            </div>
          </div>
          <div class="space-y-3">
            <div class="discord-field"><span class="discord-field-label">用户名</span><span class="text-sm text-[#EDEDED]">{{ user?.username }}</span></div>
            <div class="discord-field">
              <span class="discord-field-label">显示名称</span>
              <input v-if="editingProfile" v-model="profileForm.realName" class="discord-input" placeholder="显示名称" />
              <span v-else class="text-sm text-[#EDEDED]">{{ user?.realName || '未设置' }}</span>
            </div>
            <div class="discord-field">
              <span class="discord-field-label">邮箱</span>
              <div class="flex items-center gap-2">
                <span class="text-sm text-[#EDEDED]">{{ user?.email || '未绑定' }}</span>
                <span v-if="user?.email" class="text-[9px] font-bold bg-[#23A55A]/15 text-[#23A55A] px-1.5 py-0.5 rounded">已验证</span>
              </div>
            </div>
            <div class="discord-field border-none"><span class="discord-field-label">手机号</span><span class="text-sm text-[#EDEDED]">{{ user?.phone ? maskPhone(user.phone) : '未绑定' }}</span></div>
          </div>
        </div>

        <!-- Tab: Security -->
        <div v-if="discordTab === 'security'" class="discord-card">
          <h3 class="text-sm font-semibold text-[#EDEDED] mb-4">密码和身份验证</h3>
          <div class="space-y-0">
            <div class="discord-sec-row">
              <div class="flex-1"><div class="text-sm text-[#EDEDED]">登录密码</div><div class="text-xs text-[#B5BAC1] mt-0.5">定期更换以确保安全</div></div>
              <button class="discord-btn-outline" @click="showPasswordDialog = true">更改</button>
            </div>
            <div class="discord-sec-row">
              <div class="flex-1"><div class="text-sm text-[#EDEDED]">邮箱验证</div><div class="text-xs text-[#B5BAC1] mt-0.5">{{ user?.email || '未绑定邮箱' }}</div></div>
              <span v-if="user?.email" class="text-xs text-[#23A55A] font-medium">✓ 已验证</span>
              <button v-else class="discord-btn-primary" @click="showBindEmailDialog = true">绑定</button>
            </div>
            <div class="discord-sec-row border-none">
              <div class="flex-1"><div class="text-sm text-[#EDEDED]">手机验证</div><div class="text-xs text-[#B5BAC1] mt-0.5">{{ user?.phone ? maskPhone(user.phone) : '未绑定手机' }}</div></div>
              <span v-if="user?.phone" class="text-xs text-[#23A55A] font-medium">✓ 已绑定</span>
              <button v-else class="discord-btn-primary" @click="showBindPhoneDialog = true">添加</button>
            </div>
          </div>
        </div>

        <!-- Tab: Data & Privacy -->
        <div v-if="discordTab === 'privacy'" class="discord-card border-red-500/20">
          <h3 class="text-sm font-semibold text-red-400 mb-4">数据与隐私</h3>
          <div class="space-y-0">
            <div class="discord-sec-row">
              <div class="flex-1"><div class="text-sm text-[#EDEDED]">清除学习数据</div><div class="text-xs text-[#B5BAC1] mt-0.5">删除学习进度、打卡、成就数据</div></div>
              <button class="discord-btn-danger" @click="showClearDataDialog = true">清除</button>
            </div>
            <div class="discord-sec-row border-none">
              <div class="flex-1"><div class="text-sm text-[#EDEDED]">停用账号</div><div class="text-xs text-[#B5BAC1] mt-0.5">停用后无法登录，数据保留 30 天</div></div>
              <button class="discord-btn-danger" @click="showDisableDialog = true">停用</button>
            </div>
          </div>
          <p class="text-[10px] text-[#B5BAC1] mt-4 pt-3 border-t border-white/[0.04]">⚠️ 以上操作不可撤销，请谨慎操作</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Profile：Cover 渐变 + 属性表 + Callout + Hint
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="max-w-2xl mx-auto space-y-5">
        <!-- Cover + Avatar -->
        <div class="relative">
          <div class="h-32 rounded-2xl bg-gradient-to-r from-[#FFC800]/30 via-[#FF9600]/20 to-[#D97706]/10"></div>
          <div class="absolute -bottom-8 left-6">
            <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
              <div class="w-20 h-20 rounded-2xl bg-white border-4 border-white shadow-lg flex items-center justify-center text-3xl text-[#D97706] font-extrabold overflow-hidden">
                <img v-if="user?.avatar" :src="user.avatar" alt="avatar" class="w-full h-full object-cover" />
                <span v-else>{{ initials }}</span>
              </div>
              <div class="absolute inset-0 rounded-2xl bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                <Camera class="w-5 h-5 text-white" :stroke-width="1.75" />
              </div>
              <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarUpload" />
            </div>
          </div>
        </div>

        <div class="pt-6">
          <h1 class="text-2xl font-extrabold text-[#292524]">{{ user?.realName || user?.username || '用户' }}</h1>
          <div class="flex items-center gap-2 mt-1">
            <span class="text-sm text-[#78716C]">{{ user?.email || '未绑定邮箱' }}</span>
            <span class="px-2 py-0.5 rounded-full text-xs font-bold" :class="roleBadgeWarm">{{ roleLabel }}</span>
          </div>
        </div>

        <!-- Notion-style Property Table -->
        <div class="notion-property-table">
          <div class="flex items-center justify-between mb-3">
            <h3 class="text-sm font-extrabold text-[#292524]">📋 属性</h3>
            <button v-if="!editingProfile" class="text-xs font-bold text-[#D97706] cursor-pointer hover:underline" @click="startEditProfile">编辑</button>
            <div v-else class="flex gap-2">
              <button class="text-xs text-[#78716C] cursor-pointer hover:underline" @click="editingProfile = false">取消</button>
              <button class="text-xs font-bold text-[#D97706] cursor-pointer hover:underline" @click="saveProfile" :disabled="savingProfile">{{ savingProfile ? '保存中...' : '保存' }}</button>
            </div>
          </div>
          <div class="space-y-0">
            <div class="notion-prop-row"><span class="notion-prop-key">👤 用户名</span><span class="notion-prop-val">{{ user?.username }}</span></div>
            <div class="notion-prop-row">
              <span class="notion-prop-key">✏️ 显示名称</span>
              <input v-if="editingProfile" v-model="profileForm.realName" class="notion-prop-input" placeholder="请输入名称" />
              <span v-else class="notion-prop-val">{{ user?.realName || '—' }}</span>
            </div>
            <div class="notion-prop-row">
              <span class="notion-prop-key">📧 邮箱</span>
              <div class="flex items-center gap-2">
                <span class="notion-prop-val">{{ user?.email || '未绑定' }}</span>
                <span v-if="user?.email" class="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-1.5 py-0.5 rounded-full">✓</span>
              </div>
            </div>
            <div class="notion-prop-row"><span class="notion-prop-key">📱 手机</span><span class="notion-prop-val">{{ user?.phone ? maskPhone(user.phone) : '未绑定' }}</span></div>
            <div class="notion-prop-row border-none"><span class="notion-prop-key">📅 注册时间</span><span class="notion-prop-val text-[#A8A29E]">{{ fmtDate(user?.createdAt) }}</span></div>
          </div>
        </div>

        <!-- Security Callout -->
        <div class="notion-callout">
          <div class="text-lg mr-2">🔒</div>
          <div class="flex-1">
            <h3 class="text-sm font-extrabold text-[#292524] mb-3">安全设置</h3>
            <div class="space-y-0">
              <div class="flex items-center justify-between py-2.5">
                <span class="text-sm text-[#292524]">登录密码</span>
                <button class="text-xs font-bold text-[#D97706] cursor-pointer hover:underline" @click="showPasswordDialog = true">修改</button>
              </div>
              <div class="flex items-center justify-between py-2.5 border-t border-[#E7E5E4]">
                <span class="text-sm text-[#292524]">绑定邮箱</span>
                <span v-if="user?.email" class="text-xs font-bold text-emerald-700">已绑定 ✓</span>
                <button v-else class="text-xs font-bold text-[#D97706] cursor-pointer hover:underline" @click="showBindEmailDialog = true">去绑定</button>
              </div>
              <div class="flex items-center justify-between py-2.5 border-t border-[#E7E5E4]">
                <span class="text-sm text-[#292524]">绑定手机</span>
                <span v-if="user?.phone" class="text-xs font-bold text-emerald-700">已绑定 ✓</span>
                <button v-else class="text-xs font-bold text-[#D97706] cursor-pointer hover:underline" @click="showBindPhoneDialog = true">去绑定</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Danger Hint Card -->
        <div class="notion-danger-hint">
          <div class="flex items-center gap-2 mb-3">
            <span class="text-lg">⚠️</span>
            <h3 class="text-sm font-extrabold text-red-700">危险操作</h3>
          </div>
          <div class="flex gap-3">
            <button class="flex-1 py-2.5 rounded-xl border-2 border-red-200 text-sm font-bold text-red-600 hover:bg-red-50 transition cursor-pointer" @click="showClearDataDialog = true">清除学习数据</button>
            <button class="flex-1 py-2.5 rounded-xl border-2 border-red-200 text-sm font-bold text-red-600 hover:bg-red-50 transition cursor-pointer" @click="showDisableDialog = true">停用账号</button>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Vercel Account：Tab 切换 + ENV 键值对 + Monospace
         ================================================================ -->
    <template v-else>
      <div class="max-w-3xl mx-auto space-y-5">
        <!-- Header + Tabs -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Account Settings</span>
          <div class="flex gap-0.5">
            <button v-for="t in proTabs" :key="t.key" @click="proTab = t.key"
              :class="['px-3 py-1.5 rounded text-[11px] font-mono cursor-pointer transition-all',
                proTab === t.key ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']">
              {{ t.label }}
            </button>
          </div>
        </div>

        <!-- Tab: General -->
        <template v-if="proTab === 'general'">
          <div class="vercel-panel">
            <div class="flex items-center gap-4 pb-4 border-b border-[#F1F5F9]">
              <div class="relative group cursor-pointer" @click="triggerAvatarUpload">
                <div class="w-14 h-14 rounded-full bg-[#F1F5F9] flex items-center justify-center text-xl text-[#0F172A] font-mono font-bold overflow-hidden">
                  <img v-if="user?.avatar" :src="user.avatar" alt="avatar" class="w-full h-full object-cover" />
                  <span v-else>{{ initials }}</span>
                </div>
                <div class="absolute inset-0 rounded-full bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"><Camera class="w-4 h-4 text-white" :stroke-width="1.75" /></div>
                <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarUpload" />
              </div>
              <div class="flex-1 min-w-0">
                <code class="text-sm font-semibold text-[#0F172A]">{{ user?.realName || user?.username }}</code>
                <div class="flex items-center gap-2 mt-0.5">
                  <code class="text-xs text-[#64748B]">{{ user?.email || 'no-email' }}</code>
                  <code class="text-[10px] px-1.5 py-0.5 rounded" :class="roleBadgePro">{{ roleMapPro[(user?.role ?? '') as keyof typeof roleMapPro] || user?.role }}</code>
                </div>
              </div>
              <button v-if="!editingProfile" class="vercel-btn-outline" @click="startEditProfile">Edit</button>
              <div v-else class="flex gap-2">
                <button class="vercel-btn-outline" @click="editingProfile = false">Cancel</button>
                <button class="vercel-btn-primary" @click="saveProfile" :disabled="savingProfile">{{ savingProfile ? '...' : 'Save' }}</button>
              </div>
            </div>
            <div class="mt-4 space-y-0">
              <div class="vercel-env-row"><code class="vercel-env-key">USERNAME</code><code class="vercel-env-val">{{ user?.username }}</code></div>
              <div class="vercel-env-row">
                <code class="vercel-env-key">DISPLAY_NAME</code>
                <input v-if="editingProfile" v-model="profileForm.realName" class="vercel-env-input" />
                <code v-else class="vercel-env-val">{{ user?.realName || '—' }}</code>
              </div>
              <div class="vercel-env-row">
                <code class="vercel-env-key">EMAIL</code>
                <div class="flex items-center gap-2">
                  <code class="vercel-env-val">{{ user?.email || '—' }}</code>
                  <span v-if="user?.email" class="text-[9px] font-mono text-emerald-600">● verified</span>
                </div>
              </div>
              <div class="vercel-env-row"><code class="vercel-env-key">PHONE</code><code class="vercel-env-val">{{ user?.phone ? maskPhone(user.phone) : '—' }}</code></div>
              <div class="vercel-env-row border-none"><code class="vercel-env-key">CREATED_AT</code><code class="vercel-env-val text-[#94A3B8]">{{ user?.createdAt ? new Date(user.createdAt).toISOString().split('T')[0] : '—' }}</code></div>
            </div>
          </div>
        </template>

        <!-- Tab: Security -->
        <template v-if="proTab === 'security'">
          <div class="vercel-panel">
            <h3 class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider mb-4">Authentication</h3>
            <div class="space-y-0">
              <div class="vercel-sec-row"><span class="text-sm text-[#0F172A] font-mono">Password</span><button class="vercel-btn-outline" @click="showPasswordDialog = true">Change</button></div>
              <div class="vercel-sec-row">
                <span class="text-sm text-[#0F172A] font-mono">Email</span>
                <div class="flex items-center gap-2">
                  <code class="text-xs text-[#64748B]">{{ user?.email || '—' }}</code>
                  <span v-if="user?.email" class="text-[9px] font-mono text-emerald-600">● verified</span>
                  <button v-else class="vercel-btn-outline" @click="showBindEmailDialog = true">Bind</button>
                </div>
              </div>
              <div class="vercel-sec-row border-none">
                <span class="text-sm text-[#0F172A] font-mono">Phone</span>
                <div class="flex items-center gap-2">
                  <code class="text-xs text-[#64748B]">{{ user?.phone ? maskPhone(user.phone) : '—' }}</code>
                  <span v-if="user?.phone" class="text-[9px] font-mono text-emerald-600">● linked</span>
                  <button v-else class="vercel-btn-outline" @click="showBindPhoneDialog = true">Add</button>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- Tab: Danger Zone -->
        <template v-if="proTab === 'danger'">
          <div class="vercel-panel border-red-200">
            <h3 class="text-xs font-semibold text-red-600 uppercase tracking-wider mb-4">Danger Zone</h3>
            <div class="space-y-0">
              <div class="vercel-sec-row">
                <div><span class="text-sm text-[#0F172A] font-mono">Clear Learning Data</span><p class="text-[11px] text-[#64748B] font-mono mt-0.5">Permanently delete all progress, checkins, achievements</p></div>
                <button class="vercel-btn-danger" @click="showClearDataDialog = true">Delete</button>
              </div>
              <div class="vercel-sec-row border-none">
                <div><span class="text-sm text-[#0F172A] font-mono">Disable Account</span><p class="text-[11px] text-[#64748B] font-mono mt-0.5">Account disabled, data retained for 30 days</p></div>
                <button class="vercel-btn-danger" @click="showDisableDialog = true">Disable</button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </template>
    </template>

    <!-- ======== Shared Dialogs (theme-neutral via CSS vars) ======== -->
    <Teleport to="body">
      <div v-if="showPasswordDialog" class="modal-overlay" @click.self="showPasswordDialog = false">
        <div class="modal-card"><h3 class="modal-title">修改密码</h3><form @submit.prevent="changePassword" class="space-y-4 mt-4"><div><label class="modal-label">当前密码</label><input v-model="pwForm.oldPassword" type="password" required class="modal-input" placeholder="请输入当前密码" /></div><div><label class="modal-label">新密码</label><input v-model="pwForm.newPassword" type="password" required class="modal-input" placeholder="至少 6 位" /></div><div class="flex justify-end gap-3 pt-2"><button type="button" class="modal-cancel" @click="showPasswordDialog = false">取消</button><button type="submit" class="modal-confirm" :disabled="pwLoading">{{ pwLoading ? '修改中...' : '确认修改' }}</button></div></form></div>
      </div>
    </Teleport>
    <Teleport to="body">
      <div v-if="showBindEmailDialog" class="modal-overlay" @click.self="showBindEmailDialog = false">
        <div class="modal-card"><h3 class="modal-title">绑定邮箱</h3><form @submit.prevent="bindEmail" class="space-y-4 mt-4"><div><label class="modal-label">邮箱地址</label><input v-model="emailForm.email" type="email" required class="modal-input" placeholder="you@example.com" /></div><div><label class="modal-label">验证码</label><div class="flex gap-2"><input v-model="emailForm.code" type="text" required class="modal-input flex-1" placeholder="6 位验证码" /><button type="button" class="modal-cancel whitespace-nowrap" @click="sendEmailCode" :disabled="emailCd > 0">{{ emailCd > 0 ? `${emailCd}s` : '发送验证码' }}</button></div></div><div class="flex justify-end gap-3 pt-2"><button type="button" class="modal-cancel" @click="showBindEmailDialog = false">取消</button><button type="submit" class="modal-confirm">确认绑定</button></div></form></div>
      </div>
    </Teleport>
    <Teleport to="body">
      <div v-if="showBindPhoneDialog" class="modal-overlay" @click.self="showBindPhoneDialog = false">
        <div class="modal-card"><h3 class="modal-title">绑定手机</h3><form @submit.prevent="bindPhone" class="space-y-4 mt-4"><div><label class="modal-label">手机号</label><input v-model="phoneForm.phone" type="tel" required class="modal-input" placeholder="请输入手机号" /></div><div><label class="modal-label">验证码</label><div class="flex gap-2"><input v-model="phoneForm.code" type="text" required class="modal-input flex-1" placeholder="6 位验证码" /><button type="button" class="modal-cancel whitespace-nowrap" @click="sendPhoneCode" :disabled="phoneCd > 0">{{ phoneCd > 0 ? `${phoneCd}s` : '发送验证码' }}</button></div></div><div class="flex justify-end gap-3 pt-2"><button type="button" class="modal-cancel" @click="showBindPhoneDialog = false">取消</button><button type="submit" class="modal-confirm">确认绑定</button></div></form></div>
      </div>
    </Teleport>
    <Teleport to="body">
      <div v-if="showClearDataDialog || showDisableDialog" class="modal-overlay" @click.self="showClearDataDialog = false; showDisableDialog = false">
        <div class="modal-card"><h3 class="modal-title">{{ showClearDataDialog ? '确认清除学习数据' : '确认停用账号' }}</h3><p class="text-sm text-[#64748B] my-3">{{ showClearDataDialog ? '此操作将清除所有学习进度、打卡记录、成就数据，不可恢复。' : '停用后无法登录，数据保留 30 天后自动删除。' }}</p><form @submit.prevent="showClearDataDialog ? clearData() : disableAccount()" class="space-y-4"><div><label class="modal-label">请输入密码确认</label><input v-model="dangerPassword" type="password" required class="modal-input" placeholder="输入当前密码" /></div><div class="flex justify-end gap-3 pt-2"><button type="button" class="modal-cancel" @click="showClearDataDialog = false; showDisableDialog = false">取消</button><button type="submit" class="modal-danger" :disabled="dangerLoading">{{ dangerLoading ? '处理中...' : (showClearDataDialog ? '确认清除' : '确认停用') }}</button></div></form></div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { profileApi } from '@/api/profile'
import { toast } from '@/composables/useToast'
import {
  Camera, User as UserIcon, Lock, Shield, Pencil, AlertTriangle,
  ChevronRight, KeyRound, Mail, Smartphone, Trash2, Power,
} from 'lucide-vue-next'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const user = computed(() => userStore.userInfo)
const initials = computed(() => user.value?.realName?.charAt(0) || user.value?.username?.charAt(0) || '?')

/* ── Roles ── */
const roleMap: Record<string, string> = { ADMIN: '管理员', INSTRUCTOR: '讲师', STUDENT: '学员' }
const roleMapPro: Record<string, string> = { ADMIN: 'admin', INSTRUCTOR: 'instructor', STUDENT: 'student' }
const roleLabel = computed(() => roleMap[user.value?.role || ''] || user.value?.role || '学员')
const roleBadgeStripe = computed(() => ({ ADMIN: 'bg-red-50 text-red-600', INSTRUCTOR: 'bg-amber-50 text-amber-600', STUDENT: 'bg-[#635BFF]/10 text-[#635BFF]' }[user.value?.role || ''] || 'bg-[#635BFF]/10 text-[#635BFF]'))
const roleBadgeDark = computed(() => ({ ADMIN: 'bg-red-500/10 text-red-400', INSTRUCTOR: 'bg-amber-500/10 text-amber-400', STUDENT: 'bg-[#5865F2]/15 text-[#5865F2]' }[user.value?.role || ''] || 'bg-[#5865F2]/15 text-[#5865F2]'))
const roleBadgeWarm = computed(() => ({ ADMIN: 'bg-red-50 text-red-700', INSTRUCTOR: 'bg-amber-50 text-amber-700', STUDENT: 'bg-[#D97706]/10 text-[#D97706]' }[user.value?.role || ''] || 'bg-[#D97706]/10 text-[#D97706]'))
const roleBadgePro = computed(() => ({ ADMIN: 'bg-red-50 text-red-700', INSTRUCTOR: 'bg-amber-50 text-amber-700', STUDENT: 'bg-[#0284C7]/10 text-[#0284C7]' }[user.value?.role || ''] || 'bg-[#0284C7]/10 text-[#0284C7]'))

/* ── Theme Tabs ── */
const discordTab = ref('account')
const discordTabs = [
  { key: 'account', label: '我的账号' },
  { key: 'security', label: '安全设置' },
  { key: 'privacy', label: '数据与隐私' },
]
const proTab = ref('general')
const proTabs = [
  { key: 'general', label: 'General' },
  { key: 'security', label: 'Security' },
  { key: 'danger', label: 'Danger Zone' },
]

/* ── Profile Edit ── */
const editingProfile = ref(false), savingProfile = ref(false), profileForm = ref({ realName: '' })
function startEditProfile() { profileForm.value.realName = user.value?.realName || ''; editingProfile.value = true }
async function saveProfile() {
  savingProfile.value = true
  try { await profileApi.update({ realName: profileForm.value.realName }); await userStore.fetchUserInfo(); toast.success('资料已更新'); editingProfile.value = false }
  catch (e: any) { toast.error(e.message || '保存失败') }
  finally { savingProfile.value = false }
}

/* ── Avatar Upload ── */
const avatarInput = ref<HTMLInputElement>()
function triggerAvatarUpload() { avatarInput.value?.click() }
async function handleAvatarUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]; if (!file) return
  if (file.size > 2 * 1024 * 1024) { toast.warning('图片大小不能超过 2MB'); return }
  try { await profileApi.uploadAvatar(file); await userStore.fetchUserInfo(); toast.success('头像已更新') }
  catch (e: any) { toast.error(e.message || '上传失败') }
}

/* ── Password Change ── */
const showPasswordDialog = ref(false), pwForm = ref({ oldPassword: '', newPassword: '' }), pwLoading = ref(false)
async function changePassword() {
  if (pwForm.value.newPassword.length < 6) { toast.warning('密码至少 6 位'); return }
  pwLoading.value = true
  try { await profileApi.changePassword(pwForm.value); toast.success('密码已修改'); showPasswordDialog.value = false; pwForm.value = { oldPassword: '', newPassword: '' } }
  catch (e: any) { toast.error(e.message || '修改失败') }
  finally { pwLoading.value = false }
}

/* ── Email Binding ── */
const showBindEmailDialog = ref(false), emailForm = ref({ email: '', code: '' }), emailCd = ref(0)
async function sendEmailCode() {
  if (!emailForm.value.email) { toast.warning('请输入邮箱'); return }
  try { await profileApi.sendEmailCode({ email: emailForm.value.email }); toast.success('验证码已发送'); emailCd.value = 60; const t = setInterval(() => { if (--emailCd.value <= 0) clearInterval(t) }, 1000) }
  catch (e: any) { toast.error(e.message || '发送失败') }
}
async function bindEmail() {
  try { await profileApi.bindEmail(emailForm.value); await userStore.fetchUserInfo(); toast.success('邮箱绑定成功'); showBindEmailDialog.value = false }
  catch (e: any) { toast.error(e.message || '绑定失败') }
}

/* ── Phone Binding ── */
const showBindPhoneDialog = ref(false), phoneForm = ref({ phone: '', code: '' }), phoneCd = ref(0)
async function sendPhoneCode() {
  if (!phoneForm.value.phone) { toast.warning('请输入手机号'); return }
  try { await profileApi.sendPhoneCode({ phone: phoneForm.value.phone }); toast.success('验证码已发送'); phoneCd.value = 60; const t = setInterval(() => { if (--phoneCd.value <= 0) clearInterval(t) }, 1000) }
  catch (e: any) { toast.error(e.message || '发送失败') }
}
async function bindPhone() {
  try { await profileApi.bindPhone(phoneForm.value); await userStore.fetchUserInfo(); toast.success('手机绑定成功'); showBindPhoneDialog.value = false }
  catch (e: any) { toast.error(e.message || '绑定失败') }
}

/* ── Danger Actions ── */
const showClearDataDialog = ref(false), showDisableDialog = ref(false), dangerPassword = ref(''), dangerLoading = ref(false)
async function clearData() {
  dangerLoading.value = true
  try { await profileApi.clearLearningData({ password: dangerPassword.value }); toast.success('学习数据已清除'); showClearDataDialog.value = false; dangerPassword.value = '' }
  catch (e: any) { toast.error(e.message || '操作失败') }
  finally { dangerLoading.value = false }
}
async function disableAccount() {
  dangerLoading.value = true
  try { await profileApi.disableAccount({ password: dangerPassword.value }); toast.success('账号已停用'); showDisableDialog.value = false; userStore.logout(); router.push('/login') }
  catch (e: any) { toast.error(e.message || '操作失败') }
  finally { dangerLoading.value = false }
}

/* ── Utils ── */
function maskPhone(phone: string) { return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') }
function fmtDate(d?: string) { return d ? new Date(d).toLocaleDateString('zh-CN') : '—' }
onMounted(async () => { if (!user.value) await userStore.fetchUserInfo() })
</script>

<style scoped>
/* ======== Apple Account (Light) ======== */
.apple-inset-group {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
}
.apple-danger-group {
  border-color: rgba(220,38,38,0.2);
}
.apple-inset-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px;
  font-size: 13px;
  font-weight: 600;
  color: #0A2540;
  background: #F6F9FC;
  border-bottom: 1px solid #E3E8EE;
}
.apple-inset-rows {
  padding: 0 20px;
}
.apple-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #F0F3F7;
}
.apple-row-btn {
  width: 100%;
  cursor: pointer;
  background: transparent;
  border-left: none;
  border-right: none;
  border-top: none;
  transition: background 0.15s;
}
.apple-row-btn:hover { background: #F6F9FC; }
.apple-row-label { font-size: 14px; color: #0A2540; }
.apple-row-value { font-size: 14px; color: #425466; }
.apple-row-input {
  text-align: right; padding: 4px 10px; border-radius: 6px;
  font-size: 14px; border: 1px solid #E3E8EE; background: #fff;
  color: #0A2540; outline: none; width: 180px;
}
.apple-row-input:focus { border-color: #635BFF; box-shadow: 0 0 0 3px rgba(99,91,255,0.08); }

/* ======== Discord (Dark) ======== */
.discord-profile-card {
  background: #111113;
  border-radius: 12px;
  border: 1px solid rgba(255,255,255,0.06);
  overflow: hidden;
}
.discord-card {
  padding: 20px;
  border-radius: 12px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
}
.discord-field {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 0; border-bottom: 1px solid rgba(255,255,255,0.04);
}
.discord-field-label { font-size: 12px; color: #B5BAC1; text-transform: uppercase; font-weight: 600; letter-spacing: 0.02em; }
.discord-input {
  text-align: right; padding: 6px 10px; border-radius: 4px;
  font-size: 14px; border: 1px solid rgba(255,255,255,0.08);
  background: rgba(255,255,255,0.03); color: #EDEDED; outline: none; width: 180px;
}
.discord-input:focus { border-color: #5865F2; }
.discord-sec-row {
  display: flex; align-items: center; gap: 16px;
  padding: 14px 0; border-bottom: 1px solid rgba(255,255,255,0.04);
}
.discord-btn-outline {
  padding: 6px 14px; border-radius: 4px; font-size: 12px; font-weight: 500;
  color: #EDEDED; background: transparent; border: 1px solid rgba(255,255,255,0.15);
  cursor: pointer; transition: all 0.15s;
}
.discord-btn-outline:hover { background: rgba(255,255,255,0.06); }
.discord-btn-primary {
  padding: 6px 14px; border-radius: 4px; font-size: 12px; font-weight: 600;
  color: #fff; background: #5865F2; border: none; cursor: pointer;
}
.discord-btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.discord-btn-danger {
  padding: 6px 14px; border-radius: 4px; font-size: 12px; font-weight: 500;
  color: #ED4245; background: transparent; border: 1px solid rgba(237,66,69,0.3);
  cursor: pointer; transition: all 0.15s;
}
.discord-btn-danger:hover { background: rgba(237,66,69,0.1); }

/* ======== Notion Profile (Warm) ======== */
.notion-property-table {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E7E5E4;
  border-radius: 16px;
  box-shadow: 0 3px 0 #E7E5E4;
}
.notion-prop-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 0; border-bottom: 1px solid #E7E5E4;
}
.notion-prop-key { font-size: 13px; color: #78716C; font-weight: 600; }
.notion-prop-val { font-size: 14px; color: #292524; }
.notion-prop-input {
  text-align: right; padding: 4px 10px; border-radius: 8px;
  font-size: 14px; border: 2px solid #E7E5E4; background: #fff;
  color: #292524; outline: none; width: 180px;
}
.notion-prop-input:focus { border-color: #D97706; }
.notion-callout {
  display: flex; padding: 20px;
  background: #FFFBF5; border: 2px solid #E7E5E4;
  border-radius: 16px; box-shadow: 0 3px 0 #E7E5E4;
}
.notion-danger-hint {
  padding: 20px;
  background: linear-gradient(135deg, #FEF2F2 0%, #FFF1F2 100%);
  border: 2px solid #FECACA;
  border-radius: 16px;
  box-shadow: 0 3px 0 #FECACA;
}

/* ======== Vercel Account (Pro) ======== */
.vercel-panel {
  padding: 20px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
}
.vercel-env-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 0; border-bottom: 1px solid #F1F5F9;
}
.vercel-env-key { font-size: 11px; font-family: ui-monospace, monospace; color: #64748B; letter-spacing: 0.03em; }
.vercel-env-val { font-size: 13px; font-family: ui-monospace, monospace; color: #0F172A; }
.vercel-env-input {
  text-align: right; padding: 3px 8px; border-radius: 4px;
  font-size: 13px; font-family: ui-monospace, monospace;
  border: 1px solid #E2E8F0; background: #fff; color: #0F172A;
  outline: none; width: 180px;
}
.vercel-env-input:focus { border-color: #0284C7; }
.vercel-sec-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 0; border-bottom: 1px solid #F1F5F9;
}
.vercel-btn-outline {
  padding: 5px 12px; border-radius: 4px; font-size: 12px; font-weight: 500;
  color: #0F172A; background: transparent; border: 1px solid #E2E8F0;
  cursor: pointer; transition: all 0.15s;
}
.vercel-btn-outline:hover { background: #F8FAFC; }
.vercel-btn-primary {
  padding: 5px 14px; border-radius: 4px; font-size: 12px; font-weight: 600;
  color: #fff; background: #0F172A; border: none; cursor: pointer;
}
.vercel-btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.vercel-btn-danger {
  padding: 5px 12px; border-radius: 4px; font-size: 11px; font-weight: 500;
  color: rgb(220,38,38); background: transparent; border: 1px solid rgba(220,38,38,0.3);
  cursor: pointer; transition: all 0.15s;
}
.vercel-btn-danger:hover { background: rgba(220,38,38,0.05); }

/* ======== Shared Modal ======== */
.modal-overlay { position: fixed; inset: 0; z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 1rem; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px); animation: ovl 0.15s ease; }
.modal-card { background: rgb(var(--color-surface)); border-radius: 12px; padding: 28px; width: 100%; max-width: 420px; box-shadow: 0 25px 50px rgba(0,0,0,0.15); animation: mci 0.2s cubic-bezier(0.16,1,0.3,1); }
.modal-title { font-size: 16px; font-weight: 600; color: rgb(var(--color-text-primary)); }
.modal-label { display: block; font-size: 13px; font-weight: 500; color: rgb(var(--color-text-secondary)); margin-bottom: 6px; }
.modal-input { width: 100%; padding: 10px 12px; border-radius: 6px; font-size: 14px; border: 1px solid rgb(var(--color-border)); background: rgb(var(--color-surface)); color: rgb(var(--color-text-primary)); outline: none; transition: all 0.15s; }
.modal-input:focus { border-color: rgb(var(--color-primary)); box-shadow: 0 0 0 3px rgb(var(--color-primary) / 0.1); }
.modal-cancel { padding: 6px 14px; border-radius: 6px; font-size: 13px; font-weight: 500; color: rgb(var(--color-text-secondary)); background: transparent; border: 1px solid rgb(var(--color-border)); cursor: pointer; }
.modal-confirm { padding: 6px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; color: #fff; background: rgb(var(--color-primary)); border: none; cursor: pointer; }
.modal-confirm:disabled { opacity: 0.5; cursor: not-allowed; }
.modal-danger { padding: 6px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; color: #fff; background: rgb(var(--color-danger)); border: none; cursor: pointer; }
.modal-danger:disabled { opacity: 0.5; cursor: not-allowed; }
@keyframes ovl { from { opacity: 0; } to { opacity: 1; } }
@keyframes mci { from { opacity: 0; transform: scale(0.96) translateY(8px); } to { opacity: 1; transform: none; } }
</style>
