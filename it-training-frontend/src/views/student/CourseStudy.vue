<template>
  <div class="min-h-screen" :class="{ 'bg-white': theme === 'light', 'bg-[#1E1E1E]': theme === 'dark', 'bg-[#FFFBF5]': theme === 'warm', 'bg-[#FAFAFA]': theme === 'pro' }">

    <!-- ================================================================
         ☀️ LIGHT — Coursera：视频区 + 侧面板 + 底部导航
         ================================================================ -->
    <template v-if="theme === 'light'">
      <!-- Top Bar -->
      <header class="sticky top-0 z-30 bg-white/90 backdrop-blur-md border-b border-[#E3E8EE] py-3">
        <div class="flex items-center gap-3 max-w-7xl mx-auto w-full px-4">
          <button @click="$router.back()" class="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm text-[#425466] hover:text-[#0A2540] hover:bg-[#F6F9FC] rounded-md transition-colors cursor-pointer">
            <ArrowLeft class="w-4 h-4" :stroke-width="1.75" /> 返回
          </button>
          <div class="flex-1 min-w-0">
            <h1 class="text-sm font-semibold text-[#0A2540] truncate">{{ courseInfo?.title || '课程学习' }}</h1>
          </div>
          <div v-if="courseProgress" class="text-xs font-semibold text-[#635BFF] bg-[#635BFF]/10 px-2.5 py-1 rounded-md">
            {{ courseProgress.progress || 0 }}%
          </div>
        </div>
      </header>

      <div class="max-w-7xl mx-auto px-4 py-6">
        <div class="grid lg:grid-cols-10 gap-6">
          <!-- Left: Main Content (7 cols) -->
          <div class="lg:col-span-7 space-y-4">
            <!-- Video Player Area -->
            <div class="bg-white rounded-lg border border-[#E3E8EE] overflow-hidden shadow-[0_2px_4px_rgba(0,0,0,0.04)]">
              <div class="aspect-video bg-[#0A2540] rounded-t-lg flex items-center justify-center relative">
                <div v-if="!currentChapter" class="text-center">
                  <PlayCircle class="w-16 h-16 mx-auto text-white/30" :stroke-width="1" />
                  <p class="text-white/50 text-sm mt-3">请从右侧选择章节</p>
                </div>
                <div v-else class="text-center">
                  <PlayCircle class="w-16 h-16 mx-auto text-white/60 hover:text-white cursor-pointer transition-colors" :stroke-width="1" />
                  <p class="text-white/80 text-sm mt-3 font-medium">{{ currentChapter.title }}</p>
                  <p class="text-white/40 text-xs mt-1">{{ currentChapter.contentType || 'VIDEO' }}</p>
                </div>
              </div>
            </div>

            <!-- Progress -->
            <div v-if="courseProgress" class="bg-white rounded-lg border border-[#E3E8EE] p-5 shadow-[0_2px_4px_rgba(0,0,0,0.04)]">
              <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-[#0A2540]">课程总进度</span>
                <span class="text-sm font-medium text-[#635BFF]">{{ courseProgress.progress || 0 }}%</span>
              </div>
              <div class="w-full h-2 bg-[#E3E8EE] rounded-full overflow-hidden">
                <div class="h-full bg-[#635BFF] rounded-full transition-all duration-500" :style="{ width: (courseProgress.progress || 0) + '%' }"></div>
              </div>
              <div class="flex justify-between mt-2">
                <span class="text-xs text-[#8898AA]">{{ completedCount }} / {{ chapters.length }} 章节已完成</span>
                <span v-if="courseProgress.status === 'COMPLETED'" class="text-xs font-medium text-emerald-600">已完课</span>
              </div>
            </div>

            <!-- Chapter Content -->
            <div v-if="currentChapter" class="bg-white rounded-lg border border-[#E3E8EE] p-5 shadow-[0_2px_4px_rgba(0,0,0,0.04)]">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-sm font-semibold text-[#0A2540]">章节内容</h3>
                <button
                  v-if="!isChapterCompleted(currentChapter.id)"
                  class="px-3.5 py-1.5 rounded-md text-xs font-semibold text-white bg-[#635BFF] cursor-pointer transition-all inline-flex items-center gap-1.5 hover:brightness-110 disabled:opacity-50 disabled:cursor-not-allowed stripe-btn-shadow"
                  @click="completeCurrentChapter" :disabled="completingChapter"
                >
                  <CheckCircle class="w-3.5 h-3.5" :stroke-width="2" />
                  {{ completingChapter ? '标记中...' : '标记为已完成' }}
                </button>
                <span v-else class="inline-flex items-center gap-1 text-xs text-emerald-600 font-medium">
                  <CheckCircle class="w-3.5 h-3.5" :stroke-width="2" /> 已完成
                </span>
              </div>
              <p class="text-sm text-[#425466] leading-relaxed">
                {{ currentChapter.description || '该章节暂无详细描述。点击"标记为已完成"来记录学习进度。' }}
              </p>
            </div>

            <!-- Bottom Navigation (Coursera style) -->
            <div v-if="currentChapter" class="flex items-center justify-between bg-white rounded-lg border border-[#E3E8EE] p-4">
              <button @click="goPrevChapter" :disabled="!prevChapter"
                class="inline-flex items-center gap-1.5 px-4 py-2 text-sm font-medium text-[#425466] hover:text-[#0A2540] rounded-md border border-[#E3E8EE] hover:bg-[#F6F9FC] transition-all disabled:opacity-30 cursor-pointer">
                <ArrowLeft class="w-4 h-4" :stroke-width="1.75" /> 上一章
              </button>
              <span class="text-xs text-[#8898AA] font-mono">{{ currentChapterIdx + 1 }} / {{ chapters.length }}</span>
              <button @click="goNextChapter" :disabled="!nextChapter"
                class="inline-flex items-center gap-1.5 px-4 py-2 text-sm font-medium text-white bg-[#635BFF] rounded-md hover:brightness-110 transition-all disabled:opacity-30 cursor-pointer stripe-btn-shadow">
                下一章 <ArrowRight class="w-4 h-4" :stroke-width="1.75" />
              </button>
            </div>
          </div>

          <!-- Right: Chapter Sidebar (3 cols) -->
          <aside class="lg:col-span-3">
            <div class="bg-white rounded-lg border border-[#E3E8EE] p-4 shadow-[0_2px_4px_rgba(0,0,0,0.04)] sticky top-20">
              <h3 class="text-sm font-semibold text-[#0A2540] mb-3">
                章节列表 <span class="text-[#8898AA] font-normal">({{ chapters.length }})</span>
              </h3>
              <div v-if="loading" class="text-xs text-[#8898AA] text-center py-8">加载中...</div>
              <div v-else class="space-y-1 max-h-[calc(100vh-12rem)] overflow-y-auto">
                <div v-for="(ch, idx) in chapters" :key="ch.id" @click="selectChapter(ch)"
                  :class="['flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-all text-sm',
                    currentChapter?.id === ch.id ? 'bg-[#635BFF]/10 text-[#635BFF] font-medium' : 'hover:bg-[#F6F9FC] text-[#425466] hover:text-[#0A2540]']">
                  <div :class="['w-7 h-7 rounded-md flex items-center justify-center flex-shrink-0 text-xs font-medium',
                    isChapterCompleted(ch.id) ? 'bg-emerald-50 text-emerald-600'
                    : currentChapter?.id === ch.id ? 'bg-[#635BFF] text-white'
                    : 'bg-[#F6F9FC] text-[#8898AA]']">
                    <Check v-if="isChapterCompleted(ch.id)" class="w-3.5 h-3.5" :stroke-width="2.5" />
                    <span v-else>{{ idx + 1 }}</span>
                  </div>
                  <span class="flex-1 truncate">{{ ch.title }}</span>
                  <span v-if="ch.duration" class="text-xs text-[#8898AA] flex-shrink-0">{{ ch.duration }}min</span>
                </div>
              </div>
              <!-- Mark Course Complete -->
              <div v-if="allChaptersCompleted && courseProgress?.status !== 'COMPLETED'" class="mt-4 pt-4 border-t border-[#E3E8EE]">
                <button class="w-full px-4 py-2.5 rounded-md text-sm font-semibold text-white bg-emerald-600 cursor-pointer transition-all inline-flex items-center justify-center gap-2 hover:brightness-110 disabled:opacity-50 disabled:cursor-not-allowed"
                        @click="markCourseCompleted" :disabled="completingCourse">
                  <Trophy class="w-4 h-4" :stroke-width="2" />
                  {{ completingCourse ? '完成中...' : '完成课程' }}
                </button>
              </div>
            </div>
          </aside>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — VS Code：编辑器面板式
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="h-screen flex flex-col overflow-hidden">
        <!-- VS Code Title Bar -->
        <header class="h-9 bg-[#323233] flex items-center px-3 flex-shrink-0 border-b border-[#252526]">
          <button @click="$router.back()" class="text-[11px] text-[#CCCCCC]/60 hover:text-[#CCCCCC] transition mr-3 cursor-pointer">← 返回</button>
          <span class="text-[11px] text-[#CCCCCC]/80 truncate">{{ courseInfo?.title || '课程学习' }}</span>
          <span class="ml-auto text-[10px] font-mono text-[#CCCCCC]/40">{{ courseProgress?.progress || 0 }}%</span>
        </header>

        <!-- VS Code Tab Bar -->
        <div class="h-9 bg-[#252526] flex items-end flex-shrink-0 overflow-x-auto">
          <div v-for="(ch, idx) in chapters" :key="ch.id"
               @click="selectChapter(ch)"
               :class="['inline-flex items-center gap-2 px-3 h-[35px] text-[12px] cursor-pointer border-r border-[#252526] flex-shrink-0 transition-colors',
                 currentChapter?.id === ch.id
                   ? 'bg-[#1E1E1E] text-[#CCCCCC] border-t-2 border-t-[#818CF8]'
                   : 'bg-[#2D2D2D] text-[#999999] hover:bg-[#2D2D2D]/80']">
            <span class="w-3 h-3 rounded-sm flex-shrink-0" :class="isChapterCompleted(ch.id) ? 'bg-emerald-500/60' : 'bg-[#519ABA]/60'"></span>
            <span class="truncate max-w-[140px]">{{ ch.title }}</span>
            <span v-if="isChapterCompleted(ch.id)" class="text-emerald-400 text-[10px]">✓</span>
          </div>
        </div>

        <!-- Main Area: Explorer + Editor -->
        <div class="flex-1 flex overflow-hidden">
          <!-- Left: Explorer Sidebar -->
          <aside class="w-60 bg-[#252526] flex-shrink-0 flex flex-col border-r border-[#1E1E1E] overflow-hidden">
            <div class="px-3 py-2 text-[10px] font-semibold text-[#BBBBBB] uppercase tracking-wider">
              Explorer
            </div>
            <div class="flex-1 overflow-y-auto">
              <div class="px-2 py-1 text-[10px] text-[#BBBBBB]/60 uppercase tracking-wider font-semibold">
                ▼ {{ courseInfo?.title || 'Course' }}
              </div>
              <div v-for="(ch, idx) in chapters" :key="ch.id" @click="selectChapter(ch)"
                :class="['flex items-center gap-2 px-4 py-1.5 cursor-pointer text-[12px] transition-colors',
                  currentChapter?.id === ch.id ? 'bg-[#37373D] text-[#CCCCCC]' : 'text-[#CCCCCC]/70 hover:bg-[#2A2D2E]']">
                <span class="w-3 h-3 rounded-sm flex-shrink-0" :class="isChapterCompleted(ch.id) ? 'bg-emerald-500/60' : 'bg-[#519ABA]/40'"></span>
                <span class="truncate">{{ ch.title }}</span>
              </div>
            </div>
            <!-- Progress in Explorer -->
            <div class="p-3 border-t border-[#1E1E1E]">
              <div class="text-[10px] text-[#BBBBBB]/60 mb-1">PROGRESS</div>
              <div class="w-full h-1.5 bg-[#3C3C3C] rounded-full overflow-hidden">
                <div class="h-full bg-[#818CF8] rounded-full" :style="{ width: (courseProgress?.progress || 0) + '%' }"></div>
              </div>
              <div class="text-[10px] text-[#BBBBBB]/40 mt-1 font-mono">{{ completedCount }}/{{ chapters.length }}</div>
            </div>
          </aside>

          <!-- Center: Editor Area -->
          <div class="flex-1 bg-[#1E1E1E] flex flex-col overflow-hidden">
            <!-- Breadcrumb -->
            <div v-if="currentChapter" class="h-6 bg-[#1E1E1E] px-3 flex items-center text-[11px] text-[#CCCCCC]/40 border-b border-[#252526] flex-shrink-0">
              <span>{{ courseInfo?.title }}</span>
              <span class="mx-1.5">›</span>
              <span class="text-[#CCCCCC]/70">{{ currentChapter.title }}</span>
            </div>

            <!-- Content (Editor) -->
            <div class="flex-1 p-6 overflow-y-auto">
              <div v-if="!currentChapter" class="flex items-center justify-center h-full">
                <div class="text-center">
                  <PlayCircle class="w-16 h-16 mx-auto text-[#CCCCCC]/20" :stroke-width="1" />
                  <p class="text-[#CCCCCC]/30 text-sm mt-3 font-mono">Select a chapter from explorer</p>
                </div>
              </div>
              <div v-else class="max-w-3xl">
                <h2 class="text-xl font-semibold text-[#CCCCCC] mb-4">{{ currentChapter.title }}</h2>
                <p class="text-sm text-[#CCCCCC]/70 leading-relaxed mb-6">
                  {{ currentChapter.description || '该章节暂无详细描述。' }}
                </p>

                <!-- Action Buttons -->
                <div class="flex items-center gap-3">
                  <button
                    v-if="!isChapterCompleted(currentChapter.id)"
                    class="px-3.5 py-1.5 rounded-md text-xs font-medium text-white bg-[#818CF8] cursor-pointer transition-all inline-flex items-center gap-1.5 hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] disabled:opacity-50 disabled:cursor-not-allowed"
                    @click="completeCurrentChapter" :disabled="completingChapter"
                  >
                    <CheckCircle class="w-3.5 h-3.5" :stroke-width="2" />
                    {{ completingChapter ? 'Marking...' : 'Mark Complete' }}
                  </button>
                  <span v-else class="inline-flex items-center gap-1 text-xs text-emerald-400 font-medium">
                    <CheckCircle class="w-3.5 h-3.5" :stroke-width="2" /> Completed
                  </span>
                </div>

                <!-- Complete Course Button -->
                <div v-if="allChaptersCompleted && courseProgress?.status !== 'COMPLETED'" class="mt-6 pt-4 border-t border-[#333333]">
                  <button class="px-4 py-2.5 rounded-md text-sm font-medium text-white bg-emerald-500 cursor-pointer transition-all inline-flex items-center gap-2 hover:shadow-[0_0_20px_rgba(16,185,129,0.3)] disabled:opacity-50 disabled:cursor-not-allowed"
                          @click="markCourseCompleted" :disabled="completingCourse">
                    <Trophy class="w-4 h-4" :stroke-width="2" />
                    {{ completingCourse ? 'Completing...' : 'Complete Course' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- VS Code Status Bar -->
        <footer class="h-6 bg-[#007ACC] flex items-center px-3 flex-shrink-0 text-[11px] text-white/90">
          <span class="mr-4">{{ courseInfo?.title || 'Course' }}</span>
          <span class="mr-4 font-mono">Ch {{ currentChapterIdx + 1 }}/{{ chapters.length }}</span>
          <span class="mr-4">{{ completedCount }} completed</span>
          <span class="ml-auto font-mono">{{ courseProgress?.progress || 0 }}%</span>
        </footer>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Khan Academy：全屏学习体验
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="min-h-screen flex flex-col">
        <!-- Big Progress Bar Top -->
        <div class="bg-[#FFFBF5] px-4 py-3 flex-shrink-0">
          <div class="max-w-3xl mx-auto">
            <div class="flex items-center justify-between mb-2">
              <button @click="$router.back()" class="inline-flex items-center gap-1.5 text-sm text-[#78716C] hover:text-[#292524] transition-colors cursor-pointer">
                <ArrowLeft class="w-4 h-4" :stroke-width="1.75" /> 返回
              </button>
              <span class="text-sm font-extrabold text-[#292524]">{{ courseInfo?.title || '课程学习' }}</span>
              <span class="text-sm font-bold text-[#D97706]">{{ courseProgress?.progress || 0 }}%</span>
            </div>
            <!-- Big Colorful Progress Bar -->
            <div class="w-full h-4 bg-[#E7E5E4] rounded-full overflow-hidden">
              <div class="h-full rounded-full transition-all duration-700 relative bg-gradient-to-r from-[#58CC02] via-[#FFC800] to-[#FF9600]"
                   :style="{ width: (courseProgress?.progress || 0) + '%' }">
                <div class="absolute inset-0 bg-white/20 rounded-full"
                     style="background: repeating-linear-gradient(90deg, transparent, transparent 12px, rgba(255,255,255,0.2) 12px, rgba(255,255,255,0.2) 24px)" />
              </div>
            </div>
            <!-- Milestone dots -->
            <div class="flex items-center justify-between mt-2">
              <div v-for="(ch, idx) in chapters" :key="ch.id"
                   @click="selectChapter(ch)"
                   class="relative cursor-pointer group">
                <div :class="['w-6 h-6 rounded-full flex items-center justify-center text-[9px] font-extrabold transition-all',
                  isChapterCompleted(ch.id) ? 'bg-[#58CC02] text-white shadow-[0_2px_0_#46A302]'
                  : currentChapter?.id === ch.id ? 'bg-[#FFC800] text-white shadow-[0_2px_0_#E5A800] animate-pulse'
                  : 'bg-[#E5E7EB] text-[#A8A29E]']">
                  {{ isChapterCompleted(ch.id) ? '✓' : idx + 1 }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Encouragement Message -->
        <div v-if="courseProgress" class="text-center py-2">
          <p class="text-sm font-bold" :class="(courseProgress.progress || 0) >= 80 ? 'text-[#58CC02]' : (courseProgress.progress || 0) >= 50 ? 'text-[#FFC800]' : 'text-[#78716C]'">
            {{ encourageText }}
          </p>
        </div>

        <!-- Content Area (centered, big, friendly) -->
        <div class="flex-1 flex items-start justify-center px-4 py-6">
          <div class="max-w-2xl w-full">
            <div v-if="!currentChapter" class="text-center py-20">
              <div class="text-6xl mb-4">📚</div>
              <p class="text-lg font-bold text-[#292524]">选择一个章节开始学习吧！</p>
              <p class="text-sm text-[#78716C] mt-1">点击上方的节点选择章节</p>
            </div>
            <div v-else>
              <!-- Chapter Title Card -->
              <div class="bg-white rounded-3xl border-2 border-[#E5E7EB] shadow-[0_4px_0_#E5E7EB] p-8 mb-6">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-12 h-12 rounded-2xl bg-[#FFC800] shadow-[0_3px_0_#E5A800] flex items-center justify-center">
                    <span class="text-white text-lg font-extrabold">{{ currentChapterIdx + 1 }}</span>
                  </div>
                  <div>
                    <h2 class="text-xl font-extrabold text-[#292524]">{{ currentChapter.title }}</h2>
                    <p v-if="currentChapter.duration" class="text-xs text-[#A8A29E] font-medium">约 {{ currentChapter.duration }} 分钟</p>
                  </div>
                </div>
                <p class="text-sm text-[#78716C] leading-relaxed">
                  {{ currentChapter.description || '开始学习这个章节的内容吧！完成后点击下方的按钮标记为已完成。' }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Bottom Sticky Action Bar -->
        <div v-if="currentChapter" class="sticky bottom-0 bg-gradient-to-t from-[#FFFBF5] via-[#FFFBF5] to-transparent pt-4 pb-6 px-4 flex-shrink-0">
          <div class="max-w-2xl mx-auto flex items-center gap-3">
            <button @click="goPrevChapter" :disabled="!prevChapter"
              class="w-14 h-14 rounded-2xl border-2 border-[#E7E5E4] flex items-center justify-center hover:bg-[#FEF3C7] transition-colors disabled:opacity-30 cursor-pointer">
              <ArrowLeft class="w-5 h-5 text-[#78716C]" :stroke-width="2" />
            </button>

            <button
              v-if="!isChapterCompleted(currentChapter.id)"
              class="flex-1 py-4 rounded-2xl bg-[#58CC02] text-white text-base font-extrabold
                     shadow-[0_5px_0_#46A302] hover:brightness-105
                     active:translate-y-[3px] active:shadow-[0_2px_0_#46A302] transition-all
                     inline-flex items-center justify-center gap-2 cursor-pointer
                     disabled:opacity-50 disabled:cursor-not-allowed"
              @click="completeCurrentChapter" :disabled="completingChapter"
            >
              <CheckCircle class="w-5 h-5" :stroke-width="2.5" />
              {{ completingChapter ? '标记中...' : '完成 ✓' }}
            </button>
            <span v-else class="flex-1 py-4 rounded-2xl bg-[#58CC02]/10 text-[#58CC02] text-base font-extrabold inline-flex items-center justify-center gap-2">
              <CheckCircle class="w-5 h-5" :stroke-width="2.5" /> 已完成！
            </span>

            <button @click="goNextChapter" :disabled="!nextChapter"
              class="w-14 h-14 rounded-2xl border-2 border-[#E7E5E4] flex items-center justify-center hover:bg-[#FEF3C7] transition-colors disabled:opacity-30 cursor-pointer">
              <ArrowRight class="w-5 h-5 text-[#78716C]" :stroke-width="2" />
            </button>
          </div>

          <!-- Complete Course Banner -->
          <div v-if="allChaptersCompleted && courseProgress?.status !== 'COMPLETED'" class="max-w-2xl mx-auto mt-3">
            <button class="w-full py-4 rounded-2xl bg-[#FFC800] text-white text-base font-extrabold
                           shadow-[0_5px_0_#E5A800] hover:brightness-105
                           active:translate-y-[3px] active:shadow-[0_2px_0_#E5A800] transition-all
                           inline-flex items-center justify-center gap-2 cursor-pointer"
                    @click="markCourseCompleted" :disabled="completingCourse">
              <Trophy class="w-5 h-5" :stroke-width="2" />
              {{ completingCourse ? '完成中...' : '🎉 完成课程！获得成就' }}
            </button>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Exercism：分栏 + 终端风格
         ================================================================ -->
    <template v-else>
      <div class="h-screen flex flex-col overflow-hidden">
        <!-- Top Navigation Bar -->
        <header class="h-12 bg-white border-b border-[#E2E8F0] flex items-center px-4 flex-shrink-0">
          <button @click="$router.back()" class="text-xs text-[#64748B] hover:text-[#0F172A] transition font-mono cursor-pointer mr-4">← back</button>
          <span class="text-xs font-semibold text-[#0F172A] font-mono uppercase tracking-wider truncate">{{ courseInfo?.title || 'Course' }}</span>
          <div class="ml-auto flex items-center gap-4">
            <!-- Chapter Navigation (< 1 / 12 >) -->
            <div class="flex items-center gap-2 font-mono text-xs">
              <button @click="goPrevChapter" :disabled="!prevChapter"
                class="w-6 h-6 rounded border border-[#E2E8F0] flex items-center justify-center text-[#64748B] hover:bg-[#F1F5F9] disabled:opacity-30 cursor-pointer transition">
                <ArrowLeft class="w-3 h-3" :stroke-width="2" />
              </button>
              <span class="text-[#0F172A] font-semibold">{{ currentChapterIdx + 1 }}</span>
              <span class="text-[#94A3B8]">/</span>
              <span class="text-[#94A3B8]">{{ chapters.length }}</span>
              <button @click="goNextChapter" :disabled="!nextChapter"
                class="w-6 h-6 rounded border border-[#E2E8F0] flex items-center justify-center text-[#64748B] hover:bg-[#F1F5F9] disabled:opacity-30 cursor-pointer transition">
                <ArrowRight class="w-3 h-3" :stroke-width="2" />
              </button>
            </div>
            <div class="h-4 border-l border-[#E2E8F0]"></div>
            <span class="font-mono text-xs text-[#64748B]">{{ courseProgress?.progress || 0 }}%</span>
          </div>
        </header>

        <!-- Split Pane -->
        <div class="flex-1 flex overflow-hidden">
          <!-- Left Pane: Content -->
          <div class="flex-1 overflow-y-auto p-6 border-r border-[#E2E8F0]">
            <div v-if="!currentChapter" class="flex items-center justify-center h-full">
              <div class="text-center">
                <p class="text-xs text-[#94A3B8] font-mono">// Select a chapter to begin</p>
              </div>
            </div>
            <div v-else class="max-w-xl">
              <div class="flex items-center gap-2 mb-4">
                <code class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-2 py-0.5 rounded">chapter-{{ currentChapterIdx + 1 }}</code>
                <span v-if="isChapterCompleted(currentChapter.id)" class="text-[10px] font-mono text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded">✓ completed</span>
              </div>
              <h2 class="text-lg font-semibold text-[#0F172A] mb-4">{{ currentChapter.title }}</h2>
              <p class="text-sm text-[#64748B] leading-relaxed mb-6">
                {{ currentChapter.description || '该章节暂无详细描述。' }}
              </p>

              <div class="flex items-center gap-3">
                <button
                  v-if="!isChapterCompleted(currentChapter.id)"
                  class="px-3 py-1.5 rounded-md text-[11px] font-semibold text-white bg-[#0F172A] cursor-pointer transition-all inline-flex items-center gap-1.5 hover:bg-[#1E293B] disabled:opacity-50 disabled:cursor-not-allowed"
                  @click="completeCurrentChapter" :disabled="completingChapter"
                >
                  <CheckCircle class="w-3.5 h-3.5" :stroke-width="2" />
                  {{ completingChapter ? 'marking...' : 'Mark complete' }}
                </button>
                <span v-else class="text-[10px] text-emerald-600 font-mono font-semibold uppercase">✓ Completed</span>
              </div>

              <div v-if="allChaptersCompleted && courseProgress?.status !== 'COMPLETED'" class="mt-6 pt-4 border-t border-[#E2E8F0]">
                <button class="px-4 py-2 rounded-md text-xs font-semibold text-white bg-emerald-600 cursor-pointer transition-all inline-flex items-center gap-2 hover:bg-emerald-700 disabled:opacity-50 disabled:cursor-not-allowed"
                        @click="markCourseCompleted" :disabled="completingCourse">
                  <Trophy class="w-3.5 h-3.5" :stroke-width="2" />
                  {{ completingCourse ? 'completing...' : 'Complete course' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Right Pane: Terminal / Chapters -->
          <div class="w-[45%] bg-[#0F172A] overflow-hidden flex flex-col">
            <div class="h-9 bg-[#1E293B] flex items-center px-3 flex-shrink-0 border-b border-[#334155]">
              <div class="flex items-center gap-1.5">
                <div class="w-2.5 h-2.5 rounded-full bg-[#EF4444]"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-[#EAB308]"></div>
                <div class="w-2.5 h-2.5 rounded-full bg-[#22C55E]"></div>
              </div>
              <span class="ml-4 text-[10px] font-mono text-[#94A3B8]">chapters — {{ courseInfo?.title || 'course' }}</span>
            </div>
            <div class="flex-1 p-4 overflow-y-auto font-mono text-xs">
              <div class="text-emerald-400 mb-2">$ ls chapters/</div>
              <div v-for="(ch, idx) in chapters" :key="ch.id"
                   @click="selectChapter(ch)"
                   :class="['py-1.5 px-2 -mx-2 rounded cursor-pointer transition-colors',
                     currentChapter?.id === ch.id ? 'bg-[#1E293B] text-white' : 'text-[#94A3B8] hover:bg-[#1E293B]/50 hover:text-[#CBD5E1]']">
                <span class="text-[#64748B] mr-2">{{ String(idx + 1).padStart(2, '0') }}.</span>
                <span :class="isChapterCompleted(ch.id) ? 'text-emerald-400' : ''">{{ ch.title }}</span>
                <span v-if="isChapterCompleted(ch.id)" class="text-emerald-400 ml-2">✓</span>
                <span v-if="ch.duration" class="text-[#475569] ml-2">[{{ ch.duration }}min]</span>
              </div>
              <div class="mt-4 pt-4 border-t border-[#1E293B]">
                <div class="text-[#64748B]">$ progress --status</div>
                <div class="text-[#94A3B8] mt-1">Completed: {{ completedCount }}/{{ chapters.length }} chapters</div>
                <div class="text-[#94A3B8]">Progress: {{ courseProgress?.progress || 0 }}%</div>
                <div class="flex items-center gap-1 mt-1">
                  <span class="text-[#64748B]">[</span>
                  <div class="flex-1 h-1.5 bg-[#1E293B] rounded-full overflow-hidden">
                    <div class="h-full bg-[#0284C7] rounded-full" :style="{ width: (courseProgress?.progress || 0) + '%' }"></div>
                  </div>
                  <span class="text-[#64748B]">]</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Bottom Stats Bar -->
        <footer class="h-7 bg-[#F8FAFC] border-t border-[#E2E8F0] flex items-center px-4 text-[10px] font-mono text-[#64748B] flex-shrink-0">
          <span class="mr-6">chapters: {{ chapters.length }}</span>
          <span class="mr-6">completed: {{ completedCount }}</span>
          <span class="mr-6">progress: {{ courseProgress?.progress || 0 }}%</span>
          <span class="ml-auto">{{ currentChapter?.title || '—' }}</span>
        </footer>
      </div>
    </template>

    <!-- Course Completed Celebration (shared) -->
    <Teleport to="body">
      <div v-if="showCelebration" class="fixed inset-0 z-[1000] flex items-center justify-center p-4 bg-black/40 backdrop-blur-[4px]" @click.self="showCelebration = false">
        <div :class="modalBgClass" class="animate-[cardIn_0.25s_cubic-bezier(0.16,1,0.3,1)]">
          <div :class="celebIconBgClass" class="w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <Trophy class="w-8 h-8" :class="celebIconColorClass" :stroke-width="1.75" />
          </div>
          <h3 :class="celebTitleClass" class="text-center mb-2">恭喜完成课程！</h3>
          <p :class="celebDescClass" class="text-center mb-6">你已完成 {{ courseInfo?.title }} 的所有章节</p>
          <div class="flex gap-3 justify-center">
            <button :class="ghostBtnClass" @click="$router.push('/student/my-learning')">返回课程</button>
            <button :class="primaryBtnClass" @click="showCelebration = false">继续学习</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { courseApi } from '@/api/course'
import { learningApi } from '@/api/learning'
import { toast } from '@/composables/useToast'
import { ArrowLeft, ArrowRight, PlayCircle, CheckCircle, Check, Trophy } from 'lucide-vue-next'
import { useConfetti } from '@/composables/useConfetti'

const { celebrate, pop } = useConfetti()

const route = useRoute()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const courseId = computed(() => Number(route.params.id))

const loading = ref(false)
const courseInfo = ref<any>(null)
const chapters = ref<any[]>([])
const currentChapter = ref<any>(null)
const courseProgress = ref<any>(null)
const completedChapterIds = ref<Set<number>>(new Set())
const completingChapter = ref(false)
const completingCourse = ref(false)
const showCelebration = ref(false)

const completedCount = computed(() => completedChapterIds.value.size)
const allChaptersCompleted = computed(() => chapters.value.length > 0 && completedCount.value >= chapters.value.length)

/* ── Chapter Navigation ── */
const currentChapterIdx = computed(() => chapters.value.findIndex(c => c.id === currentChapter.value?.id))
const prevChapter = computed(() => currentChapterIdx.value > 0 ? chapters.value[currentChapterIdx.value - 1] : null)
const nextChapter = computed(() => currentChapterIdx.value < chapters.value.length - 1 ? chapters.value[currentChapterIdx.value + 1] : null)

function goPrevChapter() { if (prevChapter.value) currentChapter.value = prevChapter.value }
function goNextChapter() { if (nextChapter.value) currentChapter.value = nextChapter.value }

/* ── Warm: Encouragement Text ── */
const encourageText = computed(() => {
  const p = courseProgress.value?.progress || 0
  if (p >= 100) return '🎉 太棒了！你已经完成了全部内容！'
  if (p >= 80) return '🚀 快到终点了，加油！'
  if (p >= 50) return `💪 你已经完成了 ${p}%，太棒了！`
  if (p >= 20) return '🌟 学习进行中，继续保持！'
  return '📖 开始你的学习之旅吧！'
})

function isChapterCompleted(id: number) { return completedChapterIds.value.has(id) }
function selectChapter(ch: any) { currentChapter.value = ch }

async function completeCurrentChapter() {
  if (!currentChapter.value) return
  completingChapter.value = true
  try {
    await courseApi.completeChapter(currentChapter.value.id)
    completedChapterIds.value.add(currentChapter.value.id)
    const progress: any = await learningApi.getCourseProgress(courseId.value)
    courseProgress.value = progress
    pop()
    toast.success('章节已完成')
    const idx = chapters.value.findIndex(c => c.id === currentChapter.value.id)
    const next = chapters.value.slice(idx + 1).find(c => !isChapterCompleted(c.id))
    if (next) currentChapter.value = next
  } catch (e: any) { toast.error(e.message || '操作失败') }
  finally { completingChapter.value = false }
}

async function markCourseCompleted() {
  completingCourse.value = true
  try {
    await learningApi.markCompleted(courseId.value)
    const progress: any = await learningApi.getCourseProgress(courseId.value)
    courseProgress.value = progress
    showCelebration.value = true
    celebrate()
  } catch (e: any) { toast.error(e.message || '操作失败') }
  finally { completingCourse.value = false }
}

onMounted(async () => {
  loading.value = true
  try {
    const [info, chList, progress] = await Promise.all([
      courseApi.getById(courseId.value),
      courseApi.getChapters(courseId.value),
      learningApi.getCourseProgress(courseId.value).catch(() => null),
    ])
    courseInfo.value = info
    chapters.value = (chList as any) || []
    courseProgress.value = progress
    if (progress && (progress as any).completedChapterIds) {
      completedChapterIds.value = new Set((progress as any).completedChapterIds)
    }
    if (chapters.value.length) currentChapter.value = chapters.value[0]
  } catch { toast.error('加载课程失败') }
  finally { loading.value = false }
})

/* ── Celebration Modal (theme-aware) ── */
const modalBgClass = computed(() => ({
  light: 'bg-white rounded-xl p-8 w-full max-w-[400px] shadow-[0_25px_50px_rgba(0,0,0,0.15)]',
  dark: 'bg-[#1E1E1E] border border-[#333] rounded-xl p-8 w-full max-w-[400px] shadow-[0_25px_50px_rgba(0,0,0,0.4)]',
  warm: 'bg-white rounded-3xl p-8 w-full max-w-[400px] shadow-[0_25px_50px_rgba(0,0,0,0.1)]',
  pro: 'bg-white border border-[#E2E8F0] rounded-lg p-8 w-full max-w-[400px] shadow-[0_25px_50px_rgba(0,0,0,0.1)]',
}[theme.value] || ''))

const celebIconBgClass = computed(() => ({
  light: 'bg-emerald-50', dark: 'bg-emerald-500/10', warm: 'bg-emerald-50', pro: 'bg-emerald-50',
}[theme.value] || 'bg-emerald-50'))

const celebIconColorClass = computed(() => ({
  light: 'text-emerald-600', dark: 'text-emerald-400', warm: 'text-emerald-600', pro: 'text-emerald-600',
}[theme.value] || 'text-emerald-600'))

const celebTitleClass = computed(() => ({
  light: 'text-xl font-bold text-[#0A2540]', dark: 'text-xl font-bold text-[#CCCCCC]', warm: 'text-xl font-extrabold text-[#292524]', pro: 'text-sm font-semibold text-[#0F172A] uppercase tracking-wider',
}[theme.value] || ''))

const celebDescClass = computed(() => ({
  light: 'text-sm text-[#425466]', dark: 'text-sm text-[#999999]', warm: 'text-sm text-[#78716C]', pro: 'text-xs text-[#64748B]',
}[theme.value] || ''))

const ghostBtnClass = computed(() => ({
  light: 'px-3.5 py-1.5 rounded-md text-xs font-medium text-[#425466] border border-[#E3E8EE] cursor-pointer hover:bg-[#F6F9FC] transition',
  dark: 'px-3.5 py-1.5 rounded-md text-xs font-medium text-[#999999] border border-[#333] cursor-pointer hover:bg-[#252526] transition',
  warm: 'px-4 py-1.5 rounded-full text-xs font-bold text-[#78716C] border-2 border-[#E7E5E4] cursor-pointer hover:bg-[#FEF3C7]/50 transition',
  pro: 'px-3 py-1.5 rounded-md text-[11px] font-medium text-[#64748B] border border-[#E2E8F0] cursor-pointer hover:bg-[#F1F5F9] transition',
}[theme.value] || ''))

const primaryBtnClass = computed(() => ({
  light: 'px-3.5 py-1.5 rounded-md text-xs font-semibold text-white bg-[#635BFF] cursor-pointer hover:brightness-110 transition',
  dark: 'px-3.5 py-1.5 rounded-md text-xs font-medium text-white bg-[#818CF8] cursor-pointer hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] transition',
  warm: 'px-4 py-1.5 rounded-full text-xs font-bold text-white bg-[#58CC02] shadow-[0_3px_0_#46A302] cursor-pointer hover:brightness-105 transition',
  pro: 'px-3 py-1.5 rounded-md text-[11px] font-semibold text-white bg-[#0F172A] cursor-pointer hover:bg-[#1E293B] transition',
}[theme.value] || ''))
</script>

<style scoped>
@keyframes cardIn {
  from { opacity: 0; transform: scale(0.96) translateY(8px); }
  to { opacity: 1; transform: none; }
}
/* Stripe button shadow */
.stripe-btn-shadow {
  box-shadow:
    0 1px 3px rgba(0,0,0,0.08),
    0 0 0 1px rgba(0,0,0,0.06),
    inset 0 1px 0 rgba(255,255,255,0.1);
}
</style>
