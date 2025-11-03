// main.js
// 1. 核心依赖引入（按优先级排序：Vue核心 → 工具 → 组件库）
import { createSSRApp } from 'vue'
import App from './App.vue'

// 2. 网络请求拦截器（全局HTTP工具）
import http from './utils/request'

// 3. Mock配置（仅开发环境启用，避免生产环境冗余）
if (process.env.NODE_ENV === 'development') {
  // 动态导入Mock（减少生产环境打包体积）
  import('./mock/api')
}

// 4. UI组件库（uView-plus V3）
import uViewPlus from 'uview-plus'
import 'uview-plus/index.scss' // 确保样式文件正确引入


// 5. 应用创建与配置
export function createApp() {
  const app = createSSRApp(App)

  // 注册UI组件库
  app.use(uViewPlus)

  // 挂载全局工具（HTTP请求与取消请求）
  app.config.globalProperties.$http = http
  app.config.globalProperties.$cancelRequest = http.cancelRequest;

  // 可选：全局配置（如错误处理、全局组件等）
  // app.config.errorHandler = (err) => { ... } // 全局错误捕获

  return { app }
}