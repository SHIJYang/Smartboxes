// main.js
import App from './App.vue'
import '@/mock' // 开发环境使用 mock 数据
import { setupMock } from './mock/api'

if (process.env.NODE_ENV === 'development') {
  setupMock()
}

// 引入 uView Plus
import uViewPlus from 'uview-plus'
import 'uview-plus/index.scss'

// #ifdef VUE3
import { createSSRApp } from 'vue'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uViewPlus)
  return { app }
}
// #endif