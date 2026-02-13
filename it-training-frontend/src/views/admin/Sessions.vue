<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Schedule：日程表 + 状态 pill + 进度条 + 操作链接
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">班期管理</h1>
            <p class="text-sm text-[#425466] mt-0.5">管理培训班次与报名时段</p>
          </div>
          <button class="px-4 py-2 rounded-lg bg-[#635BFF] text-white text-sm font-medium cursor-pointer hover:brightness-110 transition inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2" /> 新建班期
          </button>
        </div>

        <!-- Search & Filter -->
        <div class="stripe-card !py-3 !px-4">
          <div class="flex gap-3 items-center flex-wrap">
            <div class="relative flex-1 min-w-[220px]">
              <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#8898AA] pointer-events-none" :stroke-width="1.75" />
              <input v-model="searchKeyword" type="text" placeholder="搜索课程名称..."
                class="w-full pl-9 pr-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none focus:border-[#635BFF] focus:ring-2 focus:ring-[#635BFF]/10 transition placeholder-[#8898AA]"
                @input="onSearch" />
            </div>
            <select v-model="filterStatus" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="loadSessions">
              <option value="">全部状态</option>
              <option value="PENDING">待开放</option>
              <option value="ENROLLING">报名中</option>
              <option value="CLOSED">已关闭</option>
              <option value="COMPLETED">已完成</option>
            </select>
          </div>
        </div>

        <!-- Table -->
        <div class="stripe-card !p-0 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="border-b border-[#E3E8EE]">
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">ID</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">课程</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">讲师</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">日期区间</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">报名进度</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">地点</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">状态</th>
                  <th class="text-right px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="8" class="text-center py-12 text-sm text-[#8898AA]">加载中...</td></tr>
                <tr v-else-if="!sessions.length"><td colspan="8" class="text-center py-12 text-sm text-[#8898AA]">暂无班期数据</td></tr>
                <tr v-for="s in sessions" :key="s.id" class="border-b border-[#F0F3F7] last:border-0 hover:bg-[#F6F9FC]/50 transition-colors">
                  <td class="px-4 py-3 text-[#8898AA] tabular-nums">{{ s.id }}</td>
                  <td class="px-4 py-3 font-medium text-[#0A2540]">{{ s.courseName }}</td>
                  <td class="px-4 py-3 text-[#425466] text-xs">{{ s.instructorName || '-' }}</td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs tabular-nums">{{ s.startDate }} ~ {{ s.endDate }}</td>
                  <td class="px-4 py-3">
                    <div class="flex items-center gap-2">
                      <div class="w-16 h-1.5 bg-[#E3E8EE] rounded-full overflow-hidden">
                        <div class="h-full bg-[#635BFF] rounded-full transition-all" :style="{ width: enrollPercent(s) + '%' }"></div>
                      </div>
                      <span class="text-xs text-[#425466] tabular-nums">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs">{{ s.location || '-' }}</td>
                  <td class="px-4 py-3">
                    <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', stripeStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex items-center justify-end gap-1">
                      <button v-if="s.status === 'PENDING' || s.status === 'CLOSED'" class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-emerald-500 hover:bg-emerald-500/5 transition cursor-pointer" title="开放报名" @click="openEnrollment(s.id)"><DoorOpen class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button v-if="s.status === 'ENROLLING'" class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-amber-500 hover:bg-amber-500/5 transition cursor-pointer" title="关闭报名" @click="closeEnrollment(s.id)"><DoorClosed class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-[#635BFF] hover:bg-[#635BFF]/5 transition cursor-pointer" title="编辑" @click="openEditDialog(s)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-red-500 hover:bg-red-500/5 transition cursor-pointer" title="删除" @click="confirmDelete(s)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord Events：暗色事件卡片 + 日程指示器 + 报名进度环
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">班期管理</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ sessions.length }} sessions</span>
          </div>
          <button class="px-4 py-2 rounded-md bg-[#5865F2] text-white text-sm font-semibold cursor-pointer hover:bg-[#4752C4] transition inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2" /> 新建班期
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#6B6B6E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索课程..."
              class="w-full pl-9 pr-3 py-2 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none focus:border-[#5865F2] transition placeholder-[#6B6B6E]"
              @input="onSearch" />
          </div>
          <div class="flex gap-1">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-3 py-2 rounded-md text-xs font-medium cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] bg-white/[0.04] hover:bg-white/[0.06]']"
              @click="filterStatus = sf.value; loadSessions()">
              {{ sf.label }}
            </button>
          </div>
        </div>

        <!-- Event Cards -->
        <div class="raycast-card !p-0">
          <div v-if="loading" class="py-12 text-center text-sm text-[#6B6B6E]">加载中...</div>
          <div v-else-if="!sessions.length" class="py-12 text-center text-sm text-[#6B6B6E]">暂无班期</div>
          <template v-else>
            <div v-for="(s, i) in sessions" :key="s.id"
              :class="['flex items-center gap-4 px-4 py-3.5 hover:bg-white/[0.03] transition-colors', i < sessions.length - 1 && 'border-b border-white/[0.04]']">
              <!-- Status Ring -->
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 border-2',
                s.status === 'ENROLLING' ? 'border-[#23A55A] text-[#23A55A]' :
                s.status === 'COMPLETED' ? 'border-[#5865F2] text-[#5865F2]' :
                s.status === 'PENDING' ? 'border-[#FEE75C] text-[#FEE75C]' : 'border-[#80848E] text-[#80848E]']">
                <component :is="s.status === 'ENROLLING' ? DoorOpen : s.status === 'COMPLETED' ? CheckCircle : s.status === 'PENDING' ? Clock : DoorClosed" class="w-4 h-4" :stroke-width="1.75" />
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-[#EDEDED] truncate">{{ s.courseName }}</span>
                  <span :class="['px-1.5 py-0.5 rounded text-[9px] font-bold uppercase', darkStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
                </div>
                <div class="text-xs text-[#6B6B6E] mt-0.5">
                  {{ s.instructorName || '未指定' }} · {{ s.startDate }} → {{ s.endDate }} · {{ s.location || '线上' }}
                </div>
              </div>
              <!-- Progress -->
              <div class="flex items-center gap-3 flex-shrink-0">
                <div class="text-right">
                  <div class="text-xs font-mono text-[#818CF8]">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}</div>
                  <div class="w-14 h-1 bg-white/[0.06] rounded-full overflow-hidden mt-1">
                    <div class="h-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4] rounded-full" :style="{ width: enrollPercent(s) + '%' }"></div>
                  </div>
                </div>
                <!-- Actions -->
                <div class="flex items-center gap-1">
                  <button v-if="s.status === 'PENDING' || s.status === 'CLOSED'" class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#23A55A] hover:bg-[#23A55A]/10 transition cursor-pointer" @click="openEnrollment(s.id)"><DoorOpen class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                  <button v-if="s.status === 'ENROLLING'" class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#FEE75C] hover:bg-[#FEE75C]/10 transition cursor-pointer" @click="closeEnrollment(s.id)"><DoorClosed class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                  <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#EDEDED] hover:bg-white/[0.06] transition cursor-pointer" @click="openEditDialog(s)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                  <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#ED4245] hover:bg-[#ED4245]/10 transition cursor-pointer" @click="confirmDelete(s)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Calendar：Emoji 日历卡 + 彩色状态 + 大号进度
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📅</span>
            <h1 class="text-xl font-extrabold text-[#292524]">班期管理</h1>
          </div>
          <button class="px-5 py-2.5 rounded-2xl bg-[#292524] text-white text-sm font-extrabold cursor-pointer hover:brightness-110 transition shadow-[0_3px_0_#1C1917] active:translate-y-[2px] active:shadow-[0_1px_0_#1C1917] inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2.5" /> 新建班期
          </button>
        </div>

        <!-- Filter Tags -->
        <div class="flex items-center gap-2 flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索课程..."
              class="w-full pl-9 pr-3 py-2.5 rounded-2xl border-2 border-[#E7E5E4] bg-white text-sm text-[#292524] outline-none focus:border-[#D97706] transition placeholder-[#A8A29E]"
              @input="onSearch" />
          </div>
          <div class="flex gap-1.5">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-4 py-2 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#292524] text-white border-[#292524] shadow-[0_2px_0_#1C1917]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:border-[#D97706]']"
              @click="filterStatus = sf.value; loadSessions()">
              {{ sf.emoji }} {{ sf.label }}
            </button>
          </div>
        </div>

        <!-- Card Grid -->
        <div v-if="loading" class="text-center py-12 text-sm text-[#78716C]">加载中...</div>
        <div v-else-if="!sessions.length" class="text-center py-12"><span class="text-3xl">📭</span><p class="text-sm text-[#78716C] mt-2">暂无班期数据</p></div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="s in sessions" :key="s.id"
            class="notion-card !p-0 overflow-hidden hover:shadow-[0_4px_0_#D97706] hover:border-[#D97706] transition-all">
            <!-- Card Header -->
            <div class="flex items-center justify-between px-5 py-3 bg-[#FFFBF5] border-b-2 border-[#E7E5E4]">
              <div class="flex items-center gap-2">
                <span class="text-base">{{ warmStatusEmoji(s.status) }}</span>
                <span class="text-sm font-extrabold text-[#292524]">{{ s.courseName }}</span>
              </div>
              <span :class="['px-2.5 py-0.5 rounded-full text-[10px] font-extrabold', warmStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
            </div>
            <!-- Card Body -->
            <div class="px-5 py-4 space-y-3">
              <div class="grid grid-cols-2 gap-3 text-xs">
                <div><span class="text-[#A8A29E]">👨‍🏫 讲师</span><div class="font-bold text-[#292524] mt-0.5">{{ s.instructorName || '未指定' }}</div></div>
                <div><span class="text-[#A8A29E]">📍 地点</span><div class="font-bold text-[#292524] mt-0.5">{{ s.location || '线上' }}</div></div>
                <div><span class="text-[#A8A29E]">📅 开始</span><div class="font-bold text-[#292524] mt-0.5">{{ s.startDate }}</div></div>
                <div><span class="text-[#A8A29E]">🏁 结束</span><div class="font-bold text-[#292524] mt-0.5">{{ s.endDate }}</div></div>
              </div>
              <!-- Progress Bar -->
              <div>
                <div class="flex items-center justify-between text-xs mb-1">
                  <span class="text-[#78716C]">👥 报名进度</span>
                  <span class="font-extrabold text-[#D97706]">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}</span>
                </div>
                <div class="w-full h-2.5 bg-[#E7E5E4] rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-[#FFC800] to-[#FF9600] rounded-full transition-all" :style="{ width: enrollPercent(s) + '%' }"></div>
                </div>
              </div>
            </div>
            <!-- Card Footer -->
            <div class="flex items-center justify-between px-5 py-3 bg-[#FFFBF5] border-t-2 border-[#E7E5E4]">
              <span class="text-[10px] text-[#A8A29E] font-bold">ID: {{ s.id }}</span>
              <div class="flex items-center gap-1">
                <button v-if="s.status === 'PENDING' || s.status === 'CLOSED'" class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-emerald-600 hover:bg-emerald-600/10 transition cursor-pointer" title="开放报名" @click="openEnrollment(s.id)"><DoorOpen class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button v-if="s.status === 'ENROLLING'" class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-amber-600 hover:bg-amber-600/10 transition cursor-pointer" title="关闭报名" @click="closeEnrollment(s.id)"><DoorClosed class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-[#D97706] hover:bg-[#D97706]/10 transition cursor-pointer" @click="openEditDialog(s)"><Pencil class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-red-600 hover:bg-red-600/10 transition cursor-pointer" @click="confirmDelete(s)"><Trash2 class="w-3.5 h-3.5" :stroke-width="2" /></button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Session Panel：monospace + 绿色指标 + 紧凑面板表
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Session Management</span>
            <code class="text-[10px] font-mono text-[#22C55E] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ sessions.length }} records</code>
          </div>
          <button class="px-3 py-1.5 rounded bg-[#0F172A] text-white text-[11px] font-mono font-semibold cursor-pointer hover:bg-[#1E293B] transition inline-flex items-center gap-1.5" @click="openCreateDialog">
            <Plus class="w-3.5 h-3.5" :stroke-width="2" /> create
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center flex-wrap">
          <div class="relative flex-1">
            <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#94A3B8] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="search sessions..."
              class="w-full pl-8 pr-3 py-1.5 rounded border border-[#E2E8F0] bg-[#F8FAFC] text-[11px] font-mono text-[#0F172A] outline-none focus:border-[#0284C7] transition placeholder-[#94A3B8]"
              @input="onSearch" />
          </div>
          <div class="flex gap-0.5">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-2.5 py-1.5 rounded text-[10px] font-mono cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']"
              @click="filterStatus = sf.value; loadSessions()">
              {{ sf.labelEn }}
            </button>
          </div>
        </div>

        <!-- Grafana Table -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <span>Table — Sessions</span>
            <span class="text-[#94A3B8]">{{ sessions.length }} rows</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-[#E2E8F0]">
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">id</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">course</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">instructor</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">period</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">capacity</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">location</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">status</th>
                  <th class="text-right px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">loading...</td></tr>
                <tr v-else-if="!sessions.length"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">no data</td></tr>
                <tr v-for="s in sessions" :key="s.id" class="border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ s.id }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#0F172A] font-semibold">{{ s.courseName }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ s.instructorName || '—' }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ s.startDate }}~{{ s.endDate }}</td>
                  <td class="px-3 py-2 text-center">
                    <span class="text-[11px] font-mono font-semibold" :class="enrollPercent(s) >= 80 ? 'text-[#EF4444]' : 'text-[#22C55E]'">
                      {{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}
                    </span>
                  </td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ s.location || '—' }}</td>
                  <td class="px-3 py-2 text-center">
                    <span :class="['text-[9px] font-mono font-semibold', proStatusColor(s.status)]">
                      {{ proStatusText(s.status) }}
                    </span>
                  </td>
                  <td class="px-3 py-2">
                    <div class="flex items-center justify-end gap-0.5">
                      <button v-if="s.status === 'PENDING' || s.status === 'CLOSED'" class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#22C55E] hover:bg-[#22C55E]/5 transition cursor-pointer" @click="openEnrollment(s.id)">open</button>
                      <button v-if="s.status === 'ENROLLING'" class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#EAB308] hover:bg-[#EAB308]/5 transition cursor-pointer" @click="closeEnrollment(s.id)">close</button>
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#64748B] hover:text-[#0284C7] hover:bg-[#0284C7]/5 transition cursor-pointer" @click="openEditDialog(s)">edit</button>
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#DC2626] hover:bg-[#DC2626]/5 transition cursor-pointer" @click="confirmDelete(s)">del</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

    <!-- ======== Shared Modals ======== -->
    <!-- Create / Edit -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card" style="max-width: 540px;">
          <h3 class="modal-title">{{ isEdit ? '编辑班期' : '新建班期' }}</h3>
          <div class="space-y-4 mt-4">
            <div>
              <label class="modal-label">课程 <span class="text-red-500">*</span></label>
              <select v-model="form.courseId" class="modal-input">
                <option :value="0" disabled>请选择课程</option>
                <option v-for="c in courseOptions" :key="c.id" :value="c.id">{{ c.title }}</option>
              </select>
            </div>
            <div>
              <label class="modal-label">讲师 <span class="text-red-500">*</span></label>
              <select v-model="form.instructorId" class="modal-input">
                <option :value="0" disabled>请选择讲师</option>
                <option v-for="u in instructorOptions" :key="u.id" :value="u.id">{{ u.realName || u.username }}</option>
              </select>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div><label class="modal-label">开始日期 <span class="text-red-500">*</span></label><input v-model="form.startDate" type="date" class="modal-input" /></div>
              <div><label class="modal-label">结束日期 <span class="text-red-500">*</span></label><input v-model="form.endDate" type="date" class="modal-input" /></div>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div><label class="modal-label">最大人数 <span class="text-red-500">*</span></label><input v-model.number="form.maxStudents" type="number" min="1" class="modal-input" placeholder="30" /></div>
              <div><label class="modal-label">地点</label><input v-model="form.location" type="text" class="modal-input" placeholder="教室/线上链接" /></div>
            </div>
          </div>
          <div class="flex justify-end gap-3 pt-4">
            <button class="modal-cancel" @click="dialogVisible = false">取消</button>
            <button class="modal-confirm" :disabled="submitting" @click="submitForm">{{ submitting ? '提交中...' : isEdit ? '更新' : '创建' }}</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Delete Confirm -->
    <Teleport to="body">
      <div v-if="deleteDialogVisible" class="modal-overlay" @click.self="deleteDialogVisible = false">
        <div class="modal-card">
          <h3 class="modal-title" style="color: rgb(var(--color-danger))">确认删除</h3>
          <p class="text-sm mt-1 mb-4" style="color: rgb(var(--color-text-secondary))">确定删除班期 <strong>#{{ deleteTarget?.id }}</strong>（{{ deleteTarget?.courseName }}）吗？此操作不可撤销。</p>
          <div class="flex justify-end gap-3">
            <button class="modal-cancel" @click="deleteDialogVisible = false">取消</button>
            <button class="modal-danger" :disabled="submitting" @click="doDelete">{{ submitting ? '删除中...' : '确认删除' }}</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { sessionApi, type Session } from '@/api/session'
import { courseApi } from '@/api/course'
import { userApi } from '@/api/user'
import { useThemeStore } from '@/stores/theme'
import { toast } from '@/composables/useToast'
import {
  Plus, Search, Pencil, Trash2,
  DoorOpen, DoorClosed, CheckCircle, Clock,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Status Filters ── */
const statusFilters = [
  { value: '', label: '全部', labelEn: 'all', emoji: '📅' },
  { value: 'PENDING', label: '待开放', labelEn: 'pending', emoji: '⏳' },
  { value: 'ENROLLING', label: '报名中', labelEn: 'enrolling', emoji: '✅' },
  { value: 'CLOSED', label: '已关闭', labelEn: 'closed', emoji: '🔒' },
  { value: 'COMPLETED', label: '已完成', labelEn: 'completed', emoji: '🏆' },
]

/* ── State ── */
const sessions = ref<Session[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
let searchTimer: ReturnType<typeof setTimeout> | null = null

/* ── Dropdown Options ── */
const courseOptions = ref<any[]>([])
const instructorOptions = ref<any[]>([])

/* ── Load ── */
async function loadSessions() {
  loading.value = true
  try {
    const params: any = {}
    if (searchKeyword.value.trim()) params.keyword = searchKeyword.value.trim()
    if (filterStatus.value) params.status = filterStatus.value
    const r: any = await sessionApi.list(params)
    sessions.value = r?.records || r || []
  } catch {
    sessions.value = []
  } finally {
    loading.value = false
  }
}

function onSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => loadSessions(), 300)
}

async function loadDropdownData() {
  try {
    const cr: any = await courseApi.listPublished()
    courseOptions.value = cr?.records || cr || []
  } catch { courseOptions.value = [] }
  try {
    const ur: any = await userApi.list({ role: 'INSTRUCTOR' })
    instructorOptions.value = ur?.records || ur || []
  } catch { instructorOptions.value = [] }
}

/* ── Helpers ── */
function statusLabel(s: string) {
  return ({ ENROLLING: '报名中', CLOSED: '已关闭', PENDING: '待开放', COMPLETED: '已完成' })[s] || s
}

function enrollPercent(s: Session) {
  if (!s.maxStudents) return 0
  return Math.min(Math.round(((s.enrolledCount ?? 0) / s.maxStudents) * 100), 100)
}

/* Theme-specific badges */
function stripeStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-emerald-50 text-emerald-600',
    PENDING: 'bg-amber-50 text-amber-600',
    CLOSED: 'bg-red-50 text-red-500',
    COMPLETED: 'bg-indigo-50 text-[#635BFF]',
  })[s] || 'bg-gray-50 text-gray-600'
}
function darkStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-[#23A55A]/15 text-[#23A55A]',
    PENDING: 'bg-[#FEE75C]/15 text-[#FEE75C]',
    CLOSED: 'bg-[#ED4245]/15 text-[#ED4245]',
    COMPLETED: 'bg-[#5865F2]/15 text-[#5865F2]',
  })[s] || 'bg-white/10 text-[#B5BAC1]'
}
function warmStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-emerald-50 text-emerald-700',
    PENDING: 'bg-amber-50 text-amber-700',
    CLOSED: 'bg-red-50 text-red-600',
    COMPLETED: 'bg-[#D97706]/10 text-[#D97706]',
  })[s] || 'bg-gray-50 text-gray-600'
}
function warmStatusEmoji(s: string) {
  return ({ ENROLLING: '✅', PENDING: '⏳', CLOSED: '🔒', COMPLETED: '🏆' })[s] || '📝'
}
function proStatusColor(s: string) {
  return ({
    ENROLLING: 'text-[#22C55E]',
    PENDING: 'text-[#EAB308]',
    CLOSED: 'text-[#EF4444]',
    COMPLETED: 'text-[#0284C7]',
  })[s] || 'text-[#94A3B8]'
}
function proStatusText(s: string) {
  return ({
    ENROLLING: '● enrolling',
    PENDING: '○ pending',
    CLOSED: '○ closed',
    COMPLETED: '● completed',
  })[s] || s.toLowerCase()
}

/* ── Open / Close Enrollment ── */
async function openEnrollment(id: number) {
  try {
    await sessionApi.open(id)
    toast.success('已开放报名')
    loadSessions()
  } catch (e: any) {
    toast.error(e?.response?.data?.message || e?.message || '操作失败')
  }
}

async function closeEnrollment(id: number) {
  try {
    await sessionApi.close(id)
    toast.success('已关闭报名')
    loadSessions()
  } catch (e: any) {
    toast.error(e?.response?.data?.message || e?.message || '操作失败')
  }
}

/* ── Create / Edit ── */
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)

const defaultForm = () => ({
  courseId: 0,
  instructorId: 0,
  startDate: '',
  endDate: '',
  maxStudents: 30,
  location: '',
})
const form = ref(defaultForm())

function openCreateDialog() {
  isEdit.value = false
  editingId.value = null
  form.value = defaultForm()
  dialogVisible.value = true
}

function openEditDialog(s: Session) {
  isEdit.value = true
  editingId.value = s.id
  form.value = {
    courseId: s.courseId,
    instructorId: s.instructorId,
    startDate: s.startDate,
    endDate: s.endDate,
    maxStudents: s.maxStudents,
    location: s.location || '',
  }
  dialogVisible.value = true
}

function validateForm(): string | null {
  if (!form.value.courseId) return '请选择课程'
  if (!form.value.instructorId) return '请选择讲师'
  if (!form.value.startDate) return '请选择开始日期'
  if (!form.value.endDate) return '请选择结束日期'
  if (form.value.endDate < form.value.startDate) return '结束日期不能早于开始日期'
  if (!form.value.maxStudents || form.value.maxStudents < 1) return '最大人数至少为 1'
  return null
}

async function submitForm() {
  const err = validateForm()
  if (err) { toast.warning(err); return }
  submitting.value = true
  try {
    const payload = { ...form.value }
    if (isEdit.value && editingId.value !== null) {
      await sessionApi.update(editingId.value, payload)
      toast.success('班期已更新')
    } else {
      await sessionApi.create(payload)
      toast.success('班期已创建')
    }
    dialogVisible.value = false
    loadSessions()
  } catch (e: any) {
    toast.error(e?.response?.data?.message || e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

/* ── Delete ── */
const deleteDialogVisible = ref(false)
const deleteTarget = ref<Session | null>(null)

function confirmDelete(s: Session) {
  deleteTarget.value = s
  deleteDialogVisible.value = true
}

async function doDelete() {
  if (!deleteTarget.value) return
  submitting.value = true
  try {
    await sessionApi.delete(deleteTarget.value.id)
    toast.success('班期已删除')
    deleteDialogVisible.value = false
    deleteTarget.value = null
    loadSessions()
  } catch (e: any) {
    toast.error(e?.response?.data?.message || e?.message || '删除失败')
  } finally {
    submitting.value = false
  }
}

/* ── Init ── */
onMounted(() => {
  loadSessions()
  loadDropdownData()
})
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card {
  padding: 20px; background: #fff; border-radius: 8px;
  border: 1px solid #E3E8EE; box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}

/* ======== RAYCAST (Dark) ======== */
.raycast-card {
  padding: 16px; border-radius: 12px;
  background: #111113; border: 1px solid rgba(255,255,255,0.06);
}

/* ======== NOTION (Warm) ======== */
.notion-card {
  padding: 20px; background: #FFFBF5;
  border: 2px solid #E7E5E4; border-radius: 20px; box-shadow: 0 3px 0 #E7E5E4;
}

/* ======== GRAFANA (Pro) ======== */
.grafana-panel { background: #fff; border: 1px solid #E2E8F0; border-radius: 4px; overflow: hidden; }
.grafana-panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #F8FAFC; border-bottom: 1px solid #E2E8F0;
  font-size: 10px; font-weight: 600; color: #0F172A; font-family: monospace; text-transform: uppercase; letter-spacing: 0.05em;
}

/* ======== Shared Modal ======== */
.modal-overlay { position: fixed; inset: 0; z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 1rem; background: rgba(0,0,0,0.4); backdrop-filter: blur(4px); animation: ovl 0.15s ease; }
.modal-card { background: rgb(var(--color-surface)); border-radius: 12px; padding: 28px; width: 100%; max-width: 480px; box-shadow: 0 25px 50px rgba(0,0,0,0.15); animation: mci 0.2s cubic-bezier(0.16,1,0.3,1); }
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
