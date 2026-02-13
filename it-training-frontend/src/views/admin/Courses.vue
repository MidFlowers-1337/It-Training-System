<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Product Catalog：卡片表 + Level Pills + Status Dots
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">课程管理</h1>
            <p class="text-sm text-[#425466] mt-0.5">管理平台课程内容</p>
          </div>
          <button class="px-4 py-2 rounded-lg bg-[#635BFF] text-white text-sm font-medium cursor-pointer hover:brightness-110 transition inline-flex items-center gap-2" @click="openCreate">
            <Plus class="w-4 h-4" :stroke-width="2" /> 新建课程
          </button>
        </div>

        <!-- Search & Filter -->
        <div class="stripe-card !py-3 !px-4">
          <div class="flex gap-3 items-center flex-wrap">
            <div class="relative flex-1 min-w-[220px]">
              <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#8898AA] pointer-events-none" :stroke-width="1.75" />
              <input v-model="keyword" type="text" placeholder="搜索课程名称..."
                class="w-full pl-9 pr-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none focus:border-[#635BFF] focus:ring-2 focus:ring-[#635BFF]/10 transition placeholder-[#8898AA]"
                @input="onSearch" />
            </div>
            <select v-model="filterCategory" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="load">
              <option value="">全部分类</option>
              <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
            </select>
            <select v-model="filterLevel" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="load">
              <option value="">全部等级</option>
              <option value="BEGINNER">入门</option>
              <option value="INTERMEDIATE">中级</option>
              <option value="ADVANCED">高级</option>
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
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">课程名称</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">分类</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">等级</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">讲师</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">报名</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">状态</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">创建时间</th>
                  <th class="text-right px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="9" class="text-center py-12 text-sm text-[#8898AA]">加载中...</td></tr>
                <tr v-else-if="!courses.length"><td colspan="9" class="text-center py-12 text-sm text-[#8898AA]">暂无课程数据</td></tr>
                <tr v-for="c in courses" :key="c.id" class="border-b border-[#F0F3F7] last:border-0 hover:bg-[#F6F9FC]/50 transition-colors">
                  <td class="px-4 py-3 text-[#8898AA] tabular-nums">{{ c.id }}</td>
                  <td class="px-4 py-3 font-medium text-[#0A2540]">{{ c.title }}</td>
                  <td class="px-4 py-3"><span v-if="c.category" class="px-2 py-0.5 rounded-full text-xs bg-[#F6F9FC] text-[#425466] border border-[#E3E8EE]">{{ c.category }}</span></td>
                  <td class="px-4 py-3"><span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', stripeLevelBadge(c.level)]">{{ levelLabel(c.level) }}</span></td>
                  <td class="px-4 py-3 text-[#425466] text-xs">{{ c.instructor || '-' }}</td>
                  <td class="px-4 py-3 text-[#635BFF] font-medium tabular-nums">{{ c.enrollCount || 0 }}</td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center gap-1.5 text-xs font-medium">
                      <span :class="['w-1.5 h-1.5 rounded-full', c.status === 'PUBLISHED' ? 'bg-emerald-500' : 'bg-[#CBD5E1]']"></span>
                      {{ c.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs tabular-nums">{{ formatDate(c.createdAt) }}</td>
                  <td class="px-4 py-3">
                    <div class="flex items-center justify-end gap-1">
                      <button v-if="c.status !== 'PUBLISHED'" class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-emerald-500 hover:bg-emerald-500/5 transition cursor-pointer" title="发布" @click="publish(c)"><Eye class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button v-else class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-amber-500 hover:bg-amber-500/5 transition cursor-pointer" title="下架" @click="unpublish(c)"><EyeOff class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-[#635BFF] hover:bg-[#635BFF]/5 transition cursor-pointer" title="编辑" @click="openEdit(c)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                      <button class="w-7 h-7 rounded-lg flex items-center justify-center text-[#8898AA] hover:text-red-500 hover:bg-red-500/5 transition cursor-pointer" title="删除" @click="openDelete(c)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
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
              <button class="stripe-page-btn" :disabled="page <= 1" @click="goPage(page - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#CBD5E1]">…</span>
                <button v-else :class="['stripe-page-btn', p === page && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="stripe-page-btn" :disabled="page >= totalPages" @click="goPage(page + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Raycast Catalog：暗色卡片列表 + 渐变标签 + 发光状态
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">课程管理</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ total }} courses</span>
          </div>
          <button class="px-4 py-2 rounded-md bg-[#5865F2] text-white text-sm font-semibold cursor-pointer hover:bg-[#4752C4] transition inline-flex items-center gap-2" @click="openCreate">
            <Plus class="w-4 h-4" :stroke-width="2" /> 新建课程
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#6B6B6E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="keyword" type="text" placeholder="搜索课程..."
              class="w-full pl-9 pr-3 py-2 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none focus:border-[#5865F2] transition placeholder-[#6B6B6E]"
              @input="onSearch" />
          </div>
          <select v-model="filterCategory" class="px-3 py-2 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none cursor-pointer" @change="load">
            <option value="" class="bg-[#111113]">全部分类</option>
            <option v-for="c in categoryOptions" :key="c" :value="c" class="bg-[#111113]">{{ c }}</option>
          </select>
          <div class="flex gap-1">
            <button v-for="lf in levelFilters" :key="lf.value"
              :class="['px-3 py-2 rounded-md text-xs font-medium cursor-pointer transition-all',
                filterLevel === lf.value ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] bg-white/[0.04] hover:bg-white/[0.06]']"
              @click="filterLevel = lf.value; load()">
              {{ lf.label }}
            </button>
          </div>
        </div>

        <!-- Course Cards List -->
        <div class="raycast-card !p-0">
          <div v-if="loading" class="py-12 text-center text-sm text-[#6B6B6E]">加载中...</div>
          <div v-else-if="!courses.length" class="py-12 text-center text-sm text-[#6B6B6E]">暂无课程</div>
          <template v-else>
            <div v-for="(c, i) in courses" :key="c.id"
              :class="['flex items-center gap-4 px-4 py-3.5 hover:bg-white/[0.03] transition-colors', i < courses.length - 1 && 'border-b border-white/[0.04]']">
              <!-- Category Icon -->
              <div class="w-10 h-10 rounded-lg bg-[#5865F2]/15 flex items-center justify-center text-[#5865F2] flex-shrink-0">
                <BookOpen class="w-5 h-5" :stroke-width="1.5" />
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-[#EDEDED] truncate">{{ c.title }}</span>
                  <span :class="['px-1.5 py-0.5 rounded text-[9px] font-bold uppercase', darkLevelBadge(c.level)]">{{ levelLabel(c.level) }}</span>
                  <span :class="['w-1.5 h-1.5 rounded-full ml-1', c.status === 'PUBLISHED' ? 'bg-[#23A55A]' : 'bg-[#80848E]']"></span>
                </div>
                <div class="text-xs text-[#6B6B6E] mt-0.5 truncate">
                  {{ c.category || '未分类' }} · {{ c.instructor || '未指定' }} · {{ c.enrollCount || 0 }} 人报名 · {{ formatDate(c.createdAt) }}
                </div>
              </div>
              <!-- Actions -->
              <div class="flex items-center gap-1 flex-shrink-0">
                <button v-if="c.status !== 'PUBLISHED'" class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#23A55A] hover:bg-[#23A55A]/10 transition cursor-pointer" @click="publish(c)"><Eye class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                <button v-else class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#FEE75C] hover:bg-[#FEE75C]/10 transition cursor-pointer" @click="unpublish(c)"><EyeOff class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#EDEDED] hover:bg-white/[0.06] transition cursor-pointer" @click="openEdit(c)"><Pencil class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
                <button class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#ED4245] hover:bg-[#ED4245]/10 transition cursor-pointer" @click="openDelete(c)"><Trash2 class="w-3.5 h-3.5" :stroke-width="1.75" /></button>
              </div>
            </div>
          </template>
          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t border-white/[0.04]">
            <span class="text-[10px] font-mono text-[#6B6B6E]">{{ total }} courses · page {{ page }}/{{ totalPages }}</span>
            <div class="flex items-center gap-1">
              <button class="discord-page-btn" :disabled="page <= 1" @click="goPage(page - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#6B6B6E]">…</span>
                <button v-else :class="['discord-page-btn', p === page && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="discord-page-btn" :disabled="page >= totalPages" @click="goPage(page + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Database：Emoji 表头 + 属性行 + 彩色等级标签
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📚</span>
            <h1 class="text-xl font-extrabold text-[#292524]">课程管理</h1>
          </div>
          <button class="px-5 py-2.5 rounded-2xl bg-[#292524] text-white text-sm font-extrabold cursor-pointer hover:brightness-110 transition shadow-[0_3px_0_#1C1917] active:translate-y-[2px] active:shadow-[0_1px_0_#1C1917] inline-flex items-center gap-2" @click="openCreate">
            <Plus class="w-4 h-4" :stroke-width="2.5" /> 新建课程
          </button>
        </div>

        <!-- Filter Tags -->
        <div class="flex items-center gap-2 flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="keyword" type="text" placeholder="搜索课程..."
              class="w-full pl-9 pr-3 py-2.5 rounded-2xl border-2 border-[#E7E5E4] bg-white text-sm text-[#292524] outline-none focus:border-[#D97706] transition placeholder-[#A8A29E]"
              @input="onSearch" />
          </div>
          <select v-model="filterCategory" class="px-3 py-2.5 rounded-2xl border-2 border-[#E7E5E4] bg-white text-sm text-[#292524] outline-none cursor-pointer" @change="load">
            <option value="">📁 全部分类</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
          </select>
          <div class="flex gap-1.5">
            <button v-for="lf in levelFilters" :key="lf.value"
              :class="['px-4 py-2 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
                filterLevel === lf.value ? 'bg-[#292524] text-white border-[#292524] shadow-[0_2px_0_#1C1917]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:border-[#D97706]']"
              @click="filterLevel = lf.value; load()">
              {{ lf.emoji }} {{ lf.label }}
            </button>
          </div>
        </div>

        <!-- Database Table -->
        <div class="notion-card !p-0 overflow-hidden">
          <div class="flex items-center gap-0 bg-[#FFFBF5] border-b-2 border-[#E7E5E4] px-4 py-2.5 text-xs font-bold text-[#78716C]">
            <span class="w-10">🔢</span>
            <span class="flex-1">📖 课程名称</span>
            <span class="w-24">📁 分类</span>
            <span class="w-16 text-center">📊 等级</span>
            <span class="w-24">👨‍🏫 讲师</span>
            <span class="w-14 text-center">👥 报名</span>
            <span class="w-16 text-center">💡 状态</span>
            <span class="w-20">📅 创建</span>
            <span class="w-28 text-right">⚡ 操作</span>
          </div>

          <div v-if="loading" class="py-12 text-center text-sm text-[#78716C]">加载中...</div>
          <div v-else-if="!courses.length" class="py-12 text-center"><span class="text-3xl">📭</span><p class="text-sm text-[#78716C] mt-2">暂无课程数据</p></div>
          <template v-else>
            <div v-for="c in courses" :key="c.id"
              class="flex items-center gap-0 px-4 py-3 border-b border-[#E7E5E4] last:border-0 hover:bg-[#D97706]/[0.03] transition-colors">
              <span class="w-10 text-xs text-[#A8A29E] font-bold">{{ c.id }}</span>
              <span class="flex-1 text-sm font-bold text-[#292524] truncate">{{ c.title }}</span>
              <span class="w-24 text-xs text-[#78716C]">{{ c.category || '—' }}</span>
              <span class="w-16 text-center">
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-extrabold', warmLevelBadge(c.level)]">{{ warmLevelEmoji(c.level) }} {{ levelLabel(c.level) }}</span>
              </span>
              <span class="w-24 text-xs text-[#292524]">{{ c.instructor || '—' }}</span>
              <span class="w-14 text-center text-xs font-bold text-[#D97706]">{{ c.enrollCount || 0 }}</span>
              <span class="w-16 text-center">
                <button @click="c.status === 'PUBLISHED' ? unpublish(c) : publish(c)" class="cursor-pointer text-base" :title="c.status === 'PUBLISHED' ? '已发布' : '草稿'">
                  {{ c.status === 'PUBLISHED' ? '✅' : '📝' }}
                </button>
              </span>
              <span class="w-20 text-xs text-[#A8A29E]">{{ formatDate(c.createdAt) }}</span>
              <div class="w-28 flex items-center justify-end gap-1">
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-[#D97706] hover:bg-[#D97706]/10 transition cursor-pointer" @click="openEdit(c)"><Pencil class="w-3.5 h-3.5" :stroke-width="2" /></button>
                <button class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-red-600 hover:bg-red-600/10 transition cursor-pointer" @click="openDelete(c)"><Trash2 class="w-3.5 h-3.5" :stroke-width="2" /></button>
              </div>
            </div>
          </template>

          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t-2 border-[#E7E5E4] bg-[#FFFBF5]">
            <span class="text-xs font-bold text-[#78716C]">共 {{ total }} 门课程</span>
            <div class="flex items-center gap-1">
              <button class="warm-page-btn" :disabled="page <= 1" @click="goPage(page - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#A8A29E]">…</span>
                <button v-else :class="['warm-page-btn', p === page && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="warm-page-btn" :disabled="page >= totalPages" @click="goPage(page + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Panel：monospace + 绿色指标 + 紧凑面板表格
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Course Management</span>
            <code class="text-[10px] font-mono text-[#22C55E] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ total }} records</code>
          </div>
          <button class="px-3 py-1.5 rounded bg-[#0F172A] text-white text-[11px] font-mono font-semibold cursor-pointer hover:bg-[#1E293B] transition inline-flex items-center gap-1.5" @click="openCreate">
            <Plus class="w-3.5 h-3.5" :stroke-width="2" /> create
          </button>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center flex-wrap">
          <div class="relative flex-1">
            <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#94A3B8] pointer-events-none" :stroke-width="1.75" />
            <input v-model="keyword" type="text" placeholder="search courses..."
              class="w-full pl-8 pr-3 py-1.5 rounded border border-[#E2E8F0] bg-[#F8FAFC] text-[11px] font-mono text-[#0F172A] outline-none focus:border-[#0284C7] transition placeholder-[#94A3B8]"
              @input="onSearch" />
          </div>
          <select v-model="filterCategory" class="px-2 py-1.5 rounded border border-[#E2E8F0] bg-white text-[10px] font-mono text-[#0F172A] outline-none cursor-pointer" @change="load">
            <option value="">all categories</option>
            <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
          </select>
          <div class="flex gap-0.5">
            <button v-for="lf in levelFilters" :key="lf.value"
              :class="['px-2.5 py-1.5 rounded text-[10px] font-mono cursor-pointer transition-all',
                filterLevel === lf.value ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']"
              @click="filterLevel = lf.value; load()">
              {{ lf.labelEn }}
            </button>
          </div>
        </div>

        <!-- Grafana Panel Table -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <span>Table — Courses</span>
            <span class="text-[#94A3B8]">{{ courses.length }} rows / {{ total }} total</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-[#E2E8F0]">
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">id</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">title</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">category</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">level</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">instructor</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">enrolls</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">status</th>
                  <th class="text-right px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">loading...</td></tr>
                <tr v-else-if="!courses.length"><td colspan="8" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">no data</td></tr>
                <tr v-for="c in courses" :key="c.id" class="border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ c.id }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#0F172A] font-semibold">{{ c.title }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ c.category || '—' }}</td>
                  <td class="px-3 py-2"><code :class="['text-[10px] px-1.5 py-0.5 rounded', proLevelBadge(c.level)]">{{ levelLabel(c.level) || '—' }}</code></td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ c.instructor || '—' }}</td>
                  <td class="px-3 py-2 text-center text-[11px] font-mono text-[#22C55E] font-semibold">{{ c.enrollCount || 0 }}</td>
                  <td class="px-3 py-2 text-center">
                    <button @click="c.status === 'PUBLISHED' ? unpublish(c) : publish(c)" class="cursor-pointer">
                      <span :class="['text-[9px] font-mono font-semibold', c.status === 'PUBLISHED' ? 'text-[#22C55E]' : 'text-[#94A3B8]']">
                        {{ c.status === 'PUBLISHED' ? '● published' : '○ draft' }}
                      </span>
                    </button>
                  </td>
                  <td class="px-3 py-2">
                    <div class="flex items-center justify-end gap-0.5">
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#64748B] hover:text-[#0284C7] hover:bg-[#0284C7]/5 transition cursor-pointer" @click="openEdit(c)">edit</button>
                      <button class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#DC2626] hover:bg-[#DC2626]/5 transition cursor-pointer" @click="openDelete(c)">del</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <!-- Pagination -->
          <div v-if="total > 0" class="flex items-center justify-between px-3 py-2 border-t border-[#E2E8F0] bg-[#F8FAFC]">
            <code class="text-[9px] text-[#94A3B8]">showing {{ (page - 1) * pageSize + 1 }}-{{ Math.min(page * pageSize, total) }} of {{ total }}</code>
            <div class="flex items-center gap-0.5">
              <button class="grafana-page-btn" :disabled="page <= 1" @click="goPage(page - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-0.5 text-[9px] text-[#94A3B8]">…</span>
                <button v-else :class="['grafana-page-btn', p === page && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="grafana-page-btn" :disabled="page >= totalPages" @click="goPage(page + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ======== Shared Modals ======== -->
    <!-- Create / Edit -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="showForm = false">
        <div class="modal-card" style="max-width: 520px;">
          <h3 class="modal-title">{{ isEdit ? '编辑课程' : '新建课程' }}</h3>
          <div class="space-y-4 mt-4">
            <div><label class="modal-label">课程名称 <span class="text-red-500">*</span></label><input v-model="form.title" type="text" class="modal-input" placeholder="请输入课程名称" /></div>
            <div><label class="modal-label">描述</label><textarea v-model="form.description" rows="3" class="modal-input" placeholder="课程简介"></textarea></div>
            <div class="grid grid-cols-2 gap-3">
              <div><label class="modal-label">分类</label>
                <select v-model="form.category" class="modal-input"><option value="">请选择</option><option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option></select>
              </div>
              <div><label class="modal-label">等级</label>
                <select v-model="form.level" class="modal-input"><option value="">请选择</option><option value="BEGINNER">入门</option><option value="INTERMEDIATE">中级</option><option value="ADVANCED">高级</option></select>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div><label class="modal-label">讲师</label><input v-model="form.instructor" type="text" class="modal-input" placeholder="讲师姓名" /></div>
              <div><label class="modal-label">时长（分钟）</label><input v-model.number="form.duration" type="number" min="0" class="modal-input" placeholder="0" /></div>
            </div>
          </div>
          <div class="flex justify-end gap-3 pt-4">
            <button class="modal-cancel" @click="showForm = false">取消</button>
            <button class="modal-confirm" @click="submitForm" :disabled="submitting || !form.title">{{ submitting ? '保存中...' : isEdit ? '更新' : '创建' }}</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Delete Confirm -->
    <Teleport to="body">
      <div v-if="showDelete" class="modal-overlay" @click.self="showDelete = false">
        <div class="modal-card">
          <h3 class="modal-title" style="color: rgb(var(--color-danger))">确认删除</h3>
          <p class="text-sm mt-1 mb-4" style="color: rgb(var(--color-text-secondary))">确定删除课程「<strong>{{ deleteTarget?.title }}</strong>」吗？此操作不可撤销。</p>
          <div class="flex justify-end gap-3">
            <button class="modal-cancel" @click="showDelete = false">取消</button>
            <button class="modal-danger" :disabled="deleting" @click="doDelete">{{ deleting ? '删除中...' : '确认删除' }}</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { courseApi, type Course } from '@/api/course'
import { useThemeStore } from '@/stores/theme'
import { toast } from '@/composables/useToast'
import { BookOpen, Plus, Search, Pencil, Trash2, Eye, EyeOff } from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Level Filters ── */
const levelFilters = [
  { value: '', label: '全部', labelEn: 'all', emoji: '📊' },
  { value: 'BEGINNER', label: '入门', labelEn: 'beginner', emoji: '🟢' },
  { value: 'INTERMEDIATE', label: '中级', labelEn: 'intermediate', emoji: '🟡' },
  { value: 'ADVANCED', label: '高级', labelEn: 'advanced', emoji: '🔴' },
]

const categoryOptions = ['Java', 'Python', '前端', '后端', '数据库', 'AI/ML', '移动端', '安全', '大数据', 'DevOps']

/* ── State ── */
const courses = ref<any[]>([])
const loading = ref(false)
const keyword = ref('')
const filterCategory = ref('')
const filterLevel = ref('')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
let searchTimer: ReturnType<typeof setTimeout> | null = null

/* ── Pagination ── */
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const displayedPages = computed(() => {
  const pages: (number | string)[] = []
  const tp = totalPages.value, cp = page.value
  if (tp <= 7) { for (let i = 1; i <= tp; i++) pages.push(i); return pages }
  pages.push(1)
  if (cp > 3) pages.push('...')
  for (let i = Math.max(2, cp - 1); i <= Math.min(tp - 1, cp + 1); i++) pages.push(i)
  if (cp < tp - 2) pages.push('...')
  pages.push(tp)
  return pages
})
function goPage(p: number) { if (p >= 1 && p <= totalPages.value) { page.value = p; load() } }

/* ── Load ── */
async function load() {
  loading.value = true
  try {
    const res: any = await courseApi.list({
      page: page.value, size: pageSize.value,
      keyword: keyword.value || undefined,
      category: filterCategory.value || undefined,
      level: filterLevel.value || undefined,
    })
    courses.value = res?.records || res?.content || res || []
    total.value = res?.total || res?.totalElements || courses.value.length
  } catch {
    courses.value = []; total.value = 0
  } finally { loading.value = false }
}

function onSearch() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 1; load() }, 300)
}

/* ── Helpers ── */
function levelLabel(lv: string) { return ({ BEGINNER: '入门', INTERMEDIATE: '中级', ADVANCED: '高级' })[lv] || lv || '' }
function formatDate(d: string) { if (!d) return '-'; try { return new Date(d).toLocaleDateString('zh-CN') } catch { return d } }

/* Theme-specific level badges */
function stripeLevelBadge(lv: string) {
  return ({ BEGINNER: 'bg-emerald-50 text-emerald-600', INTERMEDIATE: 'bg-amber-50 text-amber-600', ADVANCED: 'bg-red-50 text-red-600' })[lv] || 'bg-gray-50 text-gray-600'
}
function darkLevelBadge(lv: string) {
  return ({ BEGINNER: 'bg-[#23A55A]/15 text-[#23A55A]', INTERMEDIATE: 'bg-[#FEE75C]/15 text-[#FEE75C]', ADVANCED: 'bg-[#ED4245]/15 text-[#ED4245]' })[lv] || 'bg-white/10 text-[#B5BAC1]'
}
function warmLevelBadge(lv: string) {
  return ({ BEGINNER: 'bg-emerald-50 text-emerald-700', INTERMEDIATE: 'bg-amber-50 text-amber-700', ADVANCED: 'bg-red-50 text-red-700' })[lv] || 'bg-gray-50 text-gray-600'
}
function warmLevelEmoji(lv: string) { return ({ BEGINNER: '🟢', INTERMEDIATE: '🟡', ADVANCED: '🔴' })[lv] || '⚪' }
function proLevelBadge(lv: string) {
  return ({ BEGINNER: 'bg-emerald-50 text-emerald-700', INTERMEDIATE: 'bg-amber-50 text-amber-700', ADVANCED: 'bg-red-50 text-red-700' })[lv] || 'bg-gray-50 text-gray-600'
}

/* ── Publish / Unpublish ── */
async function publish(c: any) {
  try { await courseApi.publish(c.id); toast.success('已发布'); load() }
  catch (e: any) { toast.error(e.message || '发布失败') }
}
async function unpublish(c: any) {
  try { await courseApi.unpublish(c.id); toast.success('已下架'); load() }
  catch (e: any) { toast.error(e.message || '下架失败') }
}

/* ── Form ── */
const showForm = ref(false), isEdit = ref(false), submitting = ref(false), editId = ref<number | null>(null)
const form = ref({ title: '', description: '', category: '', level: '', instructor: '', duration: 0 })

function openCreate() {
  isEdit.value = false; editId.value = null
  form.value = { title: '', description: '', category: '', level: '', instructor: '', duration: 0 }
  showForm.value = true
}
function openEdit(c: any) {
  isEdit.value = true; editId.value = c.id
  form.value = { title: c.title || '', description: c.description || '', category: c.category || '', level: c.level || '', instructor: c.instructor || '', duration: c.duration || 0 }
  showForm.value = true
}
async function submitForm() {
  if (!form.value.title) { toast.warning('请输入课程名称'); return }
  submitting.value = true
  try {
    if (isEdit.value && editId.value) { await courseApi.update(editId.value, form.value); toast.success('课程已更新') }
    else { await courseApi.create(form.value); toast.success('课程已创建') }
    showForm.value = false; load()
  } catch (e: any) { toast.error(e.message || '操作失败') }
  finally { submitting.value = false }
}

/* ── Delete ── */
const showDelete = ref(false), deleteTarget = ref<any>(null), deleting = ref(false)
function openDelete(c: any) { deleteTarget.value = c; showDelete.value = true }
async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try { await courseApi.delete(deleteTarget.value.id); toast.success('已删除'); showDelete.value = false; load() }
  catch (e: any) { toast.error(e.message || '删除失败') }
  finally { deleting.value = false }
}

onMounted(load)
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card {
  padding: 20px; background: #fff; border-radius: 8px;
  border: 1px solid #E3E8EE; box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}
.stripe-page-btn {
  min-width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center;
  padding: 0 6px; border: 1px solid #E3E8EE; border-radius: 6px;
  background: #fff; color: #425466; font-size: 12px; cursor: pointer; transition: all 0.15s;
}
.stripe-page-btn:hover:not(:disabled):not(.active) { border-color: #635BFF; color: #635BFF; }
.stripe-page-btn.active { background: #635BFF; border-color: #635BFF; color: #fff; }
.stripe-page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ======== RAYCAST (Dark) ======== */
.raycast-card {
  padding: 16px; border-radius: 12px; background: #111113; border: 1px solid rgba(255,255,255,0.06);
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
.grafana-panel { background: #fff; border: 1px solid #E2E8F0; border-radius: 4px; overflow: hidden; }
.grafana-panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #F8FAFC; border-bottom: 1px solid #E2E8F0;
  font-size: 10px; font-weight: 600; color: #0F172A; font-family: monospace; text-transform: uppercase; letter-spacing: 0.05em;
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
