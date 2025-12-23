import { createApp } from 'vue'
import PrimeVue from 'primevue/config'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
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

// 保留 ElementPlus（兼容未重构页面）
app.use(ElementPlus)

app.use(router)
app.use(pinia)

app.mount('#app')
