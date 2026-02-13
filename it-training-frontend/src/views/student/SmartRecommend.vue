<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Netflix：大 Banner + 水平轮播行 + Hover 放大
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-8">
        <!-- AI Hero Banner -->
        <div class="relative rounded-2xl overflow-hidden bg-gradient-to-r from-[#0A2540] to-[#635BFF] p-8 lg:p-12">
          <div class="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/2"></div>
          <div class="relative z-10 max-w-2xl">
            <div class="flex items-center gap-2 mb-3">
              <Sparkles class="w-5 h-5 text-[#FFC800]" :stroke-width="2" />
              <span class="text-xs font-semibold text-white/60 uppercase tracking-wider">AI 推荐</span>
            </div>
            <h1 class="text-2xl font-bold text-white mb-2">基于你的学习，为你精选</h1>
            <p class="text-sm text-white/70 mb-5">智能分析你的学习行为和偏好，推荐最适合你的课程</p>
            <div class="flex gap-2 max-w-lg">
              <input v-model="aiQuery" type="text" placeholder="描述你想学什么..."
                class="flex-1 px-4 py-2.5 rounded-lg bg-white/10 backdrop-blur border border-white/20 text-white text-sm placeholder:text-white/40 outline-none focus:border-white/40 transition"
                @keyup.enter="askAi" />
              <button class="px-5 py-2.5 rounded-lg bg-white text-[#0A2540] text-sm font-semibold hover:bg-white/90 transition-all cursor-pointer inline-flex items-center gap-1.5 disabled:opacity-50"
                @click="askAi" :disabled="aiLoading">
                <Sparkles v-if="!aiLoading" class="w-4 h-4" :stroke-width="2" />
                <span v-else class="w-4 h-4 border-2 border-[#0A2540]/20 border-t-[#0A2540] rounded-full animate-spin"></span>
                {{ aiLoading ? '分析中' : '推荐' }}
              </button>
            </div>
            <div v-if="aiResult" class="mt-4 p-3 rounded-lg bg-white/10 backdrop-blur border border-white/10">
              <p class="text-sm text-white/80 whitespace-pre-wrap leading-relaxed">{{ aiResult }}</p>
            </div>
          </div>
        </div>

        <!-- Horizontal Carousel Rows -->
        <section v-for="row in netflixRows" :key="row.key" class="space-y-3">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <component :is="row.icon" class="w-4 h-4 text-[#635BFF]" :stroke-width="1.75" />
              <h3 class="text-base font-semibold text-[#0A2540]">{{ row.label }}</h3>
            </div>
            <button @click="activeTab = row.key" class="text-xs text-[#635BFF] font-medium hover:underline cursor-pointer">查看全部 →</button>
          </div>
          <!-- Horizontal scroll -->
          <div v-if="row.loading" class="flex gap-4">
            <div v-for="i in 4" :key="i" class="flex-shrink-0 w-56 h-48 rounded-xl bg-[#F6F9FC] animate-pulse"></div>
          </div>
          <div v-else-if="row.items.length" class="flex gap-4 overflow-x-auto pb-2 scrollbar-thin">
            <div v-for="course in row.items" :key="course.id || course.courseId"
              class="flex-shrink-0 w-56 rounded-xl overflow-hidden cursor-pointer group netflix-card"
              @click="goDetail(course)">
              <!-- Thumbnail -->
              <div class="h-32 bg-gradient-to-br flex items-center justify-center relative"
                   :class="getCategoryStyle(course.category).gradient">
                <component :is="getCategoryStyle(course.category).icon" class="w-10 h-10 text-white/60 group-hover:scale-110 transition-transform" :stroke-width="1.25" />
                <!-- Match Badge -->
                <div v-if="course.score || course.similarity" class="absolute top-2 right-2 px-2 py-0.5 rounded-full bg-black/40 backdrop-blur text-white text-[10px] font-semibold">
                  {{ Math.round((course.score || course.similarity || 0) * 100) }}% 匹配
                </div>
              </div>
              <!-- Info -->
              <div class="p-3 bg-white">
                <h4 class="text-sm font-semibold text-[#0A2540] truncate group-hover:text-[#635BFF] transition-colors">{{ course.title || course.courseName }}</h4>
                <p class="text-xs text-[#8898AA] mt-1 line-clamp-1">{{ course.description || '暂无描述' }}</p>
                <div class="flex items-center gap-2 mt-2">
                  <span v-if="course.category" class="text-[10px] font-medium px-1.5 py-0.5 rounded-full"
                    :class="[getCategoryStyle(course.category).bgClass, getCategoryStyle(course.category).textClass]">
                    {{ course.category }}
                  </span>
                  <span class="text-[10px] text-[#8898AA] ml-auto">{{ course.enrollCount || 0 }} 人</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-8 text-sm text-[#8898AA]">暂无推荐</div>
        </section>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Spotify Discover：Hero 封面 + 分类色块 + 播放列表
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-6">
        <!-- AI + Hero Featured Card -->
        <div class="spotify-hero rounded-xl overflow-hidden p-8 relative">
          <div class="absolute inset-0 bg-gradient-to-br from-[#1DB954]/30 via-[#111113] to-[#111113]"></div>
          <div class="relative z-10">
            <div class="flex items-center gap-2 mb-2">
              <Sparkles class="w-4 h-4 text-[#1DB954]" :stroke-width="2" />
              <span class="text-xs font-semibold text-[#1DB954] uppercase tracking-wider">为你精选</span>
            </div>
            <h1 class="text-2xl font-bold text-[#EDEDED] mb-1">发现新课程</h1>
            <p class="text-sm text-[#6B6B6E] mb-5">基于你的学习偏好，智能推荐最适合的内容</p>
            <div class="flex gap-2 max-w-md">
              <input v-model="aiQuery" type="text" placeholder="想学点什么？"
                class="flex-1 px-4 py-2.5 rounded-full bg-white/[0.06] border border-white/[0.08] text-[#EDEDED] text-sm placeholder:text-[#6B6B6E] outline-none focus:border-[#1DB954]/50 transition"
                @keyup.enter="askAi" />
              <button class="px-5 py-2.5 rounded-full bg-[#1DB954] text-white text-sm font-semibold hover:bg-[#1ed760] transition-all cursor-pointer inline-flex items-center gap-1.5 disabled:opacity-50"
                @click="askAi" :disabled="aiLoading">
                {{ aiLoading ? '...' : 'AI 推荐' }}
              </button>
            </div>
            <div v-if="aiResult" class="mt-4 p-3 rounded-lg bg-white/[0.04] border border-white/[0.06]">
              <p class="text-sm text-[#CCCCCC] whitespace-pre-wrap leading-relaxed">{{ aiResult }}</p>
            </div>
          </div>
        </div>

        <!-- Category Color Blocks Grid (4×2) -->
        <div>
          <h3 class="text-sm font-semibold text-[#EDEDED] mb-3">课程播放列表</h3>
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <button v-for="cat in spotifyCategories" :key="cat.key"
              @click="filterCategory = filterCategory === cat.key ? '' : cat.key"
              :class="['spotify-category-card h-20 rounded-lg flex items-end p-3 relative overflow-hidden cursor-pointer transition-all',
                filterCategory === cat.key ? 'ring-2 ring-[#1DB954] scale-[0.97]' : 'hover:scale-[1.03]']"
              :style="{ background: `linear-gradient(135deg, ${cat.color1}, ${cat.color2})` }">
              <span class="text-sm font-bold text-white drop-shadow-sm relative z-10">{{ cat.label }}</span>
              <component :is="cat.icon" class="absolute top-2 right-2 w-10 h-10 text-white/20 rotate-12" :stroke-width="1" />
            </button>
          </div>
        </div>

        <!-- Playlist Detail (like Spotify track list) -->
        <div>
          <div class="flex items-center justify-between mb-3">
            <h3 class="text-sm font-semibold text-[#EDEDED]">{{ filterCategory ? `${filterCategory} 推荐` : '全部推荐' }}</h3>
            <span class="text-[10px] text-[#6B6B6E] font-mono">{{ spotifyFilteredList.length }} courses</span>
          </div>

          <div v-if="tabLoading" class="text-center py-12">
            <div class="inline-block w-6 h-6 border-2 border-[#1DB954]/20 border-t-[#1DB954] rounded-full animate-spin"></div>
          </div>

          <div v-else-if="spotifyFilteredList.length" class="spotify-playlist-card">
            <!-- Header Row -->
            <div class="flex items-center gap-4 px-4 py-2 text-[10px] font-semibold text-[#6B6B6E] uppercase tracking-wider border-b border-white/[0.04]">
              <span class="w-8 text-center">#</span>
              <span class="flex-1">标题</span>
              <span class="w-20 text-center hidden sm:block">分类</span>
              <span class="w-16 text-center hidden sm:block">等级</span>
              <span class="w-16 text-right">匹配</span>
            </div>
            <!-- Track Rows -->
            <div v-for="(course, i) in spotifyFilteredList" :key="course.id || course.courseId"
              class="flex items-center gap-4 px-4 py-2.5 hover:bg-white/[0.04] cursor-pointer transition-colors group"
              @click="goDetail(course)">
              <span class="w-8 text-center text-sm text-[#6B6B6E] font-mono group-hover:hidden">{{ i + 1 }}</span>
              <Play class="w-4 h-4 text-white hidden group-hover:block w-8 text-center" :stroke-width="2" />
              <!-- Cover + Title -->
              <div class="flex items-center gap-3 flex-1 min-w-0">
                <div class="w-10 h-10 rounded flex items-center justify-center flex-shrink-0 bg-gradient-to-br"
                     :class="getCategoryStyle(course.category).gradient">
                  <component :is="getCategoryStyle(course.category).icon" class="w-5 h-5 text-white/80" :stroke-width="1.25" />
                </div>
                <div class="min-w-0">
                  <h4 class="text-sm font-medium text-[#EDEDED] truncate group-hover:text-[#1DB954] transition-colors">{{ course.title || course.courseName }}</h4>
                  <p class="text-[11px] text-[#6B6B6E] truncate">{{ course.description || '暂无描述' }}</p>
                </div>
              </div>
              <span class="w-20 text-center text-xs text-[#6B6B6E] hidden sm:block truncate">{{ course.category || '—' }}</span>
              <span class="w-16 text-center text-xs text-[#6B6B6E] hidden sm:block">{{ course.level || '—' }}</span>
              <span class="w-16 text-right text-xs font-mono" :class="(course.score || course.similarity) ? 'text-[#1DB954]' : 'text-[#6B6B6E]'">
                {{ (course.score || course.similarity) ? Math.round((course.score || course.similarity) * 100) + '%' : '—' }}
              </span>
            </div>
          </div>
          <div v-else class="text-center py-12 text-sm text-[#6B6B6E]">暂无推荐课程</div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — YouTube：分类 Chips + 缩略图卡片网格 + 侧栏
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- AI Ask Bar (YouTube search style) -->
        <div class="flex gap-2">
          <div class="flex-1 relative">
            <input v-model="aiQuery" type="text" placeholder="搜索或描述你想学的内容..."
              class="w-full px-4 py-3 rounded-full border-2 border-[#E7E5E4] bg-white text-[#292524] text-sm outline-none focus:border-[#D97706] transition pr-10"
              @keyup.enter="askAi" />
            <Search class="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E]" :stroke-width="2" />
          </div>
          <button class="px-5 py-3 rounded-full bg-[#D97706] text-white text-sm font-bold hover:brightness-105 transition-all cursor-pointer inline-flex items-center gap-1.5 shadow-[0_3px_0_#B45309] active:translate-y-[2px] active:shadow-[0_1px_0_#B45309] disabled:opacity-50"
            @click="askAi" :disabled="aiLoading">
            <Sparkles class="w-4 h-4" :stroke-width="2" />
            {{ aiLoading ? '分析中...' : 'AI 推荐' }}
          </button>
        </div>
        <div v-if="aiResult" class="p-4 rounded-2xl bg-[#FEF3C7] border-2 border-[#FDE68A]">
          <div class="flex items-start gap-2">
            <span class="text-lg">🤖</span>
            <p class="text-sm text-[#78716C] whitespace-pre-wrap leading-relaxed">{{ aiResult }}</p>
          </div>
        </div>

        <!-- Category Chips (scrollable) -->
        <div class="flex gap-2 overflow-x-auto pb-1 scrollbar-none">
          <button @click="ytCategory = ''"
            :class="['px-4 py-2 rounded-full text-sm font-bold border-2 cursor-pointer transition-all flex-shrink-0',
              !ytCategory ? 'bg-[#292524] text-white border-[#292524]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:bg-[#F5F5F4]']">
            全部
          </button>
          <button v-for="cat in allCategories" :key="cat" @click="ytCategory = ytCategory === cat ? '' : cat"
            :class="['px-4 py-2 rounded-full text-sm font-bold border-2 cursor-pointer transition-all flex-shrink-0',
              ytCategory === cat ? 'bg-[#292524] text-white border-[#292524]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:bg-[#F5F5F4]']">
            {{ cat }}
          </button>
        </div>

        <div v-if="tabLoading" class="text-center py-16">
          <div class="inline-block w-8 h-8 border-3 border-[#D97706]/20 border-t-[#D97706] rounded-full animate-spin"></div>
        </div>

        <!-- Main Content Area (Grid + Sidebar) -->
        <div v-else class="grid lg:grid-cols-10 gap-5">
          <!-- Video Grid (7 cols) -->
          <div class="lg:col-span-7">
            <div v-if="ytFilteredList.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <div v-for="course in ytFilteredList" :key="course.id || course.courseId"
                class="cursor-pointer group" @click="goDetail(course)">
                <!-- Thumbnail (16:9) -->
                <div class="aspect-video rounded-xl overflow-hidden bg-gradient-to-br flex items-center justify-center relative mb-2"
                     :class="getCategoryStyle(course.category).gradient">
                  <component :is="getCategoryStyle(course.category).icon" class="w-12 h-12 text-white/50 group-hover:scale-110 transition-transform" :stroke-width="1" />
                  <!-- Duration badge -->
                  <div class="absolute bottom-2 right-2 px-1.5 py-0.5 rounded bg-black/70 text-white text-[10px] font-medium">
                    {{ course.durationHours || '—' }}h
                  </div>
                </div>
                <!-- Info -->
                <div class="flex gap-2.5">
                  <!-- Avatar -->
                  <div class="w-9 h-9 rounded-full bg-[#D97706]/10 flex items-center justify-center flex-shrink-0 mt-0.5">
                    <span class="text-xs font-bold text-[#D97706]">{{ (course.instructor || course.title || '?')[0] }}</span>
                  </div>
                  <div class="min-w-0">
                    <h4 class="text-sm font-bold text-[#292524] line-clamp-2 group-hover:text-[#D97706] transition-colors">
                      {{ course.title || course.courseName }}
                    </h4>
                    <p class="text-xs text-[#78716C] mt-0.5">{{ course.instructor || '讲师' }}</p>
                    <p class="text-xs text-[#A8A29E]">{{ course.enrollCount || 0 }} 人学习 · {{ course.level || '全等级' }}</p>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-16">
              <p class="text-3xl mb-2">🎬</p>
              <p class="text-sm text-[#78716C]">暂无推荐课程</p>
            </div>
          </div>

          <!-- Sidebar: Up Next (3 cols) -->
          <aside class="lg:col-span-3">
            <div class="sticky top-20">
              <h4 class="text-sm font-extrabold text-[#292524] mb-3">🔜 接下来学什么</h4>
              <div class="space-y-2">
                <div v-for="course in sidebarList" :key="course.id || course.courseId"
                  class="flex gap-2.5 p-2 rounded-xl hover:bg-[#FEF3C7]/50 cursor-pointer transition-colors"
                  @click="goDetail(course)">
                  <div class="w-20 h-12 rounded-lg bg-gradient-to-br flex items-center justify-center flex-shrink-0"
                       :class="getCategoryStyle(course.category).gradient">
                    <component :is="getCategoryStyle(course.category).icon" class="w-5 h-5 text-white/70" :stroke-width="1.25" />
                  </div>
                  <div class="min-w-0">
                    <h5 class="text-xs font-bold text-[#292524] line-clamp-2">{{ course.title || course.courseName }}</h5>
                    <p class="text-[10px] text-[#A8A29E]">{{ course.enrollCount || 0 }} 人</p>
                  </div>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Algolia：左侧 Facet + 搜索框 + 即时结果列表
         ================================================================ -->
    <template v-else>
      <div>
        <!-- Top Search Bar -->
        <div class="algolia-search-bar mb-5">
          <div class="flex items-center gap-2 flex-1">
            <Search class="w-4 h-4 text-[#94A3B8] flex-shrink-0" :stroke-width="2" />
            <input v-model="aiQuery" type="text"
              placeholder="Search courses... (AI-powered)"
              class="flex-1 bg-transparent text-sm text-[#0F172A] outline-none placeholder:text-[#94A3B8] font-mono"
              @keyup.enter="askAi" />
            <button v-if="aiQuery" @click="aiQuery = ''" class="text-xs text-[#94A3B8] hover:text-[#0F172A] cursor-pointer font-mono">clear</button>
            <button class="px-3 py-1 rounded bg-[#0F172A] text-white text-xs font-semibold cursor-pointer hover:bg-[#1E293B] transition-colors disabled:opacity-50"
              @click="askAi" :disabled="aiLoading">
              {{ aiLoading ? '...' : 'AI' }}
            </button>
          </div>
        </div>

        <div v-if="aiResult" class="mb-4 p-3 rounded bg-[#F1F5F9] border border-[#E2E8F0]">
          <p class="text-xs text-[#64748B] whitespace-pre-wrap leading-relaxed font-mono">{{ aiResult }}</p>
        </div>

        <!-- 2-Column: Facets + Results -->
        <div class="flex gap-6">
          <!-- Left Facet Panel -->
          <aside class="w-56 flex-shrink-0 hidden lg:block">
            <!-- Category Filter -->
            <div class="mb-5">
              <h4 class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider mb-2">Category</h4>
              <div class="space-y-1.5">
                <label v-for="cat in allCategories" :key="cat"
                  class="flex items-center gap-2 cursor-pointer group">
                  <input type="checkbox" :checked="algoliaCategories.includes(cat)"
                    @change="toggleAlgoliaCategory(cat)"
                    class="w-3.5 h-3.5 rounded border-[#E2E8F0] text-[#0284C7] cursor-pointer" />
                  <span class="text-xs text-[#64748B] group-hover:text-[#0F172A] transition-colors">{{ cat }}</span>
                  <span class="text-[10px] text-[#94A3B8] font-mono ml-auto">{{ getCategoryCount(cat) }}</span>
                </label>
              </div>
            </div>

            <!-- Level Filter -->
            <div class="mb-5">
              <h4 class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider mb-2">Level</h4>
              <div class="space-y-1.5">
                <label v-for="lv in allLevels" :key="lv"
                  class="flex items-center gap-2 cursor-pointer group">
                  <input type="checkbox" :checked="algoliaLevels.includes(lv)"
                    @change="toggleAlgoliaLevel(lv)"
                    class="w-3.5 h-3.5 rounded border-[#E2E8F0] text-[#0284C7] cursor-pointer" />
                  <span class="text-xs text-[#64748B] group-hover:text-[#0F172A] transition-colors">{{ lv }}</span>
                </label>
              </div>
            </div>

            <!-- Clear Filters -->
            <button v-if="algoliaCategories.length || algoliaLevels.length"
              @click="algoliaCategories = []; algoliaLevels = []"
              class="text-xs text-[#0284C7] font-mono cursor-pointer hover:underline">
              clear all filters
            </button>
          </aside>

          <!-- Right Results -->
          <div class="flex-1 min-w-0">
            <!-- Results Header -->
            <div class="flex items-center justify-between mb-3 pb-2 border-b border-[#E2E8F0]">
              <div class="flex items-center gap-2">
                <span class="text-xs text-[#0F172A] font-semibold">Results</span>
                <span class="text-[10px] text-[#94A3B8] font-mono">{{ algoliaFilteredList.length }} found</span>
              </div>
              <!-- Tabs -->
              <div class="flex gap-1">
                <button v-for="t in algTabs" :key="t.key" @click="activeTab = t.key"
                  :class="['px-2.5 py-1 rounded text-[11px] font-medium border-none cursor-pointer transition-all font-mono',
                    activeTab === t.key ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']">
                  {{ t.label }}
                </button>
              </div>
            </div>

            <div v-if="tabLoading" class="text-center py-12 text-[#94A3B8] text-xs font-mono">loading...</div>

            <!-- Result Items -->
            <div v-else-if="algoliaFilteredList.length" class="divide-y divide-[#F1F5F9]">
              <div v-for="course in algoliaFilteredList" :key="course.id || course.courseId"
                class="py-3 hover:bg-[#F8FAFC] -mx-3 px-3 rounded cursor-pointer transition-colors"
                @click="goDetail(course)">
                <div class="flex items-start gap-3">
                  <div class="w-2 h-2 rounded-full mt-1.5 flex-shrink-0" :style="{ backgroundColor: getCategoryStyle(course.category).hex }"></div>
                  <div class="flex-1 min-w-0">
                    <h4 class="text-sm text-[#0F172A] font-medium">{{ course.title || course.courseName }}</h4>
                    <p class="text-xs text-[#64748B] mt-0.5 line-clamp-1">{{ course.description || '暂无描述' }}</p>
                    <div class="flex items-center gap-2 mt-1.5">
                      <code v-if="course.category" class="text-[10px] font-mono text-[#64748B] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ course.category }}</code>
                      <code v-if="course.level" class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ course.level }}</code>
                      <span class="text-[10px] text-[#94A3B8] font-mono ml-auto">{{ course.enrollCount || 0 }} users</span>
                      <span v-if="course.score || course.similarity" class="text-[10px] font-mono text-[#0284C7]">
                        {{ Math.round((course.score || course.similarity || 0) * 100) }}% match
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-12 text-[#94A3B8] text-xs font-mono">
              No results found. Try adjusting your filters.
            </div>

            <!-- Pagination hint -->
            <div v-if="algoliaFilteredList.length" class="mt-4 pt-3 border-t border-[#E2E8F0] text-[10px] text-[#94A3B8] font-mono text-center">
              Showing 1-{{ algoliaFilteredList.length }} of {{ algoliaFilteredList.length }} results
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { recommendApi } from '@/api/recommend'
import { aiApi } from '@/api/ai'
import { getCategoryStyle } from '@/utils/categoryColors'
import { toast } from '@/composables/useToast'
import {
  Sparkles, Heart, Flame, Clock, Search, Play,
  Brain, TrendingUp, Star, GraduationCap, Users as UsersIcon,
  Code, Database, Globe, Palette, Server, Cpu, Shield, Smartphone,
} from 'lucide-vue-next'

const router = useRouter()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── AI Recommend ── */
const aiQuery = ref('')
const aiResult = ref('')
const aiLoading = ref(false)

async function askAi() {
  if (!aiQuery.value.trim()) { toast.warning('请输入学习需求'); return }
  aiLoading.value = true
  aiResult.value = ''
  try {
    const r: any = await aiApi.recommend({ message: aiQuery.value })
    aiResult.value = r?.recommendation || r?.message || r || '暂无结果'
  } catch (e: any) {
    toast.error(e.message || '推荐失败')
  } finally {
    aiLoading.value = false
  }
}

/* ── Tab System ── */
const activeTab = ref('youMayLike')
const tabLoading = ref(false)
const tabData = ref<Record<string, any[]>>({})
const currentList = computed(() => tabData.value[activeTab.value] || [])

const tabFetcher: Record<string, () => Promise<any>> = {
  youMayLike: () => recommendApi.youMayLike(),
  popular: () => recommendApi.popular(),
  newCourses: () => recommendApi.newCourses(),
  byHistory: () => recommendApi.byHistory(),
  bySkills: () => recommendApi.bySkills(),
  byPreference: () => recommendApi.byPreference(),
  hybrid: () => recommendApi.hybrid(),
  userBased: () => recommendApi.userBased(),
  itemBased: () => recommendApi.itemBased(),
}

async function loadTab(key: string) {
  if (tabData.value[key]) return
  tabLoading.value = true
  try {
    const res: any = await (tabFetcher as Record<string, () => Promise<any>>)[key]?.()
    tabData.value[key] = Array.isArray(res) ? res : (res?.records || res?.data || [])
  } catch {
    tabData.value[key] = []
  } finally {
    tabLoading.value = false
  }
}

watch(activeTab, (key) => loadTab(key), { immediate: false })

function goDetail(course: any) {
  const id = course.id || course.courseId
  if (id) router.push(`/student/courses/${id}`)
}

/* ── Netflix Rows (Light) ── */
const netflixRows = computed(() => [
  { key: 'youMayLike', label: '猜你喜欢', icon: Heart, items: tabData.value['youMayLike'] || [], loading: false },
  { key: 'popular', label: '热门推荐', icon: Flame, items: tabData.value['popular'] || [], loading: false },
  { key: 'newCourses', label: '最新上线', icon: Star, items: tabData.value['newCourses'] || [], loading: false },
  { key: 'byHistory', label: '基于你的学习', icon: Clock, items: tabData.value['byHistory'] || [], loading: false },
])

/* ── Spotify Categories (Dark) ── */
const filterCategory = ref('')
const spotifyCategories = [
  { key: '后端开发', label: '后端开发', color1: '#1DB954', color2: '#0F7A3C', icon: Server },
  { key: '前端开发', label: '前端开发', color1: '#E91E63', color2: '#C2185B', icon: Globe },
  { key: '数据库', label: '数据库', color1: '#FF6F00', color2: '#E65100', icon: Database },
  { key: '人工智能', label: '人工智能', color1: '#7C4DFF', color2: '#6200EA', icon: Brain },
  { key: '移动开发', label: '移动开发', color1: '#00BCD4', color2: '#0097A7', icon: Smartphone },
  { key: '网络安全', label: '网络安全', color1: '#F44336', color2: '#D32F2F', icon: Shield },
  { key: '编程语言', label: '编程语言', color1: '#2196F3', color2: '#1976D2', icon: Code },
  { key: 'UI设计', label: 'UI 设计', color1: '#FF9800', color2: '#F57C00', icon: Palette },
]

const spotifyFilteredList = computed(() => {
  const list = currentList.value
  if (!filterCategory.value) return list
  return list.filter((c: any) => (c.category || '').includes(filterCategory.value))
})

/* ── YouTube Categories (Warm) ── */
const ytCategory = ref('')
const allCategories = computed(() => {
  const cats = new Set<string>()
  Object.values(tabData.value).flat().forEach((c: any) => {
    if (c.category) cats.add(c.category)
  })
  return Array.from(cats).slice(0, 8)
})

const ytFilteredList = computed(() => {
  const list = currentList.value
  if (!ytCategory.value) return list
  return list.filter((c: any) => c.category === ytCategory.value)
})

const sidebarList = computed(() => {
  const popular = tabData.value['popular'] || []
  return popular.slice(0, 6)
})

/* ── Algolia Facets (Pro) ── */
const algoliaCategories = ref<string[]>([])
const algoliaLevels = ref<string[]>([])

const allLevels = computed(() => {
  const levels = new Set<string>()
  Object.values(tabData.value).flat().forEach((c: any) => {
    if (c.level) levels.add(c.level)
  })
  return Array.from(levels)
})

const algTabs = [
  { key: 'youMayLike', label: 'Recommended' },
  { key: 'popular', label: 'Popular' },
  { key: 'newCourses', label: 'New' },
  { key: 'hybrid', label: 'Hybrid' },
]

function toggleAlgoliaCategory(cat: string) {
  const idx = algoliaCategories.value.indexOf(cat)
  if (idx >= 0) algoliaCategories.value.splice(idx, 1)
  else algoliaCategories.value.push(cat)
}

function toggleAlgoliaLevel(lv: string) {
  const idx = algoliaLevels.value.indexOf(lv)
  if (idx >= 0) algoliaLevels.value.splice(idx, 1)
  else algoliaLevels.value.push(lv)
}

function getCategoryCount(cat: string) {
  return currentList.value.filter((c: any) => c.category === cat).length
}

const algoliaFilteredList = computed(() => {
  let list = currentList.value
  if (algoliaCategories.value.length) {
    list = list.filter((c: any) => algoliaCategories.value.includes(c.category))
  }
  if (algoliaLevels.value.length) {
    list = list.filter((c: any) => algoliaLevels.value.includes(c.level))
  }
  return list
})

/* ── Init ── */
onMounted(async () => {
  // Load initial tabs in parallel
  const keys = ['youMayLike', 'popular', 'newCourses', 'byHistory']
  await Promise.allSettled(keys.map(async (key) => {
    try {
      const res: any = await (tabFetcher as Record<string, () => Promise<any>>)[key]?.()
      tabData.value[key] = Array.isArray(res) ? res : (res?.records || res?.data || [])
    } catch {
      tabData.value[key] = []
    }
  }))
})
</script>

<style scoped>
/* ======== NETFLIX (Light) ======== */
.netflix-card {
  border: 1px solid #E3E8EE;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.netflix-card:hover {
  transform: scale(1.05);
  box-shadow: 0 15px 35px rgba(60,66,87,0.12);
  z-index: 10;
}

.scrollbar-thin::-webkit-scrollbar { height: 4px; }
.scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
.scrollbar-thin::-webkit-scrollbar-thumb { background: #E3E8EE; border-radius: 2px; }
.scrollbar-thin::-webkit-scrollbar-thumb:hover { background: #CBD5E1; }

/* ======== SPOTIFY (Dark) ======== */
.spotify-hero {
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
}
.spotify-category-card {
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.spotify-category-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}
.spotify-playlist-card {
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 8px;
  overflow: hidden;
}

/* ======== YOUTUBE (Warm) ======== */
.scrollbar-none::-webkit-scrollbar { display: none; }
.scrollbar-none { -ms-overflow-style: none; scrollbar-width: none; }

.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ======== ALGOLIA (Pro) ======== */
.algolia-search-bar {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  background: #fff;
  border: 2px solid #E2E8F0;
  border-radius: 6px;
  transition: border-color 0.15s;
}
.algolia-search-bar:focus-within {
  border-color: #0284C7;
  box-shadow: 0 0 0 3px rgba(2,132,199,0.08);
}
</style>
