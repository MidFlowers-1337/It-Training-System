/**
 * main.ts - 应用入口（Design System 集成示例）
 *
 * 注意：这是示例文件，展示如何集成 Design System 和 PrimeVue Unstyled
 * 实际迁移时，需要逐步替换 Element Plus 组件
 */

import { createApp } from 'vue';

// PrimeVue
import PrimeVue from 'primevue/config';

// Design System
import { initDesignSystem, primeVueConfig } from './design-system';

// 样式文件（顺序很重要）
import './styles/variables.css';  // CSS 变量定义
import './styles/semantic.css';   // 语义层（保留用于过渡期）
import './style.css';             // 全局样式

// Element Plus（过渡期保留）
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

import App from './App.vue';
import router from './router';
import pinia from './store';

// 初始化 Design System（应用 CSS 变量）
initDesignSystem();

const app = createApp(App);

// 注册 Element Plus（过渡期保留）
app.use(ElementPlus);

// 注册 PrimeVue（Unstyled 模式）
app.use(PrimeVue, primeVueConfig);

// 路由和状态管理
app.use(router);
app.use(pinia);

app.mount('#app');
