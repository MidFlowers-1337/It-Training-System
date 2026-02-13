<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — iOS Settings：分组列表 + Toggle 开关 + 圆角行
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="max-w-2xl mx-auto space-y-5">
        <h1 class="text-xl font-bold text-[#0A2540]">设置</h1>

        <!-- Group: Account & Security -->
        <div class="ios-group">
          <div class="ios-group-title">账号与安全</div>
          <div class="ios-rows">
            <div class="ios-row">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-[#635BFF]/10 flex items-center justify-center"><KeyRound class="w-3.5 h-3.5 text-[#635BFF]" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">修改密码</span>
              </div>
              <button class="text-xs text-[#635BFF] font-medium cursor-pointer hover:underline" @click="showPasswordDialog = true">修改</button>
            </div>
            <div class="ios-row">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-emerald-500/10 flex items-center justify-center"><Shield class="w-3.5 h-3.5 text-emerald-600" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">双重认证</span>
              </div>
              <span class="text-xs text-[#8898AA]">暂未开放</span>
            </div>
            <div class="ios-row border-none">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-blue-500/10 flex items-center justify-center"><Clock class="w-3.5 h-3.5 text-blue-600" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">登录日志</span>
              </div>
              <span class="text-xs text-[#8898AA]">暂未开放</span>
            </div>
          </div>
        </div>

        <!-- Group: Notifications -->
        <div class="ios-group">
          <div class="ios-group-title">通知</div>
          <div class="ios-rows">
            <div class="ios-row">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-red-500/10 flex items-center justify-center"><Bell class="w-3.5 h-3.5 text-red-500" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">课程提醒</span>
              </div>
              <label class="ios-toggle"><input type="checkbox" v-model="settings.courseReminder" @change="saveSetting('courseReminder')" /><span class="ios-toggle-track"></span></label>
            </div>
            <div class="ios-row">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-amber-500/10 flex items-center justify-center"><Flame class="w-3.5 h-3.5 text-amber-500" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">打卡提醒</span>
              </div>
              <label class="ios-toggle"><input type="checkbox" v-model="settings.checkinReminder" @change="saveSetting('checkinReminder')" /><span class="ios-toggle-track"></span></label>
            </div>
            <div class="ios-row border-none">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-purple-500/10 flex items-center justify-center"><Megaphone class="w-3.5 h-3.5 text-purple-500" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">系统公告</span>
              </div>
              <label class="ios-toggle"><input type="checkbox" v-model="settings.systemNotice" @change="saveSetting('systemNotice')" /><span class="ios-toggle-track"></span></label>
            </div>
          </div>
        </div>

        <!-- Group: Appearance -->
        <div class="ios-group">
          <div class="ios-group-title">外观</div>
          <div class="ios-rows">
            <div class="ios-row border-none">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-[#635BFF]/10 flex items-center justify-center"><Palette class="w-3.5 h-3.5 text-[#635BFF]" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">主题</span>
              </div>
              <div class="flex gap-1.5">
                <button v-for="t in themeOptions" :key="t.key" @click="switchTheme(t.key)"
                  :class="['px-3 py-1 rounded-full text-xs font-medium transition-all cursor-pointer border',
                    theme === t.key ? 'bg-[#635BFF] text-white border-[#635BFF]' : 'bg-white text-[#425466] border-[#E3E8EE] hover:border-[#635BFF]']">
                  {{ t.label }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Group: About & Logout -->
        <div class="ios-group">
          <div class="ios-group-title">其他</div>
          <div class="ios-rows">
            <div class="ios-row">
              <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-md bg-[#0A2540]/5 flex items-center justify-center"><Info class="w-3.5 h-3.5 text-[#425466]" :stroke-width="1.75" /></div>
                <span class="text-sm text-[#0A2540]">版本</span>
              </div>
              <span class="text-xs text-[#8898AA] font-mono">v2.0.0</span>
            </div>
            <div class="ios-row border-none">
              <button class="w-full text-center text-sm text-red-500 font-medium cursor-pointer py-1" @click="doLogout">退出登录</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord Settings：左侧导航 Sidebar + 右侧内容面板
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="flex gap-0 max-w-4xl mx-auto min-h-[calc(100vh-10rem)]">
        <!-- Left Sidebar Navigation -->
        <div class="w-48 flex-shrink-0 pr-2 space-y-1 pt-2">
          <div class="text-[10px] text-[#B5BAC1] uppercase font-semibold tracking-wider px-2.5 mb-2">用户设置</div>
          <button v-for="s in discordSections" :key="s.key"
            @click="discordActive = s.key"
            :class="['w-full text-left px-2.5 py-1.5 rounded text-sm transition-all cursor-pointer',
              discordActive === s.key ? 'bg-white/[0.06] text-[#EDEDED] font-medium' : 'text-[#B5BAC1] hover:bg-white/[0.04] hover:text-[#EDEDED]']">
            {{ s.label }}
          </button>
          <div class="border-t border-white/[0.06] my-2 mx-2"></div>
          <button class="w-full text-left px-2.5 py-1.5 rounded text-sm text-red-400 hover:bg-red-500/10 hover:text-red-300 transition-all cursor-pointer" @click="doLogout">
            退出登录
          </button>
        </div>

        <!-- Right Content Panel -->
        <div class="flex-1 pl-6 border-l border-white/[0.06]">
          <!-- Password -->
          <div v-if="discordActive === 'password'" class="discord-settings-card">
            <h3 class="text-base font-semibold text-[#EDEDED] mb-1">密码和身份验证</h3>
            <p class="text-sm text-[#B5BAC1] mb-5">更改你的密码以确保账户安全</p>
            <form @submit.prevent="changePw" class="space-y-4 max-w-sm">
              <div>
                <label class="block text-xs text-[#B5BAC1] uppercase font-semibold tracking-wider mb-1.5">当前密码</label>
                <input v-model="pw.oldPassword" type="password" class="discord-input w-full" placeholder="输入当前密码" />
              </div>
              <div>
                <label class="block text-xs text-[#B5BAC1] uppercase font-semibold tracking-wider mb-1.5">新密码</label>
                <input v-model="pw.newPassword" type="password" class="discord-input w-full" placeholder="至少 6 位" />
              </div>
              <div class="flex gap-2 pt-1">
                <button type="submit" class="discord-btn-primary" :disabled="pwLoading">{{ pwLoading ? '修改中...' : '确认更改' }}</button>
                <button type="button" class="discord-btn-ghost" @click="pw = { oldPassword: '', newPassword: '' }">取消</button>
              </div>
            </form>
          </div>

          <!-- Notifications -->
          <div v-else-if="discordActive === 'notifications'" class="discord-settings-card">
            <h3 class="text-base font-semibold text-[#EDEDED] mb-1">通知设置</h3>
            <p class="text-sm text-[#B5BAC1] mb-5">自定义你想收到的通知类型</p>
            <div class="space-y-0">
              <div class="discord-setting-row">
                <div><div class="text-sm text-[#EDEDED]">课程提醒</div><div class="text-xs text-[#B5BAC1] mt-0.5">新课程发布或课程更新时通知</div></div>
                <label class="discord-toggle"><input type="checkbox" v-model="settings.courseReminder" @change="saveSetting('courseReminder')" /><span class="discord-toggle-track"></span></label>
              </div>
              <div class="discord-setting-row">
                <div><div class="text-sm text-[#EDEDED]">打卡提醒</div><div class="text-xs text-[#B5BAC1] mt-0.5">每日学习打卡提醒</div></div>
                <label class="discord-toggle"><input type="checkbox" v-model="settings.checkinReminder" @change="saveSetting('checkinReminder')" /><span class="discord-toggle-track"></span></label>
              </div>
              <div class="discord-setting-row border-none">
                <div><div class="text-sm text-[#EDEDED]">系统公告</div><div class="text-xs text-[#B5BAC1] mt-0.5">平台重要公告和维护通知</div></div>
                <label class="discord-toggle"><input type="checkbox" v-model="settings.systemNotice" @change="saveSetting('systemNotice')" /><span class="discord-toggle-track"></span></label>
              </div>
            </div>
          </div>

          <!-- Appearance -->
          <div v-else-if="discordActive === 'appearance'" class="discord-settings-card">
            <h3 class="text-base font-semibold text-[#EDEDED] mb-1">外观</h3>
            <p class="text-sm text-[#B5BAC1] mb-5">自定义你的界面外观</p>
            <div class="text-xs text-[#B5BAC1] uppercase font-semibold tracking-wider mb-3">主题</div>
            <div class="grid grid-cols-2 gap-3">
              <button v-for="t in themeOptions" :key="t.key" @click="switchTheme(t.key)"
                :class="['p-4 rounded-lg border text-left transition-all cursor-pointer group',
                  theme === t.key
                    ? 'border-[#5865F2] bg-[#5865F2]/10'
                    : 'border-white/[0.06] bg-white/[0.02] hover:border-white/[0.12]']">
                <div class="flex items-center gap-2 mb-1">
                  <div :class="['w-4 h-4 rounded-full border-2 flex items-center justify-center',
                    theme === t.key ? 'border-[#5865F2]' : 'border-[#4E5058]']">
                    <div v-if="theme === t.key" class="w-2 h-2 rounded-full bg-[#5865F2]"></div>
                  </div>
                  <span :class="['text-sm font-medium', theme === t.key ? 'text-[#EDEDED]' : 'text-[#B5BAC1]']">{{ t.label }}</span>
                </div>
                <p class="text-[11px] text-[#B5BAC1] ml-6">{{ t.desc }}</p>
              </button>
            </div>
          </div>

          <!-- About -->
          <div v-else-if="discordActive === 'about'" class="discord-settings-card">
            <h3 class="text-base font-semibold text-[#EDEDED] mb-1">关于</h3>
            <p class="text-sm text-[#B5BAC1] mb-5">IT 培训系统</p>
            <div class="space-y-3">
              <div class="flex items-center justify-between py-2 border-b border-white/[0.04]">
                <span class="text-sm text-[#B5BAC1]">版本</span>
                <code class="text-xs text-[#EDEDED] bg-white/[0.04] px-2 py-0.5 rounded">2.0.0</code>
              </div>
              <div class="flex items-center justify-between py-2 border-b border-white/[0.04]">
                <span class="text-sm text-[#B5BAC1]">客户端</span>
                <code class="text-xs text-[#EDEDED] bg-white/[0.04] px-2 py-0.5 rounded">Web</code>
              </div>
              <div class="flex items-center justify-between py-2">
                <span class="text-sm text-[#B5BAC1]">技术栈</span>
                <code class="text-xs text-[#EDEDED] bg-white/[0.04] px-2 py-0.5 rounded">Vue 3 + TypeScript</code>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — macOS System Preferences：图标网格 + 点击展开详情
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="max-w-2xl mx-auto space-y-5">
        <div class="flex items-center gap-2 mb-2">
          <span class="text-lg">⚙️</span>
          <h1 class="text-xl font-extrabold text-[#292524]">系统偏好设置</h1>
        </div>

        <!-- Icon Grid (macOS Preferences style) -->
        <div v-if="!macPref" class="mac-pref-grid">
          <button v-for="p in macPrefPanels" :key="p.key" @click="macPref = p.key"
            class="mac-pref-icon group">
            <div :class="['w-14 h-14 rounded-2xl flex items-center justify-center mb-2 transition-all group-hover:scale-105', p.bgClass]">
              <span class="text-2xl">{{ p.emoji }}</span>
            </div>
            <span class="text-[11px] font-bold text-[#292524]">{{ p.label }}</span>
          </button>
        </div>

        <!-- Expanded Detail Panel -->
        <template v-if="macPref">
          <!-- Back Button -->
          <button @click="macPref = ''" class="inline-flex items-center gap-1 text-sm font-bold text-[#D97706] cursor-pointer hover:underline mb-2">
            <ChevronLeft class="w-4 h-4" :stroke-width="2" /> 返回全部设置
          </button>

          <!-- Password Panel -->
          <div v-if="macPref === 'password'" class="mac-detail-card">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#D97706] to-[#B45309] flex items-center justify-center">
                <span class="text-2xl">🔑</span>
              </div>
              <div>
                <h3 class="text-base font-extrabold text-[#292524]">密码</h3>
                <p class="text-xs text-[#78716C]">管理你的登录密码</p>
              </div>
            </div>
            <form @submit.prevent="changePw" class="space-y-4">
              <div><label class="mac-label">当前密码</label><input v-model="pw.oldPassword" type="password" class="mac-input" placeholder="请输入当前密码" /></div>
              <div><label class="mac-label">新密码</label><input v-model="pw.newPassword" type="password" class="mac-input" placeholder="至少 6 位" /></div>
              <button type="submit" class="mac-btn" :disabled="pwLoading">{{ pwLoading ? '修改中...' : '确认修改' }}</button>
            </form>
          </div>

          <!-- Notifications Panel -->
          <div v-if="macPref === 'notifications'" class="mac-detail-card">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center">
                <span class="text-2xl">🔔</span>
              </div>
              <div>
                <h3 class="text-base font-extrabold text-[#292524]">通知</h3>
                <p class="text-xs text-[#78716C]">管理通知偏好</p>
              </div>
            </div>
            <div class="space-y-0">
              <div class="mac-setting-row">
                <div><div class="text-sm font-bold text-[#292524]">课程提醒</div><div class="text-xs text-[#78716C]">新课程发布时通知</div></div>
                <label class="mac-toggle"><input type="checkbox" v-model="settings.courseReminder" @change="saveSetting('courseReminder')" /><span class="mac-toggle-track"></span></label>
              </div>
              <div class="mac-setting-row">
                <div><div class="text-sm font-bold text-[#292524]">打卡提醒</div><div class="text-xs text-[#78716C]">每日学习提醒</div></div>
                <label class="mac-toggle"><input type="checkbox" v-model="settings.checkinReminder" @change="saveSetting('checkinReminder')" /><span class="mac-toggle-track"></span></label>
              </div>
              <div class="mac-setting-row border-none">
                <div><div class="text-sm font-bold text-[#292524]">系统公告</div><div class="text-xs text-[#78716C]">平台公告和维护通知</div></div>
                <label class="mac-toggle"><input type="checkbox" v-model="settings.systemNotice" @change="saveSetting('systemNotice')" /><span class="mac-toggle-track"></span></label>
              </div>
            </div>
          </div>

          <!-- Appearance Panel -->
          <div v-if="macPref === 'appearance'" class="mac-detail-card">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-indigo-400 to-purple-500 flex items-center justify-center">
                <span class="text-2xl">🎨</span>
              </div>
              <div>
                <h3 class="text-base font-extrabold text-[#292524]">外观</h3>
                <p class="text-xs text-[#78716C]">选择你喜欢的界面风格</p>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <button v-for="t in themeOptions" :key="t.key" @click="switchTheme(t.key)"
                :class="['p-4 rounded-2xl border-2 text-center transition-all cursor-pointer',
                  theme === t.key
                    ? 'border-[#D97706] bg-[#D97706]/5 shadow-[0_3px_0_#D97706]'
                    : 'border-[#E7E5E4] bg-white hover:border-[#D97706]/30']">
                <div class="text-2xl mb-2">{{ t.emoji }}</div>
                <div class="text-sm font-extrabold text-[#292524]">{{ t.label }}</div>
                <div class="text-[10px] text-[#78716C] mt-1">{{ t.desc }}</div>
              </button>
            </div>
          </div>

          <!-- About Panel -->
          <div v-if="macPref === 'about'" class="mac-detail-card">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-gray-400 to-gray-600 flex items-center justify-center">
                <span class="text-2xl">ℹ️</span>
              </div>
              <div>
                <h3 class="text-base font-extrabold text-[#292524]">关于</h3>
                <p class="text-xs text-[#78716C]">系统信息</p>
              </div>
            </div>
            <div class="space-y-0">
              <div class="mac-info-row"><span class="mac-info-key">系统名称</span><span class="mac-info-val">IT 培训系统</span></div>
              <div class="mac-info-row"><span class="mac-info-key">版本号</span><span class="mac-info-val">2.0.0</span></div>
              <div class="mac-info-row border-none"><span class="mac-info-key">技术栈</span><span class="mac-info-val">Vue 3 + TypeScript</span></div>
            </div>
          </div>

          <!-- Logout Panel -->
          <div v-if="macPref === 'logout'" class="mac-detail-card border-red-200">
            <div class="flex items-center gap-3 mb-5">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center">
                <span class="text-2xl">🚪</span>
              </div>
              <div>
                <h3 class="text-base font-extrabold text-red-600">退出登录</h3>
                <p class="text-xs text-[#78716C]">登出当前账号</p>
              </div>
            </div>
            <p class="text-sm text-[#78716C] mb-4">退出后需要重新登录才能使用系统。</p>
            <button @click="doLogout" class="px-6 py-2.5 rounded-2xl bg-red-600 text-white text-sm font-extrabold cursor-pointer hover:bg-red-700 transition-colors shadow-[0_3px_0_#B91C1C]">
              确认退出
            </button>
          </div>
        </template>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — VS Code Settings：搜索框 + 设置树 + inline 控件
         ================================================================ -->
    <template v-else>
      <div class="max-w-4xl mx-auto space-y-3">
        <!-- Search Bar -->
        <div class="vsc-search">
          <Search class="w-3.5 h-3.5 text-[#94A3B8] flex-shrink-0" :stroke-width="1.75" />
          <input v-model="vscSearch" type="text" placeholder="Search settings..." class="flex-1 bg-transparent text-xs font-mono text-[#0F172A] outline-none placeholder-[#94A3B8]" />
          <code v-if="vscSearch" class="text-[9px] text-[#94A3B8]">{{ filteredVscSettings.length }} results</code>
        </div>

        <!-- Breadcrumb -->
        <div class="flex items-center gap-1 text-[10px] font-mono text-[#94A3B8]">
          <span>User Settings</span>
          <ChevronRight class="w-3 h-3" :stroke-width="1.5" />
          <span class="text-[#0F172A]">{{ vscActiveLabel }}</span>
        </div>

        <div class="flex gap-4">
          <!-- Settings Tree (left sidebar) -->
          <div class="w-40 flex-shrink-0 space-y-0.5">
            <button v-for="s in vscSections" :key="s.key" @click="vscActive = s.key"
              :class="['w-full text-left px-2 py-1.5 rounded text-[11px] font-mono transition-all cursor-pointer',
                vscActive === s.key ? 'bg-[#0284C7]/10 text-[#0284C7] font-semibold' : 'text-[#64748B] hover:bg-[#F1F5F9]']">
              {{ s.label }}
            </button>
          </div>

          <!-- Settings Content -->
          <div class="flex-1 space-y-0">
            <!-- Security Settings -->
            <template v-if="vscActive === 'security'">
              <div class="vsc-setting-item" v-if="matchSearch('password')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">security.password</code>
                </div>
                <p class="vsc-setting-desc">更改你的登录密码，建议定期更换以确保安全。</p>
                <div class="mt-2">
                  <button @click="showPasswordDialog = true" class="vsc-btn">Change Password</button>
                </div>
              </div>
              <div class="vsc-setting-item" v-if="matchSearch('2fa')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">security.twoFactorAuth</code>
                  <code class="vsc-setting-tag">coming soon</code>
                </div>
                <p class="vsc-setting-desc">启用双因素认证以增强账户安全性。</p>
              </div>
            </template>

            <!-- Notification Settings -->
            <template v-if="vscActive === 'notifications'">
              <div class="vsc-setting-item" v-if="matchSearch('course reminder')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">notifications.courseReminder</code>
                </div>
                <p class="vsc-setting-desc">新课程发布或课程更新时发送通知。</p>
                <div class="mt-2">
                  <label class="vsc-checkbox"><input type="checkbox" v-model="settings.courseReminder" @change="saveSetting('courseReminder')" /><span>Enable course notifications</span></label>
                </div>
              </div>
              <div class="vsc-setting-item" v-if="matchSearch('checkin reminder')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">notifications.checkinReminder</code>
                </div>
                <p class="vsc-setting-desc">每日学习打卡提醒，帮助养成学习习惯。</p>
                <div class="mt-2">
                  <label class="vsc-checkbox"><input type="checkbox" v-model="settings.checkinReminder" @change="saveSetting('checkinReminder')" /><span>Enable daily checkin reminder</span></label>
                </div>
              </div>
              <div class="vsc-setting-item" v-if="matchSearch('system notice')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">notifications.systemNotice</code>
                </div>
                <p class="vsc-setting-desc">平台重要公告和维护通知。</p>
                <div class="mt-2">
                  <label class="vsc-checkbox"><input type="checkbox" v-model="settings.systemNotice" @change="saveSetting('systemNotice')" /><span>Enable system announcements</span></label>
                </div>
              </div>
            </template>

            <!-- Appearance Settings -->
            <template v-if="vscActive === 'appearance'">
              <div class="vsc-setting-item" v-if="matchSearch('color theme')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">workbench.colorTheme</code>
                </div>
                <p class="vsc-setting-desc">控制工作台中使用的颜色主题。</p>
                <div class="mt-2">
                  <select v-model="currentThemeKey" @change="switchTheme(currentThemeKey)"
                    class="vsc-select">
                    <option v-for="t in themeOptions" :key="t.key" :value="t.key">{{ t.label }} — {{ t.desc }}</option>
                  </select>
                </div>
              </div>
            </template>

            <!-- About -->
            <template v-if="vscActive === 'about'">
              <div class="vsc-setting-item" v-if="matchSearch('version')">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">about.version</code>
                </div>
                <div class="mt-1 space-y-1">
                  <div class="flex gap-8">
                    <code class="text-[10px] text-[#94A3B8]">version</code>
                    <code class="text-[10px] text-[#0F172A]">2.0.0</code>
                  </div>
                  <div class="flex gap-8">
                    <code class="text-[10px] text-[#94A3B8]">client</code>
                    <code class="text-[10px] text-[#0F172A]">Web Browser</code>
                  </div>
                  <div class="flex gap-8">
                    <code class="text-[10px] text-[#94A3B8]">stack</code>
                    <code class="text-[10px] text-[#0F172A]">Vue 3 + TypeScript</code>
                  </div>
                </div>
              </div>
              <div class="vsc-setting-item">
                <div class="vsc-setting-header">
                  <code class="vsc-setting-id">session.logout</code>
                </div>
                <p class="vsc-setting-desc">结束当前会话并退出登录。</p>
                <div class="mt-2">
                  <button @click="doLogout" class="vsc-btn-danger">Logout</button>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </template>

    <!-- ======== Shared Password Dialog (theme-neutral) ======== -->
    <Teleport to="body">
      <div v-if="showPasswordDialog" class="modal-overlay" @click.self="showPasswordDialog = false">
        <div class="modal-card">
          <h3 class="modal-title">修改密码</h3>
          <form @submit.prevent="changePw" class="space-y-4 mt-4">
            <div><label class="modal-label">当前密码</label><input v-model="pw.oldPassword" type="password" required class="modal-input" placeholder="请输入当前密码" /></div>
            <div><label class="modal-label">新密码</label><input v-model="pw.newPassword" type="password" required class="modal-input" placeholder="至少 6 位" /></div>
            <div class="flex justify-end gap-3 pt-2">
              <button type="button" class="modal-cancel" @click="showPasswordDialog = false">取消</button>
              <button type="submit" class="modal-confirm" :disabled="pwLoading">{{ pwLoading ? '修改中...' : '确认修改' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { profileApi } from '@/api/profile'
import { toast } from '@/composables/useToast'
import type { ThemeName } from '@/design-system/tokens/colors'
import {
  KeyRound, Shield, Clock, Bell, Flame, Megaphone, Palette, Info,
  ChevronLeft, ChevronRight, Search,
} from 'lucide-vue-next'

const router = useRouter()
const us = useUserStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Theme Options ── */
const themeOptions: { key: string; label: string; emoji: string; desc: string }[] = [
  { key: 'light', label: '浅色', emoji: '☀️', desc: '清爽明亮的界面' },
  { key: 'dark', label: '深色', emoji: '🌙', desc: '护眼暗色模式' },
  { key: 'warm', label: '暖色', emoji: '🌅', desc: '温馨活力风格' },
  { key: 'pro', label: '专业', emoji: '❄️', desc: '简洁专业风格' },
]
const currentThemeKey = ref(theme.value)

function switchTheme(key: string) {
  currentThemeKey.value = key as typeof currentThemeKey.value
  themeStore.applyTheme(key as ThemeName)
}

/* ── Password ── */
const pw = ref({ oldPassword: '', newPassword: '' })
const pwLoading = ref(false)
const showPasswordDialog = ref(false)

async function changePw() {
  if (!pw.value.oldPassword || !pw.value.newPassword) { toast.warning('请填写完整'); return }
  if (pw.value.newPassword.length < 6) { toast.warning('密码至少 6 位'); return }
  pwLoading.value = true
  try {
    await profileApi.changePassword(pw.value)
    toast.success('密码修改成功')
    pw.value = { oldPassword: '', newPassword: '' }
    showPasswordDialog.value = false
  } catch (e: any) {
    toast.error(e.message || '修改失败')
  } finally {
    pwLoading.value = false
  }
}

/* ── Notification Settings (local) ── */
const settings = ref({
  courseReminder: true,
  checkinReminder: true,
  systemNotice: true,
})

function saveSetting(_key: string) {
  // Save locally — no backend endpoint yet
  try {
    localStorage.setItem('it-training-settings', JSON.stringify(settings.value))
    toast.success('设置已保存')
  } catch {
    // silently fail
  }
}

// Load from localStorage
try {
  const saved = localStorage.getItem('it-training-settings')
  if (saved) Object.assign(settings.value, JSON.parse(saved))
} catch { /* ignore */ }

/* ── Discord Sidebar Sections (Dark) ── */
const discordActive = ref('password')
const discordSections = [
  { key: 'password', label: '密码与认证' },
  { key: 'notifications', label: '通知' },
  { key: 'appearance', label: '外观' },
  { key: 'about', label: '关于' },
]

/* ── macOS Preference Panels (Warm) ── */
const macPref = ref('')
const macPrefPanels = [
  { key: 'password', label: '密码', emoji: '🔑', bgClass: 'bg-gradient-to-br from-[#D97706] to-[#B45309]' },
  { key: 'notifications', label: '通知', emoji: '🔔', bgClass: 'bg-gradient-to-br from-red-400 to-red-600' },
  { key: 'appearance', label: '外观', emoji: '🎨', bgClass: 'bg-gradient-to-br from-indigo-400 to-purple-500' },
  { key: 'about', label: '关于', emoji: 'ℹ️', bgClass: 'bg-gradient-to-br from-gray-400 to-gray-600' },
  { key: 'logout', label: '退出', emoji: '🚪', bgClass: 'bg-gradient-to-br from-red-500 to-red-700' },
]

/* ── VS Code Settings Tree (Pro) ── */
const vscActive = ref('security')
const vscSections = [
  { key: 'security', label: 'Security' },
  { key: 'notifications', label: 'Notifications' },
  { key: 'appearance', label: 'Appearance' },
  { key: 'about', label: 'About' },
]
const vscActiveLabel = computed(() => vscSections.find(s => s.key === vscActive.value)?.label || '')
const vscSearch = ref('')

const filteredVscSettings = computed(() => {
  if (!vscSearch.value) return []
  const q = vscSearch.value.toLowerCase()
  const all = [
    'password', '2fa', 'course reminder', 'checkin reminder', 'system notice', 'color theme', 'version',
  ]
  return all.filter(s => s.includes(q))
})

function matchSearch(keyword: string) {
  if (!vscSearch.value) return true
  return keyword.toLowerCase().includes(vscSearch.value.toLowerCase())
}

/* ── Logout ── */
function doLogout() {
  us.logout()
  router.push('/login')
  toast.success('已退出登录')
}
</script>

<style scoped>
/* ======== iOS Settings (Light) ======== */
.ios-group {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
}
.ios-group-title {
  padding: 10px 20px;
  font-size: 12px;
  font-weight: 600;
  color: #8898AA;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  background: #F6F9FC;
  border-bottom: 1px solid #E3E8EE;
}
.ios-rows {
  padding: 0 20px;
}
.ios-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #F0F3F7;
}

/* iOS Toggle */
.ios-toggle {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.ios-toggle input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}
.ios-toggle-track {
  display: block;
  width: 42px;
  height: 24px;
  border-radius: 12px;
  background: #CBD5E1;
  transition: background 0.2s;
  position: relative;
}
.ios-toggle-track::after {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 20px;
  height: 20px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.15);
  transition: transform 0.2s;
}
.ios-toggle input:checked + .ios-toggle-track {
  background: #635BFF;
}
.ios-toggle input:checked + .ios-toggle-track::after {
  transform: translateX(18px);
}

/* ======== Discord Settings (Dark) ======== */
.discord-settings-card {
  padding: 24px 0;
}
.discord-input {
  padding: 10px 12px; border-radius: 4px; font-size: 14px;
  border: 1px solid rgba(255,255,255,0.08); background: rgba(255,255,255,0.03);
  color: #EDEDED; outline: none; transition: border-color 0.15s;
}
.discord-input::placeholder { color: #6B6B6E; }
.discord-input:focus { border-color: #5865F2; }
.discord-btn-primary {
  padding: 8px 18px; border-radius: 4px; font-size: 13px; font-weight: 600;
  color: #fff; background: #5865F2; border: none; cursor: pointer; transition: all 0.15s;
}
.discord-btn-primary:hover { background: #4752C4; }
.discord-btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.discord-btn-ghost {
  padding: 8px 18px; border-radius: 4px; font-size: 13px; font-weight: 500;
  color: #B5BAC1; background: transparent; border: none; cursor: pointer;
}
.discord-btn-ghost:hover { text-decoration: underline; }
.discord-setting-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 0; border-bottom: 1px solid rgba(255,255,255,0.04);
}

/* Discord Toggle */
.discord-toggle {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.discord-toggle input {
  position: absolute; opacity: 0; width: 0; height: 0;
}
.discord-toggle-track {
  display: block;
  width: 40px; height: 22px; border-radius: 11px;
  background: #4E5058; transition: background 0.2s;
  position: relative;
}
.discord-toggle-track::after {
  content: '';
  position: absolute;
  top: 3px; left: 3px;
  width: 16px; height: 16px; border-radius: 8px;
  background: #EDEDED;
  transition: transform 0.2s;
}
.discord-toggle input:checked + .discord-toggle-track {
  background: #23A55A;
}
.discord-toggle input:checked + .discord-toggle-track::after {
  transform: translateX(18px);
}

/* ======== macOS Preferences (Warm) ======== */
.mac-pref-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E7E5E4;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E7E5E4;
}
.mac-pref-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.15s;
  background: transparent;
  border: none;
}
.mac-pref-icon:hover {
  background: rgba(217,119,6,0.05);
}
.mac-detail-card {
  padding: 24px;
  background: #FFFBF5;
  border: 2px solid #E7E5E4;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E7E5E4;
}
.mac-label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  color: #292524;
  margin-bottom: 6px;
}
.mac-input {
  width: 100%; padding: 10px 14px; border-radius: 12px;
  border: 2px solid #E7E5E4; background: #fff;
  color: #292524; font-size: 14px; outline: none;
}
.mac-input:focus { border-color: #D97706; }
.mac-btn {
  padding: 10px 24px; border-radius: 12px; font-size: 14px; font-weight: 700;
  color: #fff; background: #D97706; border: none; cursor: pointer;
  box-shadow: 0 3px 0 #B45309;
}
.mac-btn:hover { filter: brightness(1.05); }
.mac-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.mac-setting-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 0; border-bottom: 1px solid #E7E5E4;
}
.mac-info-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 0; border-bottom: 1px solid #E7E5E4;
}
.mac-info-key { font-size: 13px; color: #78716C; font-weight: 600; }
.mac-info-val { font-size: 14px; color: #292524; font-weight: 700; }

/* macOS Toggle */
.mac-toggle {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.mac-toggle input {
  position: absolute; opacity: 0; width: 0; height: 0;
}
.mac-toggle-track {
  display: block;
  width: 42px; height: 24px; border-radius: 12px;
  background: #D6D3D1; transition: background 0.2s;
  position: relative;
}
.mac-toggle-track::after {
  content: '';
  position: absolute;
  top: 2px; left: 2px;
  width: 20px; height: 20px; border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.15);
  transition: transform 0.2s;
}
.mac-toggle input:checked + .mac-toggle-track {
  background: #D97706;
}
.mac-toggle input:checked + .mac-toggle-track::after {
  transform: translateX(18px);
}

/* ======== VS Code Settings (Pro) ======== */
.vsc-search {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
  background: #F8FAFC;
  transition: border-color 0.15s;
}
.vsc-search:focus-within {
  border-color: #0284C7;
}
.vsc-setting-item {
  padding: 14px 0;
  border-bottom: 1px solid #F1F5F9;
}
.vsc-setting-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}
.vsc-setting-id {
  font-size: 11px;
  font-family: ui-monospace, monospace;
  color: #0284C7;
  font-weight: 600;
}
.vsc-setting-tag {
  font-size: 9px;
  font-family: ui-monospace, monospace;
  color: #94A3B8;
  background: #F1F5F9;
  padding: 1px 5px;
  border-radius: 3px;
}
.vsc-setting-desc {
  font-size: 12px;
  color: #64748B;
  line-height: 1.5;
}
.vsc-btn {
  padding: 4px 12px; border-radius: 3px; font-size: 11px; font-weight: 500;
  color: #fff; background: #0F172A; border: none; cursor: pointer;
  font-family: ui-monospace, monospace;
}
.vsc-btn:hover { background: #1E293B; }
.vsc-btn-danger {
  padding: 4px 12px; border-radius: 3px; font-size: 11px; font-weight: 500;
  color: rgb(220,38,38); background: transparent; border: 1px solid rgba(220,38,38,0.3);
  cursor: pointer; font-family: ui-monospace, monospace; transition: all 0.15s;
}
.vsc-btn-danger:hover { background: rgba(220,38,38,0.05); }
.vsc-select {
  padding: 4px 8px; border-radius: 3px; font-size: 11px;
  border: 1px solid #E2E8F0; background: #fff; color: #0F172A;
  font-family: ui-monospace, monospace; outline: none; cursor: pointer;
}
.vsc-select:focus { border-color: #0284C7; }
.vsc-checkbox {
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: #0F172A; cursor: pointer;
  font-family: ui-monospace, monospace;
}
.vsc-checkbox input {
  width: 14px; height: 14px;
  accent-color: #0284C7;
  cursor: pointer;
}

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
@keyframes ovl { from { opacity: 0; } to { opacity: 1; } }
@keyframes mci { from { opacity: 0; transform: scale(0.96) translateY(8px); } to { opacity: 1; transform: none; } }
</style>
