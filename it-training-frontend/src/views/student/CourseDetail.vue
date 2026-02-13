<template>
  <div>
    <!-- Skeleton Loading -->
    <div v-if="!course" class="animate-pulse">
      <!-- Hero skeleton -->
      <div class="rounded-xl h-56 mb-6" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : 'bg-[#F0F3F7]'" />
      <!-- Stats row -->
      <div class="flex gap-4 mb-6">
        <div v-for="n in 3" :key="n" class="flex-1 h-16 rounded-lg" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : 'bg-[#F0F3F7]'" />
      </div>
      <!-- Content blocks -->
      <div class="grid lg:grid-cols-5 gap-4">
        <div class="lg:col-span-3 space-y-3">
          <div class="h-40 rounded-lg" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : 'bg-[#F0F3F7]'" />
          <div class="h-60 rounded-lg" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : 'bg-[#F0F3F7]'" />
        </div>
        <div class="lg:col-span-2">
          <div class="h-72 rounded-lg" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : 'bg-[#F0F3F7]'" />
        </div>
      </div>
    </div>

    <!-- ================================================================
         ☀️ LIGHT — Apple Product Page：全宽 Hero + 快捷统计 + 特性网格
         ================================================================ -->
    <template v-else-if="theme === 'light'">
      <div class="space-y-8">
        <!-- Hero (full-width gradient) -->
        <div class="relative -mx-6 -mt-6 px-6 pt-16 pb-10 bg-gradient-to-br flex flex-col items-center text-center"
             :class="getCategoryStyle(course.category).gradient">
          <!-- Back -->
          <button @click="$router.back()" class="absolute top-4 left-6 text-white/70 hover:text-white transition inline-flex items-center gap-1 text-sm cursor-pointer">
            <ArrowLeft class="w-4 h-4" :stroke-width="2" /> 返回
          </button>
          <!-- Icon -->
          <component :is="getCategoryStyle(course.category).icon" class="w-16 h-16 text-white/30 mb-4" :stroke-width="1" />
          <h1 class="text-3xl font-bold text-white mb-2 max-w-2xl">{{ course.title }}</h1>
          <p class="text-base text-white/80 max-w-xl mb-5 leading-relaxed">{{ course.description || '暂无描述' }}</p>
          <!-- Tags -->
          <div class="flex flex-wrap items-center justify-center gap-2 mb-6">
            <span v-if="course.category" class="px-3 py-1 rounded-full bg-white/20 backdrop-blur text-white text-xs font-medium">
              {{ course.category }}
            </span>
            <span v-if="course.level" class="px-3 py-1 rounded-full bg-white/20 backdrop-blur text-white text-xs font-medium">
              {{ course.level }}
            </span>
            <span class="px-3 py-1 rounded-full bg-white/20 backdrop-blur text-white text-xs font-medium flex items-center gap-1">
              <User class="w-3 h-3" :stroke-width="2" /> {{ course.instructor || '讲师' }}
            </span>
          </div>
          <button @click="enroll" :disabled="enrolling"
            class="px-8 py-3 rounded-full bg-white text-[#0A2540] text-sm font-semibold
                   shadow-[0_4px_14px_rgba(0,0,0,0.15)] hover:shadow-[0_6px_20px_rgba(0,0,0,0.2)]
                   transition-all inline-flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer">
            <GraduationCap class="w-4 h-4" :stroke-width="2" />
            {{ enrolling ? '报名中...' : '立即报名' }}
          </button>
        </div>

        <!-- Quick Stats (3 columns) -->
        <div class="flex items-center justify-center divide-x divide-[#E3E8EE] py-4">
          <div class="px-10 text-center">
            <div class="text-3xl font-bold text-[#0A2540]">{{ chapters.length }}</div>
            <div class="text-xs text-[#8898AA] mt-1">章节</div>
          </div>
          <div class="px-10 text-center">
            <div class="text-3xl font-bold text-[#0A2540]">{{ course.durationHours || '—' }}</div>
            <div class="text-xs text-[#8898AA] mt-1">学时</div>
          </div>
          <div class="px-10 text-center">
            <div class="text-3xl font-bold text-[#0A2540]">{{ course.enrollCount || 0 }}</div>
            <div class="text-xs text-[#8898AA] mt-1">学员</div>
          </div>
        </div>

        <!-- Feature Grid (2x2) -->
        <div class="grid grid-cols-2 gap-4">
          <div v-for="feat in courseFeatures" :key="feat.title"
               class="p-5 rounded-2xl transition-all hover:-translate-y-0.5"
               :class="feat.bg">
            <component :is="feat.icon" class="w-6 h-6 mb-2" :class="feat.iconColor" :stroke-width="1.75" />
            <h4 class="text-sm font-semibold text-[#0A2540] mb-1">{{ feat.title }}</h4>
            <p class="text-xs text-[#425466] leading-relaxed">{{ feat.desc }}</p>
          </div>
        </div>

        <!-- Chapters (Accordion style) -->
        <div class="bg-white rounded-xl border border-[#E3E8EE] overflow-hidden shadow-[0_2px_8px_rgba(0,0,0,0.04)]">
          <div class="px-5 py-4 border-b border-[#E3E8EE]">
            <h3 class="text-base font-semibold text-[#0A2540]">课程章节</h3>
          </div>
          <div v-if="chapters.length" class="divide-y divide-[#F0F3F7]">
            <div v-for="(ch, i) in chapters" :key="ch.id"
                 class="flex items-center gap-4 px-5 py-3.5 hover:bg-[#F6F9FC] transition-colors">
              <div class="w-8 h-8 rounded-lg bg-[#635BFF]/10 flex items-center justify-center text-xs font-semibold text-[#635BFF]">
                {{ i + 1 }}
              </div>
              <span class="flex-1 text-sm text-[#0A2540] font-medium truncate">{{ ch.title }}</span>
              <span v-if="ch.duration" class="text-xs text-[#8898AA] flex items-center gap-1">
                <Clock class="w-3 h-3" :stroke-width="2" /> {{ ch.duration }}min
              </span>
            </div>
          </div>
          <div v-else class="text-center py-8 text-sm text-[#8898AA]">暂无章节信息</div>
        </div>

        <!-- Similar Courses -->
        <div v-if="similarCourses.length">
          <div class="flex items-center gap-2 mb-4">
            <Sparkles class="w-5 h-5 text-[#635BFF]" :stroke-width="1.75" />
            <h3 class="text-base font-semibold text-[#0A2540]">相似课程</h3>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div v-for="sc in similarCourses" :key="sc.id || sc.courseId"
                 class="bg-white rounded-2xl border border-[#E3E8EE] overflow-hidden cursor-pointer
                        shadow-[0_2px_4px_rgba(0,0,0,0.04)] hover:shadow-[0_12px_40px_rgba(0,0,0,0.08)]
                        hover:-translate-y-1 transition-all"
                 @click="goDetail(sc)">
              <div class="h-24 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(sc.category).gradient">
                <component :is="getCategoryStyle(sc.category).icon" class="w-8 h-8 text-white/80" :stroke-width="1.25" />
              </div>
              <div class="p-3">
                <h4 class="text-sm font-semibold text-[#0A2540] truncate">{{ sc.title || sc.courseName }}</h4>
                <span v-if="sc.similarity" class="text-xs text-[#8898AA]">匹配 {{ Math.round((sc.similarity || 0) * 100) }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Steam Game Page：媒体区 + 双栏布局
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div>
        <!-- Back -->
        <button @click="$router.back()" class="inline-flex items-center gap-1.5 text-sm text-[#6B6B6E] hover:text-[#EDEDED] transition-colors mb-4 cursor-pointer">
          <ArrowLeft class="w-4 h-4" :stroke-width="1.75" /> 返回列表
        </button>

        <!-- Top Media Area -->
        <div class="relative rounded-xl overflow-hidden mb-5">
          <div class="h-56 bg-gradient-to-br flex items-center justify-center"
               :class="getCategoryStyle(course.category).gradient">
            <component :is="getCategoryStyle(course.category).icon" class="w-20 h-20 text-white/20" :stroke-width="1" />
          </div>
          <!-- Overlay -->
          <div class="absolute inset-0 bg-gradient-to-t from-[#08090A] via-[#08090A]/60 to-transparent" />
          <div class="absolute bottom-0 inset-x-0 p-6">
            <h1 class="text-2xl font-bold text-[#EDEDED] mb-1">{{ course.title }}</h1>
            <div class="flex items-center gap-3 text-sm text-gray-400">
              <span v-if="course.category" class="px-2 py-0.5 rounded-md bg-white/[0.06] text-xs font-medium">{{ course.category }}</span>
              <span v-if="course.level" class="px-2 py-0.5 rounded-md bg-white/[0.06] text-xs font-medium">{{ course.level }}</span>
            </div>
          </div>
          <!-- Rating Badge (top right) -->
          <div class="absolute top-4 right-4 text-center">
            <div class="text-3xl font-bold text-[#EDEDED] drop-shadow-[0_0_20px_rgba(129,140,248,0.5)]">
              {{ (course.rating || 4.5).toFixed(1) }}
            </div>
            <div class="flex items-center gap-0.5 justify-center mt-0.5">
              <Star v-for="n in 5" :key="n" class="w-3 h-3" :class="n <= Math.round(course.rating || 4.5) ? 'text-amber-400 fill-amber-400' : 'text-gray-600'" :stroke-width="0" />
            </div>
          </div>
        </div>

        <!-- 2-Column Layout -->
        <div class="grid lg:grid-cols-5 gap-5">
          <!-- Left (3 cols) — Description + Chapters -->
          <div class="lg:col-span-3 space-y-5">
            <!-- Description -->
            <div class="steam-card p-5">
              <h3 class="text-sm font-semibold text-[#EDEDED] mb-3">关于此课程</h3>
              <p class="text-sm text-gray-400 leading-relaxed">{{ course.description || '暂无描述' }}</p>
              <!-- Tags -->
              <div class="flex flex-wrap gap-2 mt-4">
                <span class="steam-tag">{{ course.category || '未分类' }}</span>
                <span class="steam-tag">{{ course.level || '全等级' }}</span>
                <span class="steam-tag">{{ chapters.length }} 章节</span>
                <span class="steam-tag">{{ course.durationHours || '—' }}h 学时</span>
              </div>
            </div>

            <!-- Chapters (Table style) -->
            <div class="steam-card overflow-hidden">
              <div class="px-5 py-3 border-b border-white/[0.04]">
                <span class="text-sm font-semibold text-[#EDEDED]">课程章节</span>
              </div>
              <div v-if="chapters.length">
                <div v-for="(ch, i) in chapters" :key="ch.id"
                     class="flex items-center gap-3 px-5 py-3 hover:bg-white/[0.02] transition-colors"
                     :class="i % 2 === 0 ? 'bg-transparent' : 'bg-white/[0.015]'">
                  <span class="text-xs font-mono text-gray-600 w-6">{{ String(i + 1).padStart(2, '0') }}</span>
                  <span class="flex-1 text-sm text-[#EDEDED] truncate">{{ ch.title }}</span>
                  <span v-if="ch.duration" class="text-xs text-gray-600 font-mono">{{ ch.duration }}min</span>
                </div>
              </div>
              <div v-else class="text-center py-8 text-sm text-gray-600">暂无章节信息</div>
            </div>
          </div>

          <!-- Right (2 cols) — Action Panel (Sticky) -->
          <div class="lg:col-span-2">
            <div class="steam-card p-5 lg:sticky lg:top-20 space-y-4">
              <button @click="enroll" :disabled="enrolling"
                class="w-full py-3 rounded-lg text-sm font-semibold bg-gradient-to-r from-[#818CF8] to-[#6366F1] text-white
                       hover:shadow-[0_0_24px_rgba(129,140,248,0.3)] transition-all
                       disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer inline-flex items-center justify-center gap-2">
                <GraduationCap class="w-4 h-4" :stroke-width="2" />
                {{ enrolling ? '报名中...' : '开始学习' }}
              </button>

              <div class="space-y-3 pt-2">
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">讲师</span>
                  <span class="text-[#EDEDED] font-medium">{{ course.instructor || '未知' }}</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">难度</span>
                  <span class="text-[#EDEDED] font-medium">{{ course.level || '全等级' }}</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">章节数</span>
                  <span class="text-[#EDEDED] font-mono">{{ chapters.length }}</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">学时</span>
                  <span class="text-[#EDEDED] font-mono">{{ course.durationHours || '—' }}h</span>
                </div>
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-500">学员数</span>
                  <span class="text-[#EDEDED] font-mono">{{ course.enrollCount || 0 }}</span>
                </div>
              </div>

              <!-- Category badge -->
              <div class="pt-3 border-t border-white/[0.04]">
                <span class="text-[10px] text-gray-600 uppercase tracking-wider">分类标签</span>
                <div class="flex flex-wrap gap-1.5 mt-2">
                  <span class="steam-tag">{{ course.category || '未分类' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Similar Courses -->
        <div v-if="similarCourses.length" class="mt-6">
          <div class="flex items-center gap-2 mb-4">
            <Sparkles class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
            <h3 class="text-sm font-semibold text-[#EDEDED]">更多推荐</h3>
          </div>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div v-for="sc in similarCourses" :key="sc.id || sc.courseId"
                 class="steam-card overflow-hidden cursor-pointer group"
                 @click="goDetail(sc)">
              <div class="h-20 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(sc.category).gradient">
                <component :is="getCategoryStyle(sc.category).icon" class="w-6 h-6 text-white/80" :stroke-width="1.25" />
              </div>
              <div class="p-3">
                <h4 class="text-sm font-medium text-[#EDEDED] truncate">{{ sc.title || sc.courseName }}</h4>
                <span v-if="sc.similarity" class="text-xs text-gray-600 font-mono">{{ Math.round((sc.similarity || 0) * 100) }}% match</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Medium Article：窄栏居中 + 阅读体验
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="max-w-[680px] mx-auto">
        <!-- Back -->
        <button @click="$router.back()" class="inline-flex items-center gap-1.5 text-sm text-[#78716C] hover:text-[#292524] transition-colors mb-6 cursor-pointer">
          <ArrowLeft class="w-4 h-4" :stroke-width="1.75" /> 返回
        </button>

        <!-- Title (Serif style) -->
        <h1 class="text-3xl font-extrabold text-[#292524] leading-tight mb-2" style="font-family: Georgia, 'Noto Serif SC', serif;">
          {{ course.title }}
        </h1>
        <p class="text-lg text-[#78716C] mb-5 leading-relaxed">{{ course.description || '暂无描述' }}</p>

        <!-- Meta Row -->
        <div class="flex items-center gap-3 mb-5">
          <div class="w-10 h-10 rounded-full bg-[#D97706]/10 flex items-center justify-center">
            <User class="w-5 h-5 text-[#D97706]" :stroke-width="1.75" />
          </div>
          <div>
            <div class="text-sm font-bold text-[#292524]">{{ course.instructor || '讲师' }}</div>
            <div class="text-xs text-[#A8A29E]">{{ course.durationHours || '—' }}h · {{ chapters.length }} 章节 · {{ course.enrollCount || 0 }} 人学习</div>
          </div>
          <div class="ml-auto flex items-center gap-2">
            <span v-if="course.category"
                  class="px-3 py-1 rounded-full text-xs font-bold"
                  :class="[getCategoryStyle(course.category).bgClass, getCategoryStyle(course.category).textClass]">
              {{ course.category }}
            </span>
            <span v-if="course.level" class="px-3 py-1 rounded-full bg-[#F5F5F4] text-[#78716C] text-xs font-bold">
              {{ course.level }}
            </span>
          </div>
        </div>

        <!-- Divider -->
        <div class="border-t border-[#E7E5E4] mb-8" />

        <!-- Description (Article body style) -->
        <div class="prose-warm mb-8">
          <div class="border-l-3 border-[#D97706] pl-4 py-1 mb-6">
            <p class="text-sm text-[#78716C] italic leading-relaxed">{{ course.description || '这门课程将带你从零开始，系统学习相关技能。' }}</p>
          </div>
        </div>

        <!-- Chapters (Ordered list style) -->
        <div class="mb-8">
          <h2 class="text-xl font-extrabold text-[#292524] mb-4" style="font-family: Georgia, 'Noto Serif SC', serif;">
            课程章节
          </h2>
          <div v-if="chapters.length" class="space-y-3">
            <div v-for="(ch, i) in chapters" :key="ch.id"
                 class="flex items-start gap-4 py-2">
              <span class="text-2xl font-bold text-[#D97706]/30 leading-none w-8 flex-shrink-0">{{ i + 1 }}</span>
              <div class="flex-1 pt-0.5">
                <h4 class="text-sm font-bold text-[#292524]">{{ ch.title }}</h4>
                <span v-if="ch.duration" class="text-xs text-[#A8A29E]">{{ ch.duration }} 分钟</span>
              </div>
            </div>
          </div>
          <p v-else class="text-sm text-[#A8A29E]">暂无章节信息</p>
        </div>

        <!-- Sticky Bottom CTA -->
        <div class="sticky bottom-0 py-4 bg-gradient-to-t from-[#FFFBF5] via-[#FFFBF5] to-transparent">
          <div class="flex items-center gap-3 p-4 bg-white rounded-2xl border-2 border-[#E5E7EB] shadow-[0_-4px_20px_rgba(0,0,0,0.06)]">
            <button @click="enroll" :disabled="enrolling"
              class="flex-1 py-3 rounded-2xl bg-[#D97706] text-white text-sm font-bold
                     shadow-[0_4px_0_#B45309] hover:brightness-105
                     active:translate-y-[2px] active:shadow-[0_2px_0_#B45309]
                     transition-all inline-flex items-center justify-center gap-2
                     disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer">
              <GraduationCap class="w-4 h-4" :stroke-width="2" />
              {{ enrolling ? '报名中...' : '开始学习' }}
            </button>
            <button class="w-12 h-12 rounded-2xl border-2 border-[#E7E5E4] flex items-center justify-center hover:bg-[#FEF3C7] transition-colors cursor-pointer">
              <Heart class="w-5 h-5 text-[#D97706]" :stroke-width="2" />
            </button>
            <button class="w-12 h-12 rounded-2xl border-2 border-[#E7E5E4] flex items-center justify-center hover:bg-[#FEF3C7] transition-colors cursor-pointer">
              <Share2 class="w-5 h-5 text-[#78716C]" :stroke-width="2" />
            </button>
          </div>
        </div>

        <!-- Similar Courses -->
        <div v-if="similarCourses.length" class="mt-8 pb-4">
          <h2 class="text-xl font-extrabold text-[#292524] mb-4" style="font-family: Georgia, 'Noto Serif SC', serif;">
            📚 相关推荐
          </h2>
          <div class="space-y-3">
            <div v-for="sc in similarCourses" :key="sc.id || sc.courseId"
                 class="flex items-center gap-4 p-3 rounded-xl border border-[#E7E5E4] hover:bg-[#FFF8ED] transition-colors cursor-pointer"
                 @click="goDetail(sc)">
              <div class="w-12 h-12 rounded-xl bg-gradient-to-br flex items-center justify-center flex-shrink-0"
                   :class="getCategoryStyle(sc.category).gradient">
                <component :is="getCategoryStyle(sc.category).icon" class="w-5 h-5 text-white/80" :stroke-width="1.5" />
              </div>
              <div class="flex-1 min-w-0">
                <h4 class="text-sm font-bold text-[#292524] truncate">{{ sc.title || sc.courseName }}</h4>
                <span v-if="sc.category" class="text-xs" :class="getCategoryStyle(sc.category).textClass">{{ sc.category }}</span>
              </div>
              <span v-if="sc.similarity" class="text-xs text-[#A8A29E] flex-shrink-0">{{ Math.round((sc.similarity || 0) * 100) }}%</span>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — npm Package Page：包名 + 徽章 + 双栏 README
         ================================================================ -->
    <template v-else>
      <div>
        <!-- Back -->
        <button @click="$router.back()" class="inline-flex items-center gap-1 text-xs text-[#64748B] hover:text-[#0F172A] transition-colors cursor-pointer font-mono mb-4">
          <ArrowLeft class="w-3.5 h-3.5" :stroke-width="1.75" /> back
        </button>

        <!-- Package Title Row -->
        <div class="flex items-start justify-between mb-3">
          <div>
            <h1 class="text-xl font-semibold text-[#0F172A] font-mono tracking-tight">
              @itts/{{ (course.category || 'course').toLowerCase().replace(/\s+/g, '-') }}
            </h1>
            <span class="text-xs text-[#64748B] font-mono">{{ course.title }}</span>
          </div>
          <div class="text-right">
            <code class="text-xs text-[#64748B] font-mono">v1.0.0</code>
          </div>
        </div>

        <!-- Badge Row -->
        <div class="flex flex-wrap gap-1.5 mb-5">
          <span v-if="course.level" class="npm-badge bg-[#0284C7]/10 text-[#0284C7]">{{ course.level }}</span>
          <span v-if="course.category" class="npm-badge bg-emerald-500/10 text-emerald-600">{{ course.category }}</span>
          <span class="npm-badge bg-amber-500/10 text-amber-600">★ {{ (course.rating || 4.5).toFixed(1) }}</span>
          <span class="npm-badge bg-[#E2E8F0] text-[#64748B]">{{ chapters.length }} chapters</span>
          <span class="npm-badge bg-[#E2E8F0] text-[#64748B]">{{ course.durationHours || '—' }}h</span>
          <span class="npm-badge bg-[#E2E8F0] text-[#64748B]">{{ course.enrollCount || 0 }} users</span>
        </div>

        <!-- 2-Column Layout -->
        <div class="grid lg:grid-cols-10 gap-6">
          <!-- Left (7 cols) — README -->
          <div class="lg:col-span-7 space-y-5">
            <!-- Install Command -->
            <div class="npm-code-block">
              <div class="flex items-center justify-between">
                <code class="text-sm text-emerald-400">$ enroll --course "{{ course.title }}"</code>
                <button class="text-xs text-[#64748B] hover:text-[#0F172A] transition cursor-pointer font-mono">copy</button>
              </div>
            </div>

            <!-- README Content -->
            <div class="npm-card">
              <h2 class="npm-heading">## 课程简介</h2>
              <p class="text-sm text-[#64748B] leading-relaxed mt-2 mb-6">{{ course.description || '暂无描述' }}</p>

              <h2 class="npm-heading">## 章节目录 (API)</h2>
              <div v-if="chapters.length" class="mt-3 space-y-0">
                <div v-for="(ch, i) in chapters" :key="ch.id"
                     class="flex items-center gap-3 py-2 border-b border-[#F1F5F9] last:border-0">
                  <code class="text-xs font-mono text-[#64748B] w-6 text-right">{{ i + 1 }}.</code>
                  <span class="text-sm text-[#0F172A] flex-1 truncate">{{ ch.title }}</span>
                  <code v-if="ch.duration" class="text-xs font-mono text-[#94A3B8]">{{ ch.duration }}min</code>
                </div>
              </div>
              <p v-else class="text-xs text-[#94A3B8] mt-2 font-mono">// No chapters available</p>
            </div>

            <!-- Similar (Dependencies) -->
            <div v-if="similarCourses.length" class="npm-card">
              <h2 class="npm-heading">## Related Packages</h2>
              <div class="mt-3 space-y-1">
                <div v-for="sc in similarCourses" :key="sc.id || sc.courseId"
                     class="flex items-center gap-3 py-1.5 hover:bg-[#F8FAFC] -mx-4 px-4 rounded cursor-pointer transition-colors"
                     @click="goDetail(sc)">
                  <code class="text-xs text-[#0284C7] font-mono">{{ sc.title || sc.courseName }}</code>
                  <span v-if="sc.similarity" class="text-[10px] text-[#94A3B8] font-mono ml-auto">~{{ Math.round((sc.similarity || 0) * 100) }}%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Right (3 cols) — Metadata Sidebar -->
          <div class="lg:col-span-3">
            <div class="npm-card lg:sticky lg:top-20 space-y-4">
              <!-- Enroll -->
              <button @click="enroll" :disabled="enrolling"
                class="w-full py-2 rounded-md text-xs font-semibold bg-[#0F172A] text-white
                       hover:bg-[#1E293B] transition-all cursor-pointer
                       disabled:opacity-50 disabled:cursor-not-allowed inline-flex items-center justify-center gap-1.5">
                <GraduationCap class="w-3.5 h-3.5" :stroke-width="2" />
                {{ enrolling ? 'enrolling...' : 'Enroll' }}
              </button>

              <div class="space-y-3 text-xs">
                <div class="flex items-center justify-between">
                  <span class="text-[#94A3B8] font-mono">instructor</span>
                  <span class="text-[#0F172A] font-medium">{{ course.instructor || 'unknown' }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-[#94A3B8] font-mono">difficulty</span>
                  <code class="text-[#0284C7] bg-[#0284C7]/5 px-1.5 py-0.5 rounded">{{ course.level || 'all' }}</code>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-[#94A3B8] font-mono">chapters</span>
                  <span class="text-[#0F172A] font-mono">{{ chapters.length }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-[#94A3B8] font-mono">duration</span>
                  <span class="text-[#0F172A] font-mono">{{ course.durationHours || '—' }}h</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-[#94A3B8] font-mono">weekly users</span>
                  <span class="text-[#0F172A] font-mono">{{ course.enrollCount || 0 }}</span>
                </div>
              </div>

              <div class="pt-3 border-t border-[#E2E8F0]">
                <span class="text-[10px] text-[#94A3B8] font-mono uppercase tracking-wider">category</span>
                <div class="mt-1.5">
                  <span class="npm-badge bg-[#E2E8F0] text-[#64748B]">{{ course.category || '未分类' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useHead } from '@unhead/vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { courseApi } from '@/api/course'
import { enrollmentApi } from '@/api/enrollment'
import { recommendApi } from '@/api/recommend'
import { getCategoryStyle } from '@/utils/categoryColors'
import { toast } from '@/composables/useToast'
import {
  ArrowLeft, User, Users, GraduationCap, ListOrdered, Sparkles,
  Clock, Star, Heart, Share2,
  Target, TrendingUp, BookOpen, Zap,
} from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const courseId = computed(() => Number(route.params.id))

const course = ref<any>(null)

useHead({ title: computed(() => course.value ? `${course.value.title} — IT 智能培训系统` : '课程详情 — IT 智能培训系统') })
const chapters = ref<any[]>([])
const similarCourses = ref<any[]>([])
const similarLoading = ref(false)
const enrolling = ref(false)

/* Light: Feature grid data */
const courseFeatures = [
  { icon: Sparkles, title: 'AI 智能推荐', desc: '根据你的学习进度智能调整内容', bg: 'bg-blue-50', iconColor: 'text-blue-500' },
  { icon: Target, title: '目标驱动', desc: '设定学习目标，跟踪完成进度', bg: 'bg-purple-50', iconColor: 'text-purple-500' },
  { icon: TrendingUp, title: '持续成长', desc: '数据化追踪你的学习曲线', bg: 'bg-emerald-50', iconColor: 'text-emerald-500' },
  { icon: Zap, title: '高效学习', desc: '精心设计的章节结构，循序渐进', bg: 'bg-amber-50', iconColor: 'text-amber-500' },
]

async function enroll() {
  enrolling.value = true
  try {
    await enrollmentApi.enroll({ courseId: courseId.value })
    toast.success('报名成功！')
  } catch (e: any) {
    toast.error(e.message || '报名失败')
  } finally {
    enrolling.value = false
  }
}

function goDetail(sc: any) {
  const id = sc.id || sc.courseId
  if (id) router.push(`/student/courses/${id}`)
}

onMounted(async () => {
  const id = courseId.value
  try {
    const [info, chList] = await Promise.all([
      courseApi.getById(id),
      courseApi.getChapters(id).catch(() => []),
    ])
    course.value = info
    chapters.value = (chList as any) || []
  } catch {
    toast.error('加载课程失败')
  }
  similarLoading.value = true
  try {
    const res: any = await recommendApi.similarCourses(id)
    similarCourses.value = Array.isArray(res) ? res : (res?.records || res?.data || [])
  } catch {
    similarCourses.value = []
  } finally {
    similarLoading.value = false
  }
})
</script>

<style scoped>
/* ======== DARK — Steam ======== */
.steam-card {
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.steam-card:hover {
  border-color: rgba(129,140,248,0.15);
  box-shadow: 0 0 20px rgba(129,140,248,0.05);
}
.steam-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 6px;
  background: rgba(255,255,255,0.04);
  color: rgba(255,255,255,0.5);
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, monospace;
}

/* ======== WARM — Medium ======== */
.border-l-3 {
  border-left-width: 3px;
}

/* ======== PRO — npm ======== */
.npm-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, monospace;
  font-weight: 600;
}
.npm-card {
  padding: 20px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
}
.npm-code-block {
  padding: 14px 16px;
  background: #0F172A;
  border-radius: 6px;
  font-family: ui-monospace, SFMono-Regular, monospace;
}
.npm-heading {
  font-size: 15px;
  font-weight: 700;
  color: #0F172A;
  font-family: ui-monospace, SFMono-Regular, monospace;
}
</style>
