import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createUnhead, headSymbol } from '@unhead/vue'
import router from './router'
import App from './App.vue'
import './styles/base.css'

const app = createApp(App)
const pinia = createPinia()
const head = createUnhead()

app.use(pinia)
app.use(router)
app.provide(headSymbol, head)

import { useThemeStore } from './stores/theme'
const themeStore = useThemeStore()
themeStore.initTheme()

import { useUserStore } from './stores/user'
const userStore = useUserStore()
userStore.init()

app.mount('#app')
