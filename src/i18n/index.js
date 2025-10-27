import Vue from 'vue'
import { createI18n } from 'vue-i18n'
import en from './locales/en.js'
import zh from './locales/zh.js'
import ja from './locales/ja.js'

Vue.use(VueI18n)

const messages = {
    'zh-CN': {
        // ...existing code...
    },
    'en-US': {
        // ...existing code...
    }
}

const i18n = createI18n({
    legacy: false,  // 使用组合式API
    locale: uni.getStorageSync('locale') || 'zh-CN',
    fallbackLocale: 'zh-CN',
    messages,
    globalInjection: true,
    missingWarn: false,
    fallbackWarn: false
})

export default i18n
