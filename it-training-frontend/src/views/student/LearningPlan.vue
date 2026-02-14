<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Asana：列表视图 + 勾选框 + 进度条 + 时间线切换
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">学习计划</h1>
            <p class="text-sm text-[#425466] mt-0.5">规划你的学习旅程</p>
          </div>
          <div class="flex items-center gap-3">
            <!-- View Toggle -->
            <div class="flex border border-[#E3E8EE] rounded-md overflow-hidden">
              <button @click="asanaView = 'list'" :class="['px-3 py-1.5 text-xs font-medium transition', asanaView === 'list' ? 'bg-[#635BFF]/10 text-[#635BFF]' : 'text-[#8898AA] hover:text-[#0A2540]']">
                <ListIcon class="w-3.5 h-3.5" :stroke-width="1.75" />
              </button>
              <button @click="asanaView = 'timeline'" :class="['px-3 py-1.5 text-xs font-medium transition', asanaView === 'timeline' ? 'bg-[#635BFF]/10 text-[#635BFF]' : 'text-[#8898AA] hover:text-[#0A2540]']">
                <GanttChartSquare class="w-3.5 h-3.5" :stroke-width="1.75" />
              </button>
            </div>
            <button @click="showCreate = !showCreate" class="px-4 py-2 bg-[#635BFF] text-white rounded-md text-sm font-semibold hover:brightness-110 transition-all cursor-pointer stripe-btn-shadow inline-flex items-center gap-1.5">
              <Plus class="w-4 h-4" :stroke-width="2" /> 创建计划
            </button>
          </div>
        </div>

        <!-- Create Form -->
        <div v-if="showCreate" class="asana-card">
          <div class="flex items-center gap-3 mb-3">
            <input v-model="newTitle" type="text" placeholder="计划标题" class="flex-1 px-3 py-2 rounded-md border border-[#E3E8EE] text-sm text-[#0A2540] outline-none focus:border-[#635BFF]" />
            <button @click="createPlan" class="px-4 py-2 bg-[#635BFF] text-white rounded-md text-sm font-medium cursor-pointer hover:brightness-110">创建</button>
          </div>
          <input v-model="newDesc" type="text" placeholder="描述（可选）" class="w-full px-3 py-2 rounded-md border border-[#E3E8EE] text-sm text-[#425466] outline-none focus:border-[#635BFF]" />
        </div>

        <!-- List View -->
        <template v-if="asanaView === 'list'">
          <div v-if="plans.length" class="asana-card overflow-hidden">
            <!-- Header Row -->
            <div class="flex items-center gap-4 px-5 py-2.5 bg-[#F6F9FC] text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider border-b border-[#E3E8EE]">
              <span class="w-6"></span>
              <span class="flex-1">计划名称</span>
              <span class="w-32 text-center">进度</span>
              <span class="w-20 text-center">状态</span>
              <span class="w-24 text-right">截止日期</span>
              <span class="w-24 text-right">操作</span>
            </div>
            <!-- Rows -->
            <div class="divide-y divide-[#F0F3F7]">
              <div v-for="p in plans" :key="p.id" class="flex items-center gap-4 px-5 py-3.5 hover:bg-[#F6F9FC]/50 transition-colors">
                <!-- Checkbox -->
                <div class="w-6 flex justify-center">
                  <div :class="['w-5 h-5 rounded-full border-2 flex items-center justify-center cursor-pointer transition-all',
                    p.status === 'COMPLETED' ? 'bg-emerald-500 border-emerald-500' : 'border-[#CBD5E1] hover:border-[#635BFF]']"
                    @click="p.status !== 'COMPLETED' && act('complete', p.id)">
                    <Check v-if="p.status === 'COMPLETED'" class="w-3 h-3 text-white" :stroke-width="3" />
                  </div>
                </div>
                <!-- Title -->
                <div class="flex-1 min-w-0">
                  <span :class="['text-sm font-medium', p.status === 'COMPLETED' ? 'text-[#8898AA] line-through' : 'text-[#0A2540]']">{{ p.title }}</span>
                  <p v-if="p.description" class="text-xs text-[#8898AA] truncate mt-0.5">{{ p.description }}</p>
                </div>
                <!-- Progress Bar -->
                <div class="w-32 flex items-center gap-2">
                  <div class="flex-1 h-1.5 bg-[#E3E8EE] rounded-full overflow-hidden">
                    <div class="h-full rounded-full transition-all" :class="planProgress(p) >= 100 ? 'bg-emerald-500' : 'bg-[#635BFF]'" :style="{ width: planProgress(p) + '%' }"></div>
                  </div>
                  <span class="text-[10px] text-[#8898AA] font-mono w-7 text-right">{{ planProgress(p) }}%</span>
                </div>
                <!-- Status Badge -->
                <div class="w-20 text-center">
                  <span class="text-[11px] font-medium px-2 py-px rounded-full" :class="scStripe(p.status)">{{ sm[p.status] || p.status }}</span>
                </div>
                <!-- Due Date -->
                <span class="w-24 text-right text-xs text-[#8898AA]">{{ p.endDate ? formatDate(p.endDate) : '—' }}</span>
                <!-- Actions -->
                <div class="w-24 text-right flex items-center justify-end gap-2">
                  <button v-if="p.status === 'ACTIVE'" @click="act('pause', p.id)" class="text-xs text-amber-600 hover:underline cursor-pointer">暂停</button>
                  <button v-if="p.status === 'PAUSED'" @click="act('resume', p.id)" class="text-xs text-[#635BFF] hover:underline cursor-pointer">恢复</button>
                  <button v-if="p.status === 'ACTIVE'" @click="act('complete', p.id)" class="text-xs text-emerald-600 hover:underline cursor-pointer">完成</button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-16">
            <ClipboardList class="w-12 h-12 text-[#8898AA]/40 mx-auto mb-3" :stroke-width="1" />
            <p class="text-sm text-[#8898AA]">暂无学习计划</p>
          </div>
        </template>

        <!-- Timeline (Gantt) View -->
        <template v-else>
          <div v-if="plans.length" class="asana-card overflow-x-auto">
            <div class="min-w-[600px]">
              <div class="flex items-center gap-3 px-4 py-2 border-b border-[#E3E8EE] text-[10px] text-[#8898AA] font-semibold uppercase">
                <span class="w-40">计划</span>
                <div class="flex-1 flex">
                  <span v-for="w in 8" :key="w" class="flex-1 text-center">W{{ w }}</span>
                </div>
              </div>
              <div v-for="p in plans" :key="p.id" class="flex items-center gap-3 px-4 py-3 border-b border-[#F0F3F7] last:border-0">
                <span class="w-40 text-sm font-medium text-[#0A2540] truncate">{{ p.title }}</span>
                <div class="flex-1 relative h-6">
                  <div class="absolute inset-y-0 rounded-full"
                    :class="p.status === 'COMPLETED' ? 'bg-emerald-100' : p.status === 'PAUSED' ? 'bg-amber-100' : 'bg-[#635BFF]/15'"
                    :style="{ left: ganttLeft(p) + '%', width: ganttWidth(p) + '%' }">
                    <div class="h-full rounded-full flex items-center px-2"
                      :class="p.status === 'COMPLETED' ? 'bg-emerald-500' : p.status === 'PAUSED' ? 'bg-amber-400' : 'bg-[#635BFF]'"
                      :style="{ width: planProgress(p) + '%' }">
                      <span class="text-[9px] font-semibold text-white whitespace-nowrap">{{ planProgress(p) }}%</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-16 text-sm text-[#8898AA]">暂无学习计划</div>
        </template>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Linear Issues：3 列看板 + 优先级标签 + 搜索过滤
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">学习计划</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ plans.length }} plans</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="relative">
              <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#6B6B6E]" :stroke-width="1.75" />
              <input v-model="searchQuery" type="text" placeholder="Filter..."
                class="pl-8 pr-3 py-1.5 rounded-lg bg-white/[0.04] border border-white/[0.06] text-xs text-[#EDEDED] outline-none focus:border-[#818CF8]/50 w-36 placeholder-[#6B6B6E]" />
            </div>
            <button @click="showCreate = !showCreate" class="px-3 py-1.5 bg-[#818CF8] text-white rounded-lg text-xs font-medium hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] transition-all cursor-pointer inline-flex items-center gap-1.5">
              <Plus class="w-3.5 h-3.5" :stroke-width="2" /> New Plan
            </button>
          </div>
        </div>

        <!-- Create Form -->
        <div v-if="showCreate" class="linear-card">
          <div class="flex items-center gap-3 mb-2">
            <input v-model="newTitle" type="text" placeholder="Plan title" class="flex-1 px-3 py-2 rounded-lg bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none focus:border-[#818CF8]/50 placeholder-[#6B6B6E]" />
            <button @click="createPlan" class="px-3 py-2 bg-[#818CF8] text-white rounded-lg text-xs font-medium cursor-pointer">Create</button>
          </div>
          <input v-model="newDesc" type="text" placeholder="Description (optional)" class="w-full px-3 py-2 rounded-lg bg-white/[0.04] border border-white/[0.06] text-xs text-[#6B6B6E] outline-none focus:border-[#818CF8]/50 placeholder-[#6B6B6E]" />
        </div>

        <!-- 3-Column Kanban -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- Active -->
          <div class="linear-column">
            <div class="flex items-center justify-between mb-3 px-1">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full bg-[#818CF8]"></div>
                <span class="text-xs font-semibold text-[#EDEDED]">Active</span>
              </div>
              <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanActive.length }}</span>
            </div>
            <div class="space-y-2">
              <div v-for="p in kanbanActive" :key="p.id" class="linear-issue-card">
                <div class="flex items-center gap-2 mb-2">
                  <span class="text-[10px] font-mono text-[#6B6B6E]">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                  <span class="ml-auto px-1.5 py-px rounded text-[9px] font-semibold" :class="priorityClass(p)">{{ priorityLabel(p) }}</span>
                </div>
                <h4 class="text-sm font-medium text-[#EDEDED] mb-1.5">{{ p.title }}</h4>
                <p v-if="p.description" class="text-[11px] text-[#6B6B6E] line-clamp-2 mb-2">{{ p.description }}</p>
                <div class="flex items-center gap-2">
                  <button @click="act('pause', p.id)" class="text-[10px] text-amber-400 hover:underline cursor-pointer">暂停</button>
                  <button @click="act('complete', p.id)" class="text-[10px] text-emerald-400 hover:underline cursor-pointer">完成</button>
                  <span v-if="p.endDate" class="text-[10px] text-[#6B6B6E] font-mono ml-auto">{{ formatDate(p.endDate) }}</span>
                </div>
              </div>
              <div v-if="!kanbanActive.length" class="text-center py-6 text-[#6B6B6E] text-xs">无活跃计划</div>
            </div>
          </div>

          <!-- Paused -->
          <div class="linear-column">
            <div class="flex items-center justify-between mb-3 px-1">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full bg-amber-400"></div>
                <span class="text-xs font-semibold text-[#EDEDED]">Paused</span>
              </div>
              <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanPaused.length }}</span>
            </div>
            <div class="space-y-2">
              <div v-for="p in kanbanPaused" :key="p.id" class="linear-issue-card">
                <div class="flex items-center gap-2 mb-2">
                  <span class="text-[10px] font-mono text-[#6B6B6E]">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                </div>
                <h4 class="text-sm font-medium text-[#EDEDED] mb-1.5">{{ p.title }}</h4>
                <button @click="act('resume', p.id)" class="text-[10px] text-[#818CF8] hover:underline cursor-pointer">恢复</button>
              </div>
              <div v-if="!kanbanPaused.length" class="text-center py-6 text-[#6B6B6E] text-xs">无暂停计划</div>
            </div>
          </div>

          <!-- Completed -->
          <div class="linear-column">
            <div class="flex items-center justify-between mb-3 px-1">
              <div class="flex items-center gap-2">
                <div class="w-2 h-2 rounded-full bg-emerald-400"></div>
                <span class="text-xs font-semibold text-[#EDEDED]">Completed</span>
              </div>
              <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-1.5 py-0.5 rounded">{{ kanbanCompleted.length }}</span>
            </div>
            <div class="space-y-2">
              <div v-for="p in kanbanCompleted" :key="p.id" class="linear-issue-card opacity-60">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-[10px] font-mono text-[#6B6B6E]">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                  <CheckCircle class="w-3 h-3 text-emerald-400 ml-auto" :stroke-width="2" />
                </div>
                <h4 class="text-sm font-medium text-[#EDEDED] line-through">{{ p.title }}</h4>
              </div>
              <div v-if="!kanbanCompleted.length" class="text-center py-6 text-[#6B6B6E] text-xs">无已完成计划</div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Todoist：按日期分组 + 彩色优先级圆点 + 行内创建
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <h1 class="text-xl font-extrabold text-[#292524]">📋 学习计划</h1>
          <span class="text-xs font-bold text-[#78716C]">{{ plans.length }} 个计划</span>
        </div>

        <!-- Inline Create (Todoist-style) -->
        <div class="flex items-center gap-2 px-4 py-3 rounded-2xl border-2 border-dashed border-[#E7E5E4] hover:border-[#D97706] transition-colors">
          <Plus class="w-5 h-5 text-[#D97706]" :stroke-width="2" />
          <input v-model="newTitle" type="text" placeholder="添加计划，按回车创建..."
            class="flex-1 bg-transparent text-sm text-[#292524] outline-none placeholder-[#A8A29E]"
            @keyup.enter="createPlan" />
          <button v-if="newTitle.trim()" @click="createPlan" class="px-3 py-1 rounded-full bg-[#D97706] text-white text-xs font-bold cursor-pointer">添加</button>
        </div>

        <!-- Section: Active Plans (Today-style) -->
        <div v-if="activePlans.length">
          <div class="flex items-center gap-2 mb-3">
            <span class="text-sm font-extrabold text-[#D97706]">🔥 进行中</span>
            <span class="text-xs text-[#A8A29E]">{{ activePlans.length }}</span>
          </div>
          <div class="space-y-1.5">
            <div v-for="p in activePlans" :key="p.id" class="todoist-item group">
              <div class="flex items-center gap-3">
                <!-- Priority Dot -->
                <div :class="['w-5 h-5 rounded-full border-2 flex items-center justify-center cursor-pointer transition-all flex-shrink-0',
                  'border-[#D97706] hover:bg-[#D97706]']"
                  @click="act('complete', p.id)">
                </div>
                <div class="flex-1 min-w-0">
                  <span class="text-sm font-medium text-[#292524]">{{ p.title }}</span>
                  <p v-if="p.description" class="text-xs text-[#A8A29E] truncate">{{ p.description }}</p>
                </div>
                <div class="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button @click="act('pause', p.id)" class="text-xs text-amber-600 hover:underline cursor-pointer">暂停</button>
                </div>
                <span v-if="p.endDate" class="text-[10px] text-[#A8A29E]">{{ formatDate(p.endDate) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Section: Paused Plans (Upcoming-style) -->
        <div v-if="pausedPlans.length">
          <div class="flex items-center gap-2 mb-3">
            <span class="text-sm font-extrabold text-[#78716C]">⏸ 已暂停</span>
            <span class="text-xs text-[#A8A29E]">{{ pausedPlans.length }}</span>
          </div>
          <div class="space-y-1.5">
            <div v-for="p in pausedPlans" :key="p.id" class="todoist-item group">
              <div class="flex items-center gap-3">
                <div class="w-5 h-5 rounded-full border-2 border-[#CBD5E1] flex-shrink-0"></div>
                <div class="flex-1 min-w-0">
                  <span class="text-sm font-medium text-[#78716C]">{{ p.title }}</span>
                </div>
                <button @click="act('resume', p.id)" class="text-xs text-[#D97706] font-bold hover:underline cursor-pointer">恢复</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Section: Completed -->
        <div v-if="completedPlans.length">
          <div class="flex items-center gap-2 mb-3">
            <span class="text-sm font-extrabold text-[#78716C]">✅ 已完成</span>
            <span class="text-xs text-[#A8A29E]">{{ completedPlans.length }}</span>
          </div>
          <div class="space-y-1.5">
            <div v-for="p in completedPlans" :key="p.id" class="todoist-item opacity-60">
              <div class="flex items-center gap-3">
                <div class="w-5 h-5 rounded-full bg-[#58CC02] flex items-center justify-center flex-shrink-0">
                  <Check class="w-3 h-3 text-white" :stroke-width="3" />
                </div>
                <span class="text-sm text-[#A8A29E] line-through">{{ p.title }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!plans.length" class="text-center py-16">
          <p class="text-3xl mb-2">📝</p>
          <p class="text-sm text-[#78716C]">还没有学习计划，快来创建一个吧！</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Jira Sprint Board：To Do / In Progress / Done + 编号
         ================================================================ -->
    <template v-else>
      <div class="space-y-4">
        <!-- Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Sprint Board</span>
            <code class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ plans.length }} issues</code>
          </div>
          <div class="flex items-center gap-2">
            <button @click="showCreate = !showCreate" class="px-3 py-1.5 bg-[#0F172A] text-white rounded-md text-xs font-medium hover:bg-[#1E293B] transition-colors cursor-pointer inline-flex items-center gap-1.5">
              <Plus class="w-3 h-3" :stroke-width="2" /> Create
            </button>
          </div>
        </div>

        <!-- Create -->
        <div v-if="showCreate" class="jira-card">
          <div class="flex items-center gap-3">
            <input v-model="newTitle" type="text" placeholder="Issue summary" class="flex-1 px-3 py-1.5 rounded border border-[#E2E8F0] text-sm text-[#0F172A] outline-none focus:border-[#0284C7] font-mono" />
            <button @click="createPlan" class="px-3 py-1.5 bg-[#0F172A] text-white rounded text-xs font-medium cursor-pointer">Create</button>
          </div>
        </div>

        <!-- Sprint Board: 3 columns -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
          <!-- To Do -->
          <div class="jira-column">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-1.5">
                <div class="w-3 h-3 rounded bg-[#CBD5E1] flex items-center justify-center">
                  <Circle class="w-2 h-2 text-[#64748B]" :stroke-width="2.5" />
                </div>
                <span class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider">To Do</span>
              </div>
              <span class="text-[10px] font-mono text-[#94A3B8]">{{ kanbanPaused.length }}</span>
            </div>
            <div class="space-y-1.5">
              <div v-for="p in kanbanPaused" :key="p.id" class="jira-issue-card">
                <div class="flex items-center gap-2">
                  <span class="text-[10px] font-mono text-[#0284C7] font-semibold">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                  <div class="w-1.5 h-1.5 rounded-full bg-[#CBD5E1] ml-auto"></div>
                </div>
                <h4 class="text-xs font-medium text-[#0F172A] mt-1 line-clamp-2">{{ p.title }}</h4>
                <div class="flex items-center justify-between mt-2">
                  <button @click="act('resume', p.id)" class="text-[10px] text-[#0284C7] font-mono hover:underline cursor-pointer">→ start</button>
                  <span v-if="p.endDate" class="text-[9px] font-mono text-[#94A3B8]">{{ formatDate(p.endDate) }}</span>
                </div>
              </div>
              <div v-if="!kanbanPaused.length" class="text-center py-4 text-[10px] text-[#94A3B8] font-mono">empty</div>
            </div>
          </div>

          <!-- In Progress -->
          <div class="jira-column">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-1.5">
                <div class="w-3 h-3 rounded bg-[#0284C7] flex items-center justify-center">
                  <span class="w-1.5 h-1.5 rounded-full bg-white"></span>
                </div>
                <span class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider">In Progress</span>
              </div>
              <span class="text-[10px] font-mono text-[#94A3B8]">{{ kanbanActive.length }}</span>
            </div>
            <div class="space-y-1.5">
              <div v-for="p in kanbanActive" :key="p.id" class="jira-issue-card border-l-2 border-l-[#0284C7]">
                <div class="flex items-center gap-2">
                  <span class="text-[10px] font-mono text-[#0284C7] font-semibold">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                  <div class="w-1.5 h-1.5 rounded-full bg-[#0284C7] ml-auto"></div>
                </div>
                <h4 class="text-xs font-medium text-[#0F172A] mt-1 line-clamp-2">{{ p.title }}</h4>
                <div class="flex items-center gap-2 mt-2">
                  <button @click="act('pause', p.id)" class="text-[10px] text-amber-600 font-mono hover:underline cursor-pointer">pause</button>
                  <button @click="act('complete', p.id)" class="text-[10px] text-emerald-600 font-mono hover:underline cursor-pointer">done</button>
                </div>
              </div>
              <div v-if="!kanbanActive.length" class="text-center py-4 text-[10px] text-[#94A3B8] font-mono">empty</div>
            </div>
          </div>

          <!-- Done -->
          <div class="jira-column">
            <div class="flex items-center justify-between mb-3">
              <div class="flex items-center gap-1.5">
                <div class="w-3 h-3 rounded bg-emerald-500 flex items-center justify-center">
                  <Check class="w-2 h-2 text-white" :stroke-width="3" />
                </div>
                <span class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider">Done</span>
              </div>
              <span class="text-[10px] font-mono text-[#94A3B8]">{{ kanbanCompleted.length }}</span>
            </div>
            <div class="space-y-1.5">
              <div v-for="p in kanbanCompleted" :key="p.id" class="jira-issue-card opacity-50">
                <div class="flex items-center gap-2">
                  <span class="text-[10px] font-mono text-[#94A3B8]">ITS-{{ String(p.id).padStart(3, '0') }}</span>
                  <CheckCircle class="w-3 h-3 text-emerald-500 ml-auto" :stroke-width="2" />
                </div>
                <h4 class="text-xs text-[#64748B] mt-1 line-through">{{ p.title }}</h4>
              </div>
              <div v-if="!kanbanCompleted.length" class="text-center py-4 text-[10px] text-[#94A3B8] font-mono">empty</div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { planApi, type LearningPlan } from '@/api/plan'
import { toast } from '@/composables/useToast'
import {
  Plus, Check, CheckCircle, Search, ClipboardList, Circle,
  List as ListIcon, GanttChartSquare,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

const plans = ref<any[]>([])
const showCreate = ref(false)
const newTitle = ref('')
const newDesc = ref('')
const searchQuery = ref('')

/* ── Asana View ── */
const asanaView = ref<'list' | 'timeline'>('list')

/* ── Status Label ── */
const sm: Record<string, string> = { ACTIVE: '进行中', PAUSED: '已暂停', COMPLETED: '已完成', CANCELLED: '已取消' }

function scStripe(s: string) {
  return { ACTIVE: 'bg-[#635BFF]/10 text-[#635BFF]', PAUSED: 'bg-amber-50 text-amber-600', COMPLETED: 'bg-emerald-50 text-emerald-600', CANCELLED: 'bg-red-50 text-red-600' }[s] || 'bg-[#F6F9FC] text-[#8898AA]'
}

/* ── Kanban columns ── */
const filteredPlans = computed(() => {
  if (!searchQuery.value) return plans.value
  const q = searchQuery.value.toLowerCase()
  return plans.value.filter(p => p.title?.toLowerCase().includes(q))
})
const kanbanActive = computed(() => filteredPlans.value.filter(p => p.status === 'ACTIVE'))
const kanbanPaused = computed(() => filteredPlans.value.filter(p => p.status === 'PAUSED' || p.status === 'CANCELLED'))
const kanbanCompleted = computed(() => filteredPlans.value.filter(p => p.status === 'COMPLETED'))

/* ── Todoist groups ── */
const activePlans = computed(() => plans.value.filter(p => p.status === 'ACTIVE'))
const pausedPlans = computed(() => plans.value.filter(p => p.status === 'PAUSED'))
const completedPlans = computed(() => plans.value.filter(p => p.status === 'COMPLETED'))

/* ── Progress calc ── */
function planProgress(p: any) {
  if (p.status === 'COMPLETED') return 100
  if (!p.startDate || !p.endDate) return p.status === 'ACTIVE' ? 30 : 0
  const start = new Date(p.startDate).getTime()
  const end = new Date(p.endDate).getTime()
  const now = Date.now()
  if (now >= end) return 90
  if (now <= start) return 0
  return Math.round(((now - start) / (end - start)) * 100)
}

/* ── Gantt helpers ── */
function ganttLeft(p: any) {
  if (!p.startDate) return 0
  const start = new Date(p.startDate)
  const weekNum = Math.floor((Date.now() - start.getTime()) / (7 * 86400000))
  return Math.max(0, Math.min(weekNum * 12.5, 87.5))
}
function ganttWidth(p: any) {
  if (!p.startDate || !p.endDate) return 25
  const dur = (new Date(p.endDate).getTime() - new Date(p.startDate).getTime()) / (7 * 86400000)
  return Math.max(12.5, Math.min(dur * 12.5, 100 - ganttLeft(p)))
}

/* ── Linear Priority ── */
function priorityLabel(p: any) {
  const d = p.endDate ? new Date(p.endDate).getTime() - Date.now() : Infinity
  if (d < 3 * 86400000) return 'Urgent'
  if (d < 7 * 86400000) return 'High'
  return 'Normal'
}
function priorityClass(p: any) {
  const label = priorityLabel(p)
  return { 'Urgent': 'bg-red-500/15 text-red-400', 'High': 'bg-orange-500/15 text-orange-400', 'Normal': 'bg-[#818CF8]/15 text-[#818CF8]' }[label]
}

/* ── Actions ── */
async function act(a: string, id: number) {
  try {
    if (a === 'pause') await planApi.pause(id)
    else if (a === 'resume') await planApi.resume(id)
    else await planApi.complete(id)
    toast.success('操作成功')
    load()
  } catch (e: any) {
    toast.error(e.message || '操作失败')
  }
}

async function createPlan() {
  if (!newTitle.value.trim()) return
  try {
    await planApi.create({ title: newTitle.value, description: newDesc.value })
    newTitle.value = ''
    newDesc.value = ''
    showCreate.value = false
    toast.success('创建成功')
    load()
  } catch (e: any) {
    toast.error(e.message || '创建失败')
  }
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

async function load() {
  try { const r: any = await planApi.list(); plans.value = r?.records || r || [] } catch { plans.value = [] }
}
onMounted(load)
</script>

<style scoped>
/* ======== ASANA (Light) ======== */
.asana-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}
.stripe-btn-shadow {
  box-shadow: 0 1px 3px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.1);
}

/* ======== LINEAR (Dark) ======== */
.linear-card {
  padding: 16px;
  border-radius: 12px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
}
.linear-column {
  min-height: 200px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.04);
}
.linear-issue-card {
  padding: 12px;
  border-radius: 8px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.linear-issue-card:hover {
  border-color: rgba(129,140,248,0.2);
  box-shadow: 0 0 20px rgba(129,140,248,0.06);
}

/* ======== TODOIST (Warm) ======== */
.todoist-item {
  padding: 10px 16px;
  border-radius: 12px;
  background: white;
  border: 1px solid #E7E5E4;
  transition: box-shadow 0.15s, transform 0.15s;
}
.todoist-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transform: translateY(-1px);
}

/* ======== JIRA (Pro) ======== */
.jira-card {
  padding: 12px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
}
.jira-column {
  padding: 12px;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
}
.jira-issue-card {
  padding: 10px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 3px;
  transition: box-shadow 0.15s;
}
.jira-issue-card:hover {
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
</style>
