import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
// 1. 引入 Pinia
import { createPinia } from 'pinia'

export function createApp() {
  const app = createSSRApp(App)
  
  // 2. 创建 Pinia 实例
  const pinia = createPinia()
  
  // 3. 挂载 Pinia
  app.use(pinia)
  
  return {
    app,
    // 4. 导出 Pinia (虽然 app.use 已经挂载，但在某些 UniApp 插件中可能需要显式返回)
    Pinia: pinia 
  }
}
// #endif