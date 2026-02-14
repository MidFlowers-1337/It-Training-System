<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — ChatGPT：全宽消息 + 交替背景 + 居中内容区
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="flex flex-col h-[calc(100vh-8rem)]">
        <!-- Top Bar -->
        <div class="flex items-center justify-between px-4 py-3 border-b border-[#E3E8EE] flex-shrink-0">
          <div class="flex items-center gap-3">
            <h1 class="text-sm font-semibold text-[#0A2540]">AI 学习助手</h1>
            <select v-model="chatMode" class="px-2.5 py-1 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-[#0A2540] text-xs outline-none cursor-pointer focus:border-[#635BFF]">
              <option v-for="m in modes" :key="m.key" :value="m.key">{{ m.label }}</option>
            </select>
          </div>
          <button v-if="messages.length" @click="clearMessages"
            class="px-3 py-1.5 rounded-lg text-xs text-[#8898AA] hover:text-[#0A2540] hover:bg-[#F6F9FC] transition-colors cursor-pointer inline-flex items-center gap-1.5">
            <Plus class="w-3.5 h-3.5 rotate-45" :stroke-width="2" /> 新对话
          </button>
        </div>

        <!-- Messages Area -->
        <div ref="messagesRef" class="flex-1 overflow-y-auto chatgpt-scroll">
          <!-- Welcome Screen -->
          <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full px-4">
            <div class="w-14 h-14 rounded-full bg-gradient-to-br from-[#10A37F] to-[#0D8A6A] flex items-center justify-center mb-6">
              <Sparkles class="w-7 h-7 text-white" :stroke-width="1.75" />
            </div>
            <h2 class="text-xl font-semibold text-[#0A2540] mb-1">今天想学什么？</h2>
            <p class="text-sm text-[#8898AA] mb-8">我可以帮你解答编程问题、制定学习计划、解释概念</p>
            <div class="grid grid-cols-2 gap-3 max-w-lg w-full">
              <button v-for="q in quickQuestions" :key="q" @click="sendQuick(q)"
                class="p-3.5 rounded-xl border border-[#E3E8EE] text-left hover:bg-[#F6F9FC] transition-colors cursor-pointer group">
                <p class="text-sm text-[#0A2540] group-hover:text-[#10A37F] transition-colors">{{ q }}</p>
              </button>
            </div>
          </div>

          <!-- Messages -->
          <template v-else>
            <div v-for="(msg, i) in messages" :key="i"
              :class="['py-6 px-4', msg.role === 'user' ? 'bg-white' : 'bg-[#F7F7F8]']">
              <div class="max-w-2xl mx-auto flex gap-4">
                <!-- Avatar -->
                <div :class="['w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0',
                  msg.role === 'user' ? 'bg-[#635BFF]' : 'bg-[#10A37F]']">
                  <User v-if="msg.role === 'user'" class="w-4 h-4 text-white" :stroke-width="2" />
                  <Sparkles v-else class="w-4 h-4 text-white" :stroke-width="2" />
                </div>
                <!-- Content -->
                <div class="flex-1 min-w-0 pt-0.5">
                  <div class="text-[13px] font-semibold text-[#0A2540] mb-1">{{ msg.role === 'user' ? '你' : 'AI 助手' }}</div>
                  <div v-if="msg.role === 'user'" class="text-sm text-[#374151] leading-relaxed whitespace-pre-wrap">{{ msg.content }}</div>
                  <div v-else class="prose prose-sm max-w-none prose-headings:text-[#0A2540] prose-p:text-[#374151] prose-code:text-[#635BFF] prose-code:bg-[#F6F9FC] prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded prose-code:before:hidden prose-code:after:hidden prose-pre:bg-[#0A2540] prose-pre:rounded-lg prose-a:text-[#635BFF]" v-html="renderMd(msg.content)" />
                </div>
              </div>
            </div>

            <!-- Typing -->
            <div v-if="loading" class="py-6 px-4 bg-[#F7F7F8]">
              <div class="max-w-2xl mx-auto flex gap-4">
                <div class="w-8 h-8 rounded-full bg-[#10A37F] flex items-center justify-center flex-shrink-0">
                  <Sparkles class="w-4 h-4 text-white" :stroke-width="2" />
                </div>
                <div class="flex items-center gap-1 pt-2">
                  <span class="chatgpt-dot"></span>
                  <span class="chatgpt-dot" style="animation-delay:0.2s"></span>
                  <span class="chatgpt-dot" style="animation-delay:0.4s"></span>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- Input Bar (ChatGPT style: centered, shadow, rounded) -->
        <div class="flex-shrink-0 px-4 pb-4 pt-3">
          <div class="max-w-2xl mx-auto">
            <div class="chatgpt-input-box flex items-end gap-2">
              <textarea
                ref="inputRef"
                v-model="input"
                :placeholder="inputPlaceholder"
                rows="1"
                class="flex-1 bg-transparent text-sm text-[#0A2540] outline-none resize-none placeholder-[#8898AA] max-h-32 leading-relaxed"
                @keydown.enter.exact.prevent="send"
                :disabled="loading"
              ></textarea>
              <button @click="send" :disabled="loading || !input.trim()"
                :class="['w-8 h-8 rounded-lg flex items-center justify-center transition-colors flex-shrink-0 cursor-pointer',
                  input.trim() ? 'bg-[#0A2540] text-white' : 'bg-[#E3E8EE] text-[#8898AA]']">
                <ArrowUp class="w-4 h-4" :stroke-width="2.5" />
              </button>
            </div>
            <p class="text-[10px] text-[#8898AA]/60 mt-2 text-center">AI 回复仅供参考，请以官方资料为准</p>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Claude Dark：深色主体 + amber 强调 + 极简分隔线
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="flex flex-col h-[calc(100vh-8rem)]">
        <!-- Header -->
        <div class="flex items-center justify-between px-5 py-3 flex-shrink-0">
          <div class="flex items-center gap-3">
            <div class="w-7 h-7 rounded-lg bg-gradient-to-br from-[#D97706] to-[#B45309] flex items-center justify-center">
              <Sparkles class="w-4 h-4 text-white" :stroke-width="2" />
            </div>
            <span class="text-sm font-semibold text-[#EDEDED]">AI 学习助手</span>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ modeLabel }}</span>
          </div>
          <div class="flex items-center gap-2">
            <select v-model="chatMode" class="px-2.5 py-1.5 rounded-lg bg-white/[0.04] border border-white/[0.06] text-xs text-[#EDEDED] outline-none cursor-pointer">
              <option v-for="m in modes" :key="m.key" :value="m.key" class="bg-[#111113]">{{ m.label }}</option>
            </select>
            <button v-if="messages.length" @click="clearMessages"
              class="px-3 py-1.5 rounded-lg text-xs text-[#6B6B6E] hover:text-[#EDEDED] hover:bg-white/[0.04] transition-colors cursor-pointer">
              清空
            </button>
          </div>
        </div>

        <!-- Messages -->
        <div ref="messagesRef" class="flex-1 overflow-y-auto px-5 claude-scroll">
          <!-- Welcome -->
          <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full">
            <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-[#D97706]/20 to-[#B45309]/10 flex items-center justify-center mb-5">
              <Sparkles class="w-8 h-8 text-[#D97706]" :stroke-width="1.5" />
            </div>
            <h2 class="text-lg font-semibold text-[#EDEDED] mb-1">Good to see you</h2>
            <p class="text-sm text-[#6B6B6E] mb-8">How can I help with your learning today?</p>
            <div class="flex flex-wrap justify-center gap-2 max-w-md">
              <button v-for="q in quickQuestions" :key="q" @click="sendQuick(q)"
                class="px-4 py-2 rounded-full bg-white/[0.04] border border-white/[0.06] text-sm text-[#CCCCCC] hover:bg-white/[0.08] hover:border-[#D97706]/30 hover:text-[#D97706] transition-all cursor-pointer">
                {{ q }}
              </button>
            </div>
          </div>

          <!-- Messages -->
          <template v-else>
            <div class="max-w-2xl mx-auto space-y-0">
              <div v-for="(msg, i) in messages" :key="i"
                :class="['py-5', i > 0 ? 'border-t border-white/[0.04]' : '']">
                <!-- Role Label -->
                <div class="flex items-center gap-2 mb-2">
                  <div :class="['w-5 h-5 rounded flex items-center justify-center',
                    msg.role === 'user' ? 'bg-[#818CF8]/15' : 'bg-[#D97706]/15']">
                    <User v-if="msg.role === 'user'" class="w-3 h-3 text-[#818CF8]" :stroke-width="2" />
                    <Sparkles v-else class="w-3 h-3 text-[#D97706]" :stroke-width="2" />
                  </div>
                  <span :class="['text-xs font-semibold', msg.role === 'user' ? 'text-[#818CF8]' : 'text-[#D97706]']">
                    {{ msg.role === 'user' ? 'Human' : 'Assistant' }}
                  </span>
                  <span class="text-[10px] text-[#6B6B6E] font-mono ml-auto">{{ formatTime(msg.time) }}</span>
                </div>
                <!-- Content -->
                <div v-if="msg.role === 'user'" class="text-sm text-[#CCCCCC] leading-relaxed whitespace-pre-wrap pl-7">{{ msg.content }}</div>
                <div v-else class="prose prose-sm prose-invert max-w-none pl-7 prose-headings:text-[#EDEDED] prose-p:text-[#CCCCCC] prose-code:text-[#D97706] prose-code:bg-white/[0.06] prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded prose-code:before:hidden prose-code:after:hidden prose-pre:bg-[#0D0D0F] prose-pre:border prose-pre:border-white/[0.06] prose-pre:rounded-lg prose-a:text-[#D97706]" v-html="renderMd(msg.content)" />
              </div>

              <!-- Typing -->
              <div v-if="loading" class="py-5 border-t border-white/[0.04]">
                <div class="flex items-center gap-2 mb-2">
                  <div class="w-5 h-5 rounded bg-[#D97706]/15 flex items-center justify-center">
                    <Sparkles class="w-3 h-3 text-[#D97706]" :stroke-width="2" />
                  </div>
                  <span class="text-xs font-semibold text-[#D97706]">Assistant</span>
                </div>
                <div class="pl-7 flex items-center gap-1.5">
                  <div class="w-1.5 h-1.5 rounded-full bg-[#D97706] animate-pulse"></div>
                  <span class="text-xs text-[#6B6B6E]">Thinking...</span>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- Input (Claude style: pill with gradient border on focus) -->
        <div class="flex-shrink-0 px-5 pb-4 pt-3">
          <div class="max-w-2xl mx-auto">
            <div class="claude-input-box">
              <textarea
                ref="inputRef"
                v-model="input"
                :placeholder="inputPlaceholder"
                rows="1"
                class="flex-1 bg-transparent text-sm text-[#EDEDED] outline-none resize-none placeholder-[#6B6B6E] max-h-32 leading-relaxed"
                @keydown.enter.exact.prevent="send"
                :disabled="loading"
              ></textarea>
              <button @click="send" :disabled="loading || !input.trim()"
                :class="['px-4 py-1.5 rounded-full text-xs font-medium transition-all cursor-pointer flex-shrink-0 inline-flex items-center gap-1.5',
                  input.trim() ? 'bg-[#D97706] text-white hover:bg-[#B45309]' : 'bg-white/[0.04] text-[#6B6B6E]']">
                <ArrowUp class="w-3.5 h-3.5" :stroke-width="2" />
              </button>
            </div>
            <p class="text-[10px] text-[#6B6B6E]/50 mt-2 text-center font-mono">AI 回复仅供学习参考</p>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion AI：文档内嵌感 + 命令面板输入 + 块引用回复
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="flex flex-col h-[calc(100vh-8rem)]">
        <!-- Header -->
        <div class="flex items-center justify-between mb-4 flex-shrink-0">
          <div class="flex items-center gap-2">
            <span class="text-lg">🤖</span>
            <h1 class="text-lg font-extrabold text-[#292524]">AI 学习助手</h1>
          </div>
          <div class="flex items-center gap-2">
            <div class="flex gap-1">
              <button v-for="m in modes" :key="m.key" @click="chatMode = m.key"
                :class="['px-3 py-1.5 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
                  chatMode === m.key ? 'bg-[#292524] text-white border-[#292524]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:bg-[#F5F5F4]']">
                {{ m.label }}
              </button>
            </div>
            <button v-if="messages.length" @click="clearMessages"
              class="px-3 py-1.5 rounded-full text-xs font-bold text-[#78716C] border-2 border-[#E7E5E4] hover:bg-[#F5F5F4] cursor-pointer transition-all">
              🗑 清空
            </button>
          </div>
        </div>

        <!-- Messages -->
        <div ref="messagesRef" class="flex-1 overflow-y-auto notion-scroll">
          <!-- Welcome -->
          <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full">
            <div class="w-20 h-20 rounded-3xl bg-[#FFC800]/10 flex items-center justify-center mb-4">
              <span class="text-4xl">✨</span>
            </div>
            <h2 class="text-xl font-extrabold text-[#292524] mb-1">嗨，有什么能帮到你的？</h2>
            <p class="text-sm text-[#78716C] mb-8">选一个话题开始，或者直接输入你的问题</p>
            <div class="grid grid-cols-1 gap-2.5 max-w-sm w-full">
              <button v-for="(q, i) in quickQuestions" :key="q" @click="sendQuick(q)"
                class="flex items-center gap-3 p-4 rounded-2xl bg-white border-2 border-[#E7E5E4] text-left hover:border-[#D97706] hover:shadow-[0_2px_0_#E7E5E4] transition-all cursor-pointer group">
                <span class="text-lg">{{ ['📚', '💻', '🔀', '🚀'][i] }}</span>
                <span class="text-sm font-bold text-[#292524] group-hover:text-[#D97706] transition-colors">{{ q }}</span>
              </button>
            </div>
          </div>

          <!-- Messages -->
          <template v-else>
            <div class="space-y-4 max-w-2xl mx-auto">
              <div v-for="(msg, i) in messages" :key="i">
                <!-- User message: simple right-aligned bubble -->
                <div v-if="msg.role === 'user'" class="flex justify-end">
                  <div class="notion-user-bubble">
                    <div class="text-sm text-[#292524] whitespace-pre-wrap">{{ msg.content }}</div>
                    <div class="text-[10px] text-[#A8A29E] mt-1.5 text-right">{{ formatTime(msg.time) }}</div>
                  </div>
                </div>
                <!-- AI message: block quote style with left border -->
                <div v-else class="notion-ai-block">
                  <div class="flex items-center gap-2 mb-2">
                    <span class="text-sm">✨</span>
                    <span class="text-xs font-extrabold text-[#D97706]">AI 助手</span>
                    <span class="text-[10px] text-[#A8A29E]">{{ formatTime(msg.time) }}</span>
                  </div>
                  <div v-if="msg.role === 'user'" class="text-sm text-[#292524] leading-relaxed whitespace-pre-wrap">{{ msg.content }}</div>
                  <div v-else class="prose prose-sm max-w-none prose-headings:text-[#292524] prose-p:text-[#292524] prose-code:text-[#D97706] prose-code:bg-[#FFF3D6] prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded-lg prose-code:before:hidden prose-code:after:hidden prose-pre:bg-[#292524] prose-pre:rounded-2xl prose-a:text-[#D97706] prose-a:font-bold" v-html="renderMd(msg.content)" />
                </div>
              </div>

              <!-- Typing -->
              <div v-if="loading" class="notion-ai-block">
                <div class="flex items-center gap-2">
                  <span class="text-sm">✨</span>
                  <span class="text-xs font-extrabold text-[#D97706]">AI 正在思考</span>
                  <span class="notion-typing-dots">
                    <span></span><span></span><span></span>
                  </span>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- Input (Notion command palette style) -->
        <div class="flex-shrink-0 pt-3">
          <div class="max-w-2xl mx-auto">
            <div class="notion-input-box">
              <span class="text-base mr-2">✨</span>
              <textarea
                ref="inputRef"
                v-model="input"
                :placeholder="inputPlaceholder"
                rows="1"
                class="flex-1 bg-transparent text-sm text-[#292524] outline-none resize-none placeholder-[#A8A29E] max-h-32 leading-relaxed"
                @keydown.enter.exact.prevent="send"
                :disabled="loading"
              ></textarea>
              <button @click="send" :disabled="loading || !input.trim()"
                :class="['px-5 py-2 rounded-full text-xs font-extrabold transition-all cursor-pointer flex-shrink-0',
                  input.trim()
                    ? 'bg-[#292524] text-white shadow-[0_3px_0_#1C1917] hover:brightness-110 active:translate-y-[2px] active:shadow-[0_1px_0_#1C1917]'
                    : 'bg-[#E7E5E4] text-[#A8A29E]']">
                发送
              </button>
            </div>
            <p class="text-[10px] text-[#A8A29E]/60 mt-2 text-center">AI 回复仅供参考 · 按 Enter 发送</p>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Terminal：monospace + 绿字 + $ 提示符 + 命令行美学
         ================================================================ -->
    <template v-else>
      <div class="flex flex-col h-[calc(100vh-8rem)]">
        <!-- Status Bar -->
        <div class="flex items-center justify-between px-4 py-2 bg-[#F8FAFC] border-b border-[#E2E8F0] flex-shrink-0">
          <div class="flex items-center gap-3">
            <div class="flex gap-1">
              <div class="w-2.5 h-2.5 rounded-full bg-[#22C55E]"></div>
              <div class="w-2.5 h-2.5 rounded-full bg-[#EAB308]"></div>
              <div class="w-2.5 h-2.5 rounded-full bg-[#EF4444]"></div>
            </div>
            <code class="text-[10px] font-mono text-[#0F172A] font-semibold">ai-assistant — {{ modeLabel }}</code>
          </div>
          <div class="flex items-center gap-3">
            <select v-model="chatMode" class="px-2 py-1 rounded border border-[#E2E8F0] bg-white text-[#0F172A] text-[10px] font-mono outline-none cursor-pointer">
              <option v-for="m in modes" :key="m.key" :value="m.key">{{ m.labelEn }}</option>
            </select>
            <button v-if="messages.length" @click="clearMessages"
              class="text-[10px] font-mono text-[#94A3B8] hover:text-[#0F172A] cursor-pointer transition-colors">
              [clear]
            </button>
            <code class="text-[10px] font-mono text-[#94A3B8]">{{ messages.length }} msgs</code>
          </div>
        </div>

        <!-- Terminal Output -->
        <div ref="messagesRef" class="flex-1 overflow-y-auto bg-[#0F172A] p-4 terminal-scroll">
          <!-- Welcome (ASCII art style) -->
          <div v-if="messages.length === 0" class="font-mono">
            <pre class="text-[#22C55E] text-xs leading-tight mb-4">
  ___  ____   __   ____  ____  ____  ____  ____
 / __)(  _ \ / _\ (  _ \(_  _)(  _ \(  __)/ ___)
( (__  ) __//    \ )   /  )(   )   / ) _) \___ \
 \___)(__)  \_/\_/(__\_) (__) (__\_)(____)(____/</pre>
            <p class="text-xs text-[#94A3B8] mb-1"><span class="text-[#22C55E]">$</span> ai-assistant --version 2.0.0</p>
            <p class="text-xs text-[#94A3B8] mb-1"><span class="text-[#22C55E]">$</span> echo "Welcome to AI Learning Assistant"</p>
            <p class="text-xs text-[#E2E8F0] mb-4">Welcome to AI Learning Assistant</p>
            <p class="text-xs text-[#94A3B8] mb-3"><span class="text-[#22C55E]">$</span> help --quick-start</p>
            <div class="space-y-1.5 mb-4">
              <button v-for="(q, i) in quickQuestions" :key="q" @click="sendQuick(q)"
                class="block text-left w-full cursor-pointer group">
                <code class="text-xs">
                  <span class="text-[#94A3B8]">  [{{ i + 1 }}]</span>
                  <span class="text-[#E2E8F0] group-hover:text-[#22C55E] transition-colors"> {{ q }}</span>
                </code>
              </button>
            </div>
            <p class="text-xs text-[#94A3B8]"><span class="text-[#22C55E]">$</span> <span class="terminal-cursor">_</span></p>
          </div>

          <!-- Messages -->
          <template v-else>
            <div class="space-y-3 font-mono">
              <div v-for="(msg, i) in messages" :key="i">
                <!-- User: command style -->
                <div v-if="msg.role === 'user'">
                  <p class="text-xs text-[#94A3B8] mb-0.5">
                    <span class="text-[#22C55E]">$</span>
                    <span class="text-[#E2E8F0] ml-1">ask</span>
                    <span class="text-[#94A3B8] ml-1">"{{ msg.content }}"</span>
                  </p>
                </div>
                <!-- AI: stdout style -->
                <div v-else>
                  <div class="text-xs text-[#475569] mb-1"># response @ {{ formatTimeMono(msg.time) }}</div>
                  <div class="terminal-response">
                    <div v-if="msg.role === 'user'" class="text-xs text-[#E2E8F0] whitespace-pre-wrap leading-relaxed">{{ msg.content }}</div>
                    <div v-else class="prose prose-sm prose-invert max-w-none font-mono prose-headings:text-[#E2E8F0] prose-headings:font-mono prose-p:text-[#E2E8F0] prose-code:text-[#22C55E] prose-code:bg-[#1E293B] prose-code:px-1 prose-code:py-0.5 prose-code:rounded prose-code:before:hidden prose-code:after:hidden prose-pre:bg-[#1E293B] prose-pre:border prose-pre:border-[#334155] prose-pre:rounded prose-a:text-[#0284C7]" v-html="renderMd(msg.content)" />
                  </div>
                </div>
              </div>

              <!-- Typing: processing animation -->
              <div v-if="loading" class="text-xs">
                <span class="text-[#22C55E]">$</span>
                <span class="text-[#EAB308] ml-1">processing</span>
                <span class="terminal-loading"></span>
              </div>
            </div>
          </template>
        </div>

        <!-- Input (command prompt style) -->
        <div class="flex-shrink-0 bg-[#1E293B] border-t border-[#334155] p-3">
          <div class="flex items-center gap-2">
            <code class="text-[#22C55E] text-xs font-mono flex-shrink-0">$</code>
            <input
              ref="inputRef"
              v-model="input"
              type="text"
              :placeholder="inputPlaceholder"
              class="flex-1 bg-transparent text-xs font-mono text-[#E2E8F0] outline-none placeholder-[#475569]"
              @keyup.enter="send"
              :disabled="loading"
            />
            <button @click="send" :disabled="loading || !input.trim()"
              :class="['px-3 py-1 rounded text-[10px] font-mono font-semibold transition-colors cursor-pointer flex-shrink-0',
                input.trim() ? 'bg-[#22C55E] text-[#0F172A] hover:bg-[#16A34A]' : 'bg-[#334155] text-[#475569]']">
              {{ loading ? 'wait...' : 'exec' }}
            </button>
          </div>
          <!-- Bottom status line -->
          <div class="flex items-center justify-between mt-2 pt-2 border-t border-[#334155]">
            <code class="text-[9px] text-[#475569]">mode: {{ modeLabel }} | msgs: {{ messages.length }}</code>
            <code class="text-[9px] text-[#475569]">AI responses are for reference only</code>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { aiApi } from '@/api/ai'
import { marked } from 'marked'
import {
  Sparkles, User, ArrowUp, Plus,
  Bot, Send, Trash2, MessageCircle,
} from 'lucide-vue-next'

/* ── Markdown Renderer ── */
marked.setOptions({
  breaks: true,
  gfm: true,
})

function renderMd(content: string): string {
  try {
    return marked.parse(content) as string
  } catch {
    return content
  }
}

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Types ── */
interface ChatMessage {
  role: 'user' | 'ai'
  content: string
  time: Date
}

/* ── Core State ── */
const messages = ref<ChatMessage[]>([])
const input = ref('')
const loading = ref(false)
const messagesRef = ref<HTMLElement | null>(null)
const inputRef = ref<HTMLInputElement | HTMLTextAreaElement | null>(null)
const chatMode = ref('normal')

/* ── Mode System ── */
const modes = [
  { key: 'normal', label: '普通对话', labelEn: 'chat' },
  { key: 'tutor', label: '学习辅导', labelEn: 'tutor' },
  { key: 'code', label: '代码助手', labelEn: 'code' },
  { key: 'exam', label: '模拟考试', labelEn: 'exam' },
]

const modeLabel = computed(() => modes.find(m => m.key === chatMode.value)?.label || '普通对话')

const systemPrompts: Record<string, string> = {
  normal: '',
  tutor: '你是一个耐心的编程学习辅导老师，会循序渐进地解释概念，并给出示例代码帮助学生理解。',
  code: '你是一个代码助手，专注于编程问题解答、代码审查和 Debug 帮助。请直接给出代码示例和解释。',
  exam: '你是一个考试模拟器。每次回答时，先提出一道与用户所问主题相关的选择题或编程题，然后等用户回答后再给出解析。',
}

const quickQuestions = [
  '如何制定学习计划？',
  '推荐入门 Java 的路径',
  '前端和后端的区别？',
  '怎么提高编程能力？',
]

const inputPlaceholder = computed(() => {
  const map: Record<string, string> = {
    normal: '输入你的问题…',
    tutor: '描述你想学习的内容…',
    code: '粘贴代码或描述问题…',
    exam: '输入想练习的知识点…',
  }
  return map[chatMode.value] || '输入你的问题…'
})

/* ── Scroll ── */
function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

/* ── Send Message ── */
function sendQuick(q: string) {
  input.value = q
  send()
}

async function send() {
  const text = input.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text, time: new Date() })
  input.value = ''
  scrollToBottom()

  loading.value = true
  try {
    let result: any
    const systemPrompt = systemPrompts[chatMode.value]
    if (systemPrompt) {
      result = await aiApi.chatWithSystem({ message: text, systemPrompt })
    } else {
      result = await aiApi.chat({ message: text })
    }
    const reply = result?.reply || result?.message || result?.content || result || '抱歉，我暂时无法回答。'
    messages.value.push({
      role: 'ai',
      content: typeof reply === 'string' ? reply : JSON.stringify(reply),
      time: new Date(),
    })
  } catch (e: any) {
    messages.value.push({
      role: 'ai',
      content: `抱歉，出现了错误：${e.message || '请稍后重试'}`,
      time: new Date(),
    })
  } finally {
    loading.value = false
    scrollToBottom()
    nextTick(() => (inputRef.value as HTMLElement)?.focus())
  }
}

function clearMessages() {
  messages.value = []
}

/* ── Format ── */
function formatTime(d: Date) {
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatTimeMono(d: Date) {
  return d.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

onMounted(() => {
  (inputRef.value as HTMLElement)?.focus()
})
</script>

<style scoped>
/* ======== ChatGPT (Light) ======== */
.chatgpt-input-box {
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #E3E8EE;
  border-radius: 16px;
  box-shadow: 0 0 15px rgba(0,0,0,0.04), 0 0 3px rgba(0,0,0,0.04);
  transition: box-shadow 0.15s, border-color 0.15s;
}
.chatgpt-input-box:focus-within {
  border-color: #C5C5D2;
  box-shadow: 0 0 15px rgba(0,0,0,0.06);
}
.chatgpt-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: #10A37F; opacity: 0.4;
  animation: chatgptBounce 1.4s infinite ease-in-out;
  display: inline-block;
}
@keyframes chatgptBounce {
  0%, 60%, 100% { opacity: 0.3; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-6px); }
}
.chatgpt-scroll::-webkit-scrollbar { width: 6px; }
.chatgpt-scroll::-webkit-scrollbar-track { background: transparent; }
.chatgpt-scroll::-webkit-scrollbar-thumb { background: #D1D5DB; border-radius: 99px; }
.chatgpt-scroll::-webkit-scrollbar-thumb:hover { background: #9CA3AF; }

/* ======== Claude Dark ======== */
.claude-input-box {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.claude-input-box:focus-within {
  border-color: rgba(217,119,6,0.3);
  box-shadow: 0 0 20px rgba(217,119,6,0.06);
}
.claude-scroll::-webkit-scrollbar { width: 4px; }
.claude-scroll::-webkit-scrollbar-track { background: transparent; }
.claude-scroll::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 99px; }

/* ======== Notion AI (Warm) ======== */
.notion-user-bubble {
  max-width: 70%;
  padding: 12px 16px;
  background: #292524;
  color: white;
  border-radius: 20px 20px 4px 20px;
}
.notion-ai-block {
  padding: 16px;
  background: #FFFBF5;
  border-left: 3px solid #D97706;
  border-radius: 0 12px 12px 0;
}
.notion-typing-dots {
  display: inline-flex;
  gap: 3px;
  margin-left: 4px;
}
.notion-typing-dots span {
  width: 5px; height: 5px; border-radius: 50%;
  background: #D97706;
  animation: notionDot 1.4s infinite ease-in-out;
}
.notion-typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.notion-typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes notionDot {
  0%, 60%, 100% { opacity: 0.2; transform: scale(0.8); }
  30% { opacity: 1; transform: scale(1.2); }
}
.notion-input-box {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 14px 16px;
  background: white;
  border: 2px solid #E7E5E4;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E7E5E4;
  transition: border-color 0.15s;
}
.notion-input-box:focus-within {
  border-color: #D97706;
  box-shadow: 0 3px 0 #D97706;
}
.notion-scroll::-webkit-scrollbar { width: 4px; }
.notion-scroll::-webkit-scrollbar-track { background: transparent; }
.notion-scroll::-webkit-scrollbar-thumb { background: #D6D3D1; border-radius: 99px; }

/* ======== Terminal (Pro) ======== */
.terminal-response {
  padding: 8px 12px;
  margin-left: 12px;
  border-left: 2px solid #334155;
  background: rgba(255,255,255,0.02);
}
.terminal-cursor {
  display: inline-block;
  width: 8px;
  background: #22C55E;
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
.terminal-loading::after {
  content: '';
  display: inline-block;
  animation: dots 1.5s steps(4) infinite;
}
@keyframes dots {
  0% { content: ''; }
  25% { content: '.'; }
  50% { content: '..'; }
  75% { content: '...'; }
}
.terminal-scroll::-webkit-scrollbar { width: 6px; }
.terminal-scroll::-webkit-scrollbar-track { background: #0F172A; }
.terminal-scroll::-webkit-scrollbar-thumb { background: #334155; border-radius: 99px; }
.terminal-scroll::-webkit-scrollbar-thumb:hover { background: #475569; }
</style>
