import { createApp } from 'vue'
import App from './App.vue'
import i18n from './i18n';

// 引入 Element Plus 和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import './assets/styles/global.css'
import './assets/styles/dark-anime.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from "@/router";

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 使用 Element Plus 插件
app.use(ElementPlus)
app.use(i18n); // 使用 i18n
app.use(router)
app.mount('#app')