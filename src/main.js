import { createSSRApp } from 'vue'
import App from './App.vue'
import { setupMock } from '../mock/api'
import { createI18n } from 'vue-i18n'
import messages from './locale'

// 开发环境下启用 mock
if (process.env.NODE_ENV === 'development') {
    setupMock()
}

const i18n = createI18n({
    legacy: false,
    globalInjection: true,
    locale: uni.getStorageSync('locale') || 'zh-CN',
    fallbackLocale: 'zh-CN',
    messages
})

export function createApp() {
    const app = createSSRApp(App)
    app.use(i18n)
    return {
        app
    }
}
