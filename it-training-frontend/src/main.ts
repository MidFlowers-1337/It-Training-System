import { createApp } from 'vue'
import PrimeVue from 'primevue/config'
// Element Plus 按需导入（通过 unplugin-vue-components 自动处理）
// 只导入必要的基础样式（重置样式）
import 'element-plus/theme-chalk/src/base.scss'
import './styles/variables.css'
import './styles/semantic.css'
import './style.css'
import App from './App.vue'
import router from './router'
import pinia from './store'
import { initDesignSystem, primeVueConfig } from '@/design-system'

// 初始化 Design System（主题、CSS 变量）
initDesignSystem()

const app = createApp(App)

// 注册 PrimeVue（unstyled 模式 + PassThrough）
app.use(PrimeVue, primeVueConfig)

// Element Plus 组件按需自动导入，无需全局注册

app.use(router)
app.use(pinia)

app.mount('#app')
