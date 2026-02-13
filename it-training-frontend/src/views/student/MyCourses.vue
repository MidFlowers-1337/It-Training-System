<template>
  <div>
    <!-- Skeleton Loading -->
    <div v-if="loading" class="text-center py-20 text-sm" :class="theme === 'dark' ? 'text-[#6B6B6E]' : theme === 'warm' ? 'text-[#78716C]' : 'text-[#8898AA]'">加载中...</div>

    <!-- ================================================================
         ☀️ LIGHT — Stripe Billing：表格 + 状态指示器 + 多层阴影
         ================================================================ -->
    <template v-else-if="theme === 'light'">
      <div class="space-y-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <h1 class="text-xl font-bold text-[#0A2540]">我的课程</h1>
          <div class="flex gap-2">
            <button
              v-for="t in tabs" :key="t.key"
              @click="tab = t.key"
              :class="[
                'inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-md text-[13px] font-medium border-none cursor-pointer transition-all',
                tab === t.key
                  ? 'bg-[#635BFF]/10 text-[#635BFF] font-semibold'
                  : 'text-[#425466] hover:bg-[#F6F9FC] hover:text-[#0A2540]'
              ]"
            >
              <component :is="t.icon" class="w-4 h-4" :stroke-width="1.75" />
              {{ t.label }}
              <span v-if="t.count !== undefined" :class="[
                'text-[11px] font-semibold px-1.5 py-px rounded-full',
                tab === t.key ? 'bg-[#635BFF]/15 text-[#635BFF]' : 'bg-[#E3E8EE] text-[#8898AA]'
              ]">{{ t.count }}</span>
            </button>
          </div>
        </div>

        <!-- Table Card -->
        <div v-if="displayList.length" class="stripe-billing-card overflow-hidden">
          <!-- Table Header -->
          <div class="flex items-center gap-4 px-5 py-3 bg-[#F6F9FC] text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider border-b border-[#E3E8EE]">
            <span class="w-6"></span>
            <span class="flex-1">课程名称</span>
            <span class="w-20 text-center hidden sm:block">状态</span>
            <span class="w-28 text-center">进度</span>
            <span class="w-24 text-right hidden sm:block">开始时间</span>
            <span class="w-20 text-right">操作</span>
          </div>
          <!-- Table Rows -->
          <div class="divide-y divide-[#F0F3F7]">
            <div
              v-for="item in displayList" :key="item.id"
              class="flex items-center gap-4 px-5 py-3.5 hover:bg-[#F6F9FC] cursor-pointer transition-colors"
              @click="goStudy(item)"
            >
              <!-- Status Dot -->
              <div class="w-6 flex justify-center">
                <div class="w-2 h-2 rounded-full"
                     :class="item.progress >= 100 ? 'bg-emerald-500' : item.progress > 0 ? 'bg-[#635BFF]' : 'bg-[#CBD5E1]'"></div>
              </div>
              <!-- Name -->
              <div class="flex-1 min-w-0 flex items-center gap-2">
                <span class="text-sm font-medium text-[#0A2540] truncate">{{ item.courseName }}</span>
              </div>
              <!-- Status Badge -->
              <div class="w-20 text-center hidden sm:flex justify-center">
                <span class="text-[11px] font-medium px-2 py-px rounded-full"
                      :class="enrollBadgeStripe(item.enrollmentStatus || item.status)">
                  {{ enrollLabel(item.enrollmentStatus || item.status) }}
                </span>
              </div>
              <!-- Progress -->
              <div class="w-28 flex items-center gap-2">
                <div class="flex-1 h-1.5 bg-[#E3E8EE] rounded-full overflow-hidden">
                  <div class="h-full rounded-full transition-all duration-500"
                       :class="item.progress >= 100 ? 'bg-emerald-500' : 'bg-[#635BFF]'"
                       :style="{ width: Math.min(item.progress || 0, 100) + '%' }"></div>
                </div>
                <span class="text-xs font-medium w-8 text-right" :class="item.progress >= 100 ? 'text-emerald-600' : 'text-[#635BFF]'">
                  {{ item.progress || 0 }}%
                </span>
              </div>
              <!-- Date -->
              <span class="w-24 text-right text-xs text-[#8898AA] hidden sm:block">
                {{ item.startedAt ? formatDate(item.startedAt) : '—' }}
              </span>
              <!-- Action -->
              <div class="w-20 text-right flex items-center justify-end gap-1">
                <button
                  v-if="item.enrollmentId && item.enrollmentStatus === 'ENROLLED' && item.progress === 0"
                  class="text-xs text-red-600 hover:underline cursor-pointer"
                  @click.stop="cancelEnroll(item)"
                >取消</button>
                <span v-else class="text-xs text-[#635BFF] font-medium">继续学习 →</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-20">
          <BookOpen class="w-12 h-12 text-[#8898AA]/50 mx-auto mb-3" :stroke-width="1" />
          <p class="text-[#8898AA] text-sm">{{ emptyText }}</p>
          <button class="mt-4 text-sm text-[#635BFF] font-medium hover:underline cursor-pointer" @click="$router.push('/student/courses')">去选课</button>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Linear Projects：看板视图（3 列拖拽感）
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Header + View Toggle -->
        <div class="flex items-center justify-between">
          <h1 class="text-xl font-bold text-[#EDEDED]">我的课程</h1>
          <div class="flex items-center gap-3">
            <div class="flex border border-white/[0.06] rounded-lg overflow-hidden">
              <button @click="darkView = 'kanban'" :class="['px-3 py-1.5 text-xs font-medium transition', darkView === 'kanban' ? 'bg-[#818CF8]/15 text-[#818CF8]' : 'text-[#6B6B6E] hover:text-[#EDEDED]']">
                <Columns3 class="w-3.5 h-3.5" :stroke-width="1.75" />
              </button>
              <button @click="darkView = 'list'" :class="['px-3 py-1.5 text-xs font-medium transition', darkView === 'list' ? 'bg-[#818CF8]/15 text-[#818CF8]' : 'text-[#6B6B6E] hover:text-[#EDEDED]']">
                <List class="w-3.5 h-3.5" :stroke-width="1.75" />
              </button>
            </div>
          </div>
        </div>

        <!-- Kanban View -->
        <template v-if="darkView === 'kanban'">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- Column: 进行中 -->
            <div class="kanban-column">
              <div class="flex items-center justify-between mb-3 px-1">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 rounded-full bg-[#818CF8]"></div>
                  <span class="text-xs font-semibold text-[#EDEDED]">进行中</span>
                </div>
                <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanInProgress.length }}</span>
              </div>
              <div class="space-y-2">
                <div v-for="item in kanbanInProgress" :key="item.id"
                     class="kanban-card cursor-pointer" @click="goStudy(item)">
                  <h4 class="text-sm font-medium text-[#EDEDED] truncate mb-2">{{ item.courseName }}</h4>
                  <div class="w-full h-1 bg-white/[0.06] rounded-full overflow-hidden mb-2">
                    <div class="h-full rounded-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4]"
                         :style="{ width: (item.progress || 0) + '%' }"></div>
                  </div>
                  <div class="flex items-center justify-between text-[10px] text-[#6B6B6E]">
                    <span class="font-mono">{{ item.progress || 0 }}%</span>
                    <span>{{ item.startedAt ? formatRelative(item.startedAt) : '' }}</span>
                  </div>
                </div>
                <div v-if="!kanbanInProgress.length" class="text-center py-8 text-[#6B6B6E] text-xs">暂无课程</div>
              </div>
            </div>

            <!-- Column: 待开始 -->
            <div class="kanban-column">
              <div class="flex items-center justify-between mb-3 px-1">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 rounded-full bg-[#6B6B6E]"></div>
                  <span class="text-xs font-semibold text-[#EDEDED]">待开始</span>
                </div>
                <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanNotStarted.length }}</span>
              </div>
              <div class="space-y-2">
                <div v-for="item in kanbanNotStarted" :key="item.id"
                     class="kanban-card cursor-pointer" @click="goStudy(item)">
                  <h4 class="text-sm font-medium text-[#EDEDED] truncate mb-2">{{ item.courseName }}</h4>
                  <div class="flex items-center justify-between">
                    <span v-if="item.enrollmentStatus" class="text-[10px] font-medium px-2 py-px rounded-full bg-white/[0.06] text-[#6B6B6E]">
                      {{ enrollLabel(item.enrollmentStatus) }}
                    </span>
                    <button
                      v-if="item.enrollmentId && item.enrollmentStatus === 'ENROLLED'"
                      class="text-[10px] text-red-400 hover:underline cursor-pointer"
                      @click.stop="cancelEnroll(item)"
                    >取消</button>
                  </div>
                </div>
                <div v-if="!kanbanNotStarted.length" class="text-center py-8 text-[#6B6B6E] text-xs">暂无课程</div>
              </div>
            </div>

            <!-- Column: 已完成 -->
            <div class="kanban-column">
              <div class="flex items-center justify-between mb-3 px-1">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 rounded-full bg-emerald-400"></div>
                  <span class="text-xs font-semibold text-[#EDEDED]">已完成</span>
                </div>
                <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanCompleted.length }}</span>
              </div>
              <div class="space-y-2">
                <div v-for="item in kanbanCompleted" :key="item.id"
                     class="kanban-card cursor-pointer" @click="goStudy(item)">
                  <h4 class="text-sm font-medium text-[#EDEDED] truncate mb-1">{{ item.courseName }}</h4>
                  <div class="flex items-center gap-1.5 text-[10px] text-emerald-400">
                    <CheckCircle class="w-3 h-3" :stroke-width="2" />
                    <span>已完成</span>
                    <span v-if="item.completedAt" class="text-[#6B6B6E] ml-auto">{{ formatDate(item.completedAt) }}</span>
                  </div>
                </div>
                <div v-if="!kanbanCompleted.length" class="text-center py-8 text-[#6B6B6E] text-xs">暂无课程</div>
              </div>
            </div>
          </div>
        </template>

        <!-- List View (fallback) -->
        <template v-else>
          <div v-if="mergedList.length" class="space-y-2">
            <div
              v-for="item in mergedList" :key="item.id"
              class="kanban-card cursor-pointer flex items-center gap-4"
              @click="goStudy(item)"
            >
              <div class="w-2 h-2 rounded-full flex-shrink-0"
                   :class="item.progress >= 100 ? 'bg-emerald-400' : item.progress > 0 ? 'bg-[#818CF8]' : 'bg-[#6B6B6E]'"></div>
              <span class="text-sm font-medium text-[#EDEDED] flex-1 truncate">{{ item.courseName }}</span>
              <div class="w-24 h-1 bg-white/[0.06] rounded-full overflow-hidden">
                <div class="h-full rounded-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4]"
                     :style="{ width: (item.progress || 0) + '%' }"></div>
              </div>
              <span class="text-xs font-mono text-[#6B6B6E] w-10 text-right">{{ item.progress || 0 }}%</span>
              <ChevronRight class="w-4 h-4 text-[#6B6B6E]" :stroke-width="1.75" />
            </div>
          </div>
          <div v-else class="text-center py-20">
            <BookOpen class="w-12 h-12 text-[#6B6B6E]/50 mx-auto mb-3" :stroke-width="1" />
            <p class="text-[#6B6B6E] text-sm">暂无课程</p>
            <button class="mt-4 text-sm text-[#818CF8] font-medium hover:underline cursor-pointer" @click="$router.push('/student/courses')">去选课</button>
          </div>
        </template>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Database：表格/画廊视图切换 + 属性列
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <h1 class="text-xl font-extrabold text-[#292524]">📚 我的课程</h1>
          <div class="flex items-center gap-3">
            <!-- View Toggle (Notion-style) -->
            <div class="flex bg-[#F5F5F4] rounded-lg p-0.5">
              <button @click="warmView = 'table'" :class="['px-3 py-1.5 rounded-md text-xs font-bold transition-all', warmView === 'table' ? 'bg-white text-[#292524] shadow-sm' : 'text-[#78716C] hover:text-[#292524]']">
                📊 表格
              </button>
              <button @click="warmView = 'gallery'" :class="['px-3 py-1.5 rounded-md text-xs font-bold transition-all', warmView === 'gallery' ? 'bg-white text-[#292524] shadow-sm' : 'text-[#78716C] hover:text-[#292524]']">
                🖼 画廊
              </button>
              <button @click="warmView = 'list'" :class="['px-3 py-1.5 rounded-md text-xs font-bold transition-all', warmView === 'list' ? 'bg-white text-[#292524] shadow-sm' : 'text-[#78716C] hover:text-[#292524]']">
                📝 列表
              </button>
            </div>
            <!-- Filter -->
            <div class="flex gap-1.5">
              <button
                v-for="f in warmFilters" :key="f.key"
                @click="warmFilter = f.key"
                :class="['px-3 py-1.5 rounded-lg text-xs font-bold border-2 transition-all cursor-pointer',
                  warmFilter === f.key
                    ? 'border-[#D97706] bg-[#D97706]/10 text-[#92400E]'
                    : 'border-[#E7E5E4] text-[#78716C] hover:border-[#D6D3D1]']"
              >{{ f.label }}</button>
            </div>
          </div>
        </div>

        <!-- Table View -->
        <template v-if="warmView === 'table'">
          <div v-if="warmDisplayList.length" class="notion-table-wrapper">
            <!-- Table Header -->
            <div class="notion-table-header">
              <span class="flex-1">名称</span>
              <span class="w-28 text-center">进度</span>
              <span class="w-24 text-center hidden sm:block">状态</span>
              <span class="w-24 text-center hidden md:block">分类</span>
              <span class="w-20 text-right">操作</span>
            </div>
            <!-- Table Rows -->
            <div v-for="item in warmDisplayList" :key="item.id"
                 class="notion-table-row cursor-pointer" @click="goStudy(item)">
              <!-- Name with emoji -->
              <div class="flex-1 min-w-0 flex items-center gap-2">
                <span class="text-lg flex-shrink-0">{{ item.progress >= 100 ? '✅' : item.progress > 0 ? '📖' : '📋' }}</span>
                <span class="text-sm font-medium text-[#292524] truncate">{{ item.courseName }}</span>
              </div>
              <!-- Progress -->
              <div class="w-28 flex items-center gap-2">
                <div class="flex-1 h-2 bg-[#E7E5E4] rounded-full overflow-hidden">
                  <div class="h-full rounded-full transition-all"
                       :class="item.progress >= 100 ? 'bg-[#58CC02]' : 'bg-[#D97706]'"
                       :style="{ width: (item.progress || 0) + '%' }"></div>
                </div>
                <span class="text-xs font-bold text-[#292524] w-8 text-right">{{ item.progress || 0 }}%</span>
              </div>
              <!-- Status (Notion Select style) -->
              <div class="w-24 text-center hidden sm:flex justify-center">
                <span class="notion-select" :class="notionStatusColor(item)">
                  {{ item.progress >= 100 ? '已完成' : item.progress > 0 ? '进行中' : '未开始' }}
                </span>
              </div>
              <!-- Category -->
              <div class="w-24 text-center hidden md:flex justify-center">
                <span class="notion-select bg-[#F3E8FF] text-[#7C3AED]">课程</span>
              </div>
              <!-- Action -->
              <div class="w-20 text-right">
                <button
                  v-if="item.enrollmentId && item.enrollmentStatus === 'ENROLLED' && item.progress === 0"
                  class="text-xs text-red-600 hover:underline cursor-pointer"
                  @click.stop="cancelEnroll(item)"
                >取消</button>
                <span v-else class="text-xs text-[#D97706] font-bold">继续 →</span>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-16">
            <p class="text-3xl mb-2">📚</p>
            <p class="text-sm text-[#78716C]">{{ emptyText }}</p>
            <button class="mt-3 text-sm text-[#D97706] font-bold hover:underline cursor-pointer" @click="$router.push('/student/courses')">去选课</button>
          </div>
        </template>

        <!-- Gallery View -->
        <template v-else-if="warmView === 'gallery'">
          <div v-if="warmDisplayList.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for="item in warmDisplayList" :key="item.id"
                 class="notion-gallery-card cursor-pointer" @click="goStudy(item)">
              <!-- Top Color Bar -->
              <div class="h-2 rounded-t-xl"
                   :class="item.progress >= 100 ? 'bg-[#58CC02]' : item.progress > 0 ? 'bg-[#D97706]' : 'bg-[#E7E5E4]'"></div>
              <div class="p-4">
                <div class="flex items-start gap-3 mb-3">
                  <span class="text-2xl">{{ item.progress >= 100 ? '🎉' : item.progress > 0 ? '📖' : '📋' }}</span>
                  <div class="flex-1 min-w-0">
                    <h4 class="text-sm font-bold text-[#292524] line-clamp-2">{{ item.courseName }}</h4>
                    <span class="text-xs text-[#A8A29E]">{{ item.startedAt ? formatDate(item.startedAt) : '未开始' }}</span>
                  </div>
                </div>
                <!-- Progress Ring (inline) -->
                <div class="flex items-center gap-3">
                  <div class="relative w-10 h-10 flex-shrink-0">
                    <svg class="w-10 h-10 -rotate-90" viewBox="0 0 40 40">
                      <circle cx="20" cy="20" r="16" fill="none" stroke="#E7E5E4" stroke-width="4" />
                      <circle cx="20" cy="20" r="16" fill="none"
                        :stroke="item.progress >= 100 ? '#58CC02' : '#D97706'"
                        stroke-width="4" stroke-linecap="round"
                        :stroke-dasharray="`${(item.progress || 0) * 1.005} 100.5`" />
                    </svg>
                    <span class="absolute inset-0 flex items-center justify-center text-[9px] font-extrabold text-[#292524]">
                      {{ item.progress || 0 }}%
                    </span>
                  </div>
                  <div class="flex-1">
                    <span class="notion-select text-xs" :class="notionStatusColor(item)">
                      {{ item.progress >= 100 ? '已完成' : item.progress > 0 ? '进行中' : '未开始' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-16">
            <p class="text-3xl mb-2">🖼</p>
            <p class="text-sm text-[#78716C]">{{ emptyText }}</p>
          </div>
        </template>

        <!-- List View (simple Notion list) -->
        <template v-else>
          <div v-if="warmDisplayList.length" class="space-y-1">
            <div v-for="item in warmDisplayList" :key="item.id"
                 class="flex items-center gap-3 px-3 py-2.5 rounded-lg hover:bg-[#FEF3C7]/50 cursor-pointer transition-colors"
                 @click="goStudy(item)">
              <span class="text-base flex-shrink-0">{{ item.progress >= 100 ? '✅' : item.progress > 0 ? '📖' : '📋' }}</span>
              <span class="text-sm font-medium text-[#292524] flex-1 truncate">{{ item.courseName }}</span>
              <span class="notion-select text-[10px]" :class="notionStatusColor(item)">
                {{ item.progress >= 100 ? '完成' : item.progress > 0 ? '进行中' : '未开始' }}
              </span>
              <span class="text-xs font-bold text-[#78716C]">{{ item.progress || 0 }}%</span>
            </div>
          </div>
          <div v-else class="text-center py-16">
            <p class="text-3xl mb-2">📝</p>
            <p class="text-sm text-[#78716C]">{{ emptyText }}</p>
          </div>
        </template>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Vercel Projects：部署列表式 + 可展开详情
         ================================================================ -->
    <template v-else>
      <div class="space-y-4">
        <!-- Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <h1 class="text-sm font-semibold text-[#0F172A] uppercase tracking-wider">Projects</h1>
            <span class="text-[10px] font-mono text-[#94A3B8]">{{ mergedList.length }} total</span>
          </div>
          <div class="flex gap-1">
            <button
              v-for="t in proTabs" :key="t.key"
              @click="tab = t.key"
              :class="[
                'px-3 py-1 rounded-md text-xs font-medium border-none cursor-pointer transition-all font-mono',
                tab === t.key
                  ? 'bg-[#0F172A] text-white'
                  : 'text-[#64748B] hover:bg-[#F1F5F9] hover:text-[#0F172A]'
              ]"
            >{{ t.label }} <span class="text-[#94A3B8]">{{ t.count }}</span></button>
          </div>
        </div>

        <!-- Project Rows -->
        <div v-if="proDisplayList.length" class="border border-[#E2E8F0] rounded-md overflow-hidden divide-y divide-[#F1F5F9]">
          <div v-for="item in proDisplayList" :key="item.id">
            <!-- Main Row -->
            <div
              class="flex items-center gap-4 px-4 py-3 hover:bg-[#F8FAFC] cursor-pointer transition-colors"
              @click="toggleExpand(item.id)"
            >
              <!-- Status indicator -->
              <div class="flex items-center gap-1.5 flex-shrink-0">
                <div class="w-2 h-2 rounded-full"
                     :class="item.progress >= 100 ? 'bg-emerald-500' : item.progress > 0 ? 'bg-[#0284C7]' : 'bg-[#CBD5E1]'"></div>
              </div>
              <!-- Project Name -->
              <div class="flex-1 min-w-0 flex items-center gap-2">
                <span class="text-sm text-[#0F172A] font-medium truncate">{{ item.courseName }}</span>
                <code class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-[#F1F5F9] text-[#64748B] hidden sm:inline">
                  {{ item.enrollmentStatus === 'ENROLLED' ? 'enrolled' : item.enrollmentStatus === 'COMPLETED' ? 'completed' : 'pending' }}
                </code>
              </div>
              <!-- Progress (like deployment status) -->
              <div class="flex items-center gap-1 flex-shrink-0">
                <template v-if="item.progress >= 100">
                  <CheckCircle class="w-3.5 h-3.5 text-emerald-500" :stroke-width="2" />
                  <span class="text-xs text-emerald-600 font-mono">Ready</span>
                </template>
                <template v-else-if="item.progress > 0">
                  <Clock class="w-3.5 h-3.5 text-[#0284C7]" :stroke-width="2" />
                  <span class="text-xs text-[#0284C7] font-mono">{{ item.progress }}%</span>
                </template>
                <template v-else>
                  <Circle class="w-3.5 h-3.5 text-[#CBD5E1]" :stroke-width="2" />
                  <span class="text-xs text-[#94A3B8] font-mono">Queued</span>
                </template>
              </div>
              <!-- Timestamp -->
              <span class="text-[10px] font-mono text-[#94A3B8] flex-shrink-0 hidden sm:block w-20 text-right">
                {{ item.startedAt ? formatRelative(item.startedAt) : '—' }}
              </span>
              <!-- Expand arrow -->
              <ChevronDown class="w-3.5 h-3.5 text-[#94A3B8] flex-shrink-0 transition-transform"
                           :class="expandedId === item.id ? 'rotate-180' : ''" :stroke-width="1.75" />
            </div>

            <!-- Expanded Detail -->
            <div v-if="expandedId === item.id" class="px-4 py-3 bg-[#F8FAFC] border-t border-[#E2E8F0]">
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-xs">
                <div>
                  <span class="text-[#94A3B8] font-mono uppercase text-[10px]">Progress</span>
                  <div class="flex items-center gap-2 mt-1">
                    <div class="flex-1 h-1.5 bg-[#E2E8F0] rounded-full overflow-hidden">
                      <div class="h-full bg-[#0284C7] rounded-full" :style="{ width: (item.progress || 0) + '%' }"></div>
                    </div>
                    <span class="font-mono text-[#0F172A] font-semibold">{{ item.progress || 0 }}%</span>
                  </div>
                </div>
                <div>
                  <span class="text-[#94A3B8] font-mono uppercase text-[10px]">Status</span>
                  <div class="mt-1 text-[#0F172A] font-medium">
                    {{ item.progress >= 100 ? '已完成' : item.progress > 0 ? '学习中' : '待开始' }}
                  </div>
                </div>
                <div>
                  <span class="text-[#94A3B8] font-mono uppercase text-[10px]">Started</span>
                  <div class="mt-1 text-[#0F172A] font-mono">{{ item.startedAt ? formatDate(item.startedAt) : '—' }}</div>
                </div>
                <div class="flex items-end">
                  <button
                    v-if="item.enrollmentId && item.enrollmentStatus === 'ENROLLED' && item.progress === 0"
                    class="px-3 py-1 rounded-md text-xs font-medium text-red-600 border border-red-200 hover:bg-red-50 transition-colors cursor-pointer"
                    @click.stop="cancelEnroll(item)"
                  >Cancel</button>
                  <button v-else
                    class="px-3 py-1 rounded-md text-xs font-medium text-white bg-[#0F172A] hover:bg-[#1E293B] transition-colors cursor-pointer"
                    @click.stop="goStudy(item)"
                  >Open →</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-16 text-[#94A3B8] text-xs font-mono">
          No projects found.
          <div class="mt-3">
            <button class="text-xs text-[#0284C7] font-medium hover:underline cursor-pointer" @click="$router.push('/student/courses')">Browse courses →</button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { learningApi } from '@/api/learning'
import { enrollmentApi } from '@/api/enrollment'
import { toast } from '@/composables/useToast'
import {
  BookOpen, CheckCircle, ChevronRight, ChevronDown,
  Columns3, List, Clock, Circle,
} from 'lucide-vue-next'

const router = useRouter()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

const tab = ref('ip')
const loading = ref(false)
const progressList = ref<any[]>([])
const enrollments = ref<any[]>([])

/* ── Dark: View Toggle ── */
const darkView = ref<'kanban' | 'list'>('kanban')

/* ── Warm: View Toggle + Filter ── */
const warmView = ref<'table' | 'gallery' | 'list'>('table')
const warmFilter = ref('all')
const warmFilters = [
  { key: 'all', label: '全部' },
  { key: 'ip', label: '进行中' },
  { key: 'done', label: '已完成' },
]

/* ── Pro: Expanded row ── */
const expandedId = ref<number | null>(null)
function toggleExpand(id: number) {
  expandedId.value = expandedId.value === id ? null : id
}

/* ── Data Types ── */
interface MergedItem {
  id: number
  courseId: number
  courseName: string
  progress: number
  status: string
  startedAt?: string
  completedAt?: string
  enrollmentId?: number
  enrollmentStatus?: string
}

const mergedList = computed<MergedItem[]>(() => {
  const enrollMap = new Map<number, any>()
  enrollments.value.forEach(e => {
    if (e.courseId) enrollMap.set(e.courseId, e)
  })

  const items: MergedItem[] = progressList.value.map(p => ({
    id: p.id || p.courseId,
    courseId: p.courseId,
    courseName: p.courseName,
    progress: p.progress || 0,
    status: p.status,
    startedAt: p.startedAt,
    completedAt: p.completedAt,
    enrollmentId: enrollMap.get(p.courseId)?.id,
    enrollmentStatus: enrollMap.get(p.courseId)?.status,
  }))

  enrollments.value.forEach(e => {
    if (!items.find(i => i.courseId === e.courseId)) {
      items.push({
        id: e.id,
        courseId: e.courseId,
        courseName: e.courseName,
        progress: 0,
        status: 'NOT_STARTED',
        enrollmentId: e.id,
        enrollmentStatus: e.status,
      })
    }
  })

  return items
})

/* ── Filtered Lists ── */
const inProgressList = computed(() => mergedList.value.filter(i => i.progress > 0 && i.progress < 100))
const completedList = computed(() => mergedList.value.filter(i => i.progress >= 100 || i.status === 'COMPLETED'))
const notStartedList = computed(() => mergedList.value.filter(i => i.progress === 0 && i.status !== 'COMPLETED'))
const enrolledList = computed(() => enrollments.value)

/* ── Kanban columns (Dark) ── */
const kanbanInProgress = computed(() => mergedList.value.filter(i => i.progress > 0 && i.progress < 100))
const kanbanCompleted = computed(() => mergedList.value.filter(i => i.progress >= 100 || i.status === 'COMPLETED'))
const kanbanNotStarted = computed(() => mergedList.value.filter(i => i.progress === 0 && i.status !== 'COMPLETED'))

/* ── Warm filtered list ── */
const warmDisplayList = computed(() => {
  if (warmFilter.value === 'ip') return mergedList.value.filter(i => i.progress > 0 && i.progress < 100)
  if (warmFilter.value === 'done') return mergedList.value.filter(i => i.progress >= 100 || i.status === 'COMPLETED')
  return mergedList.value
})

/* ── Light & Pro Tab system ── */
const tabs = computed(() => [
  { key: 'ip', label: '进行中', icon: BookOpen, count: inProgressList.value.length },
  { key: 'done', label: '已完成', icon: CheckCircle, count: completedList.value.length },
  { key: 'enrolled', label: '报名记录', icon: BookOpen, count: enrolledList.value.length },
])

const proTabs = computed(() => [
  { key: 'all', label: 'All', count: mergedList.value.length },
  { key: 'ip', label: 'Active', count: inProgressList.value.length },
  { key: 'done', label: 'Completed', count: completedList.value.length },
])

const displayList = computed(() => {
  if (tab.value === 'done') return completedList.value
  if (tab.value === 'enrolled') return enrolledList.value.map(e => ({
    id: e.id,
    courseId: e.courseId,
    courseName: e.courseName,
    progress: 0,
    status: e.status,
    startedAt: e.enrolledAt,
    enrollmentId: e.id,
    enrollmentStatus: e.status,
  }))
  return inProgressList.value
})

const proDisplayList = computed(() => {
  if (tab.value === 'ip') return mergedList.value.filter(i => i.progress > 0 && i.progress < 100)
  if (tab.value === 'done') return mergedList.value.filter(i => i.progress >= 100 || i.status === 'COMPLETED')
  return mergedList.value
})

const emptyText = computed(() => {
  return tab.value === 'done' ? '暂无已完成课程' : tab.value === 'enrolled' ? '暂无报名记录' : '暂无进行中的课程'
})

function goStudy(item: any) {
  if (item.courseId) router.push(`/student/courses/${item.courseId}/study`)
}

async function cancelEnroll(item: any) {
  if (!item.enrollmentId) return
  try {
    await enrollmentApi.cancel(item.enrollmentId)
    toast.success('已取消报名')
    enrollments.value = enrollments.value.filter(e => e.id !== item.enrollmentId)
  } catch (e: any) {
    toast.error(e.message || '取消失败')
  }
}

function enrollLabel(status: string) {
  return { ENROLLED: '已报名', CANCELLED: '已取消', COMPLETED: '已完成' }[status] || status
}

function enrollBadgeStripe(status: string) {
  return {
    ENROLLED: 'bg-[#635BFF]/10 text-[#635BFF]',
    CANCELLED: 'bg-red-50 text-red-600',
    COMPLETED: 'bg-emerald-50 text-emerald-600',
    NOT_STARTED: 'bg-[#F1F5F9] text-[#64748B]',
  }[status] || 'bg-[#E3E8EE] text-[#8898AA]'
}

function notionStatusColor(item: MergedItem) {
  if (item.progress >= 100) return 'bg-[#DBEDDB] text-[#37662A]'
  if (item.progress > 0) return 'bg-[#D3E5EF] text-[#2E5A8E]'
  return 'bg-[#E3E2E0] text-[#5A5A5A]'
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

function formatRelative(d: string) {
  const diff = Date.now() - new Date(d).getTime()
  const days = Math.floor(diff / 86400000)
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return formatDate(d)
}

onMounted(async () => {
  loading.value = true
  try {
    const [prog, enroll] = await Promise.all([
      learningApi.getProgress().catch(() => []),
      enrollmentApi.getMy().catch(() => []),
    ])
    progressList.value = (prog as any)?.records || (prog as any) || []
    enrollments.value = (enroll as any)?.records || (enroll as any) || []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* ======== STRIPE BILLING (Light) ======== */
.stripe-billing-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow:
    0 15px 35px rgba(60,66,87,0.08),
    0 5px 15px rgba(0,0,0,0.04);
}

/* ======== LINEAR KANBAN (Dark) ======== */
.kanban-column {
  min-height: 200px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.04);
}
.kanban-card {
  padding: 14px 16px;
  border-radius: 10px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.kanban-card:hover {
  border-color: rgba(129,140,248,0.2);
  box-shadow: 0 0 20px rgba(129,140,248,0.06);
}

/* ======== NOTION DATABASE (Warm) ======== */
.notion-table-wrapper {
  border: 1px solid #E7E5E4;
  border-radius: 12px;
  overflow: hidden;
}
.notion-table-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  background: #F5F5F4;
  font-size: 11px;
  font-weight: 700;
  color: #78716C;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #E7E5E4;
}
.notion-table-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  transition: background 0.15s;
  border-bottom: 1px solid #F5F5F4;
}
.notion-table-row:last-child { border-bottom: none; }
.notion-table-row:hover { background: #FEFCE8; }

.notion-select {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.notion-gallery-card {
  border: 1px solid #E7E5E4;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.15s, box-shadow 0.15s;
}
.notion-gallery-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}
</style>
