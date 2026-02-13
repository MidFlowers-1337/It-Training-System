<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Dashboard：卡片搜索栏 + 精致表格 + pill badge
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">用户管理</h1>
            <p class="text-sm text-[#425466] mt-0.5">管理平台所有用户账号</p>
          </div>
          <button class="px-4 py-2 rounded-lg bg-[#635BFF] text-white text-sm font-medium cursor-pointer hover:brightness-110 transition inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2" /> 创建用户
          </button>
        </div>

        <!-- Search & Filter Card -->
        <div class="stripe-card !py-3 !px-4">
          <div class="flex gap-3 items-center flex-wrap">
            <div class="relative flex-1 min-w-[220px]">
              <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#8898AA] pointer-events-none" :stroke-width="1.75" />
              <input v-model="searchKeyword" type="text" placeholder="搜索用户名、姓名或邮箱..."
                class="w-full pl-9 pr-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none focus:border-[#635BFF] focus:ring-2 focus:ring-[#635BFF]/10 transition placeholder-[#8898AA]"
                @input="onSearchDebounced" />
            </div>
            <select v-model="filterRole" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="loadUsers">
              <option value="">全部角色</option>
              <option value="ADMIN">管理员</option>
              <option value="INSTRUCTOR">讲师</option>
              <option value="STUDENT">学生</option>
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
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">用户名</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">姓名</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">邮箱</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">角色</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">状态</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">创建时间</th>
                  <th class="text-right px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="8" class="text-center py-12 text-sm text-[#8898AA]">加载中...</td></tr>
                <tr v-else-if="users.length === 0"><td colspan="8" class="text-center py-12 text-sm text-[#8898AA]">暂无用户数据</td></tr>
                <tr v-for="user in users" :key="user.id" class="border-b border-[#F0F3F7] last:border-0 hover:bg-[#F6F9FC]/50 transition-colors">
                  <td class="px-4 py-3 text-[#8898AA] tabular-nums">{{ user.id }}</td>
                  <td class="px-4 py-3 font-medium text-[#0A2540]">{{ user.username }}</td>
                  <td class="px-4 py-3 text-[#425466]">{{ user.realName || '-' }}</td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs">{{ user.email || '-' }}</td>
                  <td class="px-4 py-3">
                    <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', stripeBadge(user.role)]">{{ roleLabel(user.role) }}</span>
                  </td>
                  <td class="px-4 py-3">
                    <label class="stripe-toggle cursor-pointer">
                      <input type="checkbox" :checked="user.status === 1" @change="toggleStatus(user)" />
                      <span class="stripe-toggle-track"></span>
                    </label>
                  </td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs tabular-nums">{{ formatDate(user.createdAt) }}</td>
                  <td class="px-4 py-3">
                    <div class="flex items-center justify-end gap-1">
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-[#635BFF] hover:bg-[#635BFF]/5 transition cursor-pointer" title="编辑" @click="openEditDialog(user)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-amber-500 hover:bg-amber-500/5 transition cursor-pointer" title="重置密码" @click="openResetDialog(user)"><KeyRound class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-red-500 hover:bg-red-500/5 transition cursor-pointer" title="删除" @click="confirmDelete(user)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t border-[#E3E8EE]">
            <span class="text-xs text-[#8898AA]">共 <strong class="text-[#0A2540]">{{ total }}</strong> 条</span>
            <div class="flex items-center gap-1">
              <select v-model="pageSize" class="px-2 py-1 rounded border border-[#E3E8EE] text-xs text-[#425466] outline-none cursor-pointer" @change="onPageSizeChange">
                <option :value="10">10/页</option><option :value="20">20/页</option><option :value="50">50/页</option>
              </select>
              <button class="stripe-page-btn" :disabled="currentPage <= 1" @click="goToPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#CBD5E1]">…</span>
                <button v-else :class="['stripe-page-btn', p === currentPage && 'active']" @click="goToPage(p as number)">{{ p }}</button>
              </template>
              <button class="stripe-page-btn" :disabled="currentPage >= totalPages" @click="goToPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord Members：暗色成员列表 + 在线状态 + 角色色标
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-4">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">用户管理</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ total }} members</span>
          </div>
          <button class="px-4 py-2 rounded-md bg-[#5865F2] text-white text-sm font-semibold cursor-pointer hover:bg-[#4752C4] transition inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2" /> 创建用户
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center">
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#6B6B6E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索成员..."
              class="w-full pl-9 pr-3 py-2 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none focus:border-[#5865F2] transition placeholder-[#6B6B6E]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-1">
            <button v-for="r in roleFilters" :key="r.value"
              :class="['px-3 py-2 rounded-md text-xs font-medium cursor-pointer transition-all',
                filterRole === r.value ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] bg-white/[0.04] hover:bg-white/[0.06]']"
              @click="filterRole = r.value; loadUsers()">
              {{ r.label }}
            </button>
          </div>
        </div>

        <!-- Members List -->
        <div class="raycast-card !p-0">
          <div v-if="loading" class="py-12 text-center text-sm text-[#6B6B6E]">加载中...</div>
          <div v-else-if="users.length === 0" class="py-12 text-center text-sm text-[#6B6B6E]">暂无成员</div>
          <template v-else>
            <div v-for="(user, i) in users" :key="user.id"
              :class="['flex items-center gap-4 px-4 py-3 hover:bg-white/[0.03] transition-colors', i < users.length - 1 && 'border-b border-white/[0.04]']">
              <!-- Avatar -->
              <div class="relative flex-shrink-0">
                <div class="w-9 h-9 rounded-full bg-[#5865F2]/20 flex items-center justify-center text-sm font-bold text-[#5865F2]">
                  {{ (user.realName || user.username).charAt(0).toUpperCase() }}
                </div>
                <div :class="['absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full border-2 border-[#111113]',
                  user.status === 1 ? 'bg-[#23A55A]' : 'bg-[#80848E]']"></div>
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-[#EDEDED] truncate">{{ user.realName || user.username }}</span>
                  <span class="text-[10px] text-[#B5BAC1]">{{ user.username }}</span>
                  <span :class="['px-1.5 py-0.5 rounded text-[9px] font-bold uppercase', discordRoleBadge(user.role)]">{{ roleLabel(user.role) }}</span>
                </div>
                <div class="text-xs text-[#6B6B6E] mt-0.5 truncate">
                  {{ user.email || '未绑定邮箱' }} · ID: {{ user.id }} · {{ formatDate(user.createdAt) }}
                </div>
              </div>
              <!-- Actions -->
              <div class="flex items-center gap-1 flex-shrink-0">
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#EDEDED] hover:bg-white/[0.06] transition cursor-pointer" @click="openEditDialog(user)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#FEE75C] hover:bg-[#FEE75C]/10 transition cursor-pointer" @click="openResetDialog(user)"><KeyRound class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#ED4245] hover:bg-[#ED4245]/10 transition cursor-pointer" @click="toggleStatus(user)">
                  <component :is="user.status === 1 ? ShieldCheck : ShieldOff" class="w-3.5 h-3.5" :stroke-width="1.75" />
                </button>
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#ED4245] hover:bg-[#ED4245]/10 transition cursor-pointer" @click="confirmDelete(user)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
              </div>
            </div>
          </template>

          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t border-white/[0.04]">
            <span class="text-[10px] font-mono text-[#6B6B6E]">{{ total }} members · page {{ currentPage }}/{{ totalPages }}</span>
            <div class="flex items-center gap-1">
              <button class="discord-page-btn" :disabled="currentPage <= 1" @click="goToPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#6B6B6E]">…</span>
                <button v-else :class="['discord-page-btn', p === currentPage && 'active']" @click="goToPage(p as number)">{{ p }}</button>
              </template>
              <button class="discord-page-btn" :disabled="currentPage >= totalPages" @click="goToPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Database：属性表 + emoji + 粗体 + 暖色行
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">👥</span>
            <h1 class="text-xl font-extrabold text-[#292524]">用户管理</h1>
          </div>
          <button class="px-5 py-2.5 rounded-2xl bg-[#292524] text-white text-sm font-extrabold cursor-pointer hover:brightness-110 transition shadow-[0_3px_0_#1C1917] active:translate-y-[2px] active:shadow-[0_1px_0_#1C1917] inline-flex items-center gap-2" @click="openCreateDialog">
            <Plus class="w-4 h-4" :stroke-width="2.5" /> 创建用户
          </button>
        </div>

        <!-- Filter Tags -->
        <div class="flex items-center gap-2 flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索用户..."
              class="w-full pl-9 pr-3 py-2.5 rounded-2xl border-2 border-[#E7E5E4] bg-white text-sm text-[#292524] outline-none focus:border-[#D97706] transition placeholder-[#A8A29E]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-1.5">
            <button v-for="r in roleFilters" :key="r.value"
              :class="['px-4 py-2 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
                filterRole === r.value ? 'bg-[#292524] text-white border-[#292524] shadow-[0_2px_0_#1C1917]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:border-[#D97706]']"
              @click="filterRole = r.value; loadUsers()">
              {{ r.emoji }} {{ r.label }}
            </button>
          </div>
        </div>

        <!-- Database Table -->
        <div class="notion-card !p-0 overflow-hidden">
          <!-- Header Row -->
          <div class="flex items-center gap-0 bg-[#FFFBF5] border-b-2 border-[#E7E5E4] px-4 py-2.5 text-xs font-bold text-[#78716C]">
            <span class="w-10">🔢</span>
            <span class="flex-1">👤 用户名</span>
            <span class="w-28">✏️ 姓名</span>
            <span class="w-40">📧 邮箱</span>
            <span class="w-20 text-center">🏷️ 角色</span>
            <span class="w-16 text-center">💡 状态</span>
            <span class="w-24">📅 创建</span>
            <span class="w-28 text-right">⚡ 操作</span>
          </div>

          <div v-if="loading" class="py-12 text-center text-sm text-[#78716C]">加载中...</div>
          <div v-else-if="users.length === 0" class="py-12 text-center"><span class="text-3xl">📭</span><p class="text-sm text-[#78716C] mt-2">暂无用户数据</p></div>
          <template v-else>
            <div v-for="user in users" :key="user.id"
              class="flex items-center gap-0 px-4 py-3 border-b border-[#E7E5E4] last:border-0 hover:bg-[#D97706]/[0.03] transition-colors">
              <span class="w-10 text-xs text-[#A8A29E] font-bold">{{ user.id }}</span>
              <span class="flex-1 text-sm font-bold text-[#292524]">{{ user.username }}</span>
              <span class="w-28 text-sm text-[#292524]">{{ user.realName || '—' }}</span>
              <span class="w-40 text-xs text-[#78716C] truncate">{{ user.email || '—' }}</span>
              <span class="w-20 text-center">
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-extrabold', warmRoleBadge(user.role)]">{{ warmRoleEmoji(user.role) }} {{ roleLabel(user.role) }}</span>
              </span>
              <span class="w-16 text-center">
                <button @click="toggleStatus(user)" class="cursor-pointer text-base" :title="user.status === 1 ? '正常' : '禁用'">
                  {{ user.status === 1 ? '✅' : '⛔' }}
                </button>
              </span>
              <span class="w-24 text-xs text-[#A8A29E]">{{ formatDate(user.createdAt) }}</span>
              <div class="w-28 flex items-center justify-end gap-1">
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-[#D97706] hover:bg-[#D97706]/10 transition cursor-pointer" @click="openEditDialog(user)"><Pencil class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-amber-600 hover:bg-amber-600/10 transition cursor-pointer" @click="openResetDialog(user)"><KeyRound class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-red-600 hover:bg-red-600/10 transition cursor-pointer" @click="confirmDelete(user)"><Trash2 class="w-3.5 h-3.5" :stroke-width="2" /></button>
              </div>
            </div>
          </template>

          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t-2 border-[#E7E5E4] bg-[#FFFBF5]">
            <span class="text-xs font-bold text-[#78716C]">共 {{ total }} 位用户</span>
            <div class="flex items-center gap-1">
              <button class="warm-page-btn" :disabled="currentPage <= 1" @click="goToPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#A8A29E]">…</span>
                <button v-else :class="['warm-page-btn', p === currentPage && 'active']" @click="goToPage(p as number)">{{ p }}</button>
              </template>
              <button class="warm-page-btn" :disabled="currentPage >= totalPages" @click="goToPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Admin：monospace + 绿色指标 + 紧凑面板表格
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <!-- Dashboard Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">User Management</span>
            <code class="text-[10px] font-mono text-[#22C55E] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ total }} records</code>
          </div>
          <button class="px-3 py-1.5 rounded bg-[#0F172A] text-white text-[11px] font-mono font-semibold cursor-pointer hover:bg-[#1E293B] transition inline-flex items-center gap-1.5" @click="openCreateDialog">
            <Plus class="w-3.5 h-3.5" :stroke-width="2" /> create
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center">
          <div class="relative flex-1">
            <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#94A3B8] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="search users..."
              class="w-full pl-8 pr-3 py-1.5 rounded border border-[#E2E8F0] bg-[#F8FAFC] text-[11px] font-mono text-[#0F172A] outline-none focus:border-[#0284C7] transition placeholder-[#94A3B8]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-0.5">
            <button v-for="r in roleFilters" :key="r.value"
              :class="['px-2.5 py-1.5 rounded text-[10px] font-mono cursor-pointer transition-all',
                filterRole === r.value ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']"
              @click="filterRole = r.value; loadUsers()">
              {{ r.labelEn }}
            </button>
          </div>
        </div>

        <!-- Grafana Panel Table -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <span>Table — Users</span>
            <span class="text-[#94A3B8]">{{ users.length }} rows / {{ total }} total</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-[#E2E8F0]">
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">id</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">username</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">name</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">email</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">role</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">status</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">created</th>
                  <th class="text-right px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">loading...</td></tr>
                <tr v-else-if="users.length === 0"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">no data</td></tr>
                <tr v-for="user in users" :key="user.id" class="border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ user.id }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#0F172A] font-semibold">{{ user.username }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ user.realName || '—' }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ user.email || '—' }}</td>
                  <td class="px-3 py-2">
                    <code :class="['text-[10px] px-1.5 py-0.5 rounded', proRoleBadge(user.role)]">{{ user.role.toLowerCase() }}</code>
                  </td>
                  <td class="px-3 py-2 text-center">
                    <button @click="toggleStatus(user)" class="cursor-pointer">
                      <span :class="['text-[9px] font-mono font-semibold', user.status === 1 ? 'text-[#22C55E]' : 'text-[#EF4444]']">
                        {{ user.status === 1 ? '● active' : '○ disabled' }}
                      </span>
                    </button>
                  </td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ user.createdAt ? new Date(user.createdAt).toISOString().split('T')[0] : '—' }}</td>
                  <td class="px-3 py-2">
                    <div class="flex items-center justify-end gap-0.5">
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#64748B] hover:text-[#0284C7] hover:bg-[#0284C7]/5 transition cursor-pointer" @click="openEditDialog(user)">edit</button>
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#64748B] hover:text-[#EAB308] hover:bg-[#EAB308]/5 transition cursor-pointer" @click="openResetDialog(user)">reset</button>
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#DC2626] hover:bg-[#DC2626]/5 transition cursor-pointer" @click="confirmDelete(user)">del</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-3 py-2 border-t border-[#E2E8F0] bg-[#F8FAFC]">
            <code class="text-[9px] text-[#94A3B8]">showing {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, total) }} of {{ total }}</code>
            <div class="flex items-center gap-0.5">
              <select v-model="pageSize" class="px-1 py-0.5 rounded border border-[#E2E8F0] text-[9px] font-mono text-[#64748B] outline-none cursor-pointer" @change="onPageSizeChange">
                <option :value="10">10</option><option :value="20">20</option><option :value="50">50</option>
              </select>
              <button class="grafana-page-btn" :disabled="currentPage <= 1" @click="goToPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-0.5 text-[9px] text-[#94A3B8]">…</span>
                <button v-else :class="['grafana-page-btn', p === currentPage && 'active']" @click="goToPage(p as number)">{{ p }}</button>
              </template>
              <button class="grafana-page-btn" :disabled="currentPage >= totalPages" @click="goToPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ======== Shared Modals (CSS vars) ======== -->
    <!-- Create -->
    <Teleport to="body">
      <div v-if="showCreateDialog" class="modal-overlay" @click.self="showCreateDialog = false">
        <div class="modal-card">
          <h3 class="modal-title">创建用户</h3>
          <form @submit.prevent="handleCreate" class="space-y-4 mt-4">
            <div><label class="modal-label">用户名 <span class="text-red-500">*</span></label><input v-model="createForm.username" type="text" required class="modal-input" placeholder="请输入用户名" /></div>
            <div><label class="modal-label">密码 <span class="text-red-500">*</span></label><input v-model="createForm.password" type="password" required class="modal-input" placeholder="请输入密码" /></div>
            <div><label class="modal-label">姓名</label><input v-model="createForm.realName" type="text" class="modal-input" placeholder="真实姓名" /></div>
            <div><label class="modal-label">邮箱</label><input v-model="createForm.email" type="email" class="modal-input" placeholder="邮箱地址" /></div>
            <div><label class="modal-label">角色 <span class="text-red-500">*</span></label>
              <select v-model="createForm.role" required class="modal-input">
                <option value="" disabled>请选择</option>
                <option value="ADMIN">管理员</option>
                <option value="INSTRUCTOR">讲师</option>
                <option value="STUDENT">学生</option>
              </select>
            </div>
            <div class="flex justify-end gap-3 pt-2">
              <button type="button" class="modal-cancel" @click="showCreateDialog = false">取消</button>
              <button type="submit" class="modal-confirm" :disabled="createLoading">{{ createLoading ? '创建中...' : '创建' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- Edit -->
    <Teleport to="body">
      <div v-if="showEditDialog" class="modal-overlay" @click.self="showEditDialog = false">
        <div class="modal-card">
          <h3 class="modal-title">编辑用户</h3>
          <form @submit.prevent="handleEdit" class="space-y-4 mt-4">
            <div><label class="modal-label">用户名</label><input :value="editForm.username" type="text" class="modal-input opacity-50" disabled /></div>
            <div><label class="modal-label">姓名</label><input v-model="editForm.realName" type="text" class="modal-input" placeholder="真实姓名" /></div>
            <div><label class="modal-label">邮箱</label><input v-model="editForm.email" type="email" class="modal-input" placeholder="邮箱地址" /></div>
            <div><label class="modal-label">角色 <span class="text-red-500">*</span></label>
              <select v-model="editForm.role" required class="modal-input">
                <option value="ADMIN">管理员</option>
                <option value="INSTRUCTOR">讲师</option>
                <option value="STUDENT">学生</option>
              </select>
            </div>
            <div class="flex justify-end gap-3 pt-2">
              <button type="button" class="modal-cancel" @click="showEditDialog = false">取消</button>
              <button type="submit" class="modal-confirm" :disabled="editLoading">{{ editLoading ? '保存中...' : '保存' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- Reset Password -->
    <Teleport to="body">
      <div v-if="showResetDialog" class="modal-overlay" @click.self="showResetDialog = false">
        <div class="modal-card">
          <h3 class="modal-title">重置密码</h3>
          <p class="text-sm mt-1 mb-4" style="color: rgb(var(--color-text-secondary))">为 <strong>{{ resetTarget?.username }}</strong> 设置新密码</p>
          <form @submit.prevent="handleResetPassword" class="space-y-4">
            <div><label class="modal-label">新密码 <span class="text-red-500">*</span></label><input v-model="newPassword" type="password" required minlength="6" class="modal-input" placeholder="至少 6 位" /></div>
            <div class="flex justify-end gap-3 pt-2">
              <button type="button" class="modal-cancel" @click="showResetDialog = false">取消</button>
              <button type="submit" class="modal-confirm" :disabled="resetLoading">{{ resetLoading ? '重置中...' : '确认重置' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- Delete Confirm -->
    <Teleport to="body">
      <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
        <div class="modal-card">
          <h3 class="modal-title" style="color: rgb(var(--color-danger))">确认删除</h3>
          <p class="text-sm mt-1 mb-4" style="color: rgb(var(--color-text-secondary))">确定删除用户 <strong>{{ deleteTarget?.username }}</strong>？此操作不可撤销。</p>
          <div class="flex justify-end gap-3">
            <button class="modal-cancel" @click="showDeleteDialog = false">取消</button>
            <button class="modal-danger" :disabled="deleteLoading" @click="handleDelete">{{ deleteLoading ? '删除中...' : '确认删除' }}</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { userApi, type User } from '@/api/user'
import { useThemeStore } from '@/stores/theme'
import { toast } from '@/composables/useToast'
import {
  Plus, Search, Pencil, Trash2, KeyRound,
  ShieldCheck, ShieldOff,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Role Filters ── */
const roleFilters = [
  { value: '', label: '全部', labelEn: 'all', emoji: '👥' },
  { value: 'ADMIN', label: '管理员', labelEn: 'admin', emoji: '🔴' },
  { value: 'INSTRUCTOR', label: '讲师', labelEn: 'instructor', emoji: '🟡' },
  { value: 'STUDENT', label: '学生', labelEn: 'student', emoji: '🔵' },
]

/* ── State ── */
const users = ref<User[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterRole = ref('')
let searchTimer: ReturnType<typeof setTimeout> | null = null

/* ── Pagination ── */
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const displayedPages = computed(() => {
  const pages: (number | string)[] = []
  const tp = totalPages.value, cp = currentPage.value
  if (tp <= 7) { for (let i = 1; i <= tp; i++) pages.push(i); return pages }
  pages.push(1)
  if (cp > 3) pages.push('...')
  for (let i = Math.max(2, cp - 1); i <= Math.min(tp - 1, cp + 1); i++) pages.push(i)
  if (cp < tp - 2) pages.push('...')
  pages.push(tp)
  return pages
})

function goToPage(page: number) { if (page >= 1 && page <= totalPages.value) { currentPage.value = page; loadUsers() } }
function onPageSizeChange() { currentPage.value = 1; loadUsers() }

/* ── Load ── */
async function loadUsers() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: currentPage.value, size: pageSize.value }
    if (searchKeyword.value.trim()) params.keyword = searchKeyword.value.trim()
    if (filterRole.value) params.role = filterRole.value
    const res: any = await userApi.list(params)
    users.value = res?.records || res?.content || res?.data || res || []
    total.value = res?.total ?? res?.totalElements ?? (Array.isArray(res) ? res.length : 0)
  } catch (e: any) {
    toast.error(e?.message || '加载失败')
    users.value = []; total.value = 0
  } finally { loading.value = false }
}

function onSearchDebounced() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { currentPage.value = 1; loadUsers() }, 300)
}

/* ── Helpers ── */
function roleLabel(role: string) { return ({ ADMIN: '管理员', INSTRUCTOR: '讲师', STUDENT: '学生' })[role] || role }
function formatDate(d: string) { if (!d) return '-'; try { return new Date(d).toLocaleDateString('zh-CN') } catch { return d } }

/* Theme-specific badge classes */
function stripeBadge(role: string) {
  return ({ ADMIN: 'bg-red-50 text-red-600', INSTRUCTOR: 'bg-amber-50 text-amber-600', STUDENT: 'bg-indigo-50 text-[#635BFF]' })[role] || 'bg-gray-50 text-gray-600'
}
function discordRoleBadge(role: string) {
  return ({ ADMIN: 'bg-[#ED4245]/15 text-[#ED4245]', INSTRUCTOR: 'bg-[#FEE75C]/15 text-[#FEE75C]', STUDENT: 'bg-[#5865F2]/15 text-[#5865F2]' })[role] || 'bg-white/10 text-[#B5BAC1]'
}
function warmRoleBadge(role: string) {
  return ({ ADMIN: 'bg-red-50 text-red-700', INSTRUCTOR: 'bg-amber-50 text-amber-700', STUDENT: 'bg-[#D97706]/10 text-[#D97706]' })[role] || 'bg-gray-50 text-gray-600'
}
function warmRoleEmoji(role: string) { return ({ ADMIN: '🔴', INSTRUCTOR: '🟡', STUDENT: '🔵' })[role] || '⚪' }
function proRoleBadge(role: string) {
  return ({ ADMIN: 'bg-red-50 text-red-700', INSTRUCTOR: 'bg-amber-50 text-amber-700', STUDENT: 'bg-[#0284C7]/10 text-[#0284C7]' })[role] || 'bg-gray-50 text-gray-600'
}

/* ── Status Toggle ── */
async function toggleStatus(user: User) {
  const ns = user.status === 1 ? 0 : 1
  try { await userApi.updateStatus(user.id, { status: ns }); user.status = ns; toast.success(ns === 1 ? '已启用' : '已禁用') }
  catch (e: any) { toast.error(e?.message || '状态更新失败') }
}

/* ── Create ── */
const showCreateDialog = ref(false), createLoading = ref(false)
const createForm = ref({ username: '', password: '', realName: '', email: '', role: '' })
function openCreateDialog() { createForm.value = { username: '', password: '', realName: '', email: '', role: '' }; showCreateDialog.value = true }
async function handleCreate() {
  createLoading.value = true
  try {
    await userApi.create({ username: createForm.value.username, password: createForm.value.password, realName: createForm.value.realName || undefined, email: createForm.value.email || undefined, role: createForm.value.role })
    toast.success('创建成功'); showCreateDialog.value = false; loadUsers()
  } catch (e: any) { toast.error(e?.message || '创建失败') }
  finally { createLoading.value = false }
}

/* ── Edit ── */
const showEditDialog = ref(false), editLoading = ref(false), editTargetId = ref(0)
const editForm = ref({ username: '', realName: '', email: '', role: '' })
function openEditDialog(user: User) {
  editTargetId.value = user.id
  editForm.value = { username: user.username, realName: user.realName || '', email: user.email || '', role: user.role }
  showEditDialog.value = true
}
async function handleEdit() {
  editLoading.value = true
  try {
    await userApi.update(editTargetId.value, { realName: editForm.value.realName, email: editForm.value.email, role: editForm.value.role })
    toast.success('更新成功'); showEditDialog.value = false; loadUsers()
  } catch (e: any) { toast.error(e?.message || '更新失败') }
  finally { editLoading.value = false }
}

/* ── Reset Password ── */
const showResetDialog = ref(false), resetLoading = ref(false), resetTarget = ref<User | null>(null), newPassword = ref('')
function openResetDialog(user: User) { resetTarget.value = user; newPassword.value = ''; showResetDialog.value = true }
async function handleResetPassword() {
  if (!resetTarget.value) return
  resetLoading.value = true
  try { await userApi.resetPassword(resetTarget.value.id, { newPassword: newPassword.value }); toast.success('密码重置成功'); showResetDialog.value = false }
  catch (e: any) { toast.error(e?.message || '重置失败') }
  finally { resetLoading.value = false }
}

/* ── Delete ── */
const showDeleteDialog = ref(false), deleteLoading = ref(false), deleteTarget = ref<User | null>(null)
function confirmDelete(user: User) { deleteTarget.value = user; showDeleteDialog.value = true }
async function handleDelete() {
  if (!deleteTarget.value) return
  deleteLoading.value = true
  try { await userApi.delete(deleteTarget.value.id); toast.success('删除成功'); showDeleteDialog.value = false; loadUsers() }
  catch (e: any) { toast.error(e?.message || '删除失败') }
  finally { deleteLoading.value = false }
}

onMounted(loadUsers)
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}
.stripe-toggle { position: relative; display: inline-block; cursor: pointer; }
.stripe-toggle input { position: absolute; opacity: 0; width: 0; height: 0; }
.stripe-toggle-track {
  display: block; width: 36px; height: 20px; border-radius: 10px;
  background: #CBD5E1; transition: background 0.2s; position: relative;
}
.stripe-toggle-track::after {
  content: ''; position: absolute; top: 2px; left: 2px;
  width: 16px; height: 16px; border-radius: 8px; background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.15); transition: transform 0.2s;
}
.stripe-toggle input:checked + .stripe-toggle-track { background: #635BFF; }
.stripe-toggle input:checked + .stripe-toggle-track::after { transform: translateX(16px); }
.stripe-page-btn {
  min-width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center;
  padding: 0 6px; border: 1px solid #E3E8EE; border-radius: 6px;
  background: #fff; color: #425466; font-size: 12px; cursor: pointer; transition: all 0.15s;
}
.stripe-page-btn:hover:not(:disabled):not(.active) { border-color: #635BFF; color: #635BFF; }
.stripe-page-btn.active { background: #635BFF; border-color: #635BFF; color: #fff; }
.stripe-page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ======== RAYCAST / Discord (Dark) ======== */
.raycast-card {
  padding: 16px; border-radius: 12px;
  background: #111113; border: 1px solid rgba(255,255,255,0.06);
}
.discord-page-btn {
  min-width: 26px; height: 26px; display: inline-flex; align-items: center; justify-content: center;
  padding: 0 5px; border: none; border-radius: 4px;
  background: transparent; color: #B5BAC1; font-size: 12px; cursor: pointer; transition: all 0.15s;
}
.discord-page-btn:hover:not(:disabled):not(.active) { background: rgba(255,255,255,0.06); color: #EDEDED; }
.discord-page-btn.active { background: #5865F2; color: #fff; }
.discord-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ======== NOTION (Warm) ======== */
.notion-card {
  padding: 20px; background: #FFFBF5;
  border: 2px solid #E7E5E4; border-radius: 20px; box-shadow: 0 3px 0 #E7E5E4;
}
.warm-page-btn {
  min-width: 30px; height: 30px; display: inline-flex; align-items: center; justify-content: center;
  padding: 0 6px; border: 2px solid #E7E5E4; border-radius: 10px;
  background: #fff; color: #292524; font-size: 12px; font-weight: 700; cursor: pointer; transition: all 0.15s;
}
.warm-page-btn:hover:not(:disabled):not(.active) { border-color: #D97706; color: #D97706; }
.warm-page-btn.active { background: #292524; border-color: #292524; color: #fff; box-shadow: 0 2px 0 #1C1917; }
.warm-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ======== GRAFANA (Pro) ======== */
.grafana-panel {
  background: #fff; border: 1px solid #E2E8F0; border-radius: 4px; overflow: hidden;
}
.grafana-panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #F8FAFC; border-bottom: 1px solid #E2E8F0;
  font-size: 10px; font-weight: 600; color: #0F172A;
  font-family: monospace; text-transform: uppercase; letter-spacing: 0.05em;
}
.grafana-page-btn {
  min-width: 22px; height: 22px; display: inline-flex; align-items: center; justify-content: center;
  padding: 0 4px; border: 1px solid #E2E8F0; border-radius: 3px;
  background: #fff; color: #64748B; font-size: 10px; font-family: monospace; cursor: pointer; transition: all 0.15s;
}
.grafana-page-btn:hover:not(:disabled):not(.active) { border-color: #0284C7; color: #0284C7; }
.grafana-page-btn.active { background: #0F172A; border-color: #0F172A; color: #fff; }
.grafana-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

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
